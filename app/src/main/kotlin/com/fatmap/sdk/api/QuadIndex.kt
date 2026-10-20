package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class QuadIndex(
    val z: Byte,
    val x: Int,
    val y: Int
) {
    override fun toString(): String = "QuadIndex(z=${z}, x=${x}, y=${y})"
}
