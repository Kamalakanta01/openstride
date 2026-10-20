package dev.openstride.analysis

import dev.openstride.model.Activity
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Training load, fitness and freshness.
 *
 * This is the arithmetic behind the numbers the original app shows on its
 * "Fitness" and "Training" screens. The original computes them on the
 * server, from data it has about every athlete; here they are computed on the
 * device, from the activities that are actually stored.
 *
 * ## The model
 *
 * A day's *load* is a single number summarising the training done that day.
 * The original uses the Training Stress Score, which multiplies duration by
 * the square of how hard the activity was relative to the athlete's
 * threshold. Without a power meter or a heart-rate strap there is no
 * threshold to compare against, so the intensity is measured against the
 * athlete's own recent baseline instead: the median pace of the last thirty
 * days. Squaring it, as the original does, is what makes a short hard effort
 * count for more than a long easy one.
 *
 * Two exponentially weighted averages are then kept over the load:
 *
 *  - *Fitness* (CTL) rises slowly, with a 42-day time constant. It is the
 *    athlete's capacity.
 *  - *Fatigue* (ATL) rises quickly, with a 7-day time constant. It is the
 *    cost of recent work.
 *  - *Form* (TSB) is fitness minus fatigue: positive when rested, negative
 *    when the recent work has not been paid off yet.
 *
 * The time constants are the ones the original uses, so the shape of the
 * chart is comparable.
 */
object Fitness {
    /** The time constant of the fitness average, in days. */
    const val FITNESS_DAYS = 42.0

    /** The time constant of the fatigue average, in days. */
    const val FATIGUE_DAYS = 7.0

    /** One day's training load. */
    data class DailyLoad(
        val date: LocalDate,
        val load: Double,
        val durationSeconds: Int,
        val distanceMeters: Double,
        val elevationGainMeters: Double,
        val activityCount: Int,
    )

    /** A point on the fitness and freshness chart. */
    data class Point(
        val date: LocalDate,
        val fitness: Double,
        val fatigue: Double,
        val form: Double,
        val load: Double,
    )

    /** A week's total load. */
    data class Week(
        val weekStart: LocalDate,
        val load: Double,
        val durationSeconds: Int,
        val distanceMeters: Double,
        val elevationGainMeters: Double,
        val activityCount: Int,
    )

    /**
     * The load of each day that has training on it.
     *
     * @param baselineSpeed the athlete's median speed over the last thirty
     *   days, in metres per second. The intensity of an activity is measured
     *   against it.
     */
    fun dailyLoads(
        activities: List<Activity>,
        baselineSpeed: Double = medianSpeed(activities),
    ): List<DailyLoad> {
        val byDate = activities.groupBy { it.startedAt.atZone(ZoneId.systemDefault()).toLocalDate() }
        return byDate.entries
            .sortedBy { it.key }
            .map { (date, ofDay) ->
                var load = 0.0
                var seconds = 0
                var distance = 0.0
                var gain = 0.0
                for (activity in ofDay) {
                    load += loadOf(activity, baselineSpeed)
                    seconds += activity.elapsedSeconds
                    distance += activity.distanceMeters
                    gain += activity.elevationGainMeters
                }
                DailyLoad(date, load, seconds, distance, gain, ofDay.size)
            }
    }

    /**
     * The load of a single activity.
     *
     * The intensity is the athlete's baseline speed divided by the activity's
     * own speed, clamped so that a long slow walk does not read as negative
     * effort and a sprint does not read as absurd.
     */
    fun loadOf(
        activity: Activity,
        baselineSpeed: Double,
    ): Double {
        val hours = activity.elapsedSeconds / 3600.0
        if (hours <= 0) return 0.0
        val speed = activity.distanceMeters / (activity.elapsedSeconds.coerceAtLeast(1)).toDouble()
        val intensity =
            if (speed <= 0.0 || baselineSpeed <= 0.0) {
                1.0
            } else {
                (baselineSpeed / speed).coerceIn(0.4, 2.2)
            }
        // Elevation is work the athlete does not get credit for in speed.
        val climbFactor = 1.0 + (activity.elevationGainMeters / 1000.0) * 0.08
        return hours * intensity.pow(2) * 100.0 * climbFactor
    }

    /**
     * The fitness and freshness series.
     *
     * @param days how many days of history to produce, counting back from
     *   the last day with training on it.
     * @param endDate the last day of the series. It defaults to the last day
     *   with training on it, but the chart is more useful when it carries on
     *   for a while afterwards: the recovery is the interesting part.
     */
    fun series(
        loads: List<DailyLoad>,
        days: Int = 90,
        endDate: LocalDate? = null,
    ): List<Point> {
        if (loads.isEmpty()) return emptyList()
        val byDate = loads.associateBy { it.date }
        val last = endDate ?: loads.last().date
        val first = last.minusDays((days - 1).toLong())

        // Seed the averages with the load from before the window, so the
        // first days of the chart are not artificially low.
        val historyBefore = loads.filter { it.date < first }.sumOf { it.load }
        var fitness = historyBefore / FITNESS_DAYS
        var fatigue = historyBefore / FATIGUE_DAYS

        val points = mutableListOf<Point>()
        var date = first
        while (!date.isAfter(last)) {
            val load = byDate[date]?.load ?: 0.0
            fitness += (load - fitness) / FITNESS_DAYS
            fatigue += (load - fatigue) / FATIGUE_DAYS
            points += Point(date, fitness, fatigue, fitness - fatigue, load)
            date = date.plusDays(1)
        }
        return points
    }

    /** The Monday that starts the week the given date falls in. */
    fun weekStartOf(date: LocalDate): LocalDate =
        date
            .with(java.time.DayOfWeek.MONDAY)
            .with(
                java.time.temporal.TemporalAdjusters
                    .previousOrSame(java.time.DayOfWeek.MONDAY),
            )

    /** The load of each week, oldest first. */
    fun weeks(loads: List<DailyLoad>): List<Week> =
        loads
            .groupBy {
                it.date
                    .with(
                        java.time.DayOfWeek.MONDAY,
                    ).with(
                        java.time.temporal.TemporalAdjusters
                            .previousOrSame(java.time.DayOfWeek.MONDAY),
                    )
            }.entries
            .sortedBy { it.key }
            .map { (week, ofWeek) ->
                Week(
                    weekStart = week,
                    load = ofWeek.sumOf { it.load },
                    durationSeconds = ofWeek.sumOf { it.durationSeconds },
                    distanceMeters = ofWeek.sumOf { it.distanceMeters },
                    elevationGainMeters = ofWeek.sumOf { it.elevationGainMeters },
                    activityCount = ofWeek.sumOf { it.activityCount },
                )
            }

    /** The median speed of the activities, in metres per second. */
    fun medianSpeed(activities: List<Activity>): Double {
        val speeds =
            activities
                .filter { it.elapsedSeconds > 0 && it.distanceMeters > 0 }
                .map { it.distanceMeters / it.elapsedSeconds }
                .sorted()
        if (speeds.isEmpty()) return 0.0
        val middle = speeds.size / 2
        return if (speeds.size % 2 == 1) {
            speeds[middle]
        } else {
            (speeds[middle - 1] + speeds[middle]) / 2
        }
    }

    /** The number of consecutive days, ending today, with an activity. */
    fun currentStreak(activities: List<Activity>): Int {
        if (activities.isEmpty()) return 0
        val days =
            activities
                .map { it.startedAt.atZone(ZoneId.systemDefault()).toLocalDate() }
                .toSet()
        var streak = 0
        var date = LocalDate.now()
        // Today not having an activity yet does not break a streak that ran
        // up to yesterday.
        if (date !in days) {
            date = date.minusDays(1)
        }
        while (date in days) {
            streak++
            date = date.minusDays(1)
        }
        return streak
    }

    /** The number of consecutive weeks, ending with this one, with an activity. */
    fun currentWeekStreak(activities: List<Activity>): Int {
        if (activities.isEmpty()) return 0
        val weeks =
            activities
                .map {
                    it.startedAt
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .with(java.time.DayOfWeek.MONDAY)
                        .with(
                            java.time.temporal.TemporalAdjusters
                                .previousOrSame(java.time.DayOfWeek.MONDAY),
                        )
                }.toSet()
        var streak = 0
        var week = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
        if (week !in weeks) {
            week = week.minusWeeks(1)
        }
        while (week in weeks) {
            streak++
            week = week.minusWeeks(1)
        }
        return streak
    }

    /**
     * A consistency score from zero to a hundred.
     *
     * It rewards training often and regularly, and punishes both a long gap
     * and a sudden spike. The original calls this a "consistency" or
     * "training consistency" figure and keeps it to itself; this is our own.
     */
    fun consistency(loads: List<DailyLoad>): Int {
        if (loads.isEmpty()) return 0
        val last = loads.last().date
        val window = (0 until 90).map { last.minusDays(it.toLong()) }
        val byDate = loads.associateBy { it.date }
        val total = window.sumOf { byDate[it]?.load ?: 0.0 }
        if (total <= 0) return 0

        val activeDays = window.count { (byDate[it]?.load ?: 0.0) > 0 }
        val regularity =
            (1.0 - stdev(window.map { byDate[it]?.load ?: 0.0 }) / (total / window.size).coerceAtLeast(1.0))
                .coerceIn(0.0, 1.0)
        val frequency = activeDays / 90.0
        return (100 * (0.6 * frequency + 0.4 * regularity)).toInt().coerceIn(0, 100)
    }

    /** The standard deviation of a list. */
    private fun stdev(values: List<Double>): Double {
        if (values.size < 2) return 0.0
        val mean = values.average()
        return sqrt(values.sumOf { (it - mean).pow(2) } / values.size)
    }
}

/**
 * The zones an activity is split into.
 *
 * The original has heart-rate zones, power zones and speed zones, each
 * configured by the athlete. Without a strap or a meter, speed is the only
 * one that can be measured, so the zones here are relative to the athlete's
 * own best recent speed.
 */
object Zones {
    /** The number of zones, as the original uses. */
    const val COUNT = 5

    /** One zone, with the bounds it covers. */
    data class Zone(
        val index: Int,
        val name: String,
        val fromSpeed: Double,
        val toSpeed: Double,
    ) {
        fun contains(speed: Double): Boolean = speed >= fromSpeed && speed < toSpeed
    }

    /** The time spent in each zone. */
    data class Distribution(
        val zones: List<Zone>,
        val secondsInZone: List<Int>,
    ) {
        val totalSeconds: Int get() = secondsInZone.sum()
    }

    /**
     * The zones for a maximum speed.
     *
     * The bounds follow the original's shape: the slowest zone is a wide
     * band at the bottom and the fastest is narrow at the top.
     */
    fun zonesFor(maxSpeedMetersPerSecond: Double): List<Zone> {
        if (maxSpeedMetersPerSecond <= 0) return emptyList()
        val bounds = listOf(0.0, 0.45, 0.62, 0.76, 0.88, 1.0)
        val names = listOf("Very easy", "Easy", "Moderate", "Hard", "Very hard")
        return bounds.zipWithNext().mapIndexed { index, (from, to) ->
            Zone(
                index = index,
                name = names.getOrElse(index) { "Zone ${index + 1}" },
                fromSpeed = maxSpeedMetersPerSecond * from,
                toSpeed = maxSpeedMetersPerSecond * to,
            )
        }
    }

    /**
     * How long the athlete spent in each zone.
     *
     * @param samples the activity's speed samples, in metres per second,
     *   each with the time it was measured at.
     */
    fun distribution(
        samples: List<Pair<Double, Int>>,
        maxSpeedMetersPerSecond: Double,
    ): Distribution {
        val zones = zonesFor(maxSpeedMetersPerSecond)
        if (zones.isEmpty()) return Distribution(emptyList(), emptyList())
        val seconds = MutableList(zones.size) { 0 }
        var previousTime: Int? = null
        for ((speed, time) in samples) {
            val previous = previousTime
            previousTime = time
            if (previous == null) continue
            val gap = (time - previous).coerceAtLeast(0)
            if (gap > 60) continue
            val zone = zones.indexOfFirst { it.contains(speed) }
            if (zone >= 0) {
                seconds[zone] += gap
            }
        }
        return Distribution(zones, seconds)
    }
}
