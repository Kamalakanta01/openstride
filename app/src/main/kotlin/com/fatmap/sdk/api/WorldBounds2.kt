package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class WorldBounds2(
        val northEastCorner: WorldPoint2,
        val southWestCorner: WorldPoint2
) {
    fun getNorthEastCorner(): WorldPoint2 = northEastCorner
    fun getSouthWestCorner(): WorldPoint2 = southWestCorner

    override fun toString(): String = "WorldBounds2(" + listOf(northEastCorner=${northEastCorner}, southWestCorner=${southWestCorner}) + ")"
}
