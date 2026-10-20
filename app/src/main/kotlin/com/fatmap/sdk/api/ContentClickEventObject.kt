package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class ContentClickEventObject(
        val contentGroup: ContentGroup,
        val id: Long,
        val customTapId: String,
        val featureCategory: String,
        val layerName: String,
        val name: String,
        val position: String,
        val worldPoint: WorldPoint3,
        val contentSelectionHandle: ContentSelectionHandle
) {
    fun getContentGroup(): ContentGroup = contentGroup
    fun getContentSelectionHandle(): ContentSelectionHandle = contentSelectionHandle
    fun getCustomTapId(): String = customTapId
    fun getFeatureCategory(): String = featureCategory
    fun getId(): Long = id
    fun getLayerName(): String = layerName
    fun getName(): String = name
    fun getPosition(): String = position
    fun getWorldPoint(): WorldPoint3 = worldPoint

    override fun toString(): String = "ContentClickEventObject(" + listOf(contentGroup=${contentGroup}, id=${id}, customTapId=${customTapId}, featureCategory=${featureCategory}, layerName=${layerName}, name=${name}, position=${position}, worldPoint=${worldPoint}, contentSelectionHandle=${contentSelectionHandle}) + ")"
}
