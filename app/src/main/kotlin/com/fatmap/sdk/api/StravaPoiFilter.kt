package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class StravaPoiFilter internal constructor(handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ StravaPoiFilter ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_disableBikeShares(): Unit
        external fun native_disableStartPoints(): Unit
        external fun native_enableBikeShares(): Unit
        external fun native_enableStartPoints(vstartPointFilterOptions: StartPointFilterOptions): Unit
        external fun native_selectPoi(obj: ContentSelectionHandle?): Unit
        external fun native_setShow(flag: Boolean): Unit
        external fun native_unselectPoi(): Unit
        external fun nativeDestroy()
    }

    fun disableBikeShares(): Unit = mProxy.native_disableBikeShares()
    fun disableStartPoints(): Unit = mProxy.native_disableStartPoints()
    fun enableBikeShares(): Unit = mProxy.native_enableBikeShares()
    fun enableStartPoints(vstartPointFilterOptions: StartPointFilterOptions): Unit = mProxy.native_enableStartPoints(vstartPointFilterOptions)
    fun selectPoi(obj: ContentSelectionHandle?): Unit = mProxy.native_selectPoi(obj)
    fun setShow(flag: Boolean): Unit = mProxy.native_setShow(flag)
    fun unselectPoi(): Unit = mProxy.native_unselectPoi()

    override fun close() {
        mProxy.nativeDestroy()
    }
}
