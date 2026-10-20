package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class Offline internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ Offline ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_loadBundles(handle: Long, items: MutableList<String>, obj: BundleLoadListener?): Unit
        external fun native_setNonBundleResourcesAllowed(handle: Long, flag: Boolean): Unit
        external fun native_unloadBundles(handle: Long, items: MutableList<String>, obj: BundleUnloadListener?): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun loadBundles(items: MutableList<String>, obj: BundleLoadListener?): Unit = mProxy.native_loadBundles(handle, items, obj)
    fun setNonBundleResourcesAllowed(flag: Boolean): Unit = mProxy.native_setNonBundleResourcesAllowed(handle, flag)
    fun unloadBundles(items: MutableList<String>, obj: BundleUnloadListener?): Unit = mProxy.native_unloadBundles(handle, items, obj)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
