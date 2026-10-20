package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class MouseMoveEvent(
    val x: Float,
    val y: Float,
    val objects: MutableList<ContentClickEventObject>
) {
    override fun toString(): String = "MouseMoveEvent(x=${x}, y=${y}, objects=${objects})"
}
