package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class PersonalHeatmapConfig(
        val filterEnd: String,
        val filterStart: String,
        val filterType: String,
        val colorScheme: HeatmapColorScheme,
        val opacityMultiplier: Float?,
        val includeActivitiesVisibleOnlyToMe: Boolean,
        val includeActivitiesVisibleToEveryone: Boolean,
        val includeActivitiesVisibleToFollowersOnly: Boolean,
        val includeCommutes: Boolean,
        val respectPrivacyZones: Boolean
) {
    fun getColorScheme(): HeatmapColorScheme = colorScheme
    fun getFilterEnd(): String = filterEnd
    fun getFilterStart(): String = filterStart
    fun getFilterType(): String = filterType
    fun getIncludeActivitiesVisibleOnlyToMe(): Boolean = includeActivitiesVisibleOnlyToMe
    fun getIncludeActivitiesVisibleToEveryone(): Boolean = includeActivitiesVisibleToEveryone
    fun getIncludeActivitiesVisibleToFollowersOnly(): Boolean = includeActivitiesVisibleToFollowersOnly
    fun getIncludeCommutes(): Boolean = includeCommutes
    fun getOpacityMultiplier(): Float? = opacityMultiplier
    fun getRespectPrivacyZones(): Boolean = respectPrivacyZones

    override fun toString(): String = "PersonalHeatmapConfig(" + listOf(filterEnd=${filterEnd}, filterStart=${filterStart}, filterType=${filterType}, colorScheme=${colorScheme}, opacityMultiplier=${opacityMultiplier}, includeActivitiesVisibleOnlyToMe=${includeActivitiesVisibleOnlyToMe}, includeActivitiesVisibleToEveryone=${includeActivitiesVisibleToEveryone}, includeActivitiesVisibleToFollowersOnly=${includeActivitiesVisibleToFollowersOnly}, includeCommutes=${includeCommutes}, respectPrivacyZones=${respectPrivacyZones}) + ")"
}
