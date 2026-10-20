package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class PerformanceTestResult(
    val name: String,
    val unit: String,
    val value: Double
) {
    override fun toString(): String = "PerformanceTestResult(name=${name}, unit=${unit}, value=${value})"
}
