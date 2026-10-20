package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class DebugOverlayPadding(
        val horizontal: Float,
        val vertical: Float
) {
    fun getHorizontal(): Float = horizontal
    fun getVertical(): Float = vertical

    override fun toString(): String = "DebugOverlayPadding(" + listOf(horizontal=${horizontal}, vertical=${vertical}) + ")"
}
