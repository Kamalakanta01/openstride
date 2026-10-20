package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class ContentClickEvent(
        val type: ClickEventType,
        val objects: MutableList<ContentClickEventObject>,
        val source: ClickSourceType,
        val button: MouseButtonCode,
        val worldPoint: WorldPoint3
) {
    fun getButton(): MouseButtonCode = button
    fun getObjects(): MutableList<ContentClickEventObject> = objects
    fun getSource(): ClickSourceType = source
    fun getType(): ClickEventType = type
    fun getWorldPoint(): WorldPoint3 = worldPoint

    override fun toString(): String = "ContentClickEvent(" + listOf(type=${type}, objects=${objects}, source=${source}, button=${button}, worldPoint=${worldPoint}) + ")"
}
