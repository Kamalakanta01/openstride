package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class TileSourceConfig(
    val globalHeatmapURLTemplate: String,
    val gradientURLTemplate: String,
    val heightmapURLTemplate: String,
    val mapboxStreetsTerrainURLTemplate: String,
    val mapboxStreetsURLTemplate: String,
    val nightlyHeatmapURLTemplate: String,
    val personalHeatmapURLTemplate: String,
    val segmentsTemplate: String,
    val stravaContoursURLTemplate: String,
    val stravaPlanetSportURLTemplate: String,
    val stravaPlanetURLTemplate: String,
    val stravaPoiURLTemplate: String,
    val summerImageryURLTemplate: String,
    val weeklyHeatmapURLTemplate: String,
    val winterImageryURLTemplate: String,
    val winterOverlayImageryURLTemplate: String
) {
    override fun toString(): String = "TileSourceConfig(globalHeatmapURLTemplate=${globalHeatmapURLTemplate}, gradientURLTemplate=${gradientURLTemplate}, heightmapURLTemplate=${heightmapURLTemplate}, mapboxStreetsTerrainURLTemplate=${mapboxStreetsTerrainURLTemplate}, mapboxStreetsURLTemplate=${mapboxStreetsURLTemplate}, nightlyHeatmapURLTemplate=${nightlyHeatmapURLTemplate}, personalHeatmapURLTemplate=${personalHeatmapURLTemplate}, segmentsTemplate=${segmentsTemplate}, stravaContoursURLTemplate=${stravaContoursURLTemplate}, stravaPlanetSportURLTemplate=${stravaPlanetSportURLTemplate}, stravaPlanetURLTemplate=${stravaPlanetURLTemplate}, stravaPoiURLTemplate=${stravaPoiURLTemplate}, summerImageryURLTemplate=${summerImageryURLTemplate}, weeklyHeatmapURLTemplate=${weeklyHeatmapURLTemplate}, winterImageryURLTemplate=${winterImageryURLTemplate}, winterOverlayImageryURLTemplate=${winterOverlayImageryURLTemplate})"
}
