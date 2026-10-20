package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class Utils internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ Utils ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_addCustomMarker2(handle: Long, d: Double, d1: Double, text: String, text1: String, text2: String): Long
        external fun native_addCustomMarker3(handle: Long, d: Double, d1: Double, text: String, text1: String, text2: String): Long
        external fun native_getUserMarkerLocationAngle(handle: Long): Float
        external fun native_hideTileGrid(handle: Long): Unit
        external fun native_isUserMarkerOnScreen(handle: Long): Boolean
        external fun native_removeCustomMarker(handle: Long, l: Long): Unit
        external fun native_removeUserMarker(handle: Long): Unit
        external fun native_setHighlightedTiles(handle: Long, items: MutableList<HighlightedTile>): Unit
        external fun native_setUserMarker(handle: Long, d: Double, d1: Double): Unit
        external fun native_setUserMarkerAnimation(handle: Long, f: Float): Unit
        external fun native_setUserMarkerDirection(handle: Long, f: Float): Unit
        external fun native_setUserMarkerPrecision(handle: Long, f: Float): Unit
        external fun native_setUserMarkerState(handle: Long, flag: Boolean): Unit
        external fun native_showTileGrid(handle: Long, a: Byte, items: MutableList<HighlightedTile>): Unit
        external fun native_updateCustomMarkerPosition(handle: Long, l: Long, d: Double, d1: Double): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun addCustomMarker2(d: Double, d1: Double, text: String, text1: String, text2: String): Long = mProxy.native_addCustomMarker2(handle, d, d1, text, text1, text2)
    fun addCustomMarker3(d: Double, d1: Double, text: String, text1: String, text2: String): Long = mProxy.native_addCustomMarker3(handle, d, d1, text, text1, text2)
    fun getUserMarkerLocationAngle(): Float = mProxy.native_getUserMarkerLocationAngle(handle)
    fun hideTileGrid(): Unit = mProxy.native_hideTileGrid(handle)
    fun isUserMarkerOnScreen(): Boolean = mProxy.native_isUserMarkerOnScreen(handle)
    fun removeCustomMarker(l: Long): Unit = mProxy.native_removeCustomMarker(handle, l)
    fun removeUserMarker(): Unit = mProxy.native_removeUserMarker(handle)
    fun setHighlightedTiles(items: MutableList<HighlightedTile>): Unit = mProxy.native_setHighlightedTiles(handle, items)
    fun setUserMarker(d: Double, d1: Double): Unit = mProxy.native_setUserMarker(handle, d, d1)
    fun setUserMarkerAnimation(f: Float): Unit = mProxy.native_setUserMarkerAnimation(handle, f)
    fun setUserMarkerDirection(f: Float): Unit = mProxy.native_setUserMarkerDirection(handle, f)
    fun setUserMarkerPrecision(f: Float): Unit = mProxy.native_setUserMarkerPrecision(handle, f)
    fun setUserMarkerState(flag: Boolean): Unit = mProxy.native_setUserMarkerState(handle, flag)
    fun showTileGrid(a: Byte, items: MutableList<HighlightedTile>): Unit = mProxy.native_showTileGrid(handle, a, items)
    fun updateCustomMarkerPosition(l: Long, d: Double, d1: Double): Unit = mProxy.native_updateCustomMarkerPosition(handle, l, d, d1)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
