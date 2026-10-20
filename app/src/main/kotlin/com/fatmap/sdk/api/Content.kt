package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class Content internal constructor(handle: Long) : AutoCloseable {

    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ Content ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {

        external fun native_addCircleMarker(text: String, d: Double, d1: Double, f: Float): Unit
        external fun native_addCustomGradientLine(vworldLine2: WorldLine2, text: String, text1: String, text2: String, items: MutableList<Color>): Long
        external fun native_addCustomLine(vworldLine2: WorldLine2, text: String, text1: String, flag: Boolean, text2: String): Long
        external fun native_addDroppedPin(text: String, vworldPoint2: WorldPoint2): Unit
        external fun native_getActivityContent(): ActivityContent
        external fun native_getOsmFilter(): OsmFilter
        external fun native_getRecordContent(): RecordContent
        external fun native_getRouteContent(): RouteContent
        external fun native_getSegmentsFilter(): SegmentsFilter
        external fun native_getStravaPoiFilter(): StravaPoiFilter
        external fun native_removeAllDroppedPins(): Unit
        external fun native_removeCircleMarker(text: String): Unit
        external fun native_removeCustomLine(l: Long): Boolean
        external fun native_removeDroppedPin(text: String): Unit

        external fun nativeDestroy()
    }

    fun addCircleMarker(text: String, d: Double, d1: Double, f: Float): Unit = mProxy.native_addCircleMarker(text, d, d1, f)
    fun addCustomGradientLine(vworldLine2: WorldLine2, text: String, text1: String, text2: String, items: MutableList<Color>): Long = mProxy.native_addCustomGradientLine(vworldLine2, text, text1, text2, items)
    fun addCustomLine(vworldLine2: WorldLine2, text: String, text1: String, flag: Boolean, text2: String): Long = mProxy.native_addCustomLine(vworldLine2, text, text1, flag, text2)
    fun addDroppedPin(text: String, vworldPoint2: WorldPoint2): Unit = mProxy.native_addDroppedPin(text, vworldPoint2)
    fun getActivityContent(): ActivityContent = mProxy.native_getActivityContent()
    fun getOsmFilter(): OsmFilter = mProxy.native_getOsmFilter()
    fun getRecordContent(): RecordContent = mProxy.native_getRecordContent()
    fun getRouteContent(): RouteContent = mProxy.native_getRouteContent()
    fun getSegmentsFilter(): SegmentsFilter = mProxy.native_getSegmentsFilter()
    fun getStravaPoiFilter(): StravaPoiFilter = mProxy.native_getStravaPoiFilter()
    fun removeAllDroppedPins(): Unit = mProxy.native_removeAllDroppedPins()
    fun removeCircleMarker(text: String): Unit = mProxy.native_removeCircleMarker(text)
    fun removeCustomLine(l: Long): Boolean = mProxy.native_removeCustomLine(l)
    fun removeDroppedPin(text: String): Unit = mProxy.native_removeDroppedPin(text)

    override fun close() {
        mProxy.nativeDestroy()
    }
}
