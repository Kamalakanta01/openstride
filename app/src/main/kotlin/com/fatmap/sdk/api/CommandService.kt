package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class CommandService internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ CommandService ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_execute(handle: Long, text: String): String
        external fun nativeDestroy(handle: Long)
    }

    fun execute(text: String): String = mProxy.native_execute(handle, text)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
