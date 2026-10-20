package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class CameraMargins(
        val bottom: Float,
        val left: Float,
        val right: Float,
        val top: Float
) {
    fun getBottom(): Float = bottom
    fun getLeft(): Float = left
    fun getRight(): Float = right
    fun getTop(): Float = top

    override fun toString(): String = "CameraMargins(" + listOf(bottom=${bottom}, left=${left}, right=${right}, top=${top}) + ")"
}
