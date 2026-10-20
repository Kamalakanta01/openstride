package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
interface CameraStateListener {
    fun onTargetChanged(vcameraTarget: CameraTarget): Unit
    fun onViewTypeChanged(vcameraViewType: CameraViewType): Unit
    fun onZoomLevelChanged(f: Float): Unit
    fun stateToObserve(): java.util.EnumSet<CameraStateFlags>
}
