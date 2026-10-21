package dev.openstride.ui.map

import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.openstride.location.DeviceLocation
import kotlinx.coroutines.flow.collectLatest
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

/**
 * The athlete's dot on the map.
 *
 * The original app shows a dot where you are, and while recording a line
 * from that dot to the point you have moved furthest from. The dot is drawn
 * here as a map layer, from the one place the app reads the location.
 */
private const val ATHLETE_SOURCE = "openstride-athlete"

/** Draws the athlete's position onto a map. */
@Composable
fun DrawAthlete(
    map: MapLibreMap?,
    modifier: Modifier = Modifier,
) {
    var position by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(Unit) {
        DeviceLocation.location.collectLatest { position = it }
    }

    LaunchedEffect(map, position) {
        val ready = map ?: return@LaunchedEffect
        val style = ready.style ?: return@LaunchedEffect
        if (!style.isFullyLoaded) return@LaunchedEffect

        if (style.getSource(ATHLETE_SOURCE) == null) {
            style.addSource(GeoJsonSource(ATHLETE_SOURCE, FeatureCollection.fromFeatures(emptyList())))
            // The accuracy ring, under the dot.
            style.addLayer(
                CircleLayer("openstride-athlete-accuracy", ATHLETE_SOURCE).withProperties(
                    PropertyFactory.circleRadius(14f),
                    PropertyFactory.circleColor("#1A73E8"),
                    PropertyFactory.circleOpacity(0.15f),
                    PropertyFactory.circleBlur(0.2f),
                ),
            )
            // The dot itself.
            style.addLayer(
                CircleLayer("openstride-athlete-dot", ATHLETE_SOURCE).withProperties(
                    PropertyFactory.circleRadius(7f),
                    PropertyFactory.circleColor("#1A73E8"),
                    PropertyFactory.circleStrokeWidth(2.5f),
                    PropertyFactory.circleStrokeColor("#FFFFFF"),
                ),
            )
        }

        val current = position
        val features =
            if (current == null) {
                emptyList()
            } else {
                listOf(
                    Feature.fromGeometry(
                        Point.fromLngLat(current.longitude, current.latitude),
                    ),
                )
            }
        style
            .getSourceAs<GeoJsonSource>(ATHLETE_SOURCE)
            ?.setGeoJson(FeatureCollection.fromFeatures(features))
    }
}

/** Moves the camera to a position. */
fun MapLibreMap.moveTo(
    latitude: Double,
    longitude: Double,
    zoom: Double = 15.0,
) {
    animateCamera(
        org.maplibre.android.camera.CameraUpdateFactory.newLatLngZoom(
            LatLng(latitude, longitude),
            zoom,
        ),
        500,
    )
}
