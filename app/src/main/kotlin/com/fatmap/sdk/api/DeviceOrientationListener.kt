package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class DeviceOrientationListener internal constructor(handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ DeviceOrientationListener ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_onDeviceOrientationChanged(vdeviceOrientation: DeviceOrientation): Unit
        external fun nativeDestroy()
    }

    fun onDeviceOrientationChanged(vdeviceOrientation: DeviceOrientation): Unit = mProxy.native_onDeviceOrientationChanged(vdeviceOrientation)

    override fun close() {
        mProxy.nativeDestroy()
    }
}
