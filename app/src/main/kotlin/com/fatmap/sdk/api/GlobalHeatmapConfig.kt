package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class GlobalHeatmapConfig(
        val activityType: String,
        val colorScheme: HeatmapColorScheme,
        val opacityMultiplier: Float?
) {
    fun getActivityType(): String = activityType
    fun getColorScheme(): HeatmapColorScheme = colorScheme
    fun getOpacityMultiplier(): Float? = opacityMultiplier

    override fun toString(): String = "GlobalHeatmapConfig(" + listOf(activityType=${activityType}, colorScheme=${colorScheme}, opacityMultiplier=${opacityMultiplier}) + ")"
}
