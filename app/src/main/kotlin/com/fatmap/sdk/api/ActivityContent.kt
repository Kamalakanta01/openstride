package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class ActivityContent internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ ActivityContent ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_applyActivityGradient(handle: Long, text: String, items: MutableList<Color>): Unit
        external fun native_applyActivityPolylineHighlights(handle: Long, text: String, items: MutableList<PolylineHighlightSection>): Unit
        external fun native_clearActivityGradient(handle: Long, text: String): Unit
        external fun native_clearActivityPolylineHighlights(handle: Long, text: String): Unit
        external fun native_displayActivity(handle: Long, text: String, vworldLine2: WorldLine2, vintervalInt: IntervalInt): Unit
        external fun native_displayActivity2(handle: Long, text: String, vworldLine2: WorldLine2, items: MutableList<ActivitySection>): Unit
        external fun native_displayActivityTruncated(handle: Long, text: String, vworldLine2: WorldLine2, vintervalInt: IntervalInt): Unit
        external fun native_displayActivityWithPrivacyIcons(handle: Long, text: String, vworldLine2: WorldLine2, vintervalInt: IntervalInt): Unit
        external fun native_removeActivity(handle: Long, text: String): Unit
        external fun native_removeAllActivities(handle: Long): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun applyActivityGradient(text: String, items: MutableList<Color>): Unit = mProxy.native_applyActivityGradient(handle, text, items)
    fun applyActivityPolylineHighlights(text: String, items: MutableList<PolylineHighlightSection>): Unit = mProxy.native_applyActivityPolylineHighlights(handle, text, items)
    fun clearActivityGradient(text: String): Unit = mProxy.native_clearActivityGradient(handle, text)
    fun clearActivityPolylineHighlights(text: String): Unit = mProxy.native_clearActivityPolylineHighlights(handle, text)
    fun displayActivity(text: String, vworldLine2: WorldLine2, vintervalInt: IntervalInt): Unit = mProxy.native_displayActivity(handle, text, vworldLine2, vintervalInt)
    fun displayActivity2(text: String, vworldLine2: WorldLine2, items: MutableList<ActivitySection>): Unit = mProxy.native_displayActivity2(handle, text, vworldLine2, items)
    fun displayActivityTruncated(text: String, vworldLine2: WorldLine2, vintervalInt: IntervalInt): Unit = mProxy.native_displayActivityTruncated(handle, text, vworldLine2, vintervalInt)
    fun displayActivityWithPrivacyIcons(text: String, vworldLine2: WorldLine2, vintervalInt: IntervalInt): Unit = mProxy.native_displayActivityWithPrivacyIcons(handle, text, vworldLine2, vintervalInt)
    fun removeActivity(text: String): Unit = mProxy.native_removeActivity(handle, text)
    fun removeAllActivities(): Unit = mProxy.native_removeAllActivities(handle)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
