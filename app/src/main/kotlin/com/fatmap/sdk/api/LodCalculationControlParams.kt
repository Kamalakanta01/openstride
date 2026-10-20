package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class LodCalculationControlParams(
        val tileSplitThresholdOffset: Float,
        val maxLodDifferenceForOffset: Int
) {
    fun getMaxLodDifferenceForOffset(): Int = maxLodDifferenceForOffset
    fun getTileSplitThresholdOffset(): Float = tileSplitThresholdOffset

    override fun toString(): String = "LodCalculationControlParams(" + listOf(tileSplitThresholdOffset=${tileSplitThresholdOffset}, maxLodDifferenceForOffset=${maxLodDifferenceForOffset}) + ")"
}
