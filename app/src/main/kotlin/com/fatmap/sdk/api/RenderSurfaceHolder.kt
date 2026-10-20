package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
interface RenderSurfaceHolder {
    fun getGestureRecognizerControl(): GestureRecognizerControl
    fun getRenderSurfaceWindow(): Window
    fun isRenderSurfaceCreated(): Boolean
    fun notifyTerrainEngineReady(): Unit
    fun setTerrainEngine(obj: TerrainEngine): Unit
    fun setTouchListener(obj: TouchListener): Unit
    fun setWindowLifecycleListener(obj: WindowLifecycleListener): Unit
}
