package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class PolylineHighlightSection(
    val iconText: String,
    val endIndex: Long,
    val startIndex: Long,
    val type: PolylineHighlightSectionType,
    val icon: PolylineHighlightSectionIcon,
    val identifier: String
) {
    override fun toString(): String = "PolylineHighlightSection(iconText=${iconText}, endIndex=${endIndex}, startIndex=${startIndex}, type=${type}, icon=${icon}, identifier=${identifier})"
}
