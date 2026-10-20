package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class Utils internal constructor(handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ Utils ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_addCustomMarker2(d: Double, d1: Double, text: String, text1: String, text2: String): Long
        external fun native_addCustomMarker3(d: Double, d1: Double, text: String, text1: String, text2: String): Long
        external fun native_getUserMarkerLocationAngle(): Float
        external fun native_hideTileGrid(): Unit
        external fun native_isUserMarkerOnScreen(): Boolean
        external fun native_removeCustomMarker(l: Long): Unit
        external fun native_removeUserMarker(): Unit
        external fun native_setHighlightedTiles(items: MutableList<HighlightedTile>): Unit
        external fun native_setUserMarker(d: Double, d1: Double): Unit
        external fun native_setUserMarkerAnimation(f: Float): Unit
        external fun native_setUserMarkerDirection(f: Float): Unit
        external fun native_setUserMarkerPrecision(f: Float): Unit
        external fun native_setUserMarkerState(flag: Boolean): Unit
        external fun native_showTileGrid(a: Byte, items: MutableList<HighlightedTile>): Unit
        external fun native_updateCustomMarkerPosition(l: Long, d: Double, d1: Double): Unit
        external fun nativeDestroy()
    }

    fun addCustomMarker2(d: Double, d1: Double, text: String, text1: String, text2: String): Long = mProxy.native_addCustomMarker2(d, d1, text, text1, text2)
    fun addCustomMarker3(d: Double, d1: Double, text: String, text1: String, text2: String): Long = mProxy.native_addCustomMarker3(d, d1, text, text1, text2)
    fun getUserMarkerLocationAngle(): Float = mProxy.native_getUserMarkerLocationAngle()
    fun hideTileGrid(): Unit = mProxy.native_hideTileGrid()
    fun isUserMarkerOnScreen(): Boolean = mProxy.native_isUserMarkerOnScreen()
    fun removeCustomMarker(l: Long): Unit = mProxy.native_removeCustomMarker(l)
    fun removeUserMarker(): Unit = mProxy.native_removeUserMarker()
    fun setHighlightedTiles(items: MutableList<HighlightedTile>): Unit = mProxy.native_setHighlightedTiles(items)
    fun setUserMarker(d: Double, d1: Double): Unit = mProxy.native_setUserMarker(d, d1)
    fun setUserMarkerAnimation(f: Float): Unit = mProxy.native_setUserMarkerAnimation(f)
    fun setUserMarkerDirection(f: Float): Unit = mProxy.native_setUserMarkerDirection(f)
    fun setUserMarkerPrecision(f: Float): Unit = mProxy.native_setUserMarkerPrecision(f)
    fun setUserMarkerState(flag: Boolean): Unit = mProxy.native_setUserMarkerState(flag)
    fun showTileGrid(a: Byte, items: MutableList<HighlightedTile>): Unit = mProxy.native_showTileGrid(a, items)
    fun updateCustomMarkerPosition(l: Long, d: Double, d1: Double): Unit = mProxy.native_updateCustomMarkerPosition(l, d, d1)

    override fun close() {
        mProxy.nativeDestroy()
    }
}
