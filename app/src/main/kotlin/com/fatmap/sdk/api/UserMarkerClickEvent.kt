package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class UserMarkerClickEvent(
        val type: ClickEventType,
        val source: ClickSourceType,
        val button: MouseButtonCode
) {
    fun getButton(): MouseButtonCode = button
    fun getSource(): ClickSourceType = source
    fun getType(): ClickEventType = type

    override fun toString(): String = "UserMarkerClickEvent(" + listOf(type=${type}, source=${source}, button=${button}) + ")"
}
