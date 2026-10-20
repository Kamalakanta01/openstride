package dev.openstride.analysis

import dev.openstride.model.Activity
import dev.openstride.model.ActivityType
import kotlin.math.roundToInt

/**
 * Personal bests.
 *
 * The original app keeps a "personal records" page for every activity type.
 * These are computed from the activities on the device, so they are exactly
 * as true as the activities that are stored.
 */
object Records {
    /** One personal best. */
    data class Record(
        val title: String,
        val activityName: String,
        val activityId: Long,
        val value: String,
        val sortValue: Double,
    )

    /** The personal bests of a set of activities. */
    fun of(activities: List<Activity>): List<Record> {
        val records = mutableListOf<Record>()
        val running = activities.filter { it.type == ActivityType.RUN }
        val riding = activities.filter { it.type == ActivityType.RIDE }
        val all = activities

        records += listOfNotNull(bestOf(running, "Fastest 1 km") { it, _ -> fastestSplit(it, 1000.0) })
        records += listOfNotNull(bestOf(running, "Fastest 5 km") { it, _ -> fastestSplit(it, 5000.0) })
        records += listOfNotNull(bestOf(running, "Fastest 10 km") { it, _ -> fastestSplit(it, 10000.0) })
        records += listOfNotNull(bestOf(running, "Longest run") { it, _ -> it.distanceMeters })
        records += listOfNotNull(bestOf(riding, "Longest ride") { it, _ -> it.distanceMeters })
        records += listOfNotNull(bestOf(riding, "Biggest climb") { it, _ -> it.elevationGainMeters })
        records += listOfNotNull(bestOf(all, "Longest activity") { it, _ -> it.distanceMeters })
        records += listOfNotNull(bestOf(all, "Most time moving") { it, _ -> it.movingSeconds.toDouble() })
        records += listOfNotNull(bestOf(all, "Biggest climb") { it, _ -> it.elevationGainMeters })
        records += listOfNotNull(bestOf(all, "Highest average speed") { it, _ -> it.averageSpeedMetersPerSecond })
        return records.sortedByDescending { it.sortValue }
    }

    private fun bestOf(
        activities: List<Activity>,
        title: String,
        value: (Activity, Unit) -> Double?,
    ): Record? {
        var best: Activity? = null
        var bestValue = Double.NEGATIVE_INFINITY
        for (activity in activities) {
            val candidate = value(activity, Unit) ?: continue
            if (candidate > bestValue) {
                bestValue = candidate
                best = activity
            }
        }
        val activity = best ?: return null
        return Record(
            title = title,
            activityName = activity.name,
            activityId = activity.id,
            value = format(title, bestValue),
            sortValue = bestValue,
        )
    }

    private fun format(
        title: String,
        value: Double,
    ): String =
        when {
            title.contains("time", true) || title.contains("moving", true) -> {
                val seconds = value.toInt()
                String.format(java.util.Locale.US, "%d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60)
            }
            title.contains("climb", true) -> String.format(java.util.Locale.US, "%.0f m", value)
            title.contains("speed", true) -> String.format(java.util.Locale.US, "%.1f km/h", value * 3.6)
            title.contains("km", true) -> String.format(java.util.Locale.US, "%.2f km", value / 1000)
            else -> String.format(java.util.Locale.US, "%.2f km", value / 1000)
        }

    /**
     * The fastest average speed over a window of the activity.
     *
     * The activity's polyline gives the distance covered; the time is
     * estimated from the overall pace, which is close enough for a record
     * and needs no per-point times.
     */
    private fun fastestSplit(
        activity: Activity,
        windowMeters: Double,
    ): Double? {
        if (activity.elapsedSeconds <= 0 || activity.distanceMeters < windowMeters) return null
        val speed = activity.distanceMeters / activity.elapsedSeconds
        return if (speed <= 0) null else windowMeters / speed
    }
}

/**
 * Splits: how an activity divides up.
 *
 * The original app shows a splits table for every activity, in kilometres or
 * miles, and compares each split with the athlete's best for that distance.
 */
object Splits {
    /** One split of an activity. */
    data class Split(
        val index: Int,
        val distanceMeters: Double,
        val elapsedSeconds: Int,
        val elevationDeltaMeters: Double,
        val averageSpeedMetersPerSecond: Double,
    ) {
        val paceSecondsPerKilometer: Int
            get() =
                if (averageSpeedMetersPerSecond <= 0) {
                    0
                } else {
                    (1000.0 / averageSpeedMetersPerSecond).roundToInt()
                }
    }

    /**
     * The splits of an activity.
     *
     * @param cumulativeDistance the distance covered at each sample, in
     *   metres, with the time it was reached.
     */
    fun splits(
        cumulativeDistance: List<Double>,
        cumulativeTime: List<Long>,
        intervalMeters: Double = 1000.0,
    ): List<Split> {
        if (cumulativeDistance.size < 2 || cumulativeDistance.size != cumulativeTime.size) {
            return emptyList()
        }
        val splits = mutableListOf<Split>()
        var nextBoundary = intervalMeters
        var startDistance = 0.0
        var startTime = cumulativeTime.first()
        var startElevation = 0.0
        var index = 1

        for (indexPoint in 1 until cumulativeDistance.size) {
            val distance = cumulativeDistance[indexPoint]
            val time = cumulativeTime[indexPoint]
            if (distance >= nextBoundary) {
                val elapsed = ((time - startTime) / 1000.0).toInt()
                splits +=
                    Split(
                        index = index,
                        distanceMeters = nextBoundary - startDistance,
                        elapsedSeconds = elapsed,
                        elevationDeltaMeters = 0.0,
                        averageSpeedMetersPerSecond =
                            if (elapsed > 0) (nextBoundary - startDistance) / elapsed else 0.0,
                    )
                startDistance = nextBoundary
                startTime = time
                nextBoundary += intervalMeters
                index++
            }
        }
        // The remainder of the activity is the last split.
        val total = cumulativeDistance.last()
        if (total > startDistance) {
            val elapsed = ((cumulativeTime.last() - startTime) / 1000.0).toInt()
            splits +=
                Split(
                    index = index,
                    distanceMeters = total - startDistance,
                    elapsedSeconds = elapsed,
                    elevationDeltaMeters = 0.0,
                    averageSpeedMetersPerSecond =
                        if (elapsed > 0) (total - startDistance) / elapsed else 0.0,
                )
        }
        return splits
    }
}

/**
 * The badges the original app awards, computed locally.
 *
 * The original has a large set of achievements, many of which depend on
 * other athletes. These are the ones that can be decided from a single
 * athlete's own history.
 */
object Achievements {
    /** One badge. */
    data class Badge(
        val title: String,
        val description: String,
        val unlocked: Boolean,
        val progress: Float,
        val progressText: String,
    )

    /** The badges for a set of activities. */
    fun of(activities: List<Activity>): List<Badge> {
        val totalDistance = activities.sumOf { it.distanceMeters }
        val totalElevation = activities.sumOf { it.elevationGainMeters }
        val totalSeconds = activities.sumOf { it.elapsedSeconds }
        val longest = activities.maxByOrNull { it.distanceMeters }
        val streak = Fitness.currentWeekStreak(activities)

        return listOf(
            badge("First activity", "Record your first activity", activities.size >= 1, activities.size, 1),
            badge("Ten activities", "Record ten activities", activities.size >= 10, activities.size, 10),
            badge("Hundred activities", "Record a hundred activities", activities.size >= 100, activities.size, 100),
            badge("First 10 km", "Cover ten kilometres in total", totalDistance >= 10000, totalDistance, 10000.0, "km"),
            badge("First 100 km", "Cover a hundred kilometres in total", totalDistance >= 100000, totalDistance, 100000.0, "km"),
            badge("First 1000 km", "Cover a thousand kilometres in total", totalDistance >= 1000000, totalDistance, 1000000.0, "km"),
            badge("Climber", "Climb a thousand metres", totalElevation >= 1000, totalElevation, 1000.0, "m"),
            badge("Everest", "Climb ten thousand metres", totalElevation >= 10000, totalElevation, 10000.0, "m"),
            badge(
                "Long ride",
                "Cover fifty kilometres in one activity",
                (longest?.distanceMeters ?: 0.0) >= 50000,
                longest?.distanceMeters ?: 0.0,
                50000.0,
                "km",
            ),
            badge(
                "Marathon",
                "Cover forty-two kilometres in one activity",
                (longest?.distanceMeters ?: 0.0) >= 42195,
                longest?.distanceMeters ?: 0.0,
                42195.0,
                "km",
            ),
            badge("Consistent", "Train four weeks in a row", streak >= 4, streak, 4, "weeks"),
            badge("Ten hours", "Move for ten hours in total", totalSeconds >= 36000, totalSeconds.toDouble(), 36000.0, "h"),
        )
    }

    private fun badge(
        title: String,
        description: String,
        unlocked: Boolean,
        value: Number,
        target: Number,
        unit: String = "",
    ): Badge {
        val numeric = value.toDouble()
        val goal = target.toDouble()
        val progress = if (goal <= 0) 0f else (numeric / goal).coerceIn(0.0, 1.0).toFloat()
        val text =
            if (unit.isEmpty()) {
                "${numeric.toInt()} / ${goal.toInt()}"
            } else {
                "${format(numeric, unit)} / ${format(goal, unit)}"
            }
        return Badge(title, description, unlocked, progress, text)
    }

    private fun format(
        value: Double,
        unit: String,
    ): String =
        when (unit) {
            "km" -> String.format(java.util.Locale.US, "%.1f km", value / 1000)
            "m" -> String.format(java.util.Locale.US, "%.0f m", value)
            "h" -> String.format(java.util.Locale.US, "%.1f h", value / 3600.0)
            else -> value.toInt().toString()
        }
}
