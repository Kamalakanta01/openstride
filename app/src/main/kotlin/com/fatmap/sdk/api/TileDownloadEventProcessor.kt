package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
interface TileDownloadEventProcessor {
    fun processDownloadCancelled(vquadIndex: QuadIndex, text: String, obj: MutableMap<String, String>): Unit
    fun processDownloadFailed(vquadIndex: QuadIndex, text: String, obj: MutableMap<String, String>, flag: Boolean, opt: Int?): Unit
    fun processDownloadStarted(vquadIndex: QuadIndex, text: String, obj: MutableMap<String, String>): Unit
    fun processResourceNotAvailable(vquadIndex: QuadIndex, text: String, obj: MutableMap<String, String>): Unit
    fun processResourceRetrieved(vquadIndex: QuadIndex, text: String, obj: MutableMap<String, String>): Unit
    fun processResourceRevalidatedWithoutDownloading(vquadIndex: QuadIndex, text: String, obj: MutableMap<String, String>): Unit
}
