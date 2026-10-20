package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class PointScreenPosition(
    val x: Float,
    val y: Float,
    @get:JvmName("getIsOccluded") val isOccluded: Boolean
) {
    override fun toString(): String = "PointScreenPosition(x=${x}, y=${y}, isOccluded=${isOccluded})"
}
