// The few things the engine needs from the Java side.
//
// See jni_bridge.h.

#include "jni_bridge.h"

#if defined(__ANDROID__)

#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/log.h>

namespace fatmap {

JavaVM* java_vm_of(JNIEnv* env) {
    JavaVM* vm = nullptr;
    if (env != nullptr && env->GetJavaVM(&vm) != JNI_OK) {
        return nullptr;
    }
    return vm;
}

void* native_window_of(
    JNIEnv* env,
    jobject surfaceHolder) {
    if (env == nullptr || surfaceHolder == nullptr) {
        return nullptr;
    }

    // The holder gives a window: com.fatmap.sdk.api.Window.
    jclass holderClass = env->GetObjectClass(surfaceHolder);
    jmethodID windowMethod =
        env->GetMethodID(holderClass, "getRenderSurfaceWindow", "()Lcom/fatmap/sdk/api/Window;");
    if (windowMethod == nullptr) {
        env->ExceptionClear();
        env->DeleteLocalRef(holderClass);
        return nullptr;
    }
    jobject window = env->CallObjectMethod(surfaceHolder, windowMethod);
    env->DeleteLocalRef(holderClass);
    if (window == nullptr) {
        env->ExceptionClear();
        return nullptr;
    }

    // The window gives the surface.
    jclass windowClass = env->GetObjectClass(window);
    jmethodID surfaceMethod =
        env->GetMethodID(windowClass, "getRenderSurface", "()Landroid/view/Surface;");
    if (surfaceMethod == nullptr) {
        env->ExceptionClear();
        env->DeleteLocalRef(windowClass);
        env->DeleteLocalRef(window);
        return nullptr;
    }
    jobject surface = env->CallObjectMethod(window, surfaceMethod);
    env->DeleteLocalRef(windowClass);
    env->DeleteLocalRef(window);
    if (surface == nullptr) {
        env->ExceptionClear();
        return nullptr;
    }

    // The surface gives the native window.
    auto* native = ANativeWindow_fromSurface(env, surface);
    env->DeleteLocalRef(surface);
    if (native == nullptr) {
        env->ExceptionClear();
        __android_log_print(ANDROID_LOG_ERROR, "fatmap", "the surface had no native window");
        return nullptr;
    }
    return native;
}

}  // namespace fatmap

#endif  // __ANDROID__
