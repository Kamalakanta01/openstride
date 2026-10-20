package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
interface CameraInteractionListener {
    fun onInteractionEnd(): Unit
    fun onInteractionStart(): Unit
    fun onTiltInteractionPrevented(): Unit
}
