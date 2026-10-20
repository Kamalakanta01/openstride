package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class RecordContent internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ RecordContent ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_displayLiveSegments(handle: Long, items: MutableList<CandidateSegment>): Unit
        external fun native_displayRecordingLine(handle: Long, vworldLine2: WorldLine2): Unit
        external fun native_removeAllLiveSegments(handle: Long): Unit
        external fun native_removeRecordingLine(handle: Long): Unit
        external fun native_showRecordingLineFinishMarker(handle: Long, flag: Boolean): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun displayLiveSegments(items: MutableList<CandidateSegment>): Unit = mProxy.native_displayLiveSegments(handle, items)
    fun displayRecordingLine(vworldLine2: WorldLine2): Unit = mProxy.native_displayRecordingLine(handle, vworldLine2)
    fun removeAllLiveSegments(): Unit = mProxy.native_removeAllLiveSegments(handle)
    fun removeRecordingLine(): Unit = mProxy.native_removeRecordingLine(handle)
    fun showRecordingLineFinishMarker(flag: Boolean): Unit = mProxy.native_showRecordingLineFinishMarker(handle, flag)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
