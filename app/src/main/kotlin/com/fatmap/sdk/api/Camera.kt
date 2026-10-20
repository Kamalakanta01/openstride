package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class Camera internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ Camera ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_addInteractionListener(handle: Long, obj: CameraInteractionListener?): Unit
        external fun native_addStateListener(handle: Long, obj: CameraStateListener?): Unit
        external fun native_enterFirstPersonView(handle: Long, d: Double, d1: Double, f: Float): Unit
        external fun native_flyThrough(handle: Long, items: MutableList<WorldPoint3>, text: String, text1: String, vcameraMargins: CameraMargins, obj: CameraFlyThroughProgressListener?, vcameraTargetPlus: CameraTargetPlus, items1: MutableList<Float?>): Unit
        external fun native_flyThroughInteractive(handle: Long, items: MutableList<WorldPoint3>, text: String, text1: String, vcameraMargins: CameraMargins, obj: CameraFlyThroughProgressListener?, vcameraTargetPlus: CameraTargetPlus, flag: Boolean, obj1: CameraDeltaTimeProvider?, items1: MutableList<Float?>): CameraFlyThroughControls
        external fun native_flyTo(handle: Long, d: Double, d1: Double, f: Float, opt: Float?, opt1: Float?, f1: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener?): Unit
        external fun native_flyToBounds(handle: Long, vworldBounds2: WorldBounds2, opt: Float?, opt1: Float?, f: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener?): Unit
        external fun native_flyToTarget(handle: Long, vcameraTarget: CameraTarget, f: Float, flag: Boolean, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener?): Unit
        external fun native_flyToWithZoomLevel(handle: Long, d: Double, d1: Double, f: Float, opt: Float?, opt1: Float?, f1: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener?): Unit
        external fun native_flyover(handle: Long, flag: Boolean, items: MutableList<WorldPoint3>, vcameraMargins: CameraMargins, obj: CameraFlyThroughProgressListener?, vcameraTargetPlus: CameraTargetPlus, obj1: CameraDeltaTimeProvider?, items1: MutableList<Float?>): CameraFlyThroughControls
        external fun native_followUserLocation(handle: Long, opt: Float?, opt1: Float?, opt2: Float?, vfollowMode: FollowMode, vcameraMargins: CameraMargins): Unit
        external fun native_followUserLocationWithZoomLevel(handle: Long, vworldPoint2: WorldPoint2, opt: Float?, opt1: Float?, opt2: Float?, vfollowMode: FollowMode, vcameraMargins: CameraMargins): Unit
        external fun native_getCameraViewType(handle: Long): CameraViewType
        external fun native_getCustomLookAtPoint(handle: Long, f: Float, f1: Float): LookAtPoint
        external fun native_getLookAtPoint(handle: Long): LookAtPoint
        external fun native_getScaleMetersPerPixel(handle: Long): Float
        external fun native_getScreenPosition(handle: Long, vworldPoint2: WorldPoint2): PointScreenPosition
        external fun native_getTarget(handle: Long): CameraTarget
        external fun native_getVisibleAreaBounds(handle: Long, vcameraMargins: CameraMargins, f: Float): VisibleAreaBounds
        external fun native_isWorldPointVisible(handle: Long, vworldPoint3: WorldPoint3, flag: Boolean): Boolean
        external fun native_removeInteractionListener(handle: Long, obj: CameraInteractionListener?): Unit
        external fun native_removeStateListener(handle: Long, obj: CameraStateListener?): Unit
        external fun native_setBearing(handle: Long, f: Float, f1: Float, vcameraMargins: CameraMargins): Unit
        external fun native_setCameraViewType(handle: Long, vcameraViewType: CameraViewType): Unit
        external fun native_setDynamicExaggerationEnabled(handle: Long, flag: Boolean): Unit
        external fun native_setDynamicExaggerationMultiplier(handle: Long, f: Float): Unit
        external fun native_setRotationSpeed(handle: Long, f: Float, f1: Float): Unit
        external fun native_setTranslationSpeed(handle: Long, f: Float, f1: Float): Unit
        external fun native_setZoomSpeed(handle: Long, f: Float): Unit
        external fun native_stop(handle: Long): Unit
        external fun native_translateTo(handle: Long, vworldPoint2: WorldPoint2, f: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener?): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun addInteractionListener(obj: CameraInteractionListener?): Unit = mProxy.native_addInteractionListener(handle, obj)
    fun addStateListener(obj: CameraStateListener?): Unit = mProxy.native_addStateListener(handle, obj)
    fun enterFirstPersonView(d: Double, d1: Double, f: Float): Unit = mProxy.native_enterFirstPersonView(handle, d, d1, f)
    fun flyThrough(items: MutableList<WorldPoint3>, text: String, text1: String, vcameraMargins: CameraMargins, obj: CameraFlyThroughProgressListener?, vcameraTargetPlus: CameraTargetPlus, items1: MutableList<Float?>): Unit = mProxy.native_flyThrough(handle, items, text, text1, vcameraMargins, obj, vcameraTargetPlus, items1)
    fun flyThroughInteractive(items: MutableList<WorldPoint3>, text: String, text1: String, vcameraMargins: CameraMargins, obj: CameraFlyThroughProgressListener?, vcameraTargetPlus: CameraTargetPlus, flag: Boolean, obj1: CameraDeltaTimeProvider?, items1: MutableList<Float?>): CameraFlyThroughControls = mProxy.native_flyThroughInteractive(handle, items, text, text1, vcameraMargins, obj, vcameraTargetPlus, flag, obj1, items1)
    fun flyTo(d: Double, d1: Double, f: Float, opt: Float?, opt1: Float?, f1: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener?): Unit = mProxy.native_flyTo(handle, d, d1, f, opt, opt1, f1, vcameraMargins, obj)
    fun flyToBounds(vworldBounds2: WorldBounds2, opt: Float?, opt1: Float?, f: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener?): Unit = mProxy.native_flyToBounds(handle, vworldBounds2, opt, opt1, f, vcameraMargins, obj)
    fun flyToTarget(vcameraTarget: CameraTarget, f: Float, flag: Boolean, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener?): Unit = mProxy.native_flyToTarget(handle, vcameraTarget, f, flag, vcameraMargins, obj)
    fun flyToWithZoomLevel(d: Double, d1: Double, f: Float, opt: Float?, opt1: Float?, f1: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener?): Unit = mProxy.native_flyToWithZoomLevel(handle, d, d1, f, opt, opt1, f1, vcameraMargins, obj)
    fun flyover(flag: Boolean, items: MutableList<WorldPoint3>, vcameraMargins: CameraMargins, obj: CameraFlyThroughProgressListener?, vcameraTargetPlus: CameraTargetPlus, obj1: CameraDeltaTimeProvider?, items1: MutableList<Float?>): CameraFlyThroughControls = mProxy.native_flyover(handle, flag, items, vcameraMargins, obj, vcameraTargetPlus, obj1, items1)
    fun followUserLocation(opt: Float?, opt1: Float?, opt2: Float?, vfollowMode: FollowMode, vcameraMargins: CameraMargins): Unit = mProxy.native_followUserLocation(handle, opt, opt1, opt2, vfollowMode, vcameraMargins)
    fun followUserLocationWithZoomLevel(vworldPoint2: WorldPoint2, opt: Float?, opt1: Float?, opt2: Float?, vfollowMode: FollowMode, vcameraMargins: CameraMargins): Unit = mProxy.native_followUserLocationWithZoomLevel(handle, vworldPoint2, opt, opt1, opt2, vfollowMode, vcameraMargins)
    fun getCameraViewType(): CameraViewType = mProxy.native_getCameraViewType(handle)
    fun getCustomLookAtPoint(f: Float, f1: Float): LookAtPoint = mProxy.native_getCustomLookAtPoint(handle, f, f1)
    fun getLookAtPoint(): LookAtPoint = mProxy.native_getLookAtPoint(handle)
    fun getScaleMetersPerPixel(): Float = mProxy.native_getScaleMetersPerPixel(handle)
    fun getScreenPosition(vworldPoint2: WorldPoint2): PointScreenPosition = mProxy.native_getScreenPosition(handle, vworldPoint2)
    fun getTarget(): CameraTarget = mProxy.native_getTarget(handle)
    fun getVisibleAreaBounds(vcameraMargins: CameraMargins, f: Float): VisibleAreaBounds = mProxy.native_getVisibleAreaBounds(handle, vcameraMargins, f)
    fun isWorldPointVisible(vworldPoint3: WorldPoint3, flag: Boolean): Boolean = mProxy.native_isWorldPointVisible(handle, vworldPoint3, flag)
    fun removeInteractionListener(obj: CameraInteractionListener?): Unit = mProxy.native_removeInteractionListener(handle, obj)
    fun removeStateListener(obj: CameraStateListener?): Unit = mProxy.native_removeStateListener(handle, obj)
    fun setBearing(f: Float, f1: Float, vcameraMargins: CameraMargins): Unit = mProxy.native_setBearing(handle, f, f1, vcameraMargins)
    fun setCameraViewType(vcameraViewType: CameraViewType): Unit = mProxy.native_setCameraViewType(handle, vcameraViewType)
    fun setDynamicExaggerationEnabled(flag: Boolean): Unit = mProxy.native_setDynamicExaggerationEnabled(handle, flag)
    fun setDynamicExaggerationMultiplier(f: Float): Unit = mProxy.native_setDynamicExaggerationMultiplier(handle, f)
    fun setRotationSpeed(f: Float, f1: Float): Unit = mProxy.native_setRotationSpeed(handle, f, f1)
    fun setTranslationSpeed(f: Float, f1: Float): Unit = mProxy.native_setTranslationSpeed(handle, f, f1)
    fun setZoomSpeed(f: Float): Unit = mProxy.native_setZoomSpeed(handle, f)
    fun stop(): Unit = mProxy.native_stop(handle)
    fun translateTo(vworldPoint2: WorldPoint2, f: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener?): Unit = mProxy.native_translateTo(handle, vworldPoint2, f, vcameraMargins, obj)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
