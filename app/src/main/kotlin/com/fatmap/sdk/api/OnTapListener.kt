package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
interface OnTapListener {
    fun onContentTap(vcontentClickEvent: ContentClickEvent): Unit
    fun onLongContentTap(vcontentClickEvent: ContentClickEvent): Unit
    fun onLongMapTap(vmapClickEvent: MapClickEvent): Unit
    fun onLongUserMarkerTap(vuserMarkerClickEvent: UserMarkerClickEvent): Unit
    fun onMapTap(vmapClickEvent: MapClickEvent): Unit
    fun onUserMarkerTap(vuserMarkerClickEvent: UserMarkerClickEvent): Unit
}
