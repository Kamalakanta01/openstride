package dev.openstride.io

import dev.openstride.local.ActivityEntity
import dev.openstride.recording.TrackPoint
import java.io.File
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Writing an activity out.
 *
 * The original app exports GPX and TCX for the athlete to keep, and lets
 * other services read a file. There is no account and no server here, so an
 * export is a file the athlete owns: the activity, its track and its
 * numbers, in a format other software can read.
 */
object Export {
    /** The format an activity is written in. */
    enum class Format(
        val extension: String,
    ) {
        GPX("gpx"),
        JSON("json"),
    }

    /**
     * Writes an activity as GPX 1.1.
     *
     * GPX is the format every mapping tool reads, and the one the original
     * app exports too.
     */
    fun gpx(
        activity: ActivityEntity,
        track: List<TrackPoint>,
    ): String {
        val out = StringBuilder()
        val time = DateTimeFormatter.ISO_INSTANT
        out.append("""<?xml version="1.0" encoding="UTF-8"?>""").append('\n')
        out
            .append(
                """<gpx version="1.1" creator="OpenStride" xmlns="http://www.topografix.com/GPX/1/1">""",
            ).append('\n')
        out.append("  <metadata>\n")
        out.append("    <name>${escape(activity.name)}</name>\n")
        out.append("    <time>${time.format(activity.startedAt)}</time>\n")
        out.append("  </metadata>\n")
        out.append("  <trk>\n")
        out.append("    <name>${escape(activity.name)}</name>\n")
        out.append("    <type>${escape(activity.type)}</type>\n")
        out.append("    <trkseg>\n")
        for (point in track) {
            out.append(
                "      <trkpt lat=\"${point.latitude}\" lon=\"${point.longitude}\">" +
                    if (point.altitudeMeters != 0.0) {
                        "<ele>${point.altitudeMeters}</ele>"
                    } else {
                        ""
                    } +
                    "</trkpt>\n",
            )
        }
        out.append("    </trkseg>\n")
        out.append("  </trk>\n")
        out.append("</gpx>\n")
        return out.toString()
    }

    /**
     * Writes an activity as JSON.
     *
     * The backup format: everything the app knows about an activity, in a
     * form that can be read back.
     */
    fun json(
        activity: ActivityEntity,
        track: List<TrackPoint>,
    ): String {
        val out = StringBuilder()
        out.append("{\n")
        out.append("  \"name\": \"${escapeJson(activity.name)}\",\n")
        out.append("  \"type\": \"${escapeJson(activity.type)}\",\n")
        out.append("  \"startedAt\": \"${activity.startedAt}\",\n")
        out.append("  \"elapsedSeconds\": ${activity.elapsedSeconds},\n")
        out.append("  \"movingSeconds\": ${activity.movingSeconds},\n")
        out.append("  \"distanceMeters\": ${activity.distanceMeters},\n")
        out.append("  \"elevationGainMeters\": ${activity.elevationGainMeters},\n")
        out.append("  \"elevationLossMeters\": ${activity.elevationLossMeters},\n")
        out.append("  \"averageSpeedMetersPerSecond\": ${activity.averageSpeedMetersPerSecond},\n")
        out.append("  \"maxSpeedMetersPerSecond\": ${activity.maxSpeedMetersPerSecond},\n")
        out.append("  \"visibility\": \"${escapeJson(activity.visibility)}\",\n")
        out.append("  \"manual\": ${activity.manual},\n")
        out.append("  \"commute\": ${activity.commute},\n")
        out.append("  \"track\": [\n")
        track.forEachIndexed { index, point ->
            out.append(
                "    {\"lat\": ${point.latitude}, \"lon\": ${point.longitude}, " +
                    "\"ele\": ${point.altitudeMeters}, \"t\": ${point.timeMillis}}",
            )
            out.append(if (index == track.size - 1) "\n" else ",\n")
        }
        out.append("  ]\n")
        out.append("}\n")
        return out.toString()
    }

    /** The name of the file an activity is written to. */
    fun fileName(
        activity: ActivityEntity,
        format: Format,
    ): String {
        // A pattern needs a date and a time, not an instant, so the instant
        // is turned into the local time first.
        val stamp =
            DateTimeFormatter
                .ofPattern("yyyy-MM-dd-HHmm", Locale.US)
                .format(activity.startedAt.atZone(java.time.ZoneId.systemDefault()))
        val safe = activity.name.replace(Regex("[^A-Za-z0-9]+"), "-").trim('-')
        return "$stamp-${if (safe.isEmpty()) "activity" else safe}.${format.extension}"
    }

    /** Writes an activity to a file. */
    fun write(
        file: File,
        activity: ActivityEntity,
        track: List<TrackPoint>,
        format: Format,
    ): File {
        val text =
            when (format) {
                Format.GPX -> gpx(activity, track)
                Format.JSON -> json(activity, track)
            }
        file.writeText(text)
        return file
    }

    private fun escape(value: String): String =
        value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")

    private fun escapeJson(value: String): String = value.replace("\\", "\\\\").replace("\"", "\\\"")
}
