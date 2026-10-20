package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class CameraDeltaTimeProvider internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ CameraDeltaTimeProvider ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_getDeltaTime(handle: Long): Float
        external fun nativeDestroy(handle: Long)
    }

    fun getDeltaTime(): Float = mProxy.native_getDeltaTime(handle)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
