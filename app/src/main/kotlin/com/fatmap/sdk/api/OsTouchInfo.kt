package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class OsTouchInfo(
        val timestamp: Double,
        val position: OsTouchPosition
) {
    fun getPosition(): OsTouchPosition = position
    fun getTimestamp(): Double = timestamp

    override fun toString(): String = "OsTouchInfo(" + listOf(timestamp=${timestamp}, position=${position}) + ")"
}
