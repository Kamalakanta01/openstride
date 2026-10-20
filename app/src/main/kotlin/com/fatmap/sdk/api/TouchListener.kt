package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class TouchListener internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ TouchListener ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_onDoubleTap(handle: Long, vosTouch: OsTouch): Unit
        external fun native_onLongPress(handle: Long, vosTouch: OsTouch): Unit
        external fun native_onSingleTap(handle: Long, vosTouch: OsTouch): Unit
        external fun native_onTapAndMove(handle: Long, vosTouch: OsTouch, vosTouchAction: OsTouchAction): Unit
        external fun native_onTouches(handle: Long, items: MutableList<OsTouch>): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun onDoubleTap(vosTouch: OsTouch): Unit = mProxy.native_onDoubleTap(handle, vosTouch)
    fun onLongPress(vosTouch: OsTouch): Unit = mProxy.native_onLongPress(handle, vosTouch)
    fun onSingleTap(vosTouch: OsTouch): Unit = mProxy.native_onSingleTap(handle, vosTouch)
    fun onTapAndMove(vosTouch: OsTouch, vosTouchAction: OsTouchAction): Unit = mProxy.native_onTapAndMove(handle, vosTouch, vosTouchAction)
    fun onTouches(items: MutableList<OsTouch>): Unit = mProxy.native_onTouches(handle, items)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
