package dev.openstride.share

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Writing a still image.
 *
 * Some devices have no video encoder at all, so the same card can be saved
 * as a picture instead. It is the same drawing, with nothing animated.
 */
object ShareImage {
    /** Renders the card and puts it in the gallery. */
    suspend fun save(
        context: Context,
        activityName: String,
        activityType: String,
        date: String,
        distanceMeters: Double,
        elapsedSeconds: Int,
        paceSecondsPerKilometer: Int,
        elevationGainMeters: Double,
        points: List<dev.openstride.recording.TrackPoint>,
    ): Uri? =
        withContext(Dispatchers.Default) {
            val card = ShareCard()
            val bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            card.draw(
                canvas = canvas,
                activityName = activityName,
                activityType = activityType,
                date = date,
                distanceMeters = distanceMeters,
                elapsedSeconds = elapsedSeconds,
                paceSecondsPerKilometer = paceSecondsPerKilometer,
                elevationGainMeters = elevationGainMeters,
                points = points,
                progress = 1f,
            )

            val file = File(context.cacheDir, "openstride-share-${System.currentTimeMillis()}.png")
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            bitmap.recycle()
            publish(context, file)
        }

    /** Puts the picture in the gallery and returns where it went. */
    private fun publish(
        context: Context,
        file: File,
    ): Uri? {
        val values =
            ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/OpenStride")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return null
        resolver.openOutputStream(uri)?.use { out ->
            file.inputStream().use { input -> input.copyTo(out) }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
        file.delete()
        return uri
    }
}
