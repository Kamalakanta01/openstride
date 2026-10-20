package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class TestsListener internal constructor(handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ TestsListener ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_onFinished(): Unit
        external fun native_onPerformanceTestResult(vperformanceTestResult: PerformanceTestResult): Unit
        external fun native_onTestFinished(text: String): Unit
        external fun nativeDestroy()
    }

    fun onFinished(): Unit = mProxy.native_onFinished()
    fun onPerformanceTestResult(vperformanceTestResult: PerformanceTestResult): Unit = mProxy.native_onPerformanceTestResult(vperformanceTestResult)
    fun onTestFinished(text: String): Unit = mProxy.native_onTestFinished(text)

    override fun close() {
        mProxy.nativeDestroy()
    }
}
