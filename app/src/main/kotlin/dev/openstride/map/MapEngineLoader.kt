package dev.openstride.map

/**
 * Loads the native map engine.
 *
 * The engine is the clean-room implementation of the Fatmap API
 * (`map-engine/`). Its shared object exports the same 312 JNI entry points as
 * the original library, so the Kotlin API in `com.fatmap.sdk.api` binds to it
 * unchanged.
 *
 * Loading is deferred rather than done in a static initialiser: a missing
 * library should surface where it matters, not as an `ExceptionInInitializerError`
 * the first time any class in the package is touched.
 */
object MapEngineLoader {
    private const val LIBRARY = "fatmap_jni"

    @Volatile
    private var loaded = false

    /** True when the native library is loaded and ready. */
    val isLoaded: Boolean
        get() = loaded

    /**
     * Loads the native library if it is not already loaded.
     *
     * @throws UnsatisfiedLinkError if the library is not in the APK.
     */
    @Synchronized
    fun ensureLoaded() {
        if (loaded) {
            return
        }
        System.loadLibrary(LIBRARY)
        loaded = true
    }
}
