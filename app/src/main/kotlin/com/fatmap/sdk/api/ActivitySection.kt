package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class ActivitySection(
        val numPolylineEdges: Long,
        val surfaceType: RoadSurfaceType,
        val privateSection: Boolean
) {
    fun getNumPolylineEdges(): Long = numPolylineEdges
    fun getPrivateSection(): Boolean = privateSection
    fun getSurfaceType(): RoadSurfaceType = surfaceType

    override fun toString(): String = "ActivitySection(" + listOf(numPolylineEdges=${numPolylineEdges}, surfaceType=${surfaceType}, privateSection=${privateSection}) + ")"
}
