package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class TerrainEngine internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ TerrainEngine ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_addOnGridChangeListener(handle: Long, obj: OnGridChangeListener?): Unit
        external fun native_addOnMouseMoveListener(handle: Long, obj: OnMouseMoveListener?): Unit
        external fun native_addOnTapListener(handle: Long, obj: OnTapListener?): Unit
        external fun native_addOverlayPolygon(handle: Long, voverlayPolygon: OverlayPolygon): Unit
        external fun native_addPostUpdateListener(handle: Long, obj: PostUpdateListener?): Unit
        external fun native_addTileLoadingListener(handle: Long, obj: TileLoadingListener?): Unit
        external fun native_addViewUpdateListener(handle: Long, obj: ViewUpdateListener?): Unit
        external fun native_didEnterBackground(handle: Long): Unit
        external fun native_didEnterForeground(handle: Long): Unit
        external fun native_getCamera(handle: Long): Camera
        external fun native_getContent(handle: Long): Content
        external fun native_getDebugApi(handle: Long): DebugApi
        external fun native_getElevation(handle: Long, vworldPoint2: WorldPoint2): Double
        external fun native_getFps(handle: Long): Float
        external fun native_getGridUnits(handle: Long): UnitsInfo
        external fun native_getOffline(handle: Long): Offline
        external fun native_getOverriddenFeatureProperties(handle: Long): OverriddenFeatureProperties
        external fun native_getPrivateApi(handle: Long, text: String): PrivateApi
        external fun native_getUtils(handle: Long): Utils
        external fun native_receivedMemoryWarning(handle: Long): Unit
        external fun native_removeAllOverlayPolygons(handle: Long): Unit
        external fun native_removeOnGridChangeListener(handle: Long, obj: OnGridChangeListener?): Unit
        external fun native_removeOnMouseMoveListener(handle: Long, obj: OnMouseMoveListener?): Unit
        external fun native_removeOnTapListener(handle: Long, obj: OnTapListener?): Unit
        external fun native_removePostUpdateListener(handle: Long, obj: PostUpdateListener?): Unit
        external fun native_removeTileLoadingListener(handle: Long, obj: TileLoadingListener?): Unit
        external fun native_removeViewUpdateListener(handle: Long, obj: ViewUpdateListener?): Unit
        external fun native_requestRender(handle: Long): Unit
        external fun native_setAlwaysRender(handle: Long, flag: Boolean): Unit
        external fun native_setAuthenticationErrorListener(handle: Long, obj: AuthenticationErrorListener?): Unit
        external fun native_setDesiredLanguage(handle: Long, vlanguage: Language): Unit
        external fun native_setEnableScreenSymbols(handle: Long, flag: Boolean): Unit
        external fun native_setGlobalHeatmapConfig(handle: Long, vglobalHeatmapConfig: GlobalHeatmapConfig): Unit
        external fun native_setImageryOverlay(handle: Long, vimageryOverlay: ImageryOverlay): Unit
        external fun native_setIsTerrain3dEnabled(handle: Long, flag: Boolean): Unit
        external fun native_setLogLevel(handle: Long, vlogLevel: LogLevel): Unit
        external fun native_setMapType(handle: Long, vmapType: MapType): Unit
        external fun native_setNightlyHeatmapConfig(handle: Long, vnightlyHeatmapConfig: NightlyHeatmapConfig): Unit
        external fun native_setOverlayPolygons(handle: Long, items: MutableList<OverlayPolygon>): Unit
        external fun native_setOverriddenFeatureProperties(handle: Long, obj: OverriddenFeatureProperties?): Unit
        external fun native_setPaused(handle: Long, flag: Boolean): Unit
        external fun native_setPersonalHeatmapConfig(handle: Long, vpersonalHeatmapConfig: PersonalHeatmapConfig): Unit
        external fun native_setRenderingDisabled(handle: Long, flag: Boolean): Unit
        external fun native_setScreenSymbolRefreshInterval(handle: Long, f: Float): Unit
        external fun native_setServerConfig(handle: Long, vserverConfig: ServerConfig, flag: Boolean): Unit
        external fun native_setStravaAuthData(handle: Long, l: Long, items: MutableList<HttpHeader>): Unit
        external fun native_setStravaSourcesExtraHeaders(handle: Long, items: MutableList<HttpHeader>): Unit
        external fun native_setSurfaceIdentifier(handle: Long, text: String): Unit
        external fun native_setUnitSystem(handle: Long, vmeasurementUnitSystem: MeasurementUnitSystem): Unit
        external fun native_setWeeklyHeatmapConfig(handle: Long, vweeklyHeatmapConfig: WeeklyHeatmapConfig): Unit
        external fun native_update(handle: Long): Unit
        external fun native_waitToLoadMap(handle: Long, obj: OnMapLoadListener?, flag: Boolean, f: Float): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun addOnGridChangeListener(obj: OnGridChangeListener?): Unit = mProxy.native_addOnGridChangeListener(handle, obj)
    fun addOnMouseMoveListener(obj: OnMouseMoveListener?): Unit = mProxy.native_addOnMouseMoveListener(handle, obj)
    fun addOnTapListener(obj: OnTapListener?): Unit = mProxy.native_addOnTapListener(handle, obj)
    fun addOverlayPolygon(voverlayPolygon: OverlayPolygon): Unit = mProxy.native_addOverlayPolygon(handle, voverlayPolygon)
    fun addPostUpdateListener(obj: PostUpdateListener?): Unit = mProxy.native_addPostUpdateListener(handle, obj)
    fun addTileLoadingListener(obj: TileLoadingListener?): Unit = mProxy.native_addTileLoadingListener(handle, obj)
    fun addViewUpdateListener(obj: ViewUpdateListener?): Unit = mProxy.native_addViewUpdateListener(handle, obj)
    fun didEnterBackground(): Unit = mProxy.native_didEnterBackground(handle)
    fun didEnterForeground(): Unit = mProxy.native_didEnterForeground(handle)
    fun getCamera(): Camera = mProxy.native_getCamera(handle)
    fun getContent(): Content = mProxy.native_getContent(handle)
    fun getDebugApi(): DebugApi = mProxy.native_getDebugApi(handle)
    fun getElevation(vworldPoint2: WorldPoint2): Double = mProxy.native_getElevation(handle, vworldPoint2)
    fun getFps(): Float = mProxy.native_getFps(handle)
    fun getGridUnits(): UnitsInfo = mProxy.native_getGridUnits(handle)
    fun getOffline(): Offline = mProxy.native_getOffline(handle)
    fun getOverriddenFeatureProperties(): OverriddenFeatureProperties = mProxy.native_getOverriddenFeatureProperties(handle)
    fun getPrivateApi(text: String): PrivateApi = mProxy.native_getPrivateApi(handle, text)
    fun getUtils(): Utils = mProxy.native_getUtils(handle)
    fun receivedMemoryWarning(): Unit = mProxy.native_receivedMemoryWarning(handle)
    fun removeAllOverlayPolygons(): Unit = mProxy.native_removeAllOverlayPolygons(handle)
    fun removeOnGridChangeListener(obj: OnGridChangeListener?): Unit = mProxy.native_removeOnGridChangeListener(handle, obj)
    fun removeOnMouseMoveListener(obj: OnMouseMoveListener?): Unit = mProxy.native_removeOnMouseMoveListener(handle, obj)
    fun removeOnTapListener(obj: OnTapListener?): Unit = mProxy.native_removeOnTapListener(handle, obj)
    fun removePostUpdateListener(obj: PostUpdateListener?): Unit = mProxy.native_removePostUpdateListener(handle, obj)
    fun removeTileLoadingListener(obj: TileLoadingListener?): Unit = mProxy.native_removeTileLoadingListener(handle, obj)
    fun removeViewUpdateListener(obj: ViewUpdateListener?): Unit = mProxy.native_removeViewUpdateListener(handle, obj)
    fun requestRender(): Unit = mProxy.native_requestRender(handle)
    fun setAlwaysRender(flag: Boolean): Unit = mProxy.native_setAlwaysRender(handle, flag)
    fun setAuthenticationErrorListener(obj: AuthenticationErrorListener?): Unit = mProxy.native_setAuthenticationErrorListener(handle, obj)
    fun setDesiredLanguage(vlanguage: Language): Unit = mProxy.native_setDesiredLanguage(handle, vlanguage)
    fun setEnableScreenSymbols(flag: Boolean): Unit = mProxy.native_setEnableScreenSymbols(handle, flag)
    fun setGlobalHeatmapConfig(vglobalHeatmapConfig: GlobalHeatmapConfig): Unit = mProxy.native_setGlobalHeatmapConfig(handle, vglobalHeatmapConfig)
    fun setImageryOverlay(vimageryOverlay: ImageryOverlay): Unit = mProxy.native_setImageryOverlay(handle, vimageryOverlay)
    fun setIsTerrain3dEnabled(flag: Boolean): Unit = mProxy.native_setIsTerrain3dEnabled(handle, flag)
    fun setLogLevel(vlogLevel: LogLevel): Unit = mProxy.native_setLogLevel(handle, vlogLevel)
    fun setMapType(vmapType: MapType): Unit = mProxy.native_setMapType(handle, vmapType)
    fun setNightlyHeatmapConfig(vnightlyHeatmapConfig: NightlyHeatmapConfig): Unit = mProxy.native_setNightlyHeatmapConfig(handle, vnightlyHeatmapConfig)
    fun setOverlayPolygons(items: MutableList<OverlayPolygon>): Unit = mProxy.native_setOverlayPolygons(handle, items)
    fun setOverriddenFeatureProperties(obj: OverriddenFeatureProperties?): Unit = mProxy.native_setOverriddenFeatureProperties(handle, obj)
    fun setPaused(flag: Boolean): Unit = mProxy.native_setPaused(handle, flag)
    fun setPersonalHeatmapConfig(vpersonalHeatmapConfig: PersonalHeatmapConfig): Unit = mProxy.native_setPersonalHeatmapConfig(handle, vpersonalHeatmapConfig)
    fun setRenderingDisabled(flag: Boolean): Unit = mProxy.native_setRenderingDisabled(handle, flag)
    fun setScreenSymbolRefreshInterval(f: Float): Unit = mProxy.native_setScreenSymbolRefreshInterval(handle, f)
    fun setServerConfig(vserverConfig: ServerConfig, flag: Boolean): Unit = mProxy.native_setServerConfig(handle, vserverConfig, flag)
    fun setStravaAuthData(l: Long, items: MutableList<HttpHeader>): Unit = mProxy.native_setStravaAuthData(handle, l, items)
    fun setStravaSourcesExtraHeaders(items: MutableList<HttpHeader>): Unit = mProxy.native_setStravaSourcesExtraHeaders(handle, items)
    fun setSurfaceIdentifier(text: String): Unit = mProxy.native_setSurfaceIdentifier(handle, text)
    fun setUnitSystem(vmeasurementUnitSystem: MeasurementUnitSystem): Unit = mProxy.native_setUnitSystem(handle, vmeasurementUnitSystem)
    fun setWeeklyHeatmapConfig(vweeklyHeatmapConfig: WeeklyHeatmapConfig): Unit = mProxy.native_setWeeklyHeatmapConfig(handle, vweeklyHeatmapConfig)
    fun update(): Unit = mProxy.native_update(handle)
    fun waitToLoadMap(obj: OnMapLoadListener?, flag: Boolean, f: Float): Unit = mProxy.native_waitToLoadMap(handle, obj, flag, f)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
