// Getting the bytes of a tile from the application.
//
// See tile_transport.h.

#include "tiles/tile_transport.h"

#if defined(__ANDROID__)

#include <android/log.h>

namespace fatmap::tiles {

Fetcher applicationFetcher(JavaVM* vm) {
    if (vm == nullptr) {
        return nullptr;
    }

    // The class is looked up once, on the thread that is already attached to
    // the runtime, so the application's class loader is the one that is
    // used.
    jclass globalClass = nullptr;
    jmethodID fetchMethod = nullptr;
    {
        JNIEnv* env = nullptr;
        bool attached = false;
        if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
            if (vm->AttachCurrentThread(&env, nullptr) != JNI_OK) {
                __android_log_print(ANDROID_LOG_ERROR, "fatmap", "the transport could not be attached");
                return nullptr;
            }
            attached = true;
        }
        jclass transportClass = env->FindClass(kTransportClass);
        if (transportClass != nullptr) {
            fetchMethod = env->GetStaticMethodID(transportClass, "fetch", "(Ljava/lang/String;)[B");
            if (fetchMethod == nullptr) {
                __android_log_print(ANDROID_LOG_ERROR, "fatmap", "the transport has no fetch method");
            } else {
                // The class is kept for the life of the process: the engine
                // asks for tiles for as long as the app runs. It has to be a
                // global reference: a local one belongs to the frame that
                // made it, and the tiles are fetched from the rendering
                // thread.
                globalClass = static_cast<jclass>(env->NewGlobalRef(transportClass));
            }
            env->DeleteLocalRef(transportClass);
        } else {
            __android_log_print(ANDROID_LOG_ERROR, "fatmap", "the transport class was not found");
        }
        if (attached) {
            vm->DetachCurrentThread();
        }
    }
    if (globalClass == nullptr || fetchMethod == nullptr) {
        return nullptr;
    }

    return [vm, globalClass, fetchMethod](const std::string& url, std::vector<uint8_t>& out) {
        JNIEnv* env = nullptr;
        bool attached = false;
        if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
            if (vm->AttachCurrentThread(&env, nullptr) != JNI_OK) {
                return false;
            }
            attached = true;
        }

        bool ok = false;
        jstring javaUrl = env->NewStringUTF(url.c_str());
        if (javaUrl != nullptr) {
            jbyteArray bytes =
                static_cast<jbyteArray>(env->CallStaticObjectMethod(globalClass, fetchMethod, javaUrl));
            if (bytes != nullptr) {
                const jsize length = env->GetArrayLength(bytes);
                out.resize(static_cast<size_t>(length));
                if (length > 0) {
                    env->GetByteArrayRegion(bytes, 0, length, reinterpret_cast<jbyte*>(out.data()));
                }
                ok = !out.empty();
                env->DeleteLocalRef(bytes);
            }
            env->DeleteLocalRef(javaUrl);
        }
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
            ok = false;
        }
        if (attached) {
            vm->DetachCurrentThread();
        }
        return ok;
    };
}

}  // namespace fatmap::tiles

#endif  // __ANDROID__
