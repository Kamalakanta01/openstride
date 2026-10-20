package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class WorldLine2(
        val points: MutableList<WorldPoint2>
) {
    fun getPoints(): MutableList<WorldPoint2> = points

    override fun toString(): String = "WorldLine2(" + listOf(points=${points}) + ")"
}
