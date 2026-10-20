package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
interface AuthenticationErrorListener {
    fun onAuthenticationError(text: String, items: MutableList<HttpHeader>, text1: String): Unit
}
