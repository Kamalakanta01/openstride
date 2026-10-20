package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class CameraTargetPlus(
        val cameraTarget: CameraTarget,
        val avoidMountains: Boolean
) {
    fun getAvoidMountains(): Boolean = avoidMountains
    fun getCameraTarget(): CameraTarget = cameraTarget

    override fun toString(): String = "CameraTargetPlus(" + listOf(cameraTarget=${cameraTarget}, avoidMountains=${avoidMountains}) + ")"
}
