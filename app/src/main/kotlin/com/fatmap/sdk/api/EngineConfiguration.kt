package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class EngineConfiguration internal constructor(handle: Long) : AutoCloseable {

    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ EngineConfiguration ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {

        external fun createDefault(): EngineConfiguration
        external fun native_getBreadcrumbRecorder(): BreadcrumbRecorder
        external fun native_getCameraTarget(): CameraTarget
        external fun native_getClientName(): String
        external fun native_getEnableRasterMegaTiles(): Boolean
        external fun native_getLogLevel(): LogLevel
        external fun native_getLoggingToStdOut(): Boolean
        external fun native_getReindexOnDiskResources(): Boolean
        external fun native_getUseOnDiskCache(): Boolean
        external fun native_loadFromFile(text: String): Boolean
        external fun native_setBreadcrumbRecorder(obj: BreadcrumbRecorder): Unit
        external fun native_setCameraTarget(vcameraTarget: CameraTarget): Unit
        external fun native_setClientName(text: String): Unit
        external fun native_setEnableRasterMegaTiles(flag: Boolean): Unit
        external fun native_setLogLevel(vlogLevel: LogLevel): Unit
        external fun native_setLoggingToStdOut(flag: Boolean): Unit
        external fun native_setReindexOnDiskResources(flag: Boolean): Unit
        external fun native_setUseOnDiskCache(flag: Boolean): Unit

        external fun nativeDestroy()
    }

    fun createDefault(): EngineConfiguration = mProxy.createDefault()
    fun getBreadcrumbRecorder(): BreadcrumbRecorder = mProxy.native_getBreadcrumbRecorder()
    fun getCameraTarget(): CameraTarget = mProxy.native_getCameraTarget()
    fun getClientName(): String = mProxy.native_getClientName()
    fun getEnableRasterMegaTiles(): Boolean = mProxy.native_getEnableRasterMegaTiles()
    fun getLogLevel(): LogLevel = mProxy.native_getLogLevel()
    fun getLoggingToStdOut(): Boolean = mProxy.native_getLoggingToStdOut()
    fun getReindexOnDiskResources(): Boolean = mProxy.native_getReindexOnDiskResources()
    fun getUseOnDiskCache(): Boolean = mProxy.native_getUseOnDiskCache()
    fun loadFromFile(text: String): Boolean = mProxy.native_loadFromFile(text)
    fun setBreadcrumbRecorder(obj: BreadcrumbRecorder): Unit = mProxy.native_setBreadcrumbRecorder(obj)
    fun setCameraTarget(vcameraTarget: CameraTarget): Unit = mProxy.native_setCameraTarget(vcameraTarget)
    fun setClientName(text: String): Unit = mProxy.native_setClientName(text)
    fun setEnableRasterMegaTiles(flag: Boolean): Unit = mProxy.native_setEnableRasterMegaTiles(flag)
    fun setLogLevel(vlogLevel: LogLevel): Unit = mProxy.native_setLogLevel(vlogLevel)
    fun setLoggingToStdOut(flag: Boolean): Unit = mProxy.native_setLoggingToStdOut(flag)
    fun setReindexOnDiskResources(flag: Boolean): Unit = mProxy.native_setReindexOnDiskResources(flag)
    fun setUseOnDiskCache(flag: Boolean): Unit = mProxy.native_setUseOnDiskCache(flag)

    override fun close() {
        mProxy.nativeDestroy()
    }
}
