package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class PointScreenPosition(
        val x: Float,
        val y: Float,
        val isOccluded: Boolean
) {
    fun getIsOccluded(): Boolean = isOccluded
    fun getX(): Float = x
    fun getY(): Float = y

    override fun toString(): String = "PointScreenPosition(" + listOf(x=${x}, y=${y}, isOccluded=${isOccluded}) + ")"
}
