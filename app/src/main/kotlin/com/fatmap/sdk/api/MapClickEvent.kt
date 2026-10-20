package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class MapClickEvent(
    val type: ClickEventType,
    val worldPoint: WorldPoint3,
    val source: ClickSourceType,
    val button: MouseButtonCode
) {
    override fun toString(): String = "MapClickEvent(type=${type}, worldPoint=${worldPoint}, source=${source}, button=${button})"
}
