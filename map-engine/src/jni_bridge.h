// The few things the engine needs from the Java side.
//
// The engine is written so that it can be built and tested on a computer
// with no Java at all; the handful of calls it makes into the runtime live
// here, and are compiled out on the host.

#pragma once

#include <jni.h>

namespace fatmap {

#if defined(__ANDROID__)

/// The runtime, for the threads the engine starts.
JavaVM* java_vm_of(JNIEnv* env);

/// The native window of the surface the application draws on.
///
/// The application's holder gives a window, the window gives a surface, and
/// the surface gives a native window.
///
/// @return the window, which the caller owns, or null.
void* native_window_of(
    JNIEnv* env,
    jobject surfaceHolder);

#endif  // __ANDROID__

}  // namespace fatmap
