package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class UnitsInfo(
        val meters: Double,
        val size: Double,
        val unit: MeasurementUnit
) {
    fun getMeters(): Double = meters
    fun getSize(): Double = size
    fun getUnit(): MeasurementUnit = unit

    override fun toString(): String = "UnitsInfo(" + listOf(meters=${meters}, size=${size}, unit=${unit}) + ")"
}
