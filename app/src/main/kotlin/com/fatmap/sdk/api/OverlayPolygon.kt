package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class OverlayPolygon(
        val boundary: WorldLine2,
        val gradientRange: IntervalFloat,
        val heightRange: IntervalFloat,
        val aspect: MutableList<IntervalFloat>,
        val fillColor: Color
) {
    fun getAspect(): MutableList<IntervalFloat> = aspect
    fun getBoundary(): WorldLine2 = boundary
    fun getFillColor(): Color = fillColor
    fun getGradientRange(): IntervalFloat = gradientRange
    fun getHeightRange(): IntervalFloat = heightRange

    override fun toString(): String = "OverlayPolygon(" + listOf(boundary=${boundary}, gradientRange=${gradientRange}, heightRange=${heightRange}, aspect=${aspect}, fillColor=${fillColor}) + ")"
}
