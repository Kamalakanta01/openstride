package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class StartPointFilterOptions(
        val difficulty: Long,
        val distance: Long,
        val elevation: Float,
        val routeType: Long,
        val surfaceType: Long
) {
    fun getDifficulty(): Long = difficulty
    fun getDistance(): Long = distance
    fun getElevation(): Float = elevation
    fun getRouteType(): Long = routeType
    fun getSurfaceType(): Long = surfaceType

    override fun toString(): String = "StartPointFilterOptions(" + listOf(difficulty=${difficulty}, distance=${distance}, elevation=${elevation}, routeType=${routeType}, surfaceType=${surfaceType}) + ")"
}
