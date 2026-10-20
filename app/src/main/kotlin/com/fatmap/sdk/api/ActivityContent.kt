package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class ActivityContent internal constructor(handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ ActivityContent ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_applyActivityGradient(text: String, items: MutableList<Color>): Unit
        external fun native_applyActivityPolylineHighlights(text: String, items: MutableList<PolylineHighlightSection>): Unit
        external fun native_clearActivityGradient(text: String): Unit
        external fun native_clearActivityPolylineHighlights(text: String): Unit
        external fun native_displayActivity(text: String, vworldLine2: WorldLine2, vintervalInt: IntervalInt): Unit
        external fun native_displayActivity2(text: String, vworldLine2: WorldLine2, items: MutableList<ActivitySection>): Unit
        external fun native_displayActivityTruncated(text: String, vworldLine2: WorldLine2, vintervalInt: IntervalInt): Unit
        external fun native_displayActivityWithPrivacyIcons(text: String, vworldLine2: WorldLine2, vintervalInt: IntervalInt): Unit
        external fun native_removeActivity(text: String): Unit
        external fun native_removeAllActivities(): Unit
        external fun nativeDestroy()
    }

    fun applyActivityGradient(text: String, items: MutableList<Color>): Unit = mProxy.native_applyActivityGradient(text, items)
    fun applyActivityPolylineHighlights(text: String, items: MutableList<PolylineHighlightSection>): Unit = mProxy.native_applyActivityPolylineHighlights(text, items)
    fun clearActivityGradient(text: String): Unit = mProxy.native_clearActivityGradient(text)
    fun clearActivityPolylineHighlights(text: String): Unit = mProxy.native_clearActivityPolylineHighlights(text)
    fun displayActivity(text: String, vworldLine2: WorldLine2, vintervalInt: IntervalInt): Unit = mProxy.native_displayActivity(text, vworldLine2, vintervalInt)
    fun displayActivity2(text: String, vworldLine2: WorldLine2, items: MutableList<ActivitySection>): Unit = mProxy.native_displayActivity2(text, vworldLine2, items)
    fun displayActivityTruncated(text: String, vworldLine2: WorldLine2, vintervalInt: IntervalInt): Unit = mProxy.native_displayActivityTruncated(text, vworldLine2, vintervalInt)
    fun displayActivityWithPrivacyIcons(text: String, vworldLine2: WorldLine2, vintervalInt: IntervalInt): Unit = mProxy.native_displayActivityWithPrivacyIcons(text, vworldLine2, vintervalInt)
    fun removeActivity(text: String): Unit = mProxy.native_removeActivity(text)
    fun removeAllActivities(): Unit = mProxy.native_removeAllActivities()

    override fun close() {
        mProxy.nativeDestroy()
    }
}
