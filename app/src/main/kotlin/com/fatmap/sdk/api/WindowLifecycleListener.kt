package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class WindowLifecycleListener internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ WindowLifecycleListener ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_onDisplayChanged(handle: Long, f: Float): Unit
        external fun native_onWindowResized(handle: Long, i: Int, i1: Int): Unit
        external fun native_onWindowStateChanged(handle: Long, vwindowState: WindowState): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun onDisplayChanged(f: Float): Unit = mProxy.native_onDisplayChanged(handle, f)
    fun onWindowResized(i: Int, i1: Int): Unit = mProxy.native_onWindowResized(handle, i, i1)
    fun onWindowStateChanged(vwindowState: WindowState): Unit = mProxy.native_onWindowStateChanged(handle, vwindowState)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
