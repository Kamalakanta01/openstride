package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class WorldPoint2(
        val latitude: Double,
        val longitude: Double
) {
    fun getLatitude(): Double = latitude
    fun getLongitude(): Double = longitude

    override fun toString(): String = "WorldPoint2(" + listOf(latitude=${latitude}, longitude=${longitude}) + ")"
}
