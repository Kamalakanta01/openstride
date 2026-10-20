package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class TestingContextEventListener internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ TestingContextEventListener ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_onEntered(handle: Long): Unit
        external fun native_onErrorEntering(handle: Long, text: String): Unit
        external fun native_onLeft(handle: Long): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun onEntered(): Unit = mProxy.native_onEntered(handle)
    fun onErrorEntering(text: String): Unit = mProxy.native_onErrorEntering(handle, text)
    fun onLeft(): Unit = mProxy.native_onLeft(handle)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
