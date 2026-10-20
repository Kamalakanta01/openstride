package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class TerrainEngine internal constructor(handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ TerrainEngine ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_addOnGridChangeListener(obj: OnGridChangeListener): Unit
        external fun native_addOnMouseMoveListener(obj: OnMouseMoveListener): Unit
        external fun native_addOnTapListener(obj: OnTapListener): Unit
        external fun native_addOverlayPolygon(voverlayPolygon: OverlayPolygon): Unit
        external fun native_addPostUpdateListener(obj: PostUpdateListener): Unit
        external fun native_addTileLoadingListener(obj: TileLoadingListener): Unit
        external fun native_addViewUpdateListener(obj: ViewUpdateListener): Unit
        external fun native_didEnterBackground(): Unit
        external fun native_didEnterForeground(): Unit
        external fun native_getCamera(): Camera
        external fun native_getContent(): Content
        external fun native_getDebugApi(): DebugApi
        external fun native_getElevation(vworldPoint2: WorldPoint2): Double
        external fun native_getFps(): Float
        external fun native_getGridUnits(): UnitsInfo
        external fun native_getOffline(): Offline
        external fun native_getOverriddenFeatureProperties(): OverriddenFeatureProperties
        external fun native_getPrivateApi(text: String): PrivateApi
        external fun native_getUtils(): Utils
        external fun native_receivedMemoryWarning(): Unit
        external fun native_removeAllOverlayPolygons(): Unit
        external fun native_removeOnGridChangeListener(obj: OnGridChangeListener): Unit
        external fun native_removeOnMouseMoveListener(obj: OnMouseMoveListener): Unit
        external fun native_removeOnTapListener(obj: OnTapListener): Unit
        external fun native_removePostUpdateListener(obj: PostUpdateListener): Unit
        external fun native_removeTileLoadingListener(obj: TileLoadingListener): Unit
        external fun native_removeViewUpdateListener(obj: ViewUpdateListener): Unit
        external fun native_requestRender(): Unit
        external fun native_setAlwaysRender(flag: Boolean): Unit
        external fun native_setAuthenticationErrorListener(obj: AuthenticationErrorListener): Unit
        external fun native_setDesiredLanguage(vlanguage: Language): Unit
        external fun native_setEnableScreenSymbols(flag: Boolean): Unit
        external fun native_setGlobalHeatmapConfig(vglobalHeatmapConfig: GlobalHeatmapConfig): Unit
        external fun native_setImageryOverlay(vimageryOverlay: ImageryOverlay): Unit
        external fun native_setIsTerrain3dEnabled(flag: Boolean): Unit
        external fun native_setLogLevel(vlogLevel: LogLevel): Unit
        external fun native_setMapType(vmapType: MapType): Unit
        external fun native_setNightlyHeatmapConfig(vnightlyHeatmapConfig: NightlyHeatmapConfig): Unit
        external fun native_setOverlayPolygons(items: MutableList<OverlayPolygon>): Unit
        external fun native_setOverriddenFeatureProperties(voverriddenFeatureProperties: OverriddenFeatureProperties): Unit
        external fun native_setPaused(flag: Boolean): Unit
        external fun native_setPersonalHeatmapConfig(vpersonalHeatmapConfig: PersonalHeatmapConfig): Unit
        external fun native_setRenderingDisabled(flag: Boolean): Unit
        external fun native_setScreenSymbolRefreshInterval(f: Float): Unit
        external fun native_setServerConfig(vserverConfig: ServerConfig, flag: Boolean): Unit
        external fun native_setStravaAuthData(l: Long, items: MutableList<HttpHeader>): Unit
        external fun native_setStravaSourcesExtraHeaders(items: MutableList<HttpHeader>): Unit
        external fun native_setSurfaceIdentifier(text: String): Unit
        external fun native_setUnitSystem(vmeasurementUnitSystem: MeasurementUnitSystem): Unit
        external fun native_setWeeklyHeatmapConfig(vweeklyHeatmapConfig: WeeklyHeatmapConfig): Unit
        external fun native_update(): Unit
        external fun native_waitToLoadMap(obj: OnMapLoadListener, flag: Boolean, f: Float): Unit
        external fun nativeDestroy()
    }

    fun addOnGridChangeListener(obj: OnGridChangeListener): Unit = mProxy.native_addOnGridChangeListener(obj)
    fun addOnMouseMoveListener(obj: OnMouseMoveListener): Unit = mProxy.native_addOnMouseMoveListener(obj)
    fun addOnTapListener(obj: OnTapListener): Unit = mProxy.native_addOnTapListener(obj)
    fun addOverlayPolygon(voverlayPolygon: OverlayPolygon): Unit = mProxy.native_addOverlayPolygon(voverlayPolygon)
    fun addPostUpdateListener(obj: PostUpdateListener): Unit = mProxy.native_addPostUpdateListener(obj)
    fun addTileLoadingListener(obj: TileLoadingListener): Unit = mProxy.native_addTileLoadingListener(obj)
    fun addViewUpdateListener(obj: ViewUpdateListener): Unit = mProxy.native_addViewUpdateListener(obj)
    fun didEnterBackground(): Unit = mProxy.native_didEnterBackground()
    fun didEnterForeground(): Unit = mProxy.native_didEnterForeground()
    fun getCamera(): Camera = mProxy.native_getCamera()
    fun getContent(): Content = mProxy.native_getContent()
    fun getDebugApi(): DebugApi = mProxy.native_getDebugApi()
    fun getElevation(vworldPoint2: WorldPoint2): Double = mProxy.native_getElevation(vworldPoint2)
    fun getFps(): Float = mProxy.native_getFps()
    fun getGridUnits(): UnitsInfo = mProxy.native_getGridUnits()
    fun getOffline(): Offline = mProxy.native_getOffline()
    fun getOverriddenFeatureProperties(): OverriddenFeatureProperties = mProxy.native_getOverriddenFeatureProperties()
    fun getPrivateApi(text: String): PrivateApi = mProxy.native_getPrivateApi(text)
    fun getUtils(): Utils = mProxy.native_getUtils()
    fun receivedMemoryWarning(): Unit = mProxy.native_receivedMemoryWarning()
    fun removeAllOverlayPolygons(): Unit = mProxy.native_removeAllOverlayPolygons()
    fun removeOnGridChangeListener(obj: OnGridChangeListener): Unit = mProxy.native_removeOnGridChangeListener(obj)
    fun removeOnMouseMoveListener(obj: OnMouseMoveListener): Unit = mProxy.native_removeOnMouseMoveListener(obj)
    fun removeOnTapListener(obj: OnTapListener): Unit = mProxy.native_removeOnTapListener(obj)
    fun removePostUpdateListener(obj: PostUpdateListener): Unit = mProxy.native_removePostUpdateListener(obj)
    fun removeTileLoadingListener(obj: TileLoadingListener): Unit = mProxy.native_removeTileLoadingListener(obj)
    fun removeViewUpdateListener(obj: ViewUpdateListener): Unit = mProxy.native_removeViewUpdateListener(obj)
    fun requestRender(): Unit = mProxy.native_requestRender()
    fun setAlwaysRender(flag: Boolean): Unit = mProxy.native_setAlwaysRender(flag)
    fun setAuthenticationErrorListener(obj: AuthenticationErrorListener): Unit = mProxy.native_setAuthenticationErrorListener(obj)
    fun setDesiredLanguage(vlanguage: Language): Unit = mProxy.native_setDesiredLanguage(vlanguage)
    fun setEnableScreenSymbols(flag: Boolean): Unit = mProxy.native_setEnableScreenSymbols(flag)
    fun setGlobalHeatmapConfig(vglobalHeatmapConfig: GlobalHeatmapConfig): Unit = mProxy.native_setGlobalHeatmapConfig(vglobalHeatmapConfig)
    fun setImageryOverlay(vimageryOverlay: ImageryOverlay): Unit = mProxy.native_setImageryOverlay(vimageryOverlay)
    fun setIsTerrain3dEnabled(flag: Boolean): Unit = mProxy.native_setIsTerrain3dEnabled(flag)
    fun setLogLevel(vlogLevel: LogLevel): Unit = mProxy.native_setLogLevel(vlogLevel)
    fun setMapType(vmapType: MapType): Unit = mProxy.native_setMapType(vmapType)
    fun setNightlyHeatmapConfig(vnightlyHeatmapConfig: NightlyHeatmapConfig): Unit = mProxy.native_setNightlyHeatmapConfig(vnightlyHeatmapConfig)
    fun setOverlayPolygons(items: MutableList<OverlayPolygon>): Unit = mProxy.native_setOverlayPolygons(items)
    fun setOverriddenFeatureProperties(voverriddenFeatureProperties: OverriddenFeatureProperties): Unit = mProxy.native_setOverriddenFeatureProperties(voverriddenFeatureProperties)
    fun setPaused(flag: Boolean): Unit = mProxy.native_setPaused(flag)
    fun setPersonalHeatmapConfig(vpersonalHeatmapConfig: PersonalHeatmapConfig): Unit = mProxy.native_setPersonalHeatmapConfig(vpersonalHeatmapConfig)
    fun setRenderingDisabled(flag: Boolean): Unit = mProxy.native_setRenderingDisabled(flag)
    fun setScreenSymbolRefreshInterval(f: Float): Unit = mProxy.native_setScreenSymbolRefreshInterval(f)
    fun setServerConfig(vserverConfig: ServerConfig, flag: Boolean): Unit = mProxy.native_setServerConfig(vserverConfig, flag)
    fun setStravaAuthData(l: Long, items: MutableList<HttpHeader>): Unit = mProxy.native_setStravaAuthData(l, items)
    fun setStravaSourcesExtraHeaders(items: MutableList<HttpHeader>): Unit = mProxy.native_setStravaSourcesExtraHeaders(items)
    fun setSurfaceIdentifier(text: String): Unit = mProxy.native_setSurfaceIdentifier(text)
    fun setUnitSystem(vmeasurementUnitSystem: MeasurementUnitSystem): Unit = mProxy.native_setUnitSystem(vmeasurementUnitSystem)
    fun setWeeklyHeatmapConfig(vweeklyHeatmapConfig: WeeklyHeatmapConfig): Unit = mProxy.native_setWeeklyHeatmapConfig(vweeklyHeatmapConfig)
    fun update(): Unit = mProxy.native_update()
    fun waitToLoadMap(obj: OnMapLoadListener, flag: Boolean, f: Float): Unit = mProxy.native_waitToLoadMap(obj, flag, f)

    override fun close() {
        mProxy.nativeDestroy()
    }
}
