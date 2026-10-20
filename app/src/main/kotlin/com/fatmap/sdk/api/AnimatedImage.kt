package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class AnimatedImage(
        val frames: MutableList<CustomImage>,
        val fps: Float,
        val shouldLoop: Boolean
) {
    fun getFps(): Float = fps
    fun getFrames(): MutableList<CustomImage> = frames
    fun getShouldLoop(): Boolean = shouldLoop

    override fun toString(): String = "AnimatedImage(" + listOf(frames=${frames}, fps=${fps}, shouldLoop=${shouldLoop}) + ")"
}
