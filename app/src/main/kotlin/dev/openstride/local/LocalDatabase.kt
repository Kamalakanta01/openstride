package dev.openstride.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate

/**
 * The local database. This is the source of truth — there is no server.
 *
 * The table and column names follow the original app's on-device model closely
 * enough to stay legible to anyone who has read the analysis, but the schema is
 * our own design. See ../../../docs and ../../../../../spec/data-model.md.
 */
@Database(
    entities = [
        ActivityEntity::class,
        ActivityStreamEntity::class,
        AthleteEntity::class,
        RouteEntity::class,
        RoutePointEntity::class,
        SegmentEntity::class,
        SegmentEffortEntity::class,
        GearEntity::class,
        GoalEntity::class,
        PrivacyZoneEntity::class,
        UnsyncedActivityEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao

    abstract fun streamDao(): ActivityStreamDao

    abstract fun athleteDao(): AthleteDao

    abstract fun routeDao(): RouteDao

    abstract fun segmentDao(): SegmentDao

    abstract fun gearDao(): GearDao

    abstract fun goalDao(): GoalDao

    abstract fun privacyZoneDao(): PrivacyZoneDao

    abstract fun unsyncedDao(): UnsyncedActivityDao
}

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val athleteId: Long,
    val name: String,
    val type: String,
    val startedAt: Instant,
    val elapsedSeconds: Int,
    val movingSeconds: Int,
    val distanceMeters: Double,
    val elevationGainMeters: Double,
    val elevationLossMeters: Double,
    val averageSpeedMetersPerSecond: Double,
    val maxSpeedMetersPerSecond: Double,
    val averageHeartRate: Int?,
    val maxHeartRate: Int?,
    val calories: Int?,
    val commute: Boolean,
    val trainer: Boolean,
    val manual: Boolean,
    val visibility: String,
    val gearId: Long?,
    val routeId: Long?,
    val startLatitude: Double?,
    val startLongitude: Double?,
    val endLatitude: Double?,
    val endLongitude: Double?,
    val polyline: String?,
    val createdAt: Instant,
)

@Entity(tableName = "activity_streams", primaryKeys = ["activityId", "type"])
data class ActivityStreamEntity(
    val activityId: Long,
    val type: String,
    val data: ByteArray,
    val resolutionSeconds: Int,
) {
    override fun equals(other: Any?): Boolean =
        other is ActivityStreamEntity &&
            activityId == other.activityId &&
            type == other.type &&
            resolutionSeconds == other.resolutionSeconds &&
            data.contentEquals(other.data)

    override fun hashCode(): Int {
        var result = activityId.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + resolutionSeconds
        result = 31 * result + data.contentHashCode()
        return result
    }
}

@Entity(tableName = "athletes")
data class AthleteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String?,
    val city: String?,
    val country: String?,
    val timezone: String,
    val weightKilograms: Double?,
    val createdAt: Instant,
)

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val athleteId: Long,
    val name: String,
    val description: String?,
    val distanceMeters: Double,
    val elevationGainMeters: Double,
    val estimatedSeconds: Int?,
    val polyline: String?,
    val visibility: String,
    val createdAt: Instant,
)

@Entity(tableName = "route_points", primaryKeys = ["routeId", "index"])
data class RoutePointEntity(
    val routeId: Long,
    val index: Int,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Double,
    val elapsedSeconds: Int,
    val elevationMeters: Double?,
    val gradePercent: Double?,
    val tempoZone: Int?,
)

@Entity(tableName = "segments")
data class SegmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val activityType: String,
    val distanceMeters: Double,
    val elevationGainMeters: Double,
    val climbCategory: Int?,
    val localLegend: String?,
)

@Entity(tableName = "segment_efforts")
data class SegmentEffortEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val segmentId: Long,
    val activityId: Long,
    val athleteId: Long,
    val elapsedSeconds: Int,
    val distanceMeters: Double,
    val averageHeartRate: Int?,
    val maxHeartRate: Int?,
    val averageWatts: Double?,
    val isKom: Boolean,
)

@Entity(tableName = "gear")
data class GearEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val athleteId: Long,
    val name: String,
    val type: String,
    val brand: String?,
    val model: String?,
    val distanceMeters: Double,
    val retiredAt: Instant?,
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val targetValue: Double,
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
)

@Entity(tableName = "privacy_zones")
data class PrivacyZoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val centerLatitude: Double,
    val centerLongitude: Double,
    val radiusMeters: Double,
    val type: String,
)

@Entity(tableName = "unsynced_activities")
data class UnsyncedActivityEntity(
    @PrimaryKey val guid: String,
    val athleteId: Long,
    val type: String,
    val startedAt: Instant,
    val samples: ByteArray,
    val state: String,
) {
    override fun equals(other: Any?): Boolean =
        other is UnsyncedActivityEntity &&
            guid == other.guid &&
            state == other.state &&
            samples.contentEquals(other.samples)

    override fun hashCode(): Int {
        var result = guid.hashCode()
        result = 31 * result + samples.contentHashCode()
        return result
    }
}

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY startedAt DESC")
    fun observeAll(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE id = :id")
    suspend fun byId(id: Long): ActivityEntity?

    @Query("SELECT * FROM activities WHERE startedAt BETWEEN :from AND :to ORDER BY startedAt")
    suspend fun between(
        from: Instant,
        to: Instant,
    ): List<ActivityEntity>

    @Query("SELECT * FROM activities WHERE type = :type ORDER BY startedAt DESC")
    fun observeByType(type: String): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(activity: ActivityEntity): Long

    @Query("DELETE FROM activities WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface ActivityStreamDao {
    @Query("SELECT * FROM activity_streams WHERE activityId = :activityId")
    suspend fun forActivity(activityId: Long): List<ActivityStreamEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stream: ActivityStreamEntity)
}

@Dao
interface AthleteDao {
    @Query("SELECT * FROM athletes LIMIT 1")
    suspend fun current(): AthleteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(athlete: AthleteEntity): Long
}

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<RouteEntity>>

    @Query("SELECT * FROM route_points WHERE routeId = :routeId ORDER BY `index`")
    suspend fun points(routeId: Long): List<RoutePointEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(route: RouteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPoints(points: List<RoutePointEntity>)
}

@Dao
interface SegmentDao {
    @Query("SELECT * FROM segments")
    fun observeAll(): Flow<List<SegmentEntity>>

    @Query("SELECT * FROM segment_efforts WHERE segmentId = :segmentId ORDER BY elapsedSeconds")
    suspend fun efforts(segmentId: Long): List<SegmentEffortEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(segment: SegmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEffort(effort: SegmentEffortEntity): Long
}

@Dao
interface GearDao {
    @Query("SELECT * FROM gear WHERE retiredAt IS NULL ORDER BY distanceMeters DESC")
    fun observeActive(): Flow<List<GearEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(gear: GearEntity): Long
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE periodEnd >= :from")
    fun observeCurrent(from: LocalDate): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(goal: GoalEntity): Long
}

@Dao
interface PrivacyZoneDao {
    @Query("SELECT * FROM privacy_zones")
    fun observeAll(): Flow<List<PrivacyZoneEntity>>

    @Query("SELECT * FROM privacy_zones")
    suspend fun all(): List<PrivacyZoneEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(zone: PrivacyZoneEntity): Long

    @Query("DELETE FROM privacy_zones WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface UnsyncedActivityDao {
    @Query("SELECT * FROM unsynced_activities WHERE state IN ('IN_PROGRESS', 'PAUSED') ORDER BY startedAt DESC")
    suspend fun resumable(): List<UnsyncedActivityEntity>

    @Query("SELECT * FROM unsynced_activities WHERE guid = :guid")
    suspend fun byGuid(guid: String): UnsyncedActivityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(activity: UnsyncedActivityEntity)
}

class Converters {
    @TypeConverter fun instantToLong(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter fun longToInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter fun localDateToLong(value: LocalDate?): Long? = value?.toEpochDay()

    @TypeConverter fun longToLocalDate(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)
}
