package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class CameraTarget(
        val lookAtPoint: WorldPoint3,
        val bearing: Float,
        val distance: Float,
        val pitch: Float
) {
    fun getBearing(): Float = bearing
    fun getDistance(): Float = distance
    fun getLookAtPoint(): WorldPoint3 = lookAtPoint
    fun getPitch(): Float = pitch

    override fun toString(): String = "CameraTarget(" + listOf(lookAtPoint=${lookAtPoint}, bearing=${bearing}, distance=${distance}, pitch=${pitch}) + ")"
}
