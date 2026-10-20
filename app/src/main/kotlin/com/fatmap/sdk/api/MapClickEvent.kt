package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class MapClickEvent(
        val type: ClickEventType,
        val worldPoint: WorldPoint3,
        val source: ClickSourceType,
        val button: MouseButtonCode
) {
    fun getButton(): MouseButtonCode = button
    fun getSource(): ClickSourceType = source
    fun getType(): ClickEventType = type
    fun getWorldPoint(): WorldPoint3 = worldPoint

    override fun toString(): String = "MapClickEvent(" + listOf(type=${type}, worldPoint=${worldPoint}, source=${source}, button=${button}) + ")"
}
