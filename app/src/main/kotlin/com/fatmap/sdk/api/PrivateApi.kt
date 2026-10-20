package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class PrivateApi internal constructor(handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ PrivateApi ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_addPerformanceMetricsListener(obj: PerformanceMetricsListener?): Unit
        external fun native_clearLuaPersistedStore(): Unit
        external fun native_finishTwoPassVideoRendering(): Unit
        external fun native_getGivenUpDownloadsInfo(l: Long): GivenUpDownloadsInfo
        external fun native_getMemoryStats(): MemoryStats
        external fun getPerformanceTestsNames(): MutableList<String>
        external fun native_getPreparedCommandService(obj: DataExchangeService?): CommandService
        external fun native_getVisibleAreaBoundsExt(vcameraMargins: CameraMargins, f: Float, obj: VisibleAreaSamplesSink?): VisibleAreaBounds
        external fun native_haveGivenUpTileDownloads(): Boolean
        external fun native_haveVisualContentLoadingWork(flag: Boolean): Boolean
        external fun native_isVsyncEnabled(): Boolean
        external fun native_recordResourcesNeededForSecondVideoRenderingPass(): Unit
        external fun native_refreshScreenInfo(): Unit
        external fun native_reloadTerrain(): Unit
        external fun native_removePerformanceMetricsListener(obj: PerformanceMetricsListener?): Unit
        external fun native_resetOnlineAvailabilityData(): Unit
        external fun native_resetTileDownloadHistory(): Unit
        external fun native_runPerformanceTests(items: MutableList<String>, text: String, obj: TestsListener?): Unit
        external fun native_runScreenshotTests(items: MutableList<String>, text: String, obj: TestsListener?): Unit
        external fun native_saveWindowScreenshot(text: String): Unit
        external fun native_setDiskCacheSize(l: Long): Unit
        external fun native_setDownloadHeightmapsOnly(flag: Boolean): Unit
        external fun native_setLodSubdivisionThresholdEnabled(flag: Boolean): Unit
        external fun native_setLuaGlobalAnimation(text: String, vanimatedImage: AnimatedImage, flag: Boolean): Unit
        external fun native_setLuaGlobalFloat(text: String, f: Float): Unit
        external fun native_setLuaGlobalImage(text: String, vcustomImage: CustomImage, flag: Boolean): Unit
        external fun native_setLuaGlobalInt(text: String, i: Int): Unit
        external fun native_setLuaGlobalLine(text: String, vworldLine2: WorldLine2): Unit
        external fun native_setLuaGlobalString(text: String, text1: String): Unit
        external fun native_setOfflineMode(flag: Boolean): Unit
        external fun native_setPixelRatio(f: Float): Unit
        external fun native_setResourceLoadingFromDiskAllowed(flag: Boolean): Unit
        external fun native_setShaderHotReloadingEnabled(flag: Boolean): Unit
        external fun native_setShowSymbolBoundingBoxes(flag: Boolean): Unit
        external fun native_setStylingZoomLevelOverride(opt: Float?): Unit
        external fun native_setSymbolScreenPaddingEnabled(flag: Boolean): Unit
        external fun native_setSymbolVisibilityStateful(flag: Boolean): Unit
        external fun native_setTestAccountForPersonalHeatmap(vpersonalHeatmapTestAccount: PersonalHeatmapTestAccount): Unit
        external fun native_setTileDownloadEventProcessor(obj: TileDownloadEventProcessor?): Unit
        external fun native_setVsyncEnabled(flag: Boolean): Unit
        external fun native_setupDefaultMockTiles(): Unit
        external fun native_startDownloadingResourcesForSecondVideoRenderingPass(): Unit
        external fun native_startEnteringTestingContext(obj: Offline?, text: String, text1: String, obj1: TestingContextEventListener?): TestingContextHandle
        external fun native_startLuaAnimationScript(text: String, opt: Float?, obj: LuaAnimationHandler?): Unit
        external fun native_startMockTileServer(): Unit
        external fun native_startTrackPerfTest(items: MutableList<WorldPoint3>, f: Float, l: Long, flag: Boolean, obj: TrackPerfTestCompletionListener?): Unit
        external fun native_startVideoRenderingFirstPass(): Unit
        external fun native_startVideoRenderingSecondPass(): Unit
        external fun native_stopLuaAnimationScript(): Unit
        external fun native_stopMockTileServer(): Unit
        external fun native_switchToCustomStyle(text: String, flag: Boolean, flag1: Boolean, flag2: Boolean, flag3: Boolean, flag4: Boolean, flag5: Boolean, opt: Float?): Unit
        external fun native_updateLodCalculationControlParams(vlodCalculationControlParams: LodCalculationControlParams): LodCalculationControlParams
        external fun nativeDestroy()
    }

    fun addPerformanceMetricsListener(obj: PerformanceMetricsListener?): Unit = mProxy.native_addPerformanceMetricsListener(obj)
    fun clearLuaPersistedStore(): Unit = mProxy.native_clearLuaPersistedStore()
    fun finishTwoPassVideoRendering(): Unit = mProxy.native_finishTwoPassVideoRendering()
    fun getGivenUpDownloadsInfo(l: Long): GivenUpDownloadsInfo = mProxy.native_getGivenUpDownloadsInfo(l)
    fun getMemoryStats(): MemoryStats = mProxy.native_getMemoryStats()
    fun getPreparedCommandService(obj: DataExchangeService?): CommandService = mProxy.native_getPreparedCommandService(obj)
    fun getVisibleAreaBoundsExt(vcameraMargins: CameraMargins, f: Float, obj: VisibleAreaSamplesSink?): VisibleAreaBounds = mProxy.native_getVisibleAreaBoundsExt(vcameraMargins, f, obj)
    fun haveGivenUpTileDownloads(): Boolean = mProxy.native_haveGivenUpTileDownloads()
    fun haveVisualContentLoadingWork(flag: Boolean): Boolean = mProxy.native_haveVisualContentLoadingWork(flag)
    fun isVsyncEnabled(): Boolean = mProxy.native_isVsyncEnabled()
    fun recordResourcesNeededForSecondVideoRenderingPass(): Unit = mProxy.native_recordResourcesNeededForSecondVideoRenderingPass()
    fun refreshScreenInfo(): Unit = mProxy.native_refreshScreenInfo()
    fun reloadTerrain(): Unit = mProxy.native_reloadTerrain()
    fun removePerformanceMetricsListener(obj: PerformanceMetricsListener?): Unit = mProxy.native_removePerformanceMetricsListener(obj)
    fun resetOnlineAvailabilityData(): Unit = mProxy.native_resetOnlineAvailabilityData()
    fun resetTileDownloadHistory(): Unit = mProxy.native_resetTileDownloadHistory()
    fun runPerformanceTests(items: MutableList<String>, text: String, obj: TestsListener?): Unit = mProxy.native_runPerformanceTests(items, text, obj)
    fun runScreenshotTests(items: MutableList<String>, text: String, obj: TestsListener?): Unit = mProxy.native_runScreenshotTests(items, text, obj)
    fun saveWindowScreenshot(text: String): Unit = mProxy.native_saveWindowScreenshot(text)
    fun setDiskCacheSize(l: Long): Unit = mProxy.native_setDiskCacheSize(l)
    fun setDownloadHeightmapsOnly(flag: Boolean): Unit = mProxy.native_setDownloadHeightmapsOnly(flag)
    fun setLodSubdivisionThresholdEnabled(flag: Boolean): Unit = mProxy.native_setLodSubdivisionThresholdEnabled(flag)
    fun setLuaGlobalAnimation(text: String, vanimatedImage: AnimatedImage, flag: Boolean): Unit = mProxy.native_setLuaGlobalAnimation(text, vanimatedImage, flag)
    fun setLuaGlobalFloat(text: String, f: Float): Unit = mProxy.native_setLuaGlobalFloat(text, f)
    fun setLuaGlobalImage(text: String, vcustomImage: CustomImage, flag: Boolean): Unit = mProxy.native_setLuaGlobalImage(text, vcustomImage, flag)
    fun setLuaGlobalInt(text: String, i: Int): Unit = mProxy.native_setLuaGlobalInt(text, i)
    fun setLuaGlobalLine(text: String, vworldLine2: WorldLine2): Unit = mProxy.native_setLuaGlobalLine(text, vworldLine2)
    fun setLuaGlobalString(text: String, text1: String): Unit = mProxy.native_setLuaGlobalString(text, text1)
    fun setOfflineMode(flag: Boolean): Unit = mProxy.native_setOfflineMode(flag)
    fun setPixelRatio(f: Float): Unit = mProxy.native_setPixelRatio(f)
    fun setResourceLoadingFromDiskAllowed(flag: Boolean): Unit = mProxy.native_setResourceLoadingFromDiskAllowed(flag)
    fun setShaderHotReloadingEnabled(flag: Boolean): Unit = mProxy.native_setShaderHotReloadingEnabled(flag)
    fun setShowSymbolBoundingBoxes(flag: Boolean): Unit = mProxy.native_setShowSymbolBoundingBoxes(flag)
    fun setStylingZoomLevelOverride(opt: Float?): Unit = mProxy.native_setStylingZoomLevelOverride(opt)
    fun setSymbolScreenPaddingEnabled(flag: Boolean): Unit = mProxy.native_setSymbolScreenPaddingEnabled(flag)
    fun setSymbolVisibilityStateful(flag: Boolean): Unit = mProxy.native_setSymbolVisibilityStateful(flag)
    fun setTestAccountForPersonalHeatmap(vpersonalHeatmapTestAccount: PersonalHeatmapTestAccount): Unit = mProxy.native_setTestAccountForPersonalHeatmap(vpersonalHeatmapTestAccount)
    fun setTileDownloadEventProcessor(obj: TileDownloadEventProcessor?): Unit = mProxy.native_setTileDownloadEventProcessor(obj)
    fun setVsyncEnabled(flag: Boolean): Unit = mProxy.native_setVsyncEnabled(flag)
    fun setupDefaultMockTiles(): Unit = mProxy.native_setupDefaultMockTiles()
    fun startDownloadingResourcesForSecondVideoRenderingPass(): Unit = mProxy.native_startDownloadingResourcesForSecondVideoRenderingPass()
    fun startEnteringTestingContext(obj: Offline?, text: String, text1: String, obj1: TestingContextEventListener?): TestingContextHandle = mProxy.native_startEnteringTestingContext(obj, text, text1, obj1)
    fun startLuaAnimationScript(text: String, opt: Float?, obj: LuaAnimationHandler?): Unit = mProxy.native_startLuaAnimationScript(text, opt, obj)
    fun startMockTileServer(): Unit = mProxy.native_startMockTileServer()
    fun startTrackPerfTest(items: MutableList<WorldPoint3>, f: Float, l: Long, flag: Boolean, obj: TrackPerfTestCompletionListener?): Unit = mProxy.native_startTrackPerfTest(items, f, l, flag, obj)
    fun startVideoRenderingFirstPass(): Unit = mProxy.native_startVideoRenderingFirstPass()
    fun startVideoRenderingSecondPass(): Unit = mProxy.native_startVideoRenderingSecondPass()
    fun stopLuaAnimationScript(): Unit = mProxy.native_stopLuaAnimationScript()
    fun stopMockTileServer(): Unit = mProxy.native_stopMockTileServer()
    fun switchToCustomStyle(text: String, flag: Boolean, flag1: Boolean, flag2: Boolean, flag3: Boolean, flag4: Boolean, flag5: Boolean, opt: Float?): Unit = mProxy.native_switchToCustomStyle(text, flag, flag1, flag2, flag3, flag4, flag5, opt)
    fun updateLodCalculationControlParams(vlodCalculationControlParams: LodCalculationControlParams): LodCalculationControlParams = mProxy.native_updateLodCalculationControlParams(vlodCalculationControlParams)

    companion object {
        fun getPerformanceTestsNames(): MutableList<String> = CppProxy(0L).getPerformanceTestsNames()
    }

    override fun close() {
        mProxy.nativeDestroy()
    }
}
