// The OpenGL context the engine draws into.
//
// See gl_context.h. The host build has no EGL and compiles this away.

#include "gl/gl_context.h"

#if defined(__ANDROID__)

#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/log.h>

namespace fatmap::gl {

namespace {
constexpr EGLint kConfigAttributes[] = {
    EGL_SURFACE_TYPE,
    EGL_WINDOW_BIT,
    EGL_RENDERABLE_TYPE,
    EGL_OPENGL_ES2_BIT,
    EGL_RED_SIZE,
    8,
    EGL_GREEN_SIZE,
    8,
    EGL_BLUE_SIZE,
    8,
    EGL_ALPHA_SIZE,
    8,
    EGL_NONE,
};

constexpr EGLint kContextAttributes[] = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE};
}  // namespace

Context::Context(
    void* window,
    int width,
    int height,
    Callbacks callbacks) :
    mWindow(window), mWidth(width), mHeight(height), mCallbacks(std::move(callbacks)) {}

Context::~Context() {
    stop();
}

void Context::start() {
    if (mRunning.exchange(true)) {
        return;
    }
    mThread = std::thread([this] { run(); });
}

void Context::stop() {
    if (!mRunning.exchange(false)) {
        return;
    }
    if (mThread.joinable()) {
        mThread.join();
    }
    if (mDisplay != nullptr) {
        eglMakeCurrent(mDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
        if (mContext != nullptr) {
            eglDestroyContext(mDisplay, mContext);
            mContext = nullptr;
        }
        if (mSurface != nullptr) {
            eglDestroySurface(mDisplay, mSurface);
            mSurface = nullptr;
        }
        eglTerminate(mDisplay);
        mDisplay = nullptr;
    }
}

void Context::resize(
    int width,
    int height) {
    mPendingWidth.store(width);
    mPendingHeight.store(height);
    mResized.store(true);
}

std::string Context::lastError() const {
    std::lock_guard<std::mutex> lock(mErrorMutex);
    return mError;
}

bool Context::createContext() {
    mDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (mDisplay == EGL_NO_DISPLAY) {
        std::lock_guard<std::mutex> lock(mErrorMutex);
        mError = "no display";
        return false;
    }
    if (!eglInitialize(mDisplay, nullptr, nullptr)) {
        std::lock_guard<std::mutex> lock(mErrorMutex);
        mError = "the display could not be initialised";
        return false;
    }
    EGLConfig config = nullptr;
    EGLint count = 0;
    if (!eglChooseConfig(mDisplay, kConfigAttributes, &config, 1, &count) || count == 0) {
        std::lock_guard<std::mutex> lock(mErrorMutex);
        mError = "no usable configuration";
        return false;
    }
    auto* window = static_cast<ANativeWindow*>(mWindow);
    mSurface = eglCreateWindowSurface(mDisplay, config, window, nullptr);
    if (mSurface == EGL_NO_SURFACE) {
        std::lock_guard<std::mutex> lock(mErrorMutex);
        mError = "the surface could not be created";
        return false;
    }
    mContext = eglCreateContext(mDisplay, config, EGL_NO_CONTEXT, kContextAttributes);
    if (mContext == EGL_NO_CONTEXT) {
        std::lock_guard<std::mutex> lock(mErrorMutex);
        mError = "the context could not be created";
        return false;
    }
    if (!eglMakeCurrent(mDisplay, mSurface, mSurface, mContext)) {
        std::lock_guard<std::mutex> lock(mErrorMutex);
        mError = "the context could not be made current";
        return false;
    }
    return true;
}

void Context::run() {
    if (!createContext()) {
        __android_log_print(ANDROID_LOG_ERROR, "fatmap", "the GL context failed: %s", lastError().c_str());
        return;
    }
    mReady.store(true);

    while (mRunning.load()) {
        if (mResized.exchange(false)) {
            eglSurfaceAttrib(
                mDisplay, mSurface, EGL_WIDTH, mPendingWidth.load());
            eglSurfaceAttrib(
                mDisplay, mSurface, EGL_HEIGHT, mPendingHeight.load());
            if (mCallbacks.resized) {
                mCallbacks.resized(mPendingWidth.load(), mPendingHeight.load());
            }
        }
        if (mCallbacks.draw) {
            mCallbacks.draw();
        }
        if (mCallbacks.swap) {
            mCallbacks.swap();
        }
        eglSwapBuffers(mDisplay, mSurface);
    }
}

}  // namespace fatmap::gl

#endif  // __ANDROID__
