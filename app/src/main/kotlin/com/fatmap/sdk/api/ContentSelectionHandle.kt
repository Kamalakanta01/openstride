package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class ContentSelectionHandle internal constructor(handle: Long) : AutoCloseable {

    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ ContentSelectionHandle ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {



        external fun nativeDestroy()
    }



    override fun close() {
        mProxy.nativeDestroy()
    }
}
