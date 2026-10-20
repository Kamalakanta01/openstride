package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class QuadIndex(
        val z: Byte,
        val x: Int,
        val y: Int
) {
    fun getX(): Int = x
    fun getY(): Int = y
    fun getZ(): Byte = z

    override fun toString(): String = "QuadIndex(" + listOf(z=${z}, x=${x}, y=${y}) + ")"
}
