package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class PerformanceMetrics(
        val frameTime: Float,
        val frameCountFromStart: Long
) {
    fun getFrameCountFromStart(): Long = frameCountFromStart
    fun getFrameTime(): Float = frameTime

    override fun toString(): String = "PerformanceMetrics(" + listOf(frameTime=${frameTime}, frameCountFromStart=${frameCountFromStart}) + ")"
}
