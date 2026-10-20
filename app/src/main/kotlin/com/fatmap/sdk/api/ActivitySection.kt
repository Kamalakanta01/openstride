package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class ActivitySection(
    val numPolylineEdges: Long,
    val surfaceType: RoadSurfaceType,
    val privateSection: Boolean
) {
    override fun toString(): String = "ActivitySection(numPolylineEdges=${numPolylineEdges}, surfaceType=${surfaceType}, privateSection=${privateSection})"
}
