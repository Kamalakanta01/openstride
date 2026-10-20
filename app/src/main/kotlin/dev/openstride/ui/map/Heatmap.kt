package dev.openstride.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.openstride.OpenStrideApplication
import dev.openstride.analysis.Heatmap
import dev.openstride.data.AppRepository
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
    val context = LocalContext.current
    var cells by remember { mutableStateOf(emptyList<Heatmap.Cell>()) }

    LaunchedEffect(activityIds) {
        if (activityIds.isEmpty()) {
            cells = emptyList()
            return@LaunchedEffect
        }
        val repository = AppRepository((context.applicationContext as OpenStrideApplication).database)
        cells = repository.heatmap(activityIds)
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

        val busiest = cells.maxOfOrNull { it.count } ?: 0
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
