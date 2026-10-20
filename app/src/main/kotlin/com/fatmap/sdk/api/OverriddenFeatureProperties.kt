package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class OverriddenFeatureProperties internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ OverriddenFeatureProperties ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_addBooleanProperty(handle: Long, text: String, flag: Boolean): Unit
        external fun native_addDoubleProperty(handle: Long, text: String, d: Double): Unit
        external fun native_addIntegerProperty(handle: Long, text: String, l: Long): Unit
        external fun native_addStringProperty(handle: Long, text: String, text1: String): Unit
        external fun create(): OverriddenFeatureProperties
        external fun native_getBooleanProperty(handle: Long, text: String): Boolean?
        external fun native_getDoubleProperty(handle: Long, text: String): Double?
        external fun native_getIntegerProperty(handle: Long, text: String): Long?
        external fun native_getStringProperty(handle: Long, text: String): String
        external fun native_removeProperty(handle: Long, text: String): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun addBooleanProperty(text: String, flag: Boolean): Unit = mProxy.native_addBooleanProperty(handle, text, flag)
    fun addDoubleProperty(text: String, d: Double): Unit = mProxy.native_addDoubleProperty(handle, text, d)
    fun addIntegerProperty(text: String, l: Long): Unit = mProxy.native_addIntegerProperty(handle, text, l)
    fun addStringProperty(text: String, text1: String): Unit = mProxy.native_addStringProperty(handle, text, text1)
    fun getBooleanProperty(text: String): Boolean? = mProxy.native_getBooleanProperty(handle, text)
    fun getDoubleProperty(text: String): Double? = mProxy.native_getDoubleProperty(handle, text)
    fun getIntegerProperty(text: String): Long? = mProxy.native_getIntegerProperty(handle, text)
    fun getStringProperty(text: String): String = mProxy.native_getStringProperty(handle, text)
    fun removeProperty(text: String): Unit = mProxy.native_removeProperty(handle, text)

    companion object {
        fun create(): OverriddenFeatureProperties = CppProxy(0L).create()
        const val STRAVA_BIKE_LANES_DISABLED_PROPERTY: String = "strava_bike_lanes_disabled"
        const val STRAVA_BIKE_MAP_ENABLED_PROPERTY: String = "strava_bike_map_enabled"
        const val STRAVA_HIKE_MAP_ENABLED_PROPERTY: String = "strava_hike_map_enabled"
    }

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
