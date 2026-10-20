package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class OverriddenFeatureProperties internal constructor(handle: Long) : AutoCloseable {

    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ OverriddenFeatureProperties ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {

        external fun native_addBooleanProperty(text: String, flag: Boolean): Unit
        external fun native_addDoubleProperty(text: String, d: Double): Unit
        external fun native_addIntegerProperty(text: String, l: Long): Unit
        external fun native_addStringProperty(text: String, text1: String): Unit
        external fun create(): OverriddenFeatureProperties
        external fun native_getBooleanProperty(text: String): Boolean?
        external fun native_getDoubleProperty(text: String): Double?
        external fun native_getIntegerProperty(text: String): Long?
        external fun native_getStringProperty(text: String): String
        external fun native_removeProperty(text: String): Unit

        external fun nativeDestroy()
    }

    fun addBooleanProperty(text: String, flag: Boolean): Unit = mProxy.native_addBooleanProperty(text, flag)
    fun addDoubleProperty(text: String, d: Double): Unit = mProxy.native_addDoubleProperty(text, d)
    fun addIntegerProperty(text: String, l: Long): Unit = mProxy.native_addIntegerProperty(text, l)
    fun addStringProperty(text: String, text1: String): Unit = mProxy.native_addStringProperty(text, text1)
    fun create(): OverriddenFeatureProperties = mProxy.create()
    fun getBooleanProperty(text: String): Boolean? = mProxy.native_getBooleanProperty(text)
    fun getDoubleProperty(text: String): Double? = mProxy.native_getDoubleProperty(text)
    fun getIntegerProperty(text: String): Long? = mProxy.native_getIntegerProperty(text)
    fun getStringProperty(text: String): String = mProxy.native_getStringProperty(text)
    fun removeProperty(text: String): Unit = mProxy.native_removeProperty(text)

    override fun close() {
        mProxy.nativeDestroy()
    }
}
