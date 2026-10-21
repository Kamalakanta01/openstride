package dev.openstride.share

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import dev.openstride.recording.TrackPoint
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

/**
 * The card an activity is shared as.
 *
 * The original app renders a clip of the activity with the track and the
 * numbers on it. This draws the same thing: the track, drawn as the clip
 * plays, with the statistics counting up beside it.
 *
 * The drawing is plain canvas rather than Compose, so a frame can be
 * rendered straight into a bitmap and handed to the video encoder.
 */
class ShareCard(
    private val width: Int = 1080,
    private val height: Int = 1920,
) {
    /** The background. */
    var background: Int = Color.parseColor("#0B0E10")

    /** The colour the track is drawn in. */
    var trackColour: Int = Color.parseColor("#FC5200")

    /** The colour the text is drawn in. */
    var textColour: Int = Color.WHITE

    /** The colour the quieter text is drawn in. */
    var mutedColour: Int = Color.parseColor("#9AA4A8")

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bigTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /** Where the track is drawn on the card. */
    private val trackBox =
        androidx.compose.ui.geometry
            .Rect(0f, 0f, 0f, 0f)

    /**
     * The layout of the card, in fractions of its size.
     *
     * Kept apart from the drawing so it can be reasoned about, and tested,
     * without a canvas.
     */
    data class Layout(
        val trackLeft: Float,
        val trackTop: Float,
        val trackRight: Float,
        val trackBottom: Float,
        val statsTop: Float,
    ) {
        companion object {
            /** The layout, as fractions of the card. */
            fun of(): Layout =
                Layout(
                    trackLeft = 0.10f,
                    trackTop = 0.24f,
                    trackRight = 0.90f,
                    trackBottom = 0.62f,
                    statsTop = 0.70f,
                )
        }
    }

    /**
     * Draws one frame.
     *
     * @param progress how far through the clip the frame is, from zero to
     *   one. The track is drawn up to that point and the statistics have
     *   counted up to it.
     */
    fun draw(
        canvas: Canvas,
        activityName: String,
        activityType: String,
        date: String,
        distanceMeters: Double,
        elapsedSeconds: Int,
        paceSecondsPerKilometer: Int,
        elevationGainMeters: Double,
        points: List<TrackPoint>,
        progress: Float,
    ) {
        val layout = Layout.of()
        canvas.drawColor(background)

        // A soft glow behind the track, so it reads on a dark background.
        val glowTop = height * layout.trackTop
        val glowBottom = height * layout.trackBottom
        fillPaint.shader =
            LinearGradient(
                0f,
                glowTop,
                0f,
                glowBottom,
                intArrayOf(
                    Color.argb(46, 252, 82, 0),
                    Color.argb(10, 252, 82, 0),
                ),
                null,
                Shader.TileMode.CLAMP,
            )
        canvas.drawRect(0f, glowTop, width.toFloat(), glowBottom, fillPaint)
        fillPaint.shader = null

        drawTrack(canvas, points, progress, layout)

        drawHeader(canvas, activityType, date)
        drawTitle(canvas, activityName)
        drawStats(
            canvas,
            layout,
            progress,
            distanceMeters,
            elapsedSeconds,
            paceSecondsPerKilometer,
            elevationGainMeters,
        )
    }

    private fun drawHeader(
        canvas: Canvas,
        activityType: String,
        date: String,
    ) {
        labelPaint.color = mutedColour
        labelPaint.textSize = height * 0.026f
        labelPaint.typeface = android.graphics.Typeface.DEFAULT_BOLD
        canvas.drawText(activityType.uppercase(Locale.US), width * 0.08f, height * 0.11f, labelPaint)

        labelPaint.color = mutedColour
        labelPaint.textSize = height * 0.022f
        labelPaint.typeface = android.graphics.Typeface.DEFAULT
        canvas.drawText(date, width * 0.08f, height * 0.155f, labelPaint)
    }

    private fun drawTitle(
        canvas: Canvas,
        activityName: String,
    ) {
        textPaint.color = textColour
        textPaint.textSize = height * 0.045f
        textPaint.typeface = android.graphics.Typeface.DEFAULT_BOLD
        canvas.drawText(activityName, width * 0.08f, height * 0.205f, textPaint)
    }

    private fun drawStats(
        canvas: Canvas,
        layout: Layout,
        progress: Float,
        distanceMeters: Double,
        elapsedSeconds: Int,
        paceSecondsPerKilometer: Int,
        elevationGainMeters: Double,
    ) {
        val top = height * layout.statsTop
        val column = width / 3.2f

        val values =
            listOf(
                "%.2f".format(Locale.US, distanceMeters * progress / 1000.0) to "km",
                formatDuration((elapsedSeconds * progress).toInt()) to "time",
                if (paceSecondsPerKilometer > 0) {
                    formatPace(paceSecondsPerKilometer)
                } else {
                    "--:--"
                } to "/km",
            )

        values.forEachIndexed { index, (value, label) ->
            val left = width * 0.08f + index * column
            bigTextPaint.color = textColour
            bigTextPaint.textSize = height * 0.05f
            bigTextPaint.typeface = android.graphics.Typeface.DEFAULT_BOLD
            canvas.drawText(value, left, top, bigTextPaint)

            labelPaint.color = mutedColour
            labelPaint.textSize = height * 0.024f
            labelPaint.typeface = android.graphics.Typeface.DEFAULT
            canvas.drawText(label, left, top + height * 0.035f, labelPaint)
        }

        if (elevationGainMeters > 0) {
            bigTextPaint.color = textColour
            bigTextPaint.textSize = height * 0.05f
            bigTextPaint.typeface = android.graphics.Typeface.DEFAULT_BOLD
            canvas.drawText(
                "%.0f".format(Locale.US, elevationGainMeters * progress),
                width * 0.08f,
                top + height * 0.12f,
                bigTextPaint,
            )
            labelPaint.color = mutedColour
            labelPaint.textSize = height * 0.024f
            canvas.drawText("m of ascent", width * 0.08f, top + height * 0.155f, labelPaint)
        }
    }

    private fun drawTrack(
        canvas: Canvas,
        points: List<TrackPoint>,
        progress: Float,
        layout: Layout,
    ) {
        if (points.size < 2) return
        val path = TrackGeometry.path(points, width, height, layout)
        if (path == null) return

        // The whole track, faintly, so the shape is there from the start.
        glowPaint.color = Color.argb(38, 255, 255, 255)
        glowPaint.strokeWidth = height * 0.006f
        glowPaint.strokeCap = Paint.Cap.ROUND
        glowPaint.strokeJoin = Paint.Join.ROUND
        canvas.drawPath(path, glowPaint)

        // The part of the track that has been covered.
        val drawn = TrackGeometry.partialPath(points, width, height, layout, progress)
        if (drawn != null) {
            trackPaint.color = trackColour
            trackPaint.strokeWidth = height * 0.009f
            trackPaint.strokeCap = Paint.Cap.ROUND
            trackPaint.strokeJoin = Paint.Join.ROUND
            canvas.drawPath(drawn, trackPaint)
        }
    }
}

/**
 * Where a track goes on the card.
 *
 * Pure arithmetic, so it can be tested without a canvas: the track is
 * scaled to fit the box, keeping its shape, and centred.
 */
object TrackGeometry {
    /** The path of a whole track, or null when it cannot be drawn. */
    fun path(
        points: List<TrackPoint>,
        width: Int,
        height: Int,
        layout: ShareCard.Layout,
    ): Path? {
        if (points.size < 2) return null
        val left = width * layout.trackLeft
        val right = width * layout.trackRight
        val top = height * layout.trackTop
        val bottom = height * layout.trackBottom
        val boxWidth = right - left
        val boxHeight = bottom - top

        var minLat = Double.MAX_VALUE
        var maxLat = -Double.MAX_VALUE
        var minLon = Double.MAX_VALUE
        var maxLon = -Double.MAX_VALUE
        for (point in points) {
            minLat = min(minLat, point.latitude)
            maxLat = max(maxLat, point.latitude)
            minLon = min(minLon, point.longitude)
            maxLon = max(maxLon, point.longitude)
        }
        val spanLat = (maxLat - minLat).coerceAtLeast(1e-6)
        val spanLon = (maxLon - minLon).coerceAtLeast(1e-6)
        val scale = min(boxWidth / spanLon, boxHeight / spanLat)
        val drawnWidth = (spanLon * scale).toFloat()
        val drawnHeight = (spanLat * scale).toFloat()
        val offsetX = left + (boxWidth - drawnWidth) / 2f
        val offsetY = top + (boxHeight - drawnHeight) / 2f

        val path = Path()
        points.forEachIndexed { index, point ->
            val x = offsetX + ((point.longitude - minLon) * scale).toFloat()
            val y = offsetY + (drawnHeight - (point.latitude - minLat) * scale).toFloat()
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        return path
    }

    /** The path of the part of the track that has been covered. */
    fun partialPath(
        points: List<TrackPoint>,
        width: Int,
        height: Int,
        layout: ShareCard.Layout,
        progress: Float,
    ): Path? {
        if (points.size < 2) return null
        val count = (points.size * progress.coerceIn(0f, 1f)).toInt().coerceAtLeast(2)
        return path(points.take(count), width, height, layout)
    }

    /** How far apart two points are, in metres. */
    fun distanceMeters(
        a: TrackPoint,
        b: TrackPoint,
    ): Double = dev.openstride.recording.haversineMeters(a.latitude, a.longitude, b.latitude, b.longitude)
}

/** A duration as h:mm:ss or m:ss. */
fun formatDuration(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format(Locale.US, "%d:%02d", minutes, secs)
    }
}

/** A pace as m:ss. */
fun formatPace(secondsPerKilometer: Int): String {
    val minutes = secondsPerKilometer / 60
    val seconds = secondsPerKilometer % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}
