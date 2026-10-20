package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class LodCalculationControlParams(
    val tileSplitThresholdOffset: Float,
    val maxLodDifferenceForOffset: Int
) {
    override fun toString(): String = "LodCalculationControlParams(tileSplitThresholdOffset=${tileSplitThresholdOffset}, maxLodDifferenceForOffset=${maxLodDifferenceForOffset})"
}
