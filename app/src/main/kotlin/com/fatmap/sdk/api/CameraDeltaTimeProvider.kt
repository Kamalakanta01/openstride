package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class CameraDeltaTimeProvider internal constructor(handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ CameraDeltaTimeProvider ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_getDeltaTime(): Float
        external fun nativeDestroy()
    }

    fun getDeltaTime(): Float = mProxy.native_getDeltaTime()

    override fun close() {
        mProxy.nativeDestroy()
    }
}
