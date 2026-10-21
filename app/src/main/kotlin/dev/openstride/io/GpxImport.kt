package dev.openstride.io

import dev.openstride.recording.TrackPoint
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.time.Instant

/**
 * Reading a GPX file.
 *
 * The export is only worth having if something can read it back, and the
 * more interesting direction is the other way: a file recorded by another
 * app or device can be brought in.
 */
object GpxImport {
    /** What was read out of a file. */
    data class Parsed(
        val name: String?,
        val type: String?,
        val startedAt: Instant?,
        val track: List<TrackPoint>,
    ) {
        val isEmpty: Boolean get() = track.size < 2
    }

    /**
     * Reads a GPX file.
     *
     * The track points are the only thing every GPX file has; the name, the
     * type and the start time are read when they are there.
     */
    fun parse(stream: InputStream): Parsed {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = false
        val parser = factory.newPullParser()
        parser.setInput(stream, null)

        var name: String? = null
        var type: String? = null
        var startedAt: Instant? = null
        val track = mutableListOf<TrackPoint>()
        var latitude = 0.0
        var longitude = 0.0
        var altitude = 0.0
        var time: Long? = null
        var inTrack = false

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "trk" -> inTrack = true
                        "trkpt" -> {
                            latitude = parser.getAttributeValue(null, "lat")?.toDoubleOrNull() ?: 0.0
                            longitude = parser.getAttributeValue(null, "lon")?.toDoubleOrNull() ?: 0.0
                            altitude = 0.0
                            time = null
                        }
                        "ele" -> altitude = parser.nextText().toDoubleOrNull() ?: 0.0
                        "time" -> time = parser.nextText().toLongOrNull()
                    }
                }
                XmlPullParser.END_TAG ->
                    when (parser.name) {
                        "trk" -> inTrack = false
                        "name" -> if (name == null) name = parser.text
                        "type" -> if (type == null) type = parser.text
                        "time" -> if (startedAt == null) time?.let { startedAt = Instant.ofEpochMilli(it) }
                        "trkpt" ->
                            if (inTrack || true) {
                                track +=
                                    TrackPoint(
                                        latitude = latitude,
                                        longitude = longitude,
                                        altitudeMeters = altitude,
                                        timeMillis = time ?: 0L,
                                    )
                            }
                    }
            }
            event = parser.next()
        }
        return Parsed(name = name, type = type, startedAt = startedAt, track = track)
    }
}
