package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class AnimatedImage(
    val frames: MutableList<CustomImage>,
    val fps: Float,
    val shouldLoop: Boolean
) {
    override fun toString(): String = "AnimatedImage(frames=${frames}, fps=${fps}, shouldLoop=${shouldLoop})"
}
