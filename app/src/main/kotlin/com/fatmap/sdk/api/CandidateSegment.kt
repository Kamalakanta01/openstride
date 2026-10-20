package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class CandidateSegment(
        val identifier: String,
        val line: WorldLine2,
        val isStarred: Boolean,
        val title: String
) {
    fun getIdentifier(): String = identifier
    fun getIsStarred(): Boolean = isStarred
    fun getLine(): WorldLine2 = line
    fun getTitle(): String = title

    override fun toString(): String = "CandidateSegment(" + listOf(identifier=${identifier}, line=${line}, isStarred=${isStarred}, title=${title}) + ")"
}
