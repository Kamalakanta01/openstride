package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class Color(
        val a: Double,
        val b: Double,
        val g: Double,
        val r: Double
) {
    fun getA(): Double = a
    fun getB(): Double = b
    fun getG(): Double = g
    fun getR(): Double = r

    override fun toString(): String = "Color(" + listOf(a=${a}, b=${b}, g=${g}, r=${r}) + ")"
}
