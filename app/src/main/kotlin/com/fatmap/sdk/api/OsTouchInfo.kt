package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class OsTouchInfo(
    val timestamp: Double,
    val position: OsTouchPosition
) {
    override fun toString(): String = "OsTouchInfo(timestamp=${timestamp}, position=${position})"
}
