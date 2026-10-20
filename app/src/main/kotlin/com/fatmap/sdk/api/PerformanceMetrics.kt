package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class PerformanceMetrics(
    val frameTime: Float,
    val frameCountFromStart: Long
) {
    override fun toString(): String = "PerformanceMetrics(frameTime=${frameTime}, frameCountFromStart=${frameCountFromStart})"
}
