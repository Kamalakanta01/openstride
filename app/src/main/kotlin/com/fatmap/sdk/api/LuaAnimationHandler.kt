package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class LuaAnimationHandler internal constructor(handle: Long) : AutoCloseable {

    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ LuaAnimationHandler ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {

        external fun native_onError(vluaScriptError: LuaScriptError): Unit
        external fun native_onProgress(f: Float): Unit
        external fun native_shouldAdvanceAnimation(): Boolean

        external fun nativeDestroy()
    }

    fun onError(vluaScriptError: LuaScriptError): Unit = mProxy.native_onError(vluaScriptError)
    fun onProgress(f: Float): Unit = mProxy.native_onProgress(f)
    fun shouldAdvanceAnimation(): Boolean = mProxy.native_shouldAdvanceAnimation()

    override fun close() {
        mProxy.nativeDestroy()
    }
}
