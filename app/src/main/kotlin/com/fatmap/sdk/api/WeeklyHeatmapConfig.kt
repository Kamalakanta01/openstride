package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class WeeklyHeatmapConfig(
    val activityType: String,
    val colorScheme: HeatmapColorScheme,
    val opacityMultiplier: Float?
) {
    override fun toString(): String = "WeeklyHeatmapConfig(activityType=${activityType}, colorScheme=${colorScheme}, opacityMultiplier=${opacityMultiplier})"
}
