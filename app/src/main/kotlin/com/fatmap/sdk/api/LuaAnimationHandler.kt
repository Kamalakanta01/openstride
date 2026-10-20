package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class LuaAnimationHandler internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ LuaAnimationHandler ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_onError(handle: Long, vluaScriptError: LuaScriptError): Unit
        external fun native_onProgress(handle: Long, f: Float): Unit
        external fun native_shouldAdvanceAnimation(handle: Long): Boolean
        external fun nativeDestroy(handle: Long)
    }

    fun onError(vluaScriptError: LuaScriptError): Unit = mProxy.native_onError(handle, vluaScriptError)
    fun onProgress(f: Float): Unit = mProxy.native_onProgress(handle, f)
    fun shouldAdvanceAnimation(): Boolean = mProxy.native_shouldAdvanceAnimation(handle)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
