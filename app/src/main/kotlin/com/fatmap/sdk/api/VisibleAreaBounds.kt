package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class VisibleAreaBounds(
        val bottomLeftCorner: WorldPoint2,
        val bottomRightCorner: WorldPoint2,
        val topLeftCorner: WorldPoint2,
        val topRightCorner: WorldPoint2
) {
    fun getBottomLeftCorner(): WorldPoint2 = bottomLeftCorner
    fun getBottomRightCorner(): WorldPoint2 = bottomRightCorner
    fun getTopLeftCorner(): WorldPoint2 = topLeftCorner
    fun getTopRightCorner(): WorldPoint2 = topRightCorner

    override fun toString(): String = "VisibleAreaBounds(" + listOf(bottomLeftCorner=${bottomLeftCorner}, bottomRightCorner=${bottomRightCorner}, topLeftCorner=${topLeftCorner}, topRightCorner=${topRightCorner}) + ")"
}
