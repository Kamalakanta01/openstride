package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class LookAtPoint(
    val worldPoint: WorldPoint3,
    @get:JvmName("getIsPointingAtMap") val isPointingAtMap: Boolean
) {
    override fun toString(): String = "LookAtPoint(worldPoint=${worldPoint}, isPointingAtMap=${isPointingAtMap})"
}
