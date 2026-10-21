package dev.openstride

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.openstride.data.AppRepository
import dev.openstride.local.LocalDatabase

/**
 * Application entry point.
 *
 * Deliberately thin. The original app's `onCreate` starts the feed loader, the
 * remote logger, a preference migration pass and the Branch SDK — four things that
 * exist only because there is a server. Here there is nothing to start.
 */
class OpenStrideApplication : Application() {
    lateinit var database: LocalDatabase
        private set

    /**
     * The app's facade over the database.
     *
     * There is one of these, so the expensive work — rebuilding the tracks,
     * recomputing the analysis, rebuilding the heatmap — is done once and
     * shared by every screen.
     */
    lateinit var repository: AppRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database =
            Room
                .databaseBuilder(this, LocalDatabase::class.java, "strava.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
        repository = AppRepository(database)
    }
}

/**
 * Version 2 added the table that holds the shape of a local segment.
 *
 * The table is new, so the migration only has to create it; the activities
 * that are already stored are left alone.
 */
private val MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `segment_points` (
                    `segmentId` INTEGER NOT NULL,
                    `index` INTEGER NOT NULL,
                    `latitude` REAL NOT NULL,
                    `longitude` REAL NOT NULL,
                    `distanceMeters` REAL NOT NULL,
                    `elevationMeters` REAL NOT NULL,
                    PRIMARY KEY(`segmentId`, `index`)
                )
                """.trimIndent(),
            )
        }
    }

/**
 * Version 3 added the star to a segment.
 *
 * SQLite cannot add a column with a default to a table that has rows
 * without rewriting it, so the table is rebuilt.
 */
private val MIGRATION_2_3 =
    object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `segments_new` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `activityType` TEXT NOT NULL,
                    `distanceMeters` REAL NOT NULL,
                    `elevationGainMeters` REAL NOT NULL,
                    `climbCategory` INTEGER,
                    `localLegend` TEXT,
                    `starred` INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                INSERT INTO `segments_new` (`id`, `name`, `activityType`, `distanceMeters`,
                    `elevationGainMeters`, `climbCategory`, `localLegend`, `starred`)
                SELECT `id`, `name`, `activityType`, `distanceMeters`,
                    `elevationGainMeters`, `climbCategory`, `localLegend`, 0
                FROM `segments`
                """.trimIndent(),
            )
            db.execSQL("DROP TABLE `segments`")
            db.execSQL("ALTER TABLE `segments_new` RENAME TO `segments`")
        }
    }

/** Version 4 added the photographs attached to an activity. */
private val MIGRATION_3_4 =
    object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `activity_photos` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `activityId` INTEGER NOT NULL,
                    `uri` TEXT NOT NULL,
                    `caption` TEXT,
                    `takenAt` INTEGER NOT NULL
                )
                """.trimIndent(),
            )
        }
    }
