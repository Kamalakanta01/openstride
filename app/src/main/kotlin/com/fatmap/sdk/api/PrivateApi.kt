package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class PrivateApi internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ PrivateApi ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_addPerformanceMetricsListener(handle: Long, obj: PerformanceMetricsListener?): Unit
        external fun native_clearLuaPersistedStore(handle: Long): Unit
        external fun native_finishTwoPassVideoRendering(handle: Long): Unit
        external fun native_getGivenUpDownloadsInfo(handle: Long, l: Long): GivenUpDownloadsInfo
        external fun native_getMemoryStats(handle: Long): MemoryStats
        external fun getPerformanceTestsNames(): MutableList<String>
        external fun native_getPreparedCommandService(handle: Long, obj: DataExchangeService?): CommandService
        external fun native_getVisibleAreaBoundsExt(handle: Long, vcameraMargins: CameraMargins, f: Float, obj: VisibleAreaSamplesSink?): VisibleAreaBounds
        external fun native_haveGivenUpTileDownloads(handle: Long): Boolean
        external fun native_haveVisualContentLoadingWork(handle: Long, flag: Boolean): Boolean
        external fun native_isVsyncEnabled(handle: Long): Boolean
        external fun native_recordResourcesNeededForSecondVideoRenderingPass(handle: Long): Unit
        external fun native_refreshScreenInfo(handle: Long): Unit
        external fun native_reloadTerrain(handle: Long): Unit
        external fun native_removePerformanceMetricsListener(handle: Long, obj: PerformanceMetricsListener?): Unit
        external fun native_resetOnlineAvailabilityData(handle: Long): Unit
        external fun native_resetTileDownloadHistory(handle: Long): Unit
        external fun native_runPerformanceTests(handle: Long, items: MutableList<String>, text: String, obj: TestsListener?): Unit
        external fun native_runScreenshotTests(handle: Long, items: MutableList<String>, text: String, obj: TestsListener?): Unit
        external fun native_saveWindowScreenshot(handle: Long, text: String): Unit
        external fun native_setDiskCacheSize(handle: Long, l: Long): Unit
        external fun native_setDownloadHeightmapsOnly(handle: Long, flag: Boolean): Unit
        external fun native_setLodSubdivisionThresholdEnabled(handle: Long, flag: Boolean): Unit
        external fun native_setLuaGlobalAnimation(handle: Long, text: String, vanimatedImage: AnimatedImage, flag: Boolean): Unit
        external fun native_setLuaGlobalFloat(handle: Long, text: String, f: Float): Unit
        external fun native_setLuaGlobalImage(handle: Long, text: String, vcustomImage: CustomImage, flag: Boolean): Unit
        external fun native_setLuaGlobalInt(handle: Long, text: String, i: Int): Unit
        external fun native_setLuaGlobalLine(handle: Long, text: String, vworldLine2: WorldLine2): Unit
        external fun native_setLuaGlobalString(handle: Long, text: String, text1: String): Unit
        external fun native_setOfflineMode(handle: Long, flag: Boolean): Unit
        external fun native_setPixelRatio(handle: Long, f: Float): Unit
        external fun native_setResourceLoadingFromDiskAllowed(handle: Long, flag: Boolean): Unit
        external fun native_setShaderHotReloadingEnabled(handle: Long, flag: Boolean): Unit
        external fun native_setShowSymbolBoundingBoxes(handle: Long, flag: Boolean): Unit
        external fun native_setStylingZoomLevelOverride(handle: Long, opt: Float?): Unit
        external fun native_setSymbolScreenPaddingEnabled(handle: Long, flag: Boolean): Unit
        external fun native_setSymbolVisibilityStateful(handle: Long, flag: Boolean): Unit
        external fun native_setTestAccountForPersonalHeatmap(handle: Long, vpersonalHeatmapTestAccount: PersonalHeatmapTestAccount): Unit
        external fun native_setTileDownloadEventProcessor(handle: Long, obj: TileDownloadEventProcessor?): Unit
        external fun native_setVsyncEnabled(handle: Long, flag: Boolean): Unit
        external fun native_setupDefaultMockTiles(handle: Long): Unit
        external fun native_startDownloadingResourcesForSecondVideoRenderingPass(handle: Long): Unit
        external fun native_startEnteringTestingContext(handle: Long, obj: Offline?, text: String, text1: String, obj1: TestingContextEventListener?): TestingContextHandle
        external fun native_startLuaAnimationScript(handle: Long, text: String, opt: Float?, obj: LuaAnimationHandler?): Unit
        external fun native_startMockTileServer(handle: Long): Unit
        external fun native_startTrackPerfTest(handle: Long, items: MutableList<WorldPoint3>, f: Float, l: Long, flag: Boolean, obj: TrackPerfTestCompletionListener?): Unit
        external fun native_startVideoRenderingFirstPass(handle: Long): Unit
        external fun native_startVideoRenderingSecondPass(handle: Long): Unit
        external fun native_stopLuaAnimationScript(handle: Long): Unit
        external fun native_stopMockTileServer(handle: Long): Unit
        external fun native_switchToCustomStyle(handle: Long, text: String, flag: Boolean, flag1: Boolean, flag2: Boolean, flag3: Boolean, flag4: Boolean, flag5: Boolean, opt: Float?): Unit
        external fun native_updateLodCalculationControlParams(handle: Long, vlodCalculationControlParams: LodCalculationControlParams): LodCalculationControlParams
        external fun nativeDestroy(handle: Long)
    }

    fun addPerformanceMetricsListener(obj: PerformanceMetricsListener?): Unit = mProxy.native_addPerformanceMetricsListener(handle, obj)
    fun clearLuaPersistedStore(): Unit = mProxy.native_clearLuaPersistedStore(handle)
    fun finishTwoPassVideoRendering(): Unit = mProxy.native_finishTwoPassVideoRendering(handle)
    fun getGivenUpDownloadsInfo(l: Long): GivenUpDownloadsInfo = mProxy.native_getGivenUpDownloadsInfo(handle, l)
    fun getMemoryStats(): MemoryStats = mProxy.native_getMemoryStats(handle)
    fun getPreparedCommandService(obj: DataExchangeService?): CommandService = mProxy.native_getPreparedCommandService(handle, obj)
    fun getVisibleAreaBoundsExt(vcameraMargins: CameraMargins, f: Float, obj: VisibleAreaSamplesSink?): VisibleAreaBounds = mProxy.native_getVisibleAreaBoundsExt(handle, vcameraMargins, f, obj)
    fun haveGivenUpTileDownloads(): Boolean = mProxy.native_haveGivenUpTileDownloads(handle)
    fun haveVisualContentLoadingWork(flag: Boolean): Boolean = mProxy.native_haveVisualContentLoadingWork(handle, flag)
    fun isVsyncEnabled(): Boolean = mProxy.native_isVsyncEnabled(handle)
    fun recordResourcesNeededForSecondVideoRenderingPass(): Unit = mProxy.native_recordResourcesNeededForSecondVideoRenderingPass(handle)
    fun refreshScreenInfo(): Unit = mProxy.native_refreshScreenInfo(handle)
    fun reloadTerrain(): Unit = mProxy.native_reloadTerrain(handle)
    fun removePerformanceMetricsListener(obj: PerformanceMetricsListener?): Unit = mProxy.native_removePerformanceMetricsListener(handle, obj)
    fun resetOnlineAvailabilityData(): Unit = mProxy.native_resetOnlineAvailabilityData(handle)
    fun resetTileDownloadHistory(): Unit = mProxy.native_resetTileDownloadHistory(handle)
    fun runPerformanceTests(items: MutableList<String>, text: String, obj: TestsListener?): Unit = mProxy.native_runPerformanceTests(handle, items, text, obj)
    fun runScreenshotTests(items: MutableList<String>, text: String, obj: TestsListener?): Unit = mProxy.native_runScreenshotTests(handle, items, text, obj)
    fun saveWindowScreenshot(text: String): Unit = mProxy.native_saveWindowScreenshot(handle, text)
    fun setDiskCacheSize(l: Long): Unit = mProxy.native_setDiskCacheSize(handle, l)
    fun setDownloadHeightmapsOnly(flag: Boolean): Unit = mProxy.native_setDownloadHeightmapsOnly(handle, flag)
    fun setLodSubdivisionThresholdEnabled(flag: Boolean): Unit = mProxy.native_setLodSubdivisionThresholdEnabled(handle, flag)
    fun setLuaGlobalAnimation(text: String, vanimatedImage: AnimatedImage, flag: Boolean): Unit = mProxy.native_setLuaGlobalAnimation(handle, text, vanimatedImage, flag)
    fun setLuaGlobalFloat(text: String, f: Float): Unit = mProxy.native_setLuaGlobalFloat(handle, text, f)
    fun setLuaGlobalImage(text: String, vcustomImage: CustomImage, flag: Boolean): Unit = mProxy.native_setLuaGlobalImage(handle, text, vcustomImage, flag)
    fun setLuaGlobalInt(text: String, i: Int): Unit = mProxy.native_setLuaGlobalInt(handle, text, i)
    fun setLuaGlobalLine(text: String, vworldLine2: WorldLine2): Unit = mProxy.native_setLuaGlobalLine(handle, text, vworldLine2)
    fun setLuaGlobalString(text: String, text1: String): Unit = mProxy.native_setLuaGlobalString(handle, text, text1)
    fun setOfflineMode(flag: Boolean): Unit = mProxy.native_setOfflineMode(handle, flag)
    fun setPixelRatio(f: Float): Unit = mProxy.native_setPixelRatio(handle, f)
    fun setResourceLoadingFromDiskAllowed(flag: Boolean): Unit = mProxy.native_setResourceLoadingFromDiskAllowed(handle, flag)
    fun setShaderHotReloadingEnabled(flag: Boolean): Unit = mProxy.native_setShaderHotReloadingEnabled(handle, flag)
    fun setShowSymbolBoundingBoxes(flag: Boolean): Unit = mProxy.native_setShowSymbolBoundingBoxes(handle, flag)
    fun setStylingZoomLevelOverride(opt: Float?): Unit = mProxy.native_setStylingZoomLevelOverride(handle, opt)
    fun setSymbolScreenPaddingEnabled(flag: Boolean): Unit = mProxy.native_setSymbolScreenPaddingEnabled(handle, flag)
    fun setSymbolVisibilityStateful(flag: Boolean): Unit = mProxy.native_setSymbolVisibilityStateful(handle, flag)
    fun setTestAccountForPersonalHeatmap(vpersonalHeatmapTestAccount: PersonalHeatmapTestAccount): Unit = mProxy.native_setTestAccountForPersonalHeatmap(handle, vpersonalHeatmapTestAccount)
    fun setTileDownloadEventProcessor(obj: TileDownloadEventProcessor?): Unit = mProxy.native_setTileDownloadEventProcessor(handle, obj)
    fun setVsyncEnabled(flag: Boolean): Unit = mProxy.native_setVsyncEnabled(handle, flag)
    fun setupDefaultMockTiles(): Unit = mProxy.native_setupDefaultMockTiles(handle)
    fun startDownloadingResourcesForSecondVideoRenderingPass(): Unit = mProxy.native_startDownloadingResourcesForSecondVideoRenderingPass(handle)
    fun startEnteringTestingContext(obj: Offline?, text: String, text1: String, obj1: TestingContextEventListener?): TestingContextHandle = mProxy.native_startEnteringTestingContext(handle, obj, text, text1, obj1)
    fun startLuaAnimationScript(text: String, opt: Float?, obj: LuaAnimationHandler?): Unit = mProxy.native_startLuaAnimationScript(handle, text, opt, obj)
    fun startMockTileServer(): Unit = mProxy.native_startMockTileServer(handle)
    fun startTrackPerfTest(items: MutableList<WorldPoint3>, f: Float, l: Long, flag: Boolean, obj: TrackPerfTestCompletionListener?): Unit = mProxy.native_startTrackPerfTest(handle, items, f, l, flag, obj)
    fun startVideoRenderingFirstPass(): Unit = mProxy.native_startVideoRenderingFirstPass(handle)
    fun startVideoRenderingSecondPass(): Unit = mProxy.native_startVideoRenderingSecondPass(handle)
    fun stopLuaAnimationScript(): Unit = mProxy.native_stopLuaAnimationScript(handle)
    fun stopMockTileServer(): Unit = mProxy.native_stopMockTileServer(handle)
    fun switchToCustomStyle(text: String, flag: Boolean, flag1: Boolean, flag2: Boolean, flag3: Boolean, flag4: Boolean, flag5: Boolean, opt: Float?): Unit = mProxy.native_switchToCustomStyle(handle, text, flag, flag1, flag2, flag3, flag4, flag5, opt)
    fun updateLodCalculationControlParams(vlodCalculationControlParams: LodCalculationControlParams): LodCalculationControlParams = mProxy.native_updateLodCalculationControlParams(handle, vlodCalculationControlParams)

    companion object {
        fun getPerformanceTestsNames(): MutableList<String> = CppProxy(0L).getPerformanceTestsNames()
    }

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
