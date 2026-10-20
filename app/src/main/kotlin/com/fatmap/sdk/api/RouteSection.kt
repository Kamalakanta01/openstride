package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class RouteSection(
    val numPolylineEdges: Long,
    val surfaceType: RoadSurfaceType
) {
    override fun toString(): String = "RouteSection(numPolylineEdges=${numPolylineEdges}, surfaceType=${surfaceType})"
}
