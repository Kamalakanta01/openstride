package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class WorldBounds2(
    val northEastCorner: WorldPoint2,
    val southWestCorner: WorldPoint2
) {
    override fun toString(): String = "WorldBounds2(northEastCorner=${northEastCorner}, southWestCorner=${southWestCorner})"
}
