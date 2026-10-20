package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class CandidateSegment(
    val identifier: String,
    val line: WorldLine2,
    @get:JvmName("getIsStarred") val isStarred: Boolean,
    val title: String
) {
    override fun toString(): String = "CandidateSegment(identifier=${identifier}, line=${line}, isStarred=${isStarred}, title=${title})"
}
