package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class NightlyHeatmapConfig(
        val activityType: String,
        val opacityMultiplier: Float?
) {
    fun getActivityType(): String = activityType
    fun getOpacityMultiplier(): Float? = opacityMultiplier

    override fun toString(): String = "NightlyHeatmapConfig(" + listOf(activityType=${activityType}, opacityMultiplier=${opacityMultiplier}) + ")"
}
