package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class RecordContent internal constructor(handle: Long) : AutoCloseable {

    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ RecordContent ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {

        external fun native_displayLiveSegments(items: MutableList<CandidateSegment>): Unit
        external fun native_displayRecordingLine(vworldLine2: WorldLine2): Unit
        external fun native_removeAllLiveSegments(): Unit
        external fun native_removeRecordingLine(): Unit
        external fun native_showRecordingLineFinishMarker(flag: Boolean): Unit

        external fun nativeDestroy()
    }

    fun displayLiveSegments(items: MutableList<CandidateSegment>): Unit = mProxy.native_displayLiveSegments(items)
    fun displayRecordingLine(vworldLine2: WorldLine2): Unit = mProxy.native_displayRecordingLine(vworldLine2)
    fun removeAllLiveSegments(): Unit = mProxy.native_removeAllLiveSegments()
    fun removeRecordingLine(): Unit = mProxy.native_removeRecordingLine()
    fun showRecordingLineFinishMarker(flag: Boolean): Unit = mProxy.native_showRecordingLineFinishMarker(flag)

    override fun close() {
        mProxy.nativeDestroy()
    }
}
