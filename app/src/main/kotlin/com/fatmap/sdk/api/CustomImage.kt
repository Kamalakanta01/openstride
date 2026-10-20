package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class CustomImage(
        val format: ImageFormat,
        val height: Int,
        val width: Int,
        val pixelRatio: Float,
        val data: byte[]
) {
    fun getData(): byte[] = data
    fun getFormat(): ImageFormat = format
    fun getHeight(): Int = height
    fun getPixelRatio(): Float = pixelRatio
    fun getWidth(): Int = width

    override fun toString(): String = "CustomImage(" + listOf(format=${format}, height=${height}, width=${width}, pixelRatio=${pixelRatio}, data=${data}) + ")"
}
