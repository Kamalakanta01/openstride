package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class OsmFilter internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ OsmFilter ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_setShow(handle: Long, flag: Boolean): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun setShow(flag: Boolean): Unit = mProxy.native_setShow(handle, flag)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
