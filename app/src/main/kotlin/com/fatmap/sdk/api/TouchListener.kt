package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class TouchListener internal constructor(handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ TouchListener ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_onDoubleTap(vosTouch: OsTouch): Unit
        external fun native_onLongPress(vosTouch: OsTouch): Unit
        external fun native_onSingleTap(vosTouch: OsTouch): Unit
        external fun native_onTapAndMove(vosTouch: OsTouch, vosTouchAction: OsTouchAction): Unit
        external fun native_onTouches(items: MutableList<OsTouch>): Unit
        external fun nativeDestroy()
    }

    fun onDoubleTap(vosTouch: OsTouch): Unit = mProxy.native_onDoubleTap(vosTouch)
    fun onLongPress(vosTouch: OsTouch): Unit = mProxy.native_onLongPress(vosTouch)
    fun onSingleTap(vosTouch: OsTouch): Unit = mProxy.native_onSingleTap(vosTouch)
    fun onTapAndMove(vosTouch: OsTouch, vosTouchAction: OsTouchAction): Unit = mProxy.native_onTapAndMove(vosTouch, vosTouchAction)
    fun onTouches(items: MutableList<OsTouch>): Unit = mProxy.native_onTouches(items)

    override fun close() {
        mProxy.nativeDestroy()
    }
}
