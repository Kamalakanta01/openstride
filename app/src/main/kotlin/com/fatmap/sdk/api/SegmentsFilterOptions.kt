package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class SegmentsFilterOptions(
    val activityTypes: String,
    val distanceMax: String,
    val distanceMin: String,
    val elevationFilter: String,
    val surfaceTypes: String,
    val creator: Boolean?,
    val overall: Boolean?,
    val starred: Boolean?,
    val top10: Boolean?,
    val verified: Boolean?
) {
    override fun toString(): String = "SegmentsFilterOptions(activityTypes=${activityTypes}, distanceMax=${distanceMax}, distanceMin=${distanceMin}, elevationFilter=${elevationFilter}, surfaceTypes=${surfaceTypes}, creator=${creator}, overall=${overall}, starred=${starred}, top10=${top10}, verified=${verified})"
}
