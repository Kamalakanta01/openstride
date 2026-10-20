package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class Camera internal constructor(handle: Long) : AutoCloseable {

    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ Camera ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {

        external fun native_addInteractionListener(obj: CameraInteractionListener): Unit
        external fun native_addStateListener(obj: CameraStateListener): Unit
        external fun native_enterFirstPersonView(d: Double, d1: Double, f: Float): Unit
        external fun native_flyThrough(items: MutableList<WorldPoint3>, text: String, text1: String, vcameraMargins: CameraMargins, obj: CameraFlyThroughProgressListener, vcameraTargetPlus: CameraTargetPlus, items1: MutableList<Float?>): Unit
        external fun native_flyThroughInteractive(items: MutableList<WorldPoint3>, text: String, text1: String, vcameraMargins: CameraMargins, obj: CameraFlyThroughProgressListener, vcameraTargetPlus: CameraTargetPlus, flag: Boolean, obj1: CameraDeltaTimeProvider, items1: MutableList<Float?>): CameraFlyThroughControls
        external fun native_flyTo(d: Double, d1: Double, f: Float, opt: Float?, opt1: Float?, f1: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener): Unit
        external fun native_flyToBounds(vworldBounds2: WorldBounds2, opt: Float?, opt1: Float?, f: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener): Unit
        external fun native_flyToTarget(vcameraTarget: CameraTarget, f: Float, flag: Boolean, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener): Unit
        external fun native_flyToWithZoomLevel(d: Double, d1: Double, f: Float, opt: Float?, opt1: Float?, f1: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener): Unit
        external fun native_flyover(flag: Boolean, items: MutableList<WorldPoint3>, vcameraMargins: CameraMargins, obj: CameraFlyThroughProgressListener, vcameraTargetPlus: CameraTargetPlus, obj1: CameraDeltaTimeProvider, items1: MutableList<Float?>): CameraFlyThroughControls
        external fun native_followUserLocation(opt: Float?, opt1: Float?, opt2: Float?, vfollowMode: FollowMode, vcameraMargins: CameraMargins): Unit
        external fun native_followUserLocationWithZoomLevel(vworldPoint2: WorldPoint2, opt: Float?, opt1: Float?, opt2: Float?, vfollowMode: FollowMode, vcameraMargins: CameraMargins): Unit
        external fun native_getCameraViewType(): CameraViewType
        external fun native_getCustomLookAtPoint(f: Float, f1: Float): LookAtPoint
        external fun native_getLookAtPoint(): LookAtPoint
        external fun native_getScaleMetersPerPixel(): Float
        external fun native_getScreenPosition(vworldPoint2: WorldPoint2): PointScreenPosition
        external fun native_getTarget(): CameraTarget
        external fun native_getVisibleAreaBounds(vcameraMargins: CameraMargins, f: Float): VisibleAreaBounds
        external fun native_isWorldPointVisible(vworldPoint3: WorldPoint3, flag: Boolean): Boolean
        external fun native_removeInteractionListener(obj: CameraInteractionListener): Unit
        external fun native_removeStateListener(obj: CameraStateListener): Unit
        external fun native_setBearing(f: Float, f1: Float, vcameraMargins: CameraMargins): Unit
        external fun native_setCameraViewType(vcameraViewType: CameraViewType): Unit
        external fun native_setDynamicExaggerationEnabled(flag: Boolean): Unit
        external fun native_setDynamicExaggerationMultiplier(f: Float): Unit
        external fun native_setRotationSpeed(f: Float, f1: Float): Unit
        external fun native_setTranslationSpeed(f: Float, f1: Float): Unit
        external fun native_setZoomSpeed(f: Float): Unit
        external fun native_stop(): Unit
        external fun native_translateTo(vworldPoint2: WorldPoint2, f: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener): Unit

        external fun nativeDestroy()
    }

    fun addInteractionListener(obj: CameraInteractionListener): Unit = mProxy.native_addInteractionListener(obj)
    fun addStateListener(obj: CameraStateListener): Unit = mProxy.native_addStateListener(obj)
    fun enterFirstPersonView(d: Double, d1: Double, f: Float): Unit = mProxy.native_enterFirstPersonView(d, d1, f)
    fun flyThrough(items: MutableList<WorldPoint3>, text: String, text1: String, vcameraMargins: CameraMargins, obj: CameraFlyThroughProgressListener, vcameraTargetPlus: CameraTargetPlus, items1: MutableList<Float?>): Unit = mProxy.native_flyThrough(items, text, text1, vcameraMargins, obj, vcameraTargetPlus, items1)
    fun flyThroughInteractive(items: MutableList<WorldPoint3>, text: String, text1: String, vcameraMargins: CameraMargins, obj: CameraFlyThroughProgressListener, vcameraTargetPlus: CameraTargetPlus, flag: Boolean, obj1: CameraDeltaTimeProvider, items1: MutableList<Float?>): CameraFlyThroughControls = mProxy.native_flyThroughInteractive(items, text, text1, vcameraMargins, obj, vcameraTargetPlus, flag, obj1, items1)
    fun flyTo(d: Double, d1: Double, f: Float, opt: Float?, opt1: Float?, f1: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener): Unit = mProxy.native_flyTo(d, d1, f, opt, opt1, f1, vcameraMargins, obj)
    fun flyToBounds(vworldBounds2: WorldBounds2, opt: Float?, opt1: Float?, f: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener): Unit = mProxy.native_flyToBounds(vworldBounds2, opt, opt1, f, vcameraMargins, obj)
    fun flyToTarget(vcameraTarget: CameraTarget, f: Float, flag: Boolean, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener): Unit = mProxy.native_flyToTarget(vcameraTarget, f, flag, vcameraMargins, obj)
    fun flyToWithZoomLevel(d: Double, d1: Double, f: Float, opt: Float?, opt1: Float?, f1: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener): Unit = mProxy.native_flyToWithZoomLevel(d, d1, f, opt, opt1, f1, vcameraMargins, obj)
    fun flyover(flag: Boolean, items: MutableList<WorldPoint3>, vcameraMargins: CameraMargins, obj: CameraFlyThroughProgressListener, vcameraTargetPlus: CameraTargetPlus, obj1: CameraDeltaTimeProvider, items1: MutableList<Float?>): CameraFlyThroughControls = mProxy.native_flyover(flag, items, vcameraMargins, obj, vcameraTargetPlus, obj1, items1)
    fun followUserLocation(opt: Float?, opt1: Float?, opt2: Float?, vfollowMode: FollowMode, vcameraMargins: CameraMargins): Unit = mProxy.native_followUserLocation(opt, opt1, opt2, vfollowMode, vcameraMargins)
    fun followUserLocationWithZoomLevel(vworldPoint2: WorldPoint2, opt: Float?, opt1: Float?, opt2: Float?, vfollowMode: FollowMode, vcameraMargins: CameraMargins): Unit = mProxy.native_followUserLocationWithZoomLevel(vworldPoint2, opt, opt1, opt2, vfollowMode, vcameraMargins)
    fun getCameraViewType(): CameraViewType = mProxy.native_getCameraViewType()
    fun getCustomLookAtPoint(f: Float, f1: Float): LookAtPoint = mProxy.native_getCustomLookAtPoint(f, f1)
    fun getLookAtPoint(): LookAtPoint = mProxy.native_getLookAtPoint()
    fun getScaleMetersPerPixel(): Float = mProxy.native_getScaleMetersPerPixel()
    fun getScreenPosition(vworldPoint2: WorldPoint2): PointScreenPosition = mProxy.native_getScreenPosition(vworldPoint2)
    fun getTarget(): CameraTarget = mProxy.native_getTarget()
    fun getVisibleAreaBounds(vcameraMargins: CameraMargins, f: Float): VisibleAreaBounds = mProxy.native_getVisibleAreaBounds(vcameraMargins, f)
    fun isWorldPointVisible(vworldPoint3: WorldPoint3, flag: Boolean): Boolean = mProxy.native_isWorldPointVisible(vworldPoint3, flag)
    fun removeInteractionListener(obj: CameraInteractionListener): Unit = mProxy.native_removeInteractionListener(obj)
    fun removeStateListener(obj: CameraStateListener): Unit = mProxy.native_removeStateListener(obj)
    fun setBearing(f: Float, f1: Float, vcameraMargins: CameraMargins): Unit = mProxy.native_setBearing(f, f1, vcameraMargins)
    fun setCameraViewType(vcameraViewType: CameraViewType): Unit = mProxy.native_setCameraViewType(vcameraViewType)
    fun setDynamicExaggerationEnabled(flag: Boolean): Unit = mProxy.native_setDynamicExaggerationEnabled(flag)
    fun setDynamicExaggerationMultiplier(f: Float): Unit = mProxy.native_setDynamicExaggerationMultiplier(f)
    fun setRotationSpeed(f: Float, f1: Float): Unit = mProxy.native_setRotationSpeed(f, f1)
    fun setTranslationSpeed(f: Float, f1: Float): Unit = mProxy.native_setTranslationSpeed(f, f1)
    fun setZoomSpeed(f: Float): Unit = mProxy.native_setZoomSpeed(f)
    fun stop(): Unit = mProxy.native_stop()
    fun translateTo(vworldPoint2: WorldPoint2, f: Float, vcameraMargins: CameraMargins, obj: CameraActionCompletionListener): Unit = mProxy.native_translateTo(vworldPoint2, f, vcameraMargins, obj)

    override fun close() {
        mProxy.nativeDestroy()
    }
}
