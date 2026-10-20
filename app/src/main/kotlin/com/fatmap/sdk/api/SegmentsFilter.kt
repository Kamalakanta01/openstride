package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class SegmentsFilter internal constructor(handle: Long) : AutoCloseable {

    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ SegmentsFilter ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {

        external fun native_getVisibleSegments(): MutableList<SegmentFeature>
        external fun native_hide(): Unit
        external fun native_selectSegment(l: Long): Unit
        external fun native_show(vsegmentsFilterOptions: SegmentsFilterOptions): Unit
        external fun native_unselectSegment(): Unit

        external fun nativeDestroy()
    }

    fun getVisibleSegments(): MutableList<SegmentFeature> = mProxy.native_getVisibleSegments()
    fun hide(): Unit = mProxy.native_hide()
    fun selectSegment(l: Long): Unit = mProxy.native_selectSegment(l)
    fun show(vsegmentsFilterOptions: SegmentsFilterOptions): Unit = mProxy.native_show(vsegmentsFilterOptions)
    fun unselectSegment(): Unit = mProxy.native_unselectSegment()

    override fun close() {
        mProxy.nativeDestroy()
    }
}
