package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class VisibleAreaSamplesSink internal constructor(handle: Long) : AutoCloseable {

    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ VisibleAreaSamplesSink ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {

        external fun native_processVisiblePoint(vworldPoint2: WorldPoint2): Unit

        external fun nativeDestroy()
    }

    fun processVisiblePoint(vworldPoint2: WorldPoint2): Unit = mProxy.native_processVisiblePoint(vworldPoint2)

    override fun close() {
        mProxy.nativeDestroy()
    }
}
