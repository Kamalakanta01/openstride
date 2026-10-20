package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class UserMarkerClickEvent(
    val type: ClickEventType,
    val source: ClickSourceType,
    val button: MouseButtonCode
) {
    override fun toString(): String = "UserMarkerClickEvent(type=${type}, source=${source}, button=${button})"
}
