package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class PersonalHeatmapTestAccount(
        val accessToken: String,
        val stravaAthleteId: String,
        val tileEndpointHostName: String
) {
    fun getAccessToken(): String = accessToken
    fun getStravaAthleteId(): String = stravaAthleteId
    fun getTileEndpointHostName(): String = tileEndpointHostName

    override fun toString(): String = "PersonalHeatmapTestAccount(" + listOf(accessToken=${accessToken}, stravaAthleteId=${stravaAthleteId}, tileEndpointHostName=${tileEndpointHostName}) + ")"
}
