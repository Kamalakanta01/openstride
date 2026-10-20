package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class VisibleAreaBounds(
    val bottomLeftCorner: WorldPoint2,
    val bottomRightCorner: WorldPoint2,
    val topLeftCorner: WorldPoint2,
    val topRightCorner: WorldPoint2
) {
    override fun toString(): String = "VisibleAreaBounds(bottomLeftCorner=${bottomLeftCorner}, bottomRightCorner=${bottomRightCorner}, topLeftCorner=${topLeftCorner}, topRightCorner=${topRightCorner})"
}
