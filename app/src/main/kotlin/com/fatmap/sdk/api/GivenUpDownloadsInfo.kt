package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class GivenUpDownloadsInfo(
    val currentSequenceNumber: Long,
    val newGivenUpDownloads: Long,
    val haveGivenUpDownloads: Boolean
) {
    override fun toString(): String = "GivenUpDownloadsInfo(currentSequenceNumber=${currentSequenceNumber}, newGivenUpDownloads=${newGivenUpDownloads}, haveGivenUpDownloads=${haveGivenUpDownloads})"
}
