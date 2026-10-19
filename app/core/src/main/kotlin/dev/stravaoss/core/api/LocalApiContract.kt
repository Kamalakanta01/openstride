package dev.stravaoss.core.api

import dev.stravaoss.core.model.Activity
import dev.stravaoss.core.model.Athlete
import dev.stravaoss.core.model.Route
import dev.stravaoss.core.model.Segment
import dev.stravaoss.core.model.SegmentEffort
import kotlinx.coroutines.flow.Flow

/**
 * The application's data contract.
 *
 * The original app is a Retrofit client talking to `https://www.strava.com/api/v3/`
 * and `https://graphql.strava.com`. This project has **no server**, so this interface
 * is implemented directly against the local database.
 *
 * The two designs are deliberately different:
 *
 *  - The original serialises to JSON, parses into a model, then caches it.
 *  - Here, the local database *is* the model. There is no serialisation step, so
 *    there is nothing to keep in sync and no cache to invalidate.
 *
 * The endpoint shapes that the original depended on are recorded in
 * `../../../spec/api-contract.md` — useful for understanding what features exist and
 * what a future sync feature would need to implement.
 */
interface LocalDataSource {

    // ---- athlete ----

    suspend fun currentAthlete(): Athlete?

    suspend fun saveAthlete(athlete: Athlete): Long

    // ---- activities ----

    fun observeActivities(): Flow<List<Activity>>

    fun observeActivities(type: dev.stravaoss.core.model.ActivityType): Flow<List<Activity>>

    suspend fun activity(id: Long): Activity?

    suspend fun activitiesBetween(
        from: java.time.Instant,
        to: java.time.Instant,
    ): List<Activity>

    suspend fun saveActivity(activity: Activity): Long

    suspend fun deleteActivity(id: Long)

    // ---- routes ----

    fun observeRoutes(): Flow<List<Route>>

    suspend fun saveRoute(route: Route, points: List<dev.stravaoss.core.model.RoutePoint>): Long

    // ---- segments ----

    fun observeSegments(): Flow<List<Segment>>

    suspend fun efforts(segmentId: Long): List<SegmentEffort>

    suspend fun saveEffort(effort: SegmentEffort): Long

    // ---- privacy ----

    fun observePrivacyZones(): Flow<List<dev.stravaoss.core.model.PrivacyZone>>

    suspend fun savePrivacyZone(zone: dev.stravaoss.core.model.PrivacyZone): Long

    suspend fun deletePrivacyZone(id: Long)
}

/**
 * A local token pair.
 *
 * The original app's model (`com.strava.net.token.data.AccessToken`) carries an
 * expiry and a refresh token, because the token is minted by a server. Here the
 * token is minted on device and never expires, so `expiresAt` is always null.
 *
 * We keep the shape because it keeps the repository code honest: if a sync feature
 * is ever added, it has a place to put the server's token.
 */
data class LocalToken(
    val accessToken: String,
    val refreshToken: String? = null,
    val expiresAt: java.time.Instant? = null,
    val isSignUp: Boolean = false,
)

/**
 * Where a map engine gets its tiles.
 *
 * The original engine compiles in templates like
 * `{MAPBOX_TILES_SERVER_URL}/mapbox.mapbox-streets-v8/{z}/{x}/{y}.vector.pbf?access_token={...}`
 * and exposes `setServerConfig`, `setStravaAuthData` and `setStravaSourcesExtraHeaders`
 * to redirect them. A clean-room engine should offer the same three, so the app layer
 * does not care where tiles come from.
 *
 * See `../../../spec/fatmap-native.md`.
 */
data class TileSourceConfig(
    val baseUrl: String,
    val styleUrl: String? = null,
    val accessToken: String? = null,
    val extraHeaders: Map<String, String> = emptyMap(),
    val terrainBaseUrl: String? = null,
)

/**
 * The map engine facade.
 *
 * Deliberately small. The original has 312 native entry points; a clean-room
 * implementation should expose the subset the app actually uses and keep the
 * renderer swappable.
 */
interface MapEngine {
    suspend fun setServerConfig(config: TileSourceConfig)
    suspend fun setAuthData(token: LocalToken?)
    fun camera(): MapCamera
    fun content(): MapContent
    suspend fun setVisibleArea(latitude: Double, longitude: Double, zoom: Double)
    suspend fun renderToBitmap(width: Int, height: Int): android.graphics.Bitmap
}

interface MapCamera {
    suspend fun flyTo(latitude: Double, longitude: Double, zoom: Double, durationMs: Long = 800)
    suspend fun followUserLocation(enabled: Boolean)
    suspend fun currentTarget(): Triple<Double, Double, Double>
}

interface MapContent {
    suspend fun addRoute(route: Route)
    suspend fun clearRoutes()
    suspend fun addActivity(activity: Activity)
    suspend fun clearActivities()
    suspend fun setUserMarker(latitude: Double, longitude: Double)
    suspend fun removeUserMarker()
}

/**
 * The heatmap layer.
 *
 * The original serves heatmaps from Strava's servers with a visibility scope in the
 * query string (`include_everyone`, `include_followers_only`, `include_only_me`,
 * `respect_privacy_zones`, `include_commutes`, date filters).
 *
 * Here there is only one viewer, so the scope collapses to "me" — and privacy zones
 * are applied **locally**, which is strictly stronger than the original's
 * server-side enforcement: it cannot be bypassed by a modified client.
 */
interface HeatmapLayer {
    suspend fun setVisible(visible: Boolean)
    suspend fun setIncludeCommutes(include: Boolean)
    suspend fun setDateRange(from: java.time.Instant?, to: java.time.Instant?)
    suspend fun setActivityTypes(types: Set<dev.stravaoss.core.model.ActivityType>)
    suspend fun setRespectPrivacyZones(respect: Boolean)
}
