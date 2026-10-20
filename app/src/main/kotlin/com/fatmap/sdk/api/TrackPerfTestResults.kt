package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class TrackPerfTestResults(
    val avgFrameTimeMs: Long,
    val maxFrameTimeMs: Long,
    val minFrameTimeMs: Long
) {
    override fun toString(): String = "TrackPerfTestResults(avgFrameTimeMs=${avgFrameTimeMs}, maxFrameTimeMs=${maxFrameTimeMs}, minFrameTimeMs=${minFrameTimeMs})"
}
