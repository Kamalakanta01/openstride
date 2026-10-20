package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class Content internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ Content ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_addCircleMarker(handle: Long, text: String, d: Double, d1: Double, f: Float): Unit
        external fun native_addCustomGradientLine(handle: Long, vworldLine2: WorldLine2, text: String, text1: String, text2: String, items: MutableList<Color>): Long
        external fun native_addCustomLine(handle: Long, vworldLine2: WorldLine2, text: String, text1: String, flag: Boolean, text2: String): Long
        external fun native_addDroppedPin(handle: Long, text: String, vworldPoint2: WorldPoint2): Unit
        external fun native_getActivityContent(handle: Long): ActivityContent
        external fun native_getOsmFilter(handle: Long): OsmFilter
        external fun native_getRecordContent(handle: Long): RecordContent
        external fun native_getRouteContent(handle: Long): RouteContent
        external fun native_getSegmentsFilter(handle: Long): SegmentsFilter
        external fun native_getStravaPoiFilter(handle: Long): StravaPoiFilter
        external fun native_removeAllDroppedPins(handle: Long): Unit
        external fun native_removeCircleMarker(handle: Long, text: String): Unit
        external fun native_removeCustomLine(handle: Long, l: Long): Boolean
        external fun native_removeDroppedPin(handle: Long, text: String): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun addCircleMarker(text: String, d: Double, d1: Double, f: Float): Unit = mProxy.native_addCircleMarker(handle, text, d, d1, f)
    fun addCustomGradientLine(vworldLine2: WorldLine2, text: String, text1: String, text2: String, items: MutableList<Color>): Long = mProxy.native_addCustomGradientLine(handle, vworldLine2, text, text1, text2, items)
    fun addCustomLine(vworldLine2: WorldLine2, text: String, text1: String, flag: Boolean, text2: String): Long = mProxy.native_addCustomLine(handle, vworldLine2, text, text1, flag, text2)
    fun addDroppedPin(text: String, vworldPoint2: WorldPoint2): Unit = mProxy.native_addDroppedPin(handle, text, vworldPoint2)
    fun getActivityContent(): ActivityContent = mProxy.native_getActivityContent(handle)
    fun getOsmFilter(): OsmFilter = mProxy.native_getOsmFilter(handle)
    fun getRecordContent(): RecordContent = mProxy.native_getRecordContent(handle)
    fun getRouteContent(): RouteContent = mProxy.native_getRouteContent(handle)
    fun getSegmentsFilter(): SegmentsFilter = mProxy.native_getSegmentsFilter(handle)
    fun getStravaPoiFilter(): StravaPoiFilter = mProxy.native_getStravaPoiFilter(handle)
    fun removeAllDroppedPins(): Unit = mProxy.native_removeAllDroppedPins(handle)
    fun removeCircleMarker(text: String): Unit = mProxy.native_removeCircleMarker(handle, text)
    fun removeCustomLine(l: Long): Boolean = mProxy.native_removeCustomLine(handle, l)
    fun removeDroppedPin(text: String): Unit = mProxy.native_removeDroppedPin(handle, text)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
