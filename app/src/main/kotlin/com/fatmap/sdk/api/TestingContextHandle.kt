package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class TestingContextHandle internal constructor(handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ TestingContextHandle ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_status(): TestingContextStatus
        external fun native_stopEnteringOrInitiateLeaving(): Unit
        external fun nativeDestroy()
    }

    fun status(): TestingContextStatus = mProxy.native_status()
    fun stopEnteringOrInitiateLeaving(): Unit = mProxy.native_stopEnteringOrInitiateLeaving()

    override fun close() {
        mProxy.nativeDestroy()
    }
}
