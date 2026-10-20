package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class TrackPerfTestResults(
        val avgFrameTimeMs: Long,
        val maxFrameTimeMs: Long,
        val minFrameTimeMs: Long
) {
    fun getAvgFrameTimeMs(): Long = avgFrameTimeMs
    fun getMaxFrameTimeMs(): Long = maxFrameTimeMs
    fun getMinFrameTimeMs(): Long = minFrameTimeMs

    override fun toString(): String = "TrackPerfTestResults(" + listOf(avgFrameTimeMs=${avgFrameTimeMs}, maxFrameTimeMs=${maxFrameTimeMs}, minFrameTimeMs=${minFrameTimeMs}) + ")"
}
