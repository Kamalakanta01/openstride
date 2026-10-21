package dev.openstride.map

import android.content.Context
import android.graphics.PixelFormat
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.fatmap.sdk.api.Camera
import com.fatmap.sdk.api.EngineConfiguration
import com.fatmap.sdk.api.GestureRecognizerControl
import com.fatmap.sdk.api.MREHolder
import com.fatmap.sdk.api.RenderSurfaceHolder
import com.fatmap.sdk.api.TerrainEngine
import com.fatmap.sdk.api.TouchListener
import com.fatmap.sdk.api.Window
import com.fatmap.sdk.api.WindowLifecycleListener

/**
 * The map, drawn by our own engine.
 *
 * The engine is the 1:1 reimplementation of the original's map engine: the
 * same interface, the same tile scheme, the same camera. This hands it a
 * surface to draw on and moves its camera.
 *
 * The engine talks to the application through the Fatmap API, exactly as
 * the original's does: the application implements [RenderSurfaceHolder] and
 * gives the engine a surface, and the engine calls back when it is ready.
 */
class FatmapMapView(
    context: Context,
) : SurfaceView(context),
    SurfaceHolder.Callback,
    RenderSurfaceHolder {
    private var holderImpl: MREHolder? = null
    private var engineImpl: TerrainEngine? = null
    private var surfaceReady = false
    private var engineReady = false

    // / Called when the engine hands over the terrain engine.
    var onEngineReady: ((TerrainEngine) -> Unit)? = null

    init {
        setZOrderOnTop(false)
        holder.addCallback(this)
        setBackgroundColor(android.graphics.Color.TRANSPARENT)
        // The engine draws opaque, so the surface does not need transparency.
        holder.setFormat(PixelFormat.RGBX_8888)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceReady = true
        startEngine()
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int,
    ) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        surfaceReady = false
    }

    /** Starts the engine, once there is a surface to draw on. */
    private fun startEngine() {
        if (holderImpl != null || !surfaceReady) return
        MapEngineLoader.ensureLoaded()
        val configuration = EngineConfiguration.createDefault()
        configuration.setClientName("openstride")
        val created =
            MREHolder.create(
                this,
                null,
                "openstride",
                "OpenStride/0.1 (Android)",
                configuration,
            )
        holderImpl = created
    }

    /** The engine's camera, once the engine is ready. */
    val camera: Camera?
        get() = engineImpl?.getCamera()

    /** The engine, once it is ready. */
    val engine: TerrainEngine?
        get() = engineImpl

    // ---- the Fatmap API ----

    override fun getGestureRecognizerControl(): GestureRecognizerControl? = null

    override fun getRenderSurfaceWindow(): Window = EngineWindow(this)

    override fun isRenderSurfaceCreated(): Boolean = surfaceReady

    override fun notifyTerrainEngineReady() {
        // The engine is telling the application it is ready. It hands the
        // engine over separately.
        engineReady = true
    }

    override fun setTerrainEngine(obj: TerrainEngine?) {
        engineImpl = obj
        if (obj != null) {
            onEngineReady?.invoke(obj)
        }
    }

    override fun setTouchListener(obj: TouchListener?) {
    }

    override fun setWindowLifecycleListener(listener: WindowLifecycleListener?) {
    }

    /** The window the engine draws on. */
    private class EngineWindow(
        private val view: FatmapMapView,
    ) : Window {
        override fun getPixelRatio(): Float = view.resources.displayMetrics.density

        override fun getRenderSurface(): Surface = view.holder.surface
    }
}

/**
 * The engine's map, in the user interface.
 *
 * @param latitude where the camera starts.
 * @param longitude where the camera starts.
 * @param zoom how far in the camera starts.
 */
@Composable
fun FatmapMap(
    modifier: Modifier = Modifier,
    latitude: Double = 0.0,
    longitude: Double = 0.0,
    zoom: Double = 12.0,
    onEngineReady: (TerrainEngine) -> Unit = {},
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val view = remember { FatmapMapView(context) }
    // The engine arrives after the surface is made, so it is held as state:
    // the camera is only moved once there is a camera to move.
    var engine by remember { mutableStateOf<TerrainEngine?>(null) }

    DisposableEffect(view) {
        onDispose {
            view.engine?.close()
        }
    }

    DisposableEffect(view, onEngineReady) {
        view.onEngineReady = { arrived ->
            engine = arrived
            onEngineReady(arrived)
        }
        onDispose {
            view.onEngineReady = null
        }
    }

    AndroidView(
        factory = { view },
        modifier = modifier,
        update = { current ->
            val camera = engine?.getCamera() ?: return@AndroidView
            val target = camera.getTarget().lookAtPoint
            android.util.Log.d("openstride-map", "the camera is at ${target.latitude},${target.longitude}")
            if (target.latitude != latitude || target.longitude != longitude) {
                camera.flyTo(
                    latitude,
                    longitude,
                    zoom.toFloat(),
                    null,
                    null,
                    0.0f,
                    com.fatmap.sdk.api
                        .CameraMargins(0.0f, 0.0f, 0.0f, 0.0f),
                    null,
                )
            }
        },
    )
}
