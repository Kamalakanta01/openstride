package dev.openstride.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.openstride.ui.MapViewModel
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

/**
 * The athlete's personal heatmap, drawn on the map.
 *
 * The original app has a heatmap of everywhere the athlete has been. Here it
 * is built from the activities on the device.
 */
private const val HEATMAP_SOURCE = "openstride-heatmap"

/** Draws the heatmap of the given activities onto a map. */
@Composable
fun DrawHeatmap(
    map: MapLibreMap?,
    activityIds: List<Long>,
    modifier: Modifier = Modifier,
) {
    val viewModel: MapViewModel = viewModel()
    val cells by viewModel.heatmap.collectAsStateWithLifecycle()

    LaunchedEffect(activityIds) {
        viewModel.setActivities(activityIds)
    }

    LaunchedEffect(map, cells) {
        val ready = map ?: return@LaunchedEffect
        val style = ready.style ?: return@LaunchedEffect
        if (!style.isFullyLoaded) return@LaunchedEffect

        if (style.getSource(HEATMAP_SOURCE) == null) {
            style.addSource(GeoJsonSource(HEATMAP_SOURCE, FeatureCollection.fromFeatures(emptyList())))
            style.addLayer(
                CircleLayer("openstride-heatmap-layer", HEATMAP_SOURCE).withProperties(
                    PropertyFactory.circleRadius(4f),
                    PropertyFactory.circleColor("#FC5200"),
                    PropertyFactory.circleOpacity(0.45f),
                    PropertyFactory.circleBlur(0.4f),
                ),
            )
        }

        val features =
            cells.map { cell ->
                Feature.fromGeometry(Point.fromLngLat(cell.longitude, cell.latitude))
            }
        style.getSourceAs<GeoJsonSource>(HEATMAP_SOURCE)?.setGeoJson(FeatureCollection.fromFeatures(features))

        if (features.isNotEmpty()) {
            ready.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(cells.first().latitude, cells.first().longitude),
                    12.0,
                ),
                600,
            )
        }
    }
}
