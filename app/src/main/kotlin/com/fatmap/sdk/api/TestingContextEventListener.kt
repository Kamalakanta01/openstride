package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class TestingContextEventListener internal constructor(handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ TestingContextEventListener ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_onEntered(): Unit
        external fun native_onErrorEntering(text: String): Unit
        external fun native_onLeft(): Unit
        external fun nativeDestroy()
    }

    fun onEntered(): Unit = mProxy.native_onEntered()
    fun onErrorEntering(text: String): Unit = mProxy.native_onErrorEntering(text)
    fun onLeft(): Unit = mProxy.native_onLeft()

    override fun close() {
        mProxy.nativeDestroy()
    }
}
