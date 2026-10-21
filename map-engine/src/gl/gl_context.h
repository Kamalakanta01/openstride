// The OpenGL context the engine draws into.
//
// The original engine draws into a surface the application provides through
// the Fatmap API's Window interface. This is that surface, wrapped in an EGL
// context on a thread of its own, so the drawing never blocks the
// application.
//
// The host build has no EGL, so the whole file is empty there and the
// engine's other layers stay testable.

#pragma once

#if defined(__ANDROID__)

#include <atomic>
#include <condition_variable>
#include <functional>
#include <mutex>
#include <string>
#include <thread>

namespace fatmap::gl {

/**
 * An EGL context on a rendering thread.
 *
 * The thread owns the context: it is made current there, and the
 * application hands it work through [post].
 */
class Context {
  public:
    /// The callbacks the renderer needs.
    struct Callbacks {
        /// Called on the rendering thread, with the context current.
        std::function<void()> draw;
        /// Called when the surface should be swapped.
        std::function<void()> swap;
        /// Called when the surface changes size.
        std::function<void(int width, int height)> resized;
    };

    /// Creates the context for a surface. The window is an ANativeWindow*.
    Context(void* window, int width, int height, Callbacks callbacks);
    ~Context();

    Context(const Context&) = delete;
    Context& operator=(const Context&) = delete;

    /// Starts the rendering thread.
    void start();

    /// Stops the rendering thread and releases the context.
    void stop();

    /// Tells the renderer the surface changed size.
    void resize(int width, int height);

    /// Whether the context is usable.
    bool isReady() const { return mReady.load(); }

    /// The last error, if there was one.
    std::string lastError() const;

  private:
    void run();
    bool createContext();

    void* mWindow = nullptr;
    int mWidth = 0;
    int mHeight = 0;
    Callbacks mCallbacks;

    void* mDisplay = nullptr;
    void* mSurface = nullptr;
    void* mContext = nullptr;

    std::thread mThread;
    std::atomic<bool> mRunning{false};
    std::atomic<bool> mReady{false};
    std::atomic<bool> mResized{false};
    std::atomic<int> mPendingWidth{0};
    std::atomic<int> mPendingHeight{0};
    mutable std::mutex mErrorMutex;
    std::string mError;
};

}  // namespace fatmap::gl

#endif  // __ANDROID__
