package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class DebugApi internal constructor(handle: Long) : AutoCloseable {

    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ DebugApi ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {

        external fun native_dumpEngineState(): String
        external fun native_hideFpsStatsOverlay(): Unit
        external fun native_showFpsStatsOverlay(vdebugOverlayCorner: DebugOverlayCorner, vdebugOverlayPadding: DebugOverlayPadding): Unit

        external fun nativeDestroy()
    }

    fun dumpEngineState(): String = mProxy.native_dumpEngineState()
    fun hideFpsStatsOverlay(): Unit = mProxy.native_hideFpsStatsOverlay()
    fun showFpsStatsOverlay(vdebugOverlayCorner: DebugOverlayCorner, vdebugOverlayPadding: DebugOverlayPadding): Unit = mProxy.native_showFpsStatsOverlay(vdebugOverlayCorner, vdebugOverlayPadding)

    override fun close() {
        mProxy.nativeDestroy()
    }
}
