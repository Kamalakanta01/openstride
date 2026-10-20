package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class LookAtPoint(
        val worldPoint: WorldPoint3,
        val isPointingAtMap: Boolean
) {
    fun getIsPointingAtMap(): Boolean = isPointingAtMap
    fun getWorldPoint(): WorldPoint3 = worldPoint

    override fun toString(): String = "LookAtPoint(" + listOf(worldPoint=${worldPoint}, isPointingAtMap=${isPointingAtMap}) + ")"
}
