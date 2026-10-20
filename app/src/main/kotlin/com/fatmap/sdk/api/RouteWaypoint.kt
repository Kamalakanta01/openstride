package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class RouteWaypoint(
    val identifier: String,
    val point: WorldPoint2,
    val title: String,
    val category: RouteWaypointCategory
) {
    override fun toString(): String = "RouteWaypoint(identifier=${identifier}, point=${point}, title=${title}, category=${category})"
}
