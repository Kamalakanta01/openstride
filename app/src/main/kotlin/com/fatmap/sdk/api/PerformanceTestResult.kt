package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class PerformanceTestResult(
        val name: String,
        val unit: String,
        val value: Double
) {
    fun getName(): String = name
    fun getUnit(): String = unit
    fun getValue(): Double = value

    override fun toString(): String = "PerformanceTestResult(" + listOf(name=${name}, unit=${unit}, value=${value}) + ")"
}
