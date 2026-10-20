package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class HighlightedTile(
    val index: QuadIndex,
    val color: Color
) {
    override fun toString(): String = "HighlightedTile(index=${index}, color=${color})"
}
