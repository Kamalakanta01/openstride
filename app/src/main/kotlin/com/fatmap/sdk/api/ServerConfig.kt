package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class ServerConfig(
    val mapboxServerSecret: String,
    val mapboxServerURL: String,
    val segmentsServerURL: String,
    val stravaPoiServerURL: String,
    val stravaTileServerURL: String,
    val tileSourceConfig: TileSourceConfig
) {
    override fun toString(): String = "ServerConfig(mapboxServerSecret=${mapboxServerSecret}, mapboxServerURL=${mapboxServerURL}, segmentsServerURL=${segmentsServerURL}, stravaPoiServerURL=${stravaPoiServerURL}, stravaTileServerURL=${stravaTileServerURL}, tileSourceConfig=${tileSourceConfig})"
}
