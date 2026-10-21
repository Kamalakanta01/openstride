package dev.openstride.share

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.Surface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Writing a clip.
 *
 * The frames are rendered by [ShareCard] and encoded to an MP4 with the
 * platform's media codecs, then put in the gallery so the athlete can post
 * it wherever they like.
 *
 * The frames go through the encoder's input surface, so the video never
 * leaves the hardware encoder.
 *
 * Not every device has an encoder - the bare Android images used for
 * emulators have none - so the encoder is looked up rather than assumed, and
 * the caller is told when there is not one.
 */
class VideoEncoder(
    private val context: Context,
    private val width: Int = 1080,
    private val height: Int = 1920,
    private val framesPerSecond: Int = 30,
) {
    /** The encoder that will be used, or null when the device has none. */
    private val encoderName: String? by lazy { findEncoder() }

    /** Whether this device can make a clip at all. */
    val isSupported: Boolean get() = encoderName != null

    /**
     * Renders the frames and writes the clip.
     *
     * @param frameCount how many frames the clip has.
     * @param render draws one frame, given how far through the clip it is.
     * @return where the clip was written, or null when it could not be made.
     */
    suspend fun encode(
        frameCount: Int,
        render: (progress: Float, canvas: Canvas) -> Unit,
    ): Uri? =
        withContext(Dispatchers.Default) {
            val name = encoderName ?: return@withContext null
            val size = supportedSize(name) ?: return@withContext null

            val format =
                MediaFormat.createVideoFormat(MIME, size.first, size.second).apply {
                    setInteger(
                        MediaFormat.KEY_COLOR_FORMAT,
                        MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface,
                    )
                    setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE)
                    setInteger(MediaFormat.KEY_FRAME_RATE, framesPerSecond)
                    setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
                }

            val codec =
                try {
                    MediaCodec.createByCodecName(name)
                } catch (_: Exception) {
                    return@withContext null
                }
            var muxer: MediaMuxer? = null
            val bitmap = Bitmap.createBitmap(size.first, size.second, Bitmap.Config.ARGB_8888)
            val bitmapCanvas = Canvas(bitmap)
            val file = temporaryFile()

            try {
                codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                val surface = codec.createInputSurface()
                codec.start()
                muxer = MediaMuxer(file.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

                // The frames are paced: the surface timestamps them as they
                // are drawn, so a clip rendered as fast as the machine can
                // would come out as a blur.
                val frameDelay = 1_000L / framesPerSecond
                for (index in 0 until frameCount) {
                    val progress = (index + 1f) / frameCount
                    bitmap.eraseColor(android.graphics.Color.BLACK)
                    render(progress, bitmapCanvas)
                    drawOntoSurface(bitmap, surface)
                    delay(frameDelay)
                }

                codec.signalEndOfInputStream()
                drain(codec, muxer)
                codec.stop()
                muxer.stop()
                muxer.release()
                muxer = null

                publish(file)
            } catch (_: Exception) {
                runCatching { codec.stop() }
                runCatching { muxer?.release() }
                file.delete()
                null
            } finally {
                bitmap.recycle()
                runCatching { codec.release() }
            }
        }

    /**
     * The encoder to use.
     *
     * A hardware encoder is preferred, because a software one cannot keep
     * up with thirty frames a second.
     */
    private fun findEncoder(): String? {
        val list = MediaCodecList(MediaCodecList.REGULAR_CODECS)
        val candidates =
            list.codecInfos.filter {
                it.isEncoder && it.supportedTypes.any { type -> type.equals(MIME, true) }
            }
        if (candidates.isEmpty()) return null
        val hardware = candidates.firstOrNull { it.isHardwareAccelerated }
        val chosen = hardware ?: candidates.first()
        return chosen.name
    }

    /**
     * The size the encoder will take.
     *
     * Some encoders will not take a tall frame at all, so the size is
     * checked against what the encoder says it supports.
     */
    private fun supportedSize(name: String): Pair<Int, Int>? {
        val info = MediaCodecList(MediaCodecList.REGULAR_CODECS).codecInfos.firstOrNull { it.name == name }
        val capabilities = info?.getCapabilitiesForType(MIME) ?: return null
        val video = capabilities.videoCapabilities ?: return width to height

        val widths = video.supportedWidths
        val heights = video.supportedHeights
        if (widths.contains(width) && heights.contains(height)) return width to height

        // The encoder will not take the size that was asked for, so the
        // largest one it will take is used instead, aligned the way it wants.
        val wantedWidth = widths.upper.coerceAtMost(width)
        val wantedHeight = heights.upper.coerceAtMost(height)
        val alignedWidth = wantedWidth - (wantedWidth % video.widthAlignment.coerceAtLeast(1))
        val alignedHeight = wantedHeight - (wantedHeight % video.heightAlignment.coerceAtLeast(1))
        if (alignedWidth <= 0 || alignedHeight <= 0) return null
        return alignedWidth to alignedHeight
    }

    /**
     * Moves the encoded frames into the file.
     *
     * The muxer only starts once the codec has told it which track to
     * write, which happens after the first frame comes out.
     */
    private fun drain(
        codec: MediaCodec,
        muxer: MediaMuxer,
    ) {
        val info = MediaCodec.BufferInfo()
        var track = -1
        var started = false
        while (true) {
            val index = codec.dequeueOutputBuffer(info, TIMEOUT_US)
            when {
                index == MediaCodec.INFO_TRY_AGAIN_LATER -> continue
                index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    check(!started) { "the format changed twice" }
                    track = muxer.addTrack(codec.outputFormat)
                    muxer.start()
                    started = true
                }
                index >= 0 -> {
                    val buffer = codec.getOutputBuffer(index)
                    if (info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                        info.size = 0
                    }
                    if (info.size > 0) {
                        check(started) { "the muxer was not started" }
                        val data = buffer
                        if (data != null) {
                            data.position(info.offset)
                            data.limit(info.offset + info.size)
                            muxer.writeSampleData(track, data, info)
                        }
                    }
                    codec.releaseOutputBuffer(index, false)
                    if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) break
                }
            }
        }
    }

    /** Draws a bitmap onto the encoder's surface. */
    private fun drawOntoSurface(
        bitmap: Bitmap,
        surface: Surface,
    ) {
        val canvas = surface.lockCanvas(null)
        try {
            canvas.drawBitmap(bitmap, 0f, 0f, null)
        } finally {
            surface.unlockCanvasAndPost(canvas)
        }
    }

    private fun temporaryFile(): File = File(context.cacheDir, "openstride-share-${System.currentTimeMillis()}.mp4")

    /** Puts the clip in the gallery and returns where it went. */
    private fun publish(file: File): Uri? {
        val values =
            ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, file.name)
                put(MediaStore.Video.Media.MIME_TYPE, MIME)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/OpenStride")
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                }
            }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values) ?: return null
        resolver.openOutputStream(uri)?.use { out ->
            file.inputStream().use { input -> input.copyTo(out) }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Video.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
        file.delete()
        return uri
    }

    companion object {
        private const val MIME = "video/mp4"
        private const val BIT_RATE = 8_000_000
        private const val TIMEOUT_US = 10_000L
    }
}
