package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class MouseMoveEvent(
        val x: Float,
        val y: Float,
        val objects: MutableList<ContentClickEventObject>
) {
    fun getObjects(): MutableList<ContentClickEventObject> = objects
    fun getX(): Float = x
    fun getY(): Float = y

    override fun toString(): String = "MouseMoveEvent(" + listOf(x=${x}, y=${y}, objects=${objects}) + ")"
}
