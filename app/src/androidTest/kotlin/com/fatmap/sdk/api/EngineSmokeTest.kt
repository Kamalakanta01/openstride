package com.fatmap.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.openstride.map.MapEngineLoader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Exercises the whole native boundary on a device.
 *
 * Every call here crosses JNI: Java objects are marshalled into C++ structs,
 * the engine mutates its state, and the result is marshalled back. If any of
 * the generated marshalling is wrong, this fails.
 *
 * Run with:
 *     ./gradlew :app:connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class EngineSmokeTest {
    @Before
    fun loadEngine() {
        MapEngineLoader.ensureLoaded()
    }

    @Test
    fun theLibraryLoads() {
        assertTrue(MapEngineLoader.isLoaded)
        assertNotNull(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun configurationRoundTripsThroughTheNativeBoundary() {
        val configuration = EngineConfiguration.createDefault()
        configuration.setClientName("openstride-device-test")
        assertEquals("openstride-device-test", configuration.getClientName())

        configuration.setUseOnDiskCache(false)
        assertEquals(false, configuration.getUseOnDiskCache())

        configuration.setEnableRasterMegaTiles(true)
        assertEquals(true, configuration.getEnableRasterMegaTiles())

        configuration.setLogLevel(LogLevel.DEBUG)
        assertEquals(LogLevel.DEBUG, configuration.getLogLevel())
    }

    @Test
    fun aCameraTargetSurvivesTheRoundTrip() {
        val configuration = EngineConfiguration.createDefault()
        val target =
            CameraTarget(
                lookAtPoint = WorldPoint3(latitude = 48.8584, longitude = 2.2945, elevation = 33.0f),
                bearing = 42.0f,
                distance = 1500.0f,
                pitch = -30.0f,
            )
        configuration.setCameraTarget(target)

        val readBack = configuration.getCameraTarget()
        assertEquals(48.8584, readBack.lookAtPoint.latitude, 1e-9)
        assertEquals(2.2945, readBack.lookAtPoint.longitude, 1e-9)
        assertEquals(42.0f, readBack.bearing, 1e-4f)
        assertEquals(1500.0f, readBack.distance, 1e-4f)
        assertEquals(-30.0f, readBack.pitch, 1e-4f)
    }

    @Test
    fun theEngineIsReachableThroughTheSurfaceHolderBridge() {
        var engine: TerrainEngine? = null
        var ready = false

        val holder =
            object : RenderSurfaceHolder {
                override fun getGestureRecognizerControl(): GestureRecognizerControl? = null

                override fun getRenderSurfaceWindow(): Window? = null

                override fun isRenderSurfaceCreated(): Boolean = true

                override fun notifyTerrainEngineReady() {
                    ready = true
                }

                override fun setTerrainEngine(terrainEngine: TerrainEngine?) {
                    engine = terrainEngine
                }

                override fun setTouchListener(touchListener: TouchListener?) = Unit

                override fun setWindowLifecycleListener(listener: WindowLifecycleListener?) = Unit
            }

        val configuration = EngineConfiguration.createDefault()
        MREHolder.create(holder, null, "openstride", "openstride-test", configuration)

        assertTrue("the engine should have announced itself", ready)
        val terrainEngine = engine
        assertNotNull("the surface holder should have received a TerrainEngine", terrainEngine)

        // The camera, content and the rest of the engine hang off it.
        val camera = terrainEngine!!.getCamera()
        assertNotNull(camera)
        assertNotNull(terrainEngine.getContent())
        assertNotNull(terrainEngine.getOffline())
        assertNotNull(terrainEngine.getUtils())
        assertNotNull(terrainEngine.getDebugApi())
        assertNotNull(terrainEngine.getPrivateApi("openstride"))
    }

    @Test
    fun theCameraFliesAndReportsItsTarget() {
        var engine: TerrainEngine? = null
        val holder =
            object : RenderSurfaceHolder {
                override fun getGestureRecognizerControl(): GestureRecognizerControl? = null

                override fun getRenderSurfaceWindow(): Window? = null

                override fun isRenderSurfaceCreated(): Boolean = true

                override fun notifyTerrainEngineReady() = Unit

                override fun setTerrainEngine(terrainEngine: TerrainEngine?) {
                    engine = terrainEngine
                }

                override fun setTouchListener(touchListener: TouchListener?) = Unit

                override fun setWindowLifecycleListener(listener: WindowLifecycleListener?) = Unit
            }
        val configuration = EngineConfiguration.createDefault()
        MREHolder.create(holder, null, "openstride", "openstride-test", configuration)

        val camera = engine!!.getCamera()
        // flyTo(latitude, longitude, zoom, bearing, pitch, duration, margins, listener)
        camera.flyTo(
            48.8584,
            2.2945,
            14.0f,
            null,
            null,
            0.0f,
            CameraMargins(0.0f, 0.0f, 0.0f, 0.0f),
            null,
        )

        val target = camera.getTarget()
        assertEquals(48.8584, target.lookAtPoint.latitude, 1e-4)
        assertEquals(2.2945, target.lookAtPoint.longitude, 1e-4)
        assertTrue("the camera should report a ground resolution", camera.getScaleMetersPerPixel() > 0f)
    }

    @Test
    fun contentCanBeAddedAndRemoved() {
        var engine: TerrainEngine? = null
        val holder =
            object : RenderSurfaceHolder {
                override fun getGestureRecognizerControl(): GestureRecognizerControl? = null

                override fun getRenderSurfaceWindow(): Window? = null

                override fun isRenderSurfaceCreated(): Boolean = true

                override fun notifyTerrainEngineReady() = Unit

                override fun setTerrainEngine(terrainEngine: TerrainEngine?) {
                    engine = terrainEngine
                }

                override fun setTouchListener(touchListener: TouchListener?) = Unit

                override fun setWindowLifecycleListener(listener: WindowLifecycleListener?) = Unit
            }
        MREHolder.create(holder, null, "openstride", "openstride-test", EngineConfiguration.createDefault())

        val content = engine!!.getContent()
        val line =
            WorldLine2(
                points =
                    mutableListOf(
                        WorldPoint2(latitude = 47.3769, longitude = 8.5417),
                        WorldPoint2(latitude = 47.3769, longitude = 8.5418),
                    ),
            )
        val handle = content.addCustomLine(line, "red", "5", false, "line-1")
        assertTrue(handle > 0)
        assertTrue(content.removeCustomLine(handle))
    }
}
