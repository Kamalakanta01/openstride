package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class SegmentFeature(
    val activityType: Long,
    val darkSparklineUrl: String,
    val score: Double,
    val id: Long,
    val darkThumbnailUrl: String,
    val name: String,
    val sparklineUrl: String,
    val thumbnailUrl: String,
    val verifiedStatus: String,
    val averageGrade: Float?,
    val distance: Double?,
    val athletesAllTime: Long?,
    val attemptsAllTime: Long?,
    val startXYLat: Float?,
    val startXYLng: Float?
) {
    override fun toString(): String = "SegmentFeature(activityType=${activityType}, darkSparklineUrl=${darkSparklineUrl}, score=${score}, id=${id}, darkThumbnailUrl=${darkThumbnailUrl}, name=${name}, sparklineUrl=${sparklineUrl}, thumbnailUrl=${thumbnailUrl}, verifiedStatus=${verifiedStatus}, averageGrade=${averageGrade}, distance=${distance}, athletesAllTime=${athletesAllTime}, attemptsAllTime=${attemptsAllTime}, startXYLat=${startXYLat}, startXYLng=${startXYLng})"
}
