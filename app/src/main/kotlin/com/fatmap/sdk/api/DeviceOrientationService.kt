package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
interface DeviceOrientationService {
    fun setDeviceOrientationListener(obj: DeviceOrientationListener?): Unit
    fun start(): Unit
    fun stop(): Unit
}
