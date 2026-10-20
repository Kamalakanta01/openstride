package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class GivenUpDownloadsInfo(
        val currentSequenceNumber: Long,
        val newGivenUpDownloads: Long,
        val haveGivenUpDownloads: Boolean
) {
    fun getCurrentSequenceNumber(): Long = currentSequenceNumber
    fun getHaveGivenUpDownloads(): Boolean = haveGivenUpDownloads
    fun getNewGivenUpDownloads(): Long = newGivenUpDownloads

    override fun toString(): String = "GivenUpDownloadsInfo(" + listOf(currentSequenceNumber=${currentSequenceNumber}, newGivenUpDownloads=${newGivenUpDownloads}, haveGivenUpDownloads=${haveGivenUpDownloads}) + ")"
}
