package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class UnitsInfo(
    val meters: Double,
    val size: Double,
    val unit: MeasurementUnit
) {
    override fun toString(): String = "UnitsInfo(meters=${meters}, size=${size}, unit=${unit})"
}
