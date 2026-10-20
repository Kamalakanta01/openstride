package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class OsTouchPosition(
        val normalizedX: Float,
        val normalizedY: Float
) {
    fun getNormalizedX(): Float = normalizedX
    fun getNormalizedY(): Float = normalizedY

    override fun toString(): String = "OsTouchPosition(" + listOf(normalizedX=${normalizedX}, normalizedY=${normalizedY}) + ")"
}
