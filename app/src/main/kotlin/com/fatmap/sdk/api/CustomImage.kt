package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class CustomImage(
    val format: ImageFormat,
    val height: Int,
    val width: Int,
    val pixelRatio: Float,
    val data: ByteArray
) {
    override fun toString(): String = "CustomImage(format=${format}, height=${height}, width=${width}, pixelRatio=${pixelRatio}, data=${data})"
}
