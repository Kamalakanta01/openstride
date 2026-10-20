package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class DebugApi internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ DebugApi ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_dumpEngineState(handle: Long): String
        external fun native_hideFpsStatsOverlay(handle: Long): Unit
        external fun native_showFpsStatsOverlay(handle: Long, vdebugOverlayCorner: DebugOverlayCorner, vdebugOverlayPadding: DebugOverlayPadding): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun dumpEngineState(): String = mProxy.native_dumpEngineState(handle)
    fun hideFpsStatsOverlay(): Unit = mProxy.native_hideFpsStatsOverlay(handle)
    fun showFpsStatsOverlay(vdebugOverlayCorner: DebugOverlayCorner, vdebugOverlayPadding: DebugOverlayPadding): Unit = mProxy.native_showFpsStatsOverlay(handle, vdebugOverlayCorner, vdebugOverlayPadding)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
