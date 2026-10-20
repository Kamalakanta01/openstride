package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class OsTouch(
        val id: Long,
        val currentInfo: OsTouchInfo
) {
    fun getCurrentInfo(): OsTouchInfo = currentInfo
    fun getId(): Long = id

    override fun toString(): String = "OsTouch(" + listOf(id=${id}, currentInfo=${currentInfo}) + ")"
}
