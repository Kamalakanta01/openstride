package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class WorldPoint3(
        val latitude: Double,
        val longitude: Double,
        val elevation: Float
) {
    fun getElevation(): Float = elevation
    fun getLatitude(): Double = latitude
    fun getLongitude(): Double = longitude

    override fun toString(): String = "WorldPoint3(" + listOf(latitude=${latitude}, longitude=${longitude}, elevation=${elevation}) + ")"
}
