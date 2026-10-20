package dev.openstride

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

    override fun onCreate() {
        super.onCreate()
        database =
            Room
                .databaseBuilder(this, LocalDatabase::class.java, "strava.db")
                .addMigrations(MIGRATION_1_2)
                .build()
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
