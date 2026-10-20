package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class CameraFlyThroughControls internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ CameraFlyThroughControls ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_getDurationSeconds(handle: Long): Float
        external fun native_getTrackProgress(handle: Long): Float
        external fun native_getTrackSpeedMultiplier(handle: Long): Float
        external fun native_isActive(handle: Long): Boolean
        external fun native_isCameraDetached(handle: Long): Boolean
        external fun native_isRefocusPossible(handle: Long): Boolean
        external fun native_refocus(handle: Long): Unit
        external fun native_seekToAnimationStart(handle: Long): Unit
        external fun native_setAnimationSpeedMultiplier(handle: Long, f: Float): Unit
        external fun native_setCameraDistanceMultiplier(handle: Long, f: Float): Unit
        external fun native_setTrackProgress(handle: Long, f: Float): Unit
        external fun native_setTrackSpeedMultiplier(handle: Long, f: Float): Unit
        external fun native_stop(handle: Long): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun getDurationSeconds(): Float = mProxy.native_getDurationSeconds(handle)
    fun getTrackProgress(): Float = mProxy.native_getTrackProgress(handle)
    fun getTrackSpeedMultiplier(): Float = mProxy.native_getTrackSpeedMultiplier(handle)
    fun isActive(): Boolean = mProxy.native_isActive(handle)
    fun isCameraDetached(): Boolean = mProxy.native_isCameraDetached(handle)
    fun isRefocusPossible(): Boolean = mProxy.native_isRefocusPossible(handle)
    fun refocus(): Unit = mProxy.native_refocus(handle)
    fun seekToAnimationStart(): Unit = mProxy.native_seekToAnimationStart(handle)
    fun setAnimationSpeedMultiplier(f: Float): Unit = mProxy.native_setAnimationSpeedMultiplier(handle, f)
    fun setCameraDistanceMultiplier(f: Float): Unit = mProxy.native_setCameraDistanceMultiplier(handle, f)
    fun setTrackProgress(f: Float): Unit = mProxy.native_setTrackProgress(handle, f)
    fun setTrackSpeedMultiplier(f: Float): Unit = mProxy.native_setTrackSpeedMultiplier(handle, f)
    fun stop(): Unit = mProxy.native_stop(handle)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
