package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class MREHolder internal constructor(handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ MREHolder ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun create(obj: RenderSurfaceHolder?, obj1: DeviceOrientationService?, text: String, text1: String, obj2: EngineConfiguration?): MREHolder
        external fun native_handleMemoryWarning(): Unit
        external fun native_handleOnAppDidEnterBackground(): Unit
        external fun native_handleOnAppDidEnterForeground(): Unit
        external fun nativeDestroy()
    }

    fun handleMemoryWarning(): Unit = mProxy.native_handleMemoryWarning()
    fun handleOnAppDidEnterBackground(): Unit = mProxy.native_handleOnAppDidEnterBackground()
    fun handleOnAppDidEnterForeground(): Unit = mProxy.native_handleOnAppDidEnterForeground()

    companion object {
        fun create(obj: RenderSurfaceHolder?, obj1: DeviceOrientationService?, text: String, text1: String, obj2: EngineConfiguration?): MREHolder = CppProxy(0L).create(obj, obj1, text, text1, obj2)
    }

    override fun close() {
        mProxy.nativeDestroy()
    }
}
