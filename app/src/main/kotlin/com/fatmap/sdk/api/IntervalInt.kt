package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class IntervalInt(
        val from: Long,
        val to: Long
) {
    fun getFrom(): Long = from
    fun getTo(): Long = to

    override fun toString(): String = "IntervalInt(" + listOf(from=${from}, to=${to}) + ")"
}
