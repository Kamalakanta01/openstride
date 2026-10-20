package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class RouteSection(
        val numPolylineEdges: Long,
        val surfaceType: RoadSurfaceType
) {
    fun getNumPolylineEdges(): Long = numPolylineEdges
    fun getSurfaceType(): RoadSurfaceType = surfaceType

    override fun toString(): String = "RouteSection(" + listOf(numPolylineEdges=${numPolylineEdges}, surfaceType=${surfaceType}) + ")"
}
