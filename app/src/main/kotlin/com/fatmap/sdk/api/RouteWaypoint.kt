package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class RouteWaypoint(
        val identifier: String,
        val point: WorldPoint2,
        val title: String,
        val category: RouteWaypointCategory
) {
    fun getCategory(): RouteWaypointCategory = category
    fun getIdentifier(): String = identifier
    fun getPoint(): WorldPoint2 = point
    fun getTitle(): String = title

    override fun toString(): String = "RouteWaypoint(" + listOf(identifier=${identifier}, point=${point}, title=${title}, category=${category}) + ")"
}
