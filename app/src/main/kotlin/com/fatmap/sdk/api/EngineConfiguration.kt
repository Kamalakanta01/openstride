package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class EngineConfiguration internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ EngineConfiguration ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun createDefault(): EngineConfiguration
        external fun native_getBreadcrumbRecorder(handle: Long): BreadcrumbRecorder?
        external fun native_getCameraTarget(handle: Long): CameraTarget
        external fun native_getClientName(handle: Long): String
        external fun native_getEnableRasterMegaTiles(handle: Long): Boolean
        external fun native_getLogLevel(handle: Long): LogLevel
        external fun native_getLoggingToStdOut(handle: Long): Boolean
        external fun native_getReindexOnDiskResources(handle: Long): Boolean
        external fun native_getUseOnDiskCache(handle: Long): Boolean
        external fun native_loadFromFile(handle: Long, text: String): Boolean
        external fun native_setBreadcrumbRecorder(handle: Long, obj: BreadcrumbRecorder?): Unit
        external fun native_setCameraTarget(handle: Long, vcameraTarget: CameraTarget): Unit
        external fun native_setClientName(handle: Long, text: String): Unit
        external fun native_setEnableRasterMegaTiles(handle: Long, flag: Boolean): Unit
        external fun native_setLogLevel(handle: Long, vlogLevel: LogLevel): Unit
        external fun native_setLoggingToStdOut(handle: Long, flag: Boolean): Unit
        external fun native_setReindexOnDiskResources(handle: Long, flag: Boolean): Unit
        external fun native_setUseOnDiskCache(handle: Long, flag: Boolean): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun getBreadcrumbRecorder(): BreadcrumbRecorder? = mProxy.native_getBreadcrumbRecorder(handle)
    fun getCameraTarget(): CameraTarget = mProxy.native_getCameraTarget(handle)
    fun getClientName(): String = mProxy.native_getClientName(handle)
    fun getEnableRasterMegaTiles(): Boolean = mProxy.native_getEnableRasterMegaTiles(handle)
    fun getLogLevel(): LogLevel = mProxy.native_getLogLevel(handle)
    fun getLoggingToStdOut(): Boolean = mProxy.native_getLoggingToStdOut(handle)
    fun getReindexOnDiskResources(): Boolean = mProxy.native_getReindexOnDiskResources(handle)
    fun getUseOnDiskCache(): Boolean = mProxy.native_getUseOnDiskCache(handle)
    fun loadFromFile(text: String): Boolean = mProxy.native_loadFromFile(handle, text)
    fun setBreadcrumbRecorder(obj: BreadcrumbRecorder?): Unit = mProxy.native_setBreadcrumbRecorder(handle, obj)
    fun setCameraTarget(vcameraTarget: CameraTarget): Unit = mProxy.native_setCameraTarget(handle, vcameraTarget)
    fun setClientName(text: String): Unit = mProxy.native_setClientName(handle, text)
    fun setEnableRasterMegaTiles(flag: Boolean): Unit = mProxy.native_setEnableRasterMegaTiles(handle, flag)
    fun setLogLevel(vlogLevel: LogLevel): Unit = mProxy.native_setLogLevel(handle, vlogLevel)
    fun setLoggingToStdOut(flag: Boolean): Unit = mProxy.native_setLoggingToStdOut(handle, flag)
    fun setReindexOnDiskResources(flag: Boolean): Unit = mProxy.native_setReindexOnDiskResources(handle, flag)
    fun setUseOnDiskCache(flag: Boolean): Unit = mProxy.native_setUseOnDiskCache(handle, flag)

    companion object {
        fun createDefault(): EngineConfiguration = CppProxy(0L).createDefault()
    }

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
