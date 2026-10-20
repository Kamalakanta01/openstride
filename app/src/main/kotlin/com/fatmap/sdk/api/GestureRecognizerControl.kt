package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class GestureRecognizerControl internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ GestureRecognizerControl ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_setAllGesturesEnabled(handle: Long, flag: Boolean): Unit
        external fun native_setDoubleTapGesturesEnabled(handle: Long, flag: Boolean): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun setAllGesturesEnabled(flag: Boolean): Unit = mProxy.native_setAllGesturesEnabled(handle, flag)
    fun setDoubleTapGesturesEnabled(flag: Boolean): Unit = mProxy.native_setDoubleTapGesturesEnabled(handle, flag)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
