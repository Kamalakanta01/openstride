package dev.openstride.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.openstride.recording.TrackPoint
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.OnMapReadyCallback
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

/**
 * The map.
 *
 * MapLibre draws the basemap. The Fatmap engine, which this project also
 * builds, is the 1:1 reimplementation of the original's map engine, but it
 * renders nothing yet: it has no GL context, no tile fetching and no
 * rasteriser. Until that work lands, the basemap comes from MapLibre and the
 * app talks to the engine through the generated Fatmap API.
 *
 * The style is OpenFreeMap's "liberty", which needs no API key.
 */
private const val STYLE_URL = "https://tiles.openfreemap.org/styles/liberty"
private const val TRACK_SOURCE = "openstride-track"
private const val TRACK_LAYER = "openstride-track-line"

@Composable
fun OpenStrideMap(
    modifier: Modifier = Modifier,
    track: List<TrackPoint> = emptyList(),
    followTrack: Boolean = false,
    onTap: ((TrackPoint) -> Unit)? = null,
    onMapReady: (MapLibreMap) -> Unit = {},
) {
    val context = LocalContext.current
    var map by remember { mutableStateOf<MapLibreMap?>(null) }
    var hasPermission by remember { mutableStateOf(hasLocationPermission(context)) }

    val mapView =
        remember {
            MapView(context).apply {
                onCreate(null)
                getMapAsync(
                    OnMapReadyCallback { ready ->
                        ready.setStyle(Style.Builder().fromUri(STYLE_URL))
                        val (position, zoom) = MapStart.cameraFor(context)
                        ready.cameraPosition =
                            CameraPosition
                                .Builder()
                                .target(position)
                                .zoom(zoom)
                                .build()
                        map = ready
                        onMapReady(ready)
                    },
                )
            }
        }

    DisposableEffect(mapView) {
        onDispose {
            mapView.onDestroy()
        }
    }

    // The map opens before the athlete has given permission, so it cannot
    // centre on them yet. When the permission arrives - which happens in a
    // system dialog, and the app is resumed afterwards - the camera moves.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    val granted = hasLocationPermission(context)
                    if (granted && !hasPermission) {
                        hasPermission = true
                    }
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(map, hasPermission) {
        if (!hasPermission) return@LaunchedEffect
        val ready = map ?: return@LaunchedEffect
        val (position, zoom) = MapStart.cameraFor(context)
        ready.cameraPosition =
            CameraPosition
                .Builder()
                .target(position)
                .zoom(zoom)
                .build()
    }

    // Tapping the map reports the point that was tapped, which is how the
    // route planner is drawn.
    val tapHandler = onTap
    LaunchedEffect(map, tapHandler) {
        val ready = map ?: return@LaunchedEffect
        val handler = tapHandler ?: return@LaunchedEffect
        ready.addOnMapClickListener { latLng ->
            handler(
                TrackPoint(
                    latitude = latLng.latitude,
                    longitude = latLng.longitude,
                    altitudeMeters = 0.0,
                    timeMillis = 0L,
                ),
            )
            true
        }
    }

    // The style arrives after the map does, so the track waits for it
    // rather than being quietly left off the map.
    var style by remember { mutableStateOf<Style?>(null) }
    LaunchedEffect(map) {
        val ready = map ?: return@LaunchedEffect
        ready.getStyle { loaded -> style = loaded }
    }

    // The track is a separate effect: the view itself must not be rebuilt
    // every time a new point arrives.
    LaunchedEffect(style, track) {
        val ready = map ?: return@LaunchedEffect
        val style = style ?: return@LaunchedEffect

        if (style.getSource(TRACK_SOURCE) == null) {
            style.addSource(GeoJsonSource(TRACK_SOURCE, emptyLineString()))
            style.addLayer(
                LineLayer(TRACK_LAYER, TRACK_SOURCE).withProperties(
                    PropertyFactory.lineColor(TRACK_COLOUR),
                    PropertyFactory.lineWidth(6f),
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                ),
            )
        }

        if (track.size < 2) {
            style.getSourceAs<GeoJsonSource>(TRACK_SOURCE)?.setGeoJson(emptyLineString())
            return@LaunchedEffect
        }

        val coordinates = track.map { LatLng(it.latitude, it.longitude) }
        style
            .getSourceAs<GeoJsonSource>(TRACK_SOURCE)
            ?.setGeoJson(LineString.fromLngLats(coordinates.map { Point.fromLngLat(it.longitude, it.latitude) }))

        if (followTrack) {
            val bounds = LatLngBounds.Builder().includes(coordinates).build()
            ready.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64), 600)
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
    )
}

/** The colour of the recorded track. */
private const val TRACK_COLOUR = "#FC5200"

/** An empty track, for before anything has been recorded. */
private fun emptyLineString(): LineString = LineString.fromLngLats(emptyList<Point>())
