package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class SegmentsFilter internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ SegmentsFilter ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_getVisibleSegments(handle: Long): MutableList<SegmentFeature>
        external fun native_hide(handle: Long): Unit
        external fun native_selectSegment(handle: Long, l: Long): Unit
        external fun native_show(handle: Long, vsegmentsFilterOptions: SegmentsFilterOptions): Unit
        external fun native_unselectSegment(handle: Long): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun getVisibleSegments(): MutableList<SegmentFeature> = mProxy.native_getVisibleSegments(handle)
    fun hide(): Unit = mProxy.native_hide(handle)
    fun selectSegment(l: Long): Unit = mProxy.native_selectSegment(handle, l)
    fun show(vsegmentsFilterOptions: SegmentsFilterOptions): Unit = mProxy.native_show(handle, vsegmentsFilterOptions)
    fun unselectSegment(): Unit = mProxy.native_unselectSegment(handle)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
