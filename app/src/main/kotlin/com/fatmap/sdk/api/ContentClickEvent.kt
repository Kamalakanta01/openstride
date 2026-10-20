package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class ContentClickEvent(
    val type: ClickEventType,
    val objects: MutableList<ContentClickEventObject>,
    val source: ClickSourceType,
    val button: MouseButtonCode,
    val worldPoint: WorldPoint3
) {
    override fun toString(): String = "ContentClickEvent(type=${type}, objects=${objects}, source=${source}, button=${button}, worldPoint=${worldPoint})"
}
