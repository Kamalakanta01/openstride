package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class CameraFlyThroughControls internal constructor(handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ CameraFlyThroughControls ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_getDurationSeconds(): Float
        external fun native_getTrackProgress(): Float
        external fun native_getTrackSpeedMultiplier(): Float
        external fun native_isActive(): Boolean
        external fun native_isCameraDetached(): Boolean
        external fun native_isRefocusPossible(): Boolean
        external fun native_refocus(): Unit
        external fun native_seekToAnimationStart(): Unit
        external fun native_setAnimationSpeedMultiplier(f: Float): Unit
        external fun native_setCameraDistanceMultiplier(f: Float): Unit
        external fun native_setTrackProgress(f: Float): Unit
        external fun native_setTrackSpeedMultiplier(f: Float): Unit
        external fun native_stop(): Unit
        external fun nativeDestroy()
    }

    fun getDurationSeconds(): Float = mProxy.native_getDurationSeconds()
    fun getTrackProgress(): Float = mProxy.native_getTrackProgress()
    fun getTrackSpeedMultiplier(): Float = mProxy.native_getTrackSpeedMultiplier()
    fun isActive(): Boolean = mProxy.native_isActive()
    fun isCameraDetached(): Boolean = mProxy.native_isCameraDetached()
    fun isRefocusPossible(): Boolean = mProxy.native_isRefocusPossible()
    fun refocus(): Unit = mProxy.native_refocus()
    fun seekToAnimationStart(): Unit = mProxy.native_seekToAnimationStart()
    fun setAnimationSpeedMultiplier(f: Float): Unit = mProxy.native_setAnimationSpeedMultiplier(f)
    fun setCameraDistanceMultiplier(f: Float): Unit = mProxy.native_setCameraDistanceMultiplier(f)
    fun setTrackProgress(f: Float): Unit = mProxy.native_setTrackProgress(f)
    fun setTrackSpeedMultiplier(f: Float): Unit = mProxy.native_setTrackSpeedMultiplier(f)
    fun stop(): Unit = mProxy.native_stop()

    override fun close() {
        mProxy.nativeDestroy()
    }
}
