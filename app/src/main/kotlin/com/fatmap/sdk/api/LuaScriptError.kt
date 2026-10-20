package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class LuaScriptError(
        val what: String
) {
    fun getWhat(): String = what

    override fun toString(): String = "LuaScriptError(" + listOf(what=${what}) + ")"
}
