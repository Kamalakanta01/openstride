package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class IntervalFloat(
        val from: Float,
        val to: Float
) {
    fun getFrom(): Float = from
    fun getTo(): Float = to

    override fun toString(): String = "IntervalFloat(" + listOf(from=${from}, to=${to}) + ")"
}
