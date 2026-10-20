package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class StravaPoiFilter internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ StravaPoiFilter ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_disableBikeShares(handle: Long): Unit
        external fun native_disableStartPoints(handle: Long): Unit
        external fun native_enableBikeShares(handle: Long): Unit
        external fun native_enableStartPoints(handle: Long, vstartPointFilterOptions: StartPointFilterOptions): Unit
        external fun native_selectPoi(handle: Long, obj: ContentSelectionHandle?): Unit
        external fun native_setShow(handle: Long, flag: Boolean): Unit
        external fun native_unselectPoi(handle: Long): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun disableBikeShares(): Unit = mProxy.native_disableBikeShares(handle)
    fun disableStartPoints(): Unit = mProxy.native_disableStartPoints(handle)
    fun enableBikeShares(): Unit = mProxy.native_enableBikeShares(handle)
    fun enableStartPoints(vstartPointFilterOptions: StartPointFilterOptions): Unit = mProxy.native_enableStartPoints(handle, vstartPointFilterOptions)
    fun selectPoi(obj: ContentSelectionHandle?): Unit = mProxy.native_selectPoi(handle, obj)
    fun setShow(flag: Boolean): Unit = mProxy.native_setShow(handle, flag)
    fun unselectPoi(): Unit = mProxy.native_unselectPoi(handle)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
