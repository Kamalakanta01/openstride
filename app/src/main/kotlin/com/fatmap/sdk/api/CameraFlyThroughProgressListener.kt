package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
interface CameraFlyThroughProgressListener {
    fun onFlyThroughProgress(f: Float, f1: Float, l: Long, f2: Float): Unit
    fun onInterrupted(): Unit
}
