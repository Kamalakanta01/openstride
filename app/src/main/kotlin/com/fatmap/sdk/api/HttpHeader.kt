package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class HttpHeader(
        val name: String,
        val value: String
) {
    fun getName(): String = name
    fun getValue(): String = value

    override fun toString(): String = "HttpHeader(" + listOf(name=${name}, value=${value}) + ")"
}
