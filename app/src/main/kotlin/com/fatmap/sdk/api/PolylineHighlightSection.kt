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
    fun getEndIndex(): Long = endIndex
    fun getIcon(): PolylineHighlightSectionIcon = icon
    fun getIconText(): String = iconText
    fun getIdentifier(): String = identifier
    fun getStartIndex(): Long = startIndex
    fun getType(): PolylineHighlightSectionType = type

    override fun toString(): String = "PolylineHighlightSection(" + listOf(iconText=${iconText}, endIndex=${endIndex}, startIndex=${startIndex}, type=${type}, icon=${icon}, identifier=${identifier}) + ")"
}
