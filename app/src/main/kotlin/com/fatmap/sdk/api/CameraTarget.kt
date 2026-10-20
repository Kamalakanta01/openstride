package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class CameraTarget(
    val lookAtPoint: WorldPoint3,
    val bearing: Float,
    val distance: Float,
    val pitch: Float
) {
    override fun toString(): String = "CameraTarget(lookAtPoint=${lookAtPoint}, bearing=${bearing}, distance=${distance}, pitch=${pitch})"
}
