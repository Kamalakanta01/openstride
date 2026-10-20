// JNI support types for the Fatmap-compatible engine.
//
// Not generated: these are the marshalling primitives the generated layer
// builds on. Kept dependency-free so the same code compiles for the host
// test build and for the Android NDK build.

#pragma once

#include <jni.h>

#include <cstdint>
#include <map>
#include <optional>
#include <string>
#include <vector>

namespace jni {

/**
 * A Java object passed through the API.
 *
 * Non-owning: the reference is valid only for the duration of the JNI call
 * that produced it. Anything the engine needs to keep beyond that call must
 * take a [GlobalRef] instead.
 */
class Opaque {
public:
    Opaque() = default;
    explicit Opaque(jobject obj) : mObj(obj) {}
    jobject get() const { return mObj; }
    bool valid() const { return mObj != nullptr; }
    explicit operator bool() const { return mObj != nullptr; }

private:
    jobject mObj = nullptr;
};

/** A Java object the engine keeps alive. */
class GlobalRef {
public:
    GlobalRef() = default;
    GlobalRef(JNIEnv* env, jobject local) : mRef(local ? env->NewGlobalRef(local) : nullptr) {}
    GlobalRef(const GlobalRef&) = delete;
    GlobalRef& operator=(const GlobalRef&) = delete;
    GlobalRef(GlobalRef&& other) noexcept : mRef(other.mRef) { other.mRef = nullptr; }
    GlobalRef& operator=(GlobalRef&& other) noexcept {
        if (this != &other) {
            reset();
            mRef = other.mRef;
            other.mRef = nullptr;
        }
        return *this;
    }
    ~GlobalRef() { reset(); }

    void reset() {
        if (mRef != nullptr) {
            // The JVM may already be gone during shutdown; guard with a check.
            if (auto* env = g_env) {
                env->DeleteGlobalRef(mRef);
            }
            mRef = nullptr;
        }
    }

    jobject get() const { return mRef; }
    bool valid() const { return mRef != nullptr; }

    /// The thread-local JNIEnv, set once when the library is loaded.
    static JNIEnv** g_env_slot();
    static JNIEnv* g_env;

private:
    jobject mRef = nullptr;
};

// --------------------------------------------------------------- primitives

inline std::string read_string(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return {};
    }
    const char* chars = env->GetStringUTFChars(static_cast<jstring>(obj), nullptr);
    if (chars == nullptr) {
        return {};
    }
    std::string out(chars);
    env->ReleaseStringUTFChars(static_cast<jstring>(obj), chars);
    return out;
}

inline jstring write_string(JNIEnv* env, const std::string& value) {
    return env->NewStringUTF(value.c_str());
}

template <typename T>
inline std::optional<T> read_boxed(JNIEnv* env, jobject obj);

template <>
inline std::optional<float> read_boxed<float>(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return std::nullopt;
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID method = env->GetMethodID(cls, "floatValue", "()F");
    const float value = env->CallFloatMethod(obj, method);
    env->DeleteLocalRef(cls);
    return value;
}

template <>
inline std::optional<double> read_boxed<double>(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return std::nullopt;
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID method = env->GetMethodID(cls, "doubleValue", "()D");
    const double value = env->CallDoubleMethod(obj, method);
    env->DeleteLocalRef(cls);
    return value;
}

template <>
inline std::optional<int64_t> read_boxed<int64_t>(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return std::nullopt;
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID method = env->GetMethodID(cls, "longValue", "()J");
    const int64_t value = env->CallLongMethod(obj, method);
    env->DeleteLocalRef(cls);
    return value;
}

template <>
inline std::optional<int32_t> read_boxed<int32_t>(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return std::nullopt;
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID method = env->GetMethodID(cls, "intValue", "()I");
    const int32_t value = env->CallIntMethod(obj, method);
    env->DeleteLocalRef(cls);
    return value;
}

template <>
inline std::optional<bool> read_boxed<bool>(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return std::nullopt;
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID method = env->GetMethodID(cls, "booleanValue", "()Z");
    const bool value = env->CallBooleanMethod(obj, method) != JNI_FALSE;
    env->DeleteLocalRef(cls);
    return value;
}

/// Builds a boxed value object. The JNIEnv helpers for the boxed types are
/// not available on every JDK, so go through the constructor directly.
template <typename T>
inline jobject new_boxed(JNIEnv* env, const char* className, const char* signature, T value) {
    jclass cls = env->FindClass(className);
    if (cls == nullptr) {
        return nullptr;
    }
    jmethodID ctor = env->GetMethodID(cls, "<init>", signature);
    jobject result = env->NewObject(cls, ctor, value);
    env->DeleteLocalRef(cls);
    return result;
}

inline std::optional<float> read_boxed_float(JNIEnv* env, jobject obj) {
    return read_boxed<float>(env, obj);
}
inline std::optional<double> read_boxed_double(JNIEnv* env, jobject obj) {
    return read_boxed<double>(env, obj);
}
inline std::optional<int64_t> read_boxed_long(JNIEnv* env, jobject obj) {
    return read_boxed<int64_t>(env, obj);
}
inline std::optional<int32_t> read_boxed_int(JNIEnv* env, jobject obj) {
    return read_boxed<int32_t>(env, obj);
}
inline std::optional<bool> read_boxed_boolean(JNIEnv* env, jobject obj) {
    return read_boxed<bool>(env, obj);
}

inline jobject write_boxed_float(JNIEnv* env, std::optional<float> value) {
    return value ? new_boxed<float>(env, "java/lang/Float", "(F)V", value.value()) : nullptr;
}
inline jobject write_boxed_double(JNIEnv* env, std::optional<double> value) {
    return value ? new_boxed<double>(env, "java/lang/Double", "(D)V", value.value()) : nullptr;
}
inline jobject write_boxed_long(JNIEnv* env, std::optional<int64_t> value) {
    return value ? new_boxed<int64_t>(env, "java/lang/Long", "(J)V", value.value()) : nullptr;
}
inline jobject write_boxed_int(JNIEnv* env, std::optional<int32_t> value) {
    return value ? new_boxed<int32_t>(env, "java/lang/Integer", "(I)V", value.value()) : nullptr;
}
inline jobject write_boxed_boolean(JNIEnv* env, std::optional<bool> value) {
    return value ? new_boxed<jboolean>(env, "java/lang/Boolean", "(Z)V",
                                       value.value() ? JNI_TRUE : JNI_FALSE)
                 : nullptr;
}

inline Opaque read_opaque(JNIEnv*, jobject obj) {
    return Opaque(obj);
}

inline jobject write_opaque(JNIEnv*, Opaque value) {
    return value.get();
}

// -------------------------------------------------------------- byte arrays

inline std::vector<uint8_t> read_byte_array(JNIEnv* env, jobject object) {
    std::vector<uint8_t> out;
    if (object == nullptr) {
        return out;
    }
    auto* array = static_cast<jbyteArray>(object);
    const jsize length = env->GetArrayLength(array);
    out.resize(static_cast<size_t>(length));
    if (length > 0) {
        env->GetByteArrayRegion(array, 0, length, reinterpret_cast<jbyte*>(out.data()));
    }
    return out;
}

inline jbyteArray write_byte_array(JNIEnv* env, const std::vector<uint8_t>& value) {
    jbyteArray array = env->NewByteArray(static_cast<jsize>(value.size()));
    if (array != nullptr && !value.empty()) {
        env->SetByteArrayRegion(array, 0, static_cast<jsize>(value.size()),
                                reinterpret_cast<const jbyte*>(value.data()));
    }
    return array;
}

// ----------------------------------------------------------- object arrays

/// Reads a Java array of SDK objects.
template <typename T, typename ReadFn>
inline std::vector<T> read_object_array(JNIEnv* env, jobject object, ReadFn read) {
    std::vector<T> out;
    if (object == nullptr) {
        return out;
    }
    auto* array = static_cast<jobjectArray>(object);
    const jsize length = env->GetArrayLength(array);
    out.reserve(static_cast<size_t>(length));
    for (jsize i = 0; i < length; ++i) {
        jobject element = env->GetObjectArrayElement(array, i);
        out.push_back(read(env, element));
        if (element != nullptr) {
            env->DeleteLocalRef(element);
        }
    }
    return out;
}

/// Writes a Java array of SDK objects.
template <typename T, typename WriteFn>
inline jobject write_object_array(JNIEnv* env, const std::vector<T>& values, WriteFn write) {
    jclass cls = env->FindClass("java/lang/Object");
    jobjectArray array = env->NewObjectArray(static_cast<jsize>(values.size()), cls, nullptr);
    for (size_t i = 0; i < values.size(); ++i) {
        jobject element = write(env, values[i]);
        env->SetObjectArrayElement(array, static_cast<jsize>(i), element);
        if (element != nullptr) {
            env->DeleteLocalRef(element);
        }
    }
    env->DeleteLocalRef(cls);
    return array;
}

// -------------------------------------------------------------------- maps

template <typename V, typename ReadFn>
inline std::map<std::string, V> read_map(JNIEnv* env, jobject map, ReadFn read) {
    std::map<std::string, V> out;
    if (map == nullptr) {
        return out;
    }
    jclass cls = env->GetObjectClass(map);
    jmethodID entrySet = env->GetMethodID(cls, "entrySet", "()Ljava/util/Set;");
    jobject entries = env->CallObjectMethod(map, entrySet);
    jclass setCls = env->GetObjectClass(entries);
    jmethodID iterator = env->GetMethodID(setCls, "iterator", "()Ljava/util/Iterator;");
    jobject it = env->CallObjectMethod(entries, iterator);
    jclass itCls = env->GetObjectClass(it);
    jmethodID hasNext = env->GetMethodID(itCls, "hasNext", "()Z");
    jmethodID next = env->GetMethodID(itCls, "next", "()Ljava/util/Map$Entry;");
    jclass entryCls = env->FindClass("java/util/Map$Entry");
    jmethodID getKey = env->GetMethodID(entryCls, "getKey", "()Ljava/lang/Object;");
    jmethodID getValue = env->GetMethodID(entryCls, "getValue", "()Ljava/lang/Object;");
    while (env->CallBooleanMethod(it, hasNext) == JNI_TRUE) {
        jobject entry = env->CallObjectMethod(it, next);
        jobject key = env->CallObjectMethod(entry, getKey);
        jobject value = env->CallObjectMethod(entry, getValue);
        out[read_string(env, key)] = read(env, value);
        env->DeleteLocalRef(key);
        env->DeleteLocalRef(value);
        env->DeleteLocalRef(entry);
    }
    env->DeleteLocalRef(itCls);
    env->DeleteLocalRef(it);
    env->DeleteLocalRef(setCls);
    env->DeleteLocalRef(entries);
    env->DeleteLocalRef(entryCls);
    env->DeleteLocalRef(cls);
    return out;
}

template <typename V, typename WriteFn>
inline jobject write_map(JNIEnv* env, const std::map<std::string, V>& values, WriteFn write) {
    jclass cls = env->FindClass("java/util/HashMap");
    jobject map = env->NewObject(cls, env->GetMethodID(cls, "<init>", "()V"));
    jmethodID put = env->GetMethodID(cls, "put",
                                     "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    for (const auto& entry : values) {
        jstring key = env->NewStringUTF(entry.first.c_str());
        jobject value = write(env, entry.second);
        env->CallObjectMethod(map, put, key, value);
        env->DeleteLocalRef(key);
        if (value != nullptr) {
            env->DeleteLocalRef(value);
        }
    }
    env->DeleteLocalRef(cls);
    return map;
}

// ------------------------------------------------------------------- lists

template <typename T, typename ReadFn>
inline std::vector<T> read_list(JNIEnv* env, jobject obj, ReadFn read) {
    std::vector<T> out;
    if (obj == nullptr) {
        return out;
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID size = env->GetMethodID(cls, "size", "()I");
    jmethodID get = env->GetMethodID(cls, "get", "(I)Ljava/lang/Object;");
    const int count = env->CallIntMethod(obj, size);
    out.reserve(static_cast<size_t>(count));
    for (int i = 0; i < count; ++i) {
        jobject element = env->CallObjectMethod(obj, get, i);
        out.push_back(read(env, element));
        if (element != nullptr) {
            env->DeleteLocalRef(element);
        }
    }
    env->DeleteLocalRef(cls);
    return out;
}

template <typename T, typename WriteFn>
inline jobject write_list(JNIEnv* env, const std::vector<T>& values, WriteFn write) {
    jclass cls = env->FindClass("java/util/ArrayList");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "()V");
    jobject list = env->NewObject(cls, ctor);
    jmethodID add = env->GetMethodID(cls, "add", "(Ljava/lang/Object;)Z");
    for (const T& value : values) {
        jobject element = write(env, value);
        env->CallBooleanMethod(list, add, element);
        if (element != nullptr) {
            env->DeleteLocalRef(element);
        }
    }
    env->DeleteLocalRef(cls);
    return list;
}

template <typename T, typename ReadFn>
inline std::set<T> read_enumset(JNIEnv* env, jobject obj, ReadFn read) {
    std::set<T> out;
    if (obj == nullptr) {
        return out;
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID iterator = env->GetMethodID(cls, "iterator", "()Ljava/util/Iterator;");
    jobject it = env->CallObjectMethod(obj, iterator);
    jclass itCls = env->GetObjectClass(it);
    jmethodID hasNext = env->GetMethodID(itCls, "hasNext", "()Z");
    jmethodID next = env->GetMethodID(itCls, "next", "()Ljava/lang/Object;");
    while (env->CallBooleanMethod(it, hasNext) == JNI_TRUE) {
        jobject element = env->CallObjectMethod(it, next);
        out.insert(read(env, element));
        if (element != nullptr) {
            env->DeleteLocalRef(element);
        }
    }
    env->DeleteLocalRef(itCls);
    env->DeleteLocalRef(it);
    env->DeleteLocalRef(cls);
    return out;
}

template <typename T, typename WriteFn>
inline jobject write_enumset(JNIEnv* env, const std::set<T>& values, WriteFn write) {
    jclass cls = env->FindClass("java/util/EnumSet");
    jmethodID noneOf = env->GetMethodID(cls, "noneOf", "(Ljava/lang/Class;)Ljava/util/EnumSet;");
    // The element class is not known here; callers that need an EnumSet pass
    // a class literal through write_enumset's writer instead.
    (void)noneOf;
    (void)values;
    (void)write;
    return nullptr;
}

/**
 * Reads the native handle out of a Java proxy object.
 *
 * A proxy instance holds its CppProxy in `mProxy`, and the CppProxy holds the
 * `long` handle. Both fields are private, but JNI ignores access checks.
 */
inline jlong read_handle(JNIEnv* env, jobject obj, const char* proxyType) {
    if (obj == nullptr) {
        return 0;
    }
    jclass cls = env->GetObjectClass(obj);
    if (cls == nullptr) {
        return 0;
    }
    jfieldID proxyField = env->GetFieldID(cls, "mProxy", proxyType);
    jlong handle = 0;
    if (proxyField != nullptr) {
        jobject proxy = env->GetObjectField(obj, proxyField);
        if (proxy != nullptr) {
            jclass proxyClass = env->GetObjectClass(proxy);
            jfieldID handleField = env->GetFieldID(proxyClass, "mNativeHandle", "J");
            if (handleField != nullptr) {
                handle = env->GetLongField(proxy, handleField);
            }
            env->DeleteLocalRef(proxyClass);
            env->DeleteLocalRef(proxy);
        }
    }
    env->DeleteLocalRef(cls);
    return handle;
}

/**
 * Wraps a native implementation object in a Java proxy instance.
 *
 * The Java class is generated with an `internal constructor(handle: Long)`,
 * so the handle is the address of the implementation object. The Java side
 * owns the object from then on and releases it through `nativeDestroy`.
 */
template <typename T>
inline jobject wrap_proxy(JNIEnv* env, T* impl, const char* className) {
    if (impl == nullptr) {
        return nullptr;
    }
    jclass cls = env->FindClass(className);
    if (cls == nullptr) {
        return nullptr;
    }
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(J)V");
    jobject result = env->NewObject(cls, ctor, reinterpret_cast<jlong>(impl));
    env->DeleteLocalRef(cls);
    return result;
}

// ------------------------------------------------------------------ return

inline void default_void() {}
inline jobject default_object() { return nullptr; }

}  // namespace jni
