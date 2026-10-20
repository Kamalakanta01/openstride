package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class StartPointFilterOptions(
    val difficulty: Long,
    val distance: Long,
    val elevation: Float,
    val routeType: Long,
    val surfaceType: Long
) {
    override fun toString(): String = "StartPointFilterOptions(difficulty=${difficulty}, distance=${distance}, elevation=${elevation}, routeType=${routeType}, surfaceType=${surfaceType})"
}
