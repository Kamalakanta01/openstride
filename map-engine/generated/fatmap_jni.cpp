// Generated from the Fatmap SDK interface contract. Do not edit by hand.
//
// One JNI entry point per native method of the original Fatmap SDK, with
// identical symbol names, so the shared object is a drop-in replacement.

#include <jni.h>

#include "fatmap_api.h"
#include "fatmap_engine.h"
#include "fatmap_jni_types.h"
#include "fatmap_jni_values.h"

using namespace fatmap;

namespace {

// ---- ActivityContent -------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_ActivityContent_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::ActivityContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_ActivityContent_00024CppProxy_native_1applyActivityGradient(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::ActivityContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_list<Color>(env, a1, [](JNIEnv* e, jobject o) { return jni::read_Color(e, o); });
    proxy->native_applyActivityGradient(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_ActivityContent_00024CppProxy_native_1applyActivityPolylineHighlights(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::ActivityContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_list<PolylineHighlightSection>(env, a1, [](JNIEnv* e, jobject o) { return jni::read_PolylineHighlightSection(e, o); });
    proxy->native_applyActivityPolylineHighlights(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_ActivityContent_00024CppProxy_native_1clearActivityGradient(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::ActivityContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    proxy->native_clearActivityGradient(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_ActivityContent_00024CppProxy_native_1clearActivityPolylineHighlights(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::ActivityContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    proxy->native_clearActivityPolylineHighlights(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_ActivityContent_00024CppProxy_native_1displayActivity(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2) {
    auto* proxy = fatmap::resolve<Proxy::ActivityContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_WorldLine2(env, a1);
    const auto p2 = jni::read_IntervalInt(env, a2);
    proxy->native_displayActivity(p0, p1, p2);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_ActivityContent_00024CppProxy_native_1displayActivity2(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2) {
    auto* proxy = fatmap::resolve<Proxy::ActivityContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_WorldLine2(env, a1);
    const auto p2 = jni::read_list<ActivitySection>(env, a2, [](JNIEnv* e, jobject o) { return jni::read_ActivitySection(e, o); });
    proxy->native_displayActivity2(p0, p1, p2);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_ActivityContent_00024CppProxy_native_1displayActivityTruncated(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2) {
    auto* proxy = fatmap::resolve<Proxy::ActivityContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_WorldLine2(env, a1);
    const auto p2 = jni::read_IntervalInt(env, a2);
    proxy->native_displayActivityTruncated(p0, p1, p2);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_ActivityContent_00024CppProxy_native_1displayActivityWithPrivacyIcons(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2) {
    auto* proxy = fatmap::resolve<Proxy::ActivityContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_WorldLine2(env, a1);
    const auto p2 = jni::read_IntervalInt(env, a2);
    proxy->native_displayActivityWithPrivacyIcons(p0, p1, p2);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_ActivityContent_00024CppProxy_native_1removeActivity(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::ActivityContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    proxy->native_removeActivity(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_ActivityContent_00024CppProxy_native_1removeAllActivities(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::ActivityContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_removeAllActivities();
}

// ---- Camera ----------------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1addInteractionListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_addInteractionListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1addStateListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_addStateListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1enterFirstPersonView(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    double a0,
    double a1,
    float a2) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    const auto p1 = a1;
    const auto p2 = a2;
    proxy->native_enterFirstPersonView(p0, p1, p2);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1flyThrough(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2,
    jobject a3,
    jobject a4,
    jobject a5,
    jobject a6) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_list<WorldPoint3>(env, a0, [](JNIEnv* e, jobject o) { return jni::read_WorldPoint3(e, o); });
    const auto p1 = jni::read_string(env, a1);
    const auto p2 = jni::read_string(env, a2);
    const auto p3 = jni::read_CameraMargins(env, a3);
    const auto p4 = jni::read_opaque(env, a4);
    const auto p5 = jni::read_CameraTargetPlus(env, a5);
    const auto p6 = jni::read_list<std::optional<float>>(env, a6, [](JNIEnv* e, jobject o) { return jni::read_boxed_float(e, o); });
    proxy->native_flyThrough(p0, p1, p2, p3, p4, p5, p6);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1flyThroughInteractive(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2,
    jobject a3,
    jobject a4,
    jobject a5,
    bool a6,
    jobject a7,
    jobject a8) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = jni::read_list<WorldPoint3>(env, a0, [](JNIEnv* e, jobject o) { return jni::read_WorldPoint3(e, o); });
    const auto p1 = jni::read_string(env, a1);
    const auto p2 = jni::read_string(env, a2);
    const auto p3 = jni::read_CameraMargins(env, a3);
    const auto p4 = jni::read_opaque(env, a4);
    const auto p5 = jni::read_CameraTargetPlus(env, a5);
    const auto p6 = a6;
    const auto p7 = jni::read_opaque(env, a7);
    const auto p8 = jni::read_list<std::optional<float>>(env, a8, [](JNIEnv* e, jobject o) { return jni::read_boxed_float(e, o); });
    const auto result = proxy->native_flyThroughInteractive(p0, p1, p2, p3, p4, p5, p6, p7, p8);
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/CameraFlyThroughControls");
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1flyTo(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    double a0,
    double a1,
    float a2,
    jobject a3,
    jobject a4,
    float a5,
    jobject a6,
    jobject a7) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    const auto p1 = a1;
    const auto p2 = a2;
    const auto p3 = jni::read_boxed_float(env, a3);
    const auto p4 = jni::read_boxed_float(env, a4);
    const auto p5 = a5;
    const auto p6 = jni::read_CameraMargins(env, a6);
    const auto p7 = jni::read_opaque(env, a7);
    proxy->native_flyTo(p0, p1, p2, p3, p4, p5, p6, p7);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1flyToBounds(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2,
    float a3,
    jobject a4,
    jobject a5) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_WorldBounds2(env, a0);
    const auto p1 = jni::read_boxed_float(env, a1);
    const auto p2 = jni::read_boxed_float(env, a2);
    const auto p3 = a3;
    const auto p4 = jni::read_CameraMargins(env, a4);
    const auto p5 = jni::read_opaque(env, a5);
    proxy->native_flyToBounds(p0, p1, p2, p3, p4, p5);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1flyToTarget(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    float a1,
    bool a2,
    jobject a3,
    jobject a4) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_CameraTarget(env, a0);
    const auto p1 = a1;
    const auto p2 = a2;
    const auto p3 = jni::read_CameraMargins(env, a3);
    const auto p4 = jni::read_opaque(env, a4);
    proxy->native_flyToTarget(p0, p1, p2, p3, p4);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1flyToWithZoomLevel(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    double a0,
    double a1,
    float a2,
    jobject a3,
    jobject a4,
    float a5,
    jobject a6,
    jobject a7) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    const auto p1 = a1;
    const auto p2 = a2;
    const auto p3 = jni::read_boxed_float(env, a3);
    const auto p4 = jni::read_boxed_float(env, a4);
    const auto p5 = a5;
    const auto p6 = jni::read_CameraMargins(env, a6);
    const auto p7 = jni::read_opaque(env, a7);
    proxy->native_flyToWithZoomLevel(p0, p1, p2, p3, p4, p5, p6, p7);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1flyover(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0,
    jobject a1,
    jobject a2,
    jobject a3,
    jobject a4,
    jobject a5,
    jobject a6) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = a0;
    const auto p1 = jni::read_list<WorldPoint3>(env, a1, [](JNIEnv* e, jobject o) { return jni::read_WorldPoint3(e, o); });
    const auto p2 = jni::read_CameraMargins(env, a2);
    const auto p3 = jni::read_opaque(env, a3);
    const auto p4 = jni::read_CameraTargetPlus(env, a4);
    const auto p5 = jni::read_opaque(env, a5);
    const auto p6 = jni::read_list<std::optional<float>>(env, a6, [](JNIEnv* e, jobject o) { return jni::read_boxed_float(e, o); });
    const auto result = proxy->native_flyover(p0, p1, p2, p3, p4, p5, p6);
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/CameraFlyThroughControls");
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1followUserLocation(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2,
    jobject a3,
    jobject a4) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_boxed_float(env, a0);
    const auto p1 = jni::read_boxed_float(env, a1);
    const auto p2 = jni::read_boxed_float(env, a2);
    const auto p3 = jni::read_FollowMode(env, a3);
    const auto p4 = jni::read_CameraMargins(env, a4);
    proxy->native_followUserLocation(p0, p1, p2, p3, p4);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1followUserLocationWithZoomLevel(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2,
    jobject a3,
    jobject a4,
    jobject a5) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_WorldPoint2(env, a0);
    const auto p1 = jni::read_boxed_float(env, a1);
    const auto p2 = jni::read_boxed_float(env, a2);
    const auto p3 = jni::read_boxed_float(env, a3);
    const auto p4 = jni::read_FollowMode(env, a4);
    const auto p5 = jni::read_CameraMargins(env, a5);
    proxy->native_followUserLocationWithZoomLevel(p0, p1, p2, p3, p4, p5);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1getCameraViewType(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getCameraViewType();
    return jni::write_CameraViewType(env, result);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1getCustomLookAtPoint(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0,
    float a1) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = a0;
    const auto p1 = a1;
    const auto result = proxy->native_getCustomLookAtPoint(p0, p1);
    return jni::write_LookAtPoint(env, result);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1getLookAtPoint(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getLookAtPoint();
    return jni::write_LookAtPoint(env, result);
}
extern "C" JNIEXPORT float JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1getScaleMetersPerPixel(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_getScaleMetersPerPixel();
    return result;
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1getScreenPosition(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = jni::read_WorldPoint2(env, a0);
    const auto result = proxy->native_getScreenPosition(p0);
    return jni::write_PointScreenPosition(env, result);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1getTarget(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getTarget();
    return jni::write_CameraTarget(env, result);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1getVisibleAreaBounds(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    float a1) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = jni::read_CameraMargins(env, a0);
    const auto p1 = a1;
    const auto result = proxy->native_getVisibleAreaBounds(p0, p1);
    return jni::write_VisibleAreaBounds(env, result);
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1isWorldPointVisible(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    bool a1) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto p0 = jni::read_WorldPoint3(env, a0);
    const auto p1 = a1;
    const auto result = proxy->native_isWorldPointVisible(p0, p1);
    return result;
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1removeInteractionListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_removeInteractionListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1removeStateListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_removeStateListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1setBearing(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0,
    float a1,
    jobject a2) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    const auto p1 = a1;
    const auto p2 = jni::read_CameraMargins(env, a2);
    proxy->native_setBearing(p0, p1, p2);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1setCameraViewType(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_CameraViewType(env, a0);
    proxy->native_setCameraViewType(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1setDynamicExaggerationEnabled(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setDynamicExaggerationEnabled(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1setDynamicExaggerationMultiplier(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setDynamicExaggerationMultiplier(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1setRotationSpeed(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0,
    float a1) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    const auto p1 = a1;
    proxy->native_setRotationSpeed(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1setTranslationSpeed(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0,
    float a1) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    const auto p1 = a1;
    proxy->native_setTranslationSpeed(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1setZoomSpeed(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setZoomSpeed(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1stop(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_stop();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1translateTo(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    float a1,
    jobject a2,
    jobject a3) {
    auto* proxy = fatmap::resolve<Proxy::Camera>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_WorldPoint2(env, a0);
    const auto p1 = a1;
    const auto p2 = jni::read_CameraMargins(env, a2);
    const auto p3 = jni::read_opaque(env, a3);
    proxy->native_translateTo(p0, p1, p2, p3);
}

// ---- CameraDeltaTimeProvider -----------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_CameraDeltaTimeProvider_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::CameraDeltaTimeProvider>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT float JNICALL Java_com_fatmap_sdk_api_CameraDeltaTimeProvider_00024CppProxy_native_1getDeltaTime(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::CameraDeltaTimeProvider>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_getDeltaTime();
    return result;
}

// ---- CameraFlyThroughControls ----------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_CameraFlyThroughControls_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::CameraFlyThroughControls>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT float JNICALL Java_com_fatmap_sdk_api_CameraFlyThroughControls_00024CppProxy_native_1getDurationSeconds(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::CameraFlyThroughControls>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_getDurationSeconds();
    return result;
}
extern "C" JNIEXPORT float JNICALL Java_com_fatmap_sdk_api_CameraFlyThroughControls_00024CppProxy_native_1getTrackProgress(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::CameraFlyThroughControls>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_getTrackProgress();
    return result;
}
extern "C" JNIEXPORT float JNICALL Java_com_fatmap_sdk_api_CameraFlyThroughControls_00024CppProxy_native_1getTrackSpeedMultiplier(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::CameraFlyThroughControls>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_getTrackSpeedMultiplier();
    return result;
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_CameraFlyThroughControls_00024CppProxy_native_1isActive(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::CameraFlyThroughControls>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_isActive();
    return result;
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_CameraFlyThroughControls_00024CppProxy_native_1isCameraDetached(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::CameraFlyThroughControls>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_isCameraDetached();
    return result;
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_CameraFlyThroughControls_00024CppProxy_native_1isRefocusPossible(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::CameraFlyThroughControls>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_isRefocusPossible();
    return result;
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_CameraFlyThroughControls_00024CppProxy_native_1refocus(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::CameraFlyThroughControls>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_refocus();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_CameraFlyThroughControls_00024CppProxy_native_1seekToAnimationStart(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::CameraFlyThroughControls>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_seekToAnimationStart();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_CameraFlyThroughControls_00024CppProxy_native_1setAnimationSpeedMultiplier(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0) {
    auto* proxy = fatmap::resolve<Proxy::CameraFlyThroughControls>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setAnimationSpeedMultiplier(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_CameraFlyThroughControls_00024CppProxy_native_1setCameraDistanceMultiplier(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0) {
    auto* proxy = fatmap::resolve<Proxy::CameraFlyThroughControls>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setCameraDistanceMultiplier(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_CameraFlyThroughControls_00024CppProxy_native_1setTrackProgress(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0) {
    auto* proxy = fatmap::resolve<Proxy::CameraFlyThroughControls>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setTrackProgress(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_CameraFlyThroughControls_00024CppProxy_native_1setTrackSpeedMultiplier(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0) {
    auto* proxy = fatmap::resolve<Proxy::CameraFlyThroughControls>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setTrackSpeedMultiplier(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_CameraFlyThroughControls_00024CppProxy_native_1stop(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::CameraFlyThroughControls>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_stop();
}

// ---- CommandService --------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_CommandService_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::CommandService>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_CommandService_00024CppProxy_native_1execute(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::CommandService>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto result = proxy->native_execute(p0);
    return jni::write_string(env, result);
}

// ---- Content ---------------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_native_1addCircleMarker(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    double a1,
    double a2,
    float a3) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = a1;
    const auto p2 = a2;
    const auto p3 = a3;
    proxy->native_addCircleMarker(p0, p1, p2, p3);
}
extern "C" JNIEXPORT int64_t JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_native_1addCustomGradientLine(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2,
    jobject a3,
    jobject a4) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto p0 = jni::read_WorldLine2(env, a0);
    const auto p1 = jni::read_string(env, a1);
    const auto p2 = jni::read_string(env, a2);
    const auto p3 = jni::read_string(env, a3);
    const auto p4 = jni::read_list<Color>(env, a4, [](JNIEnv* e, jobject o) { return jni::read_Color(e, o); });
    const auto result = proxy->native_addCustomGradientLine(p0, p1, p2, p3, p4);
    return result;
}
extern "C" JNIEXPORT int64_t JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_native_1addCustomLine(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2,
    bool a3,
    jobject a4) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto p0 = jni::read_WorldLine2(env, a0);
    const auto p1 = jni::read_string(env, a1);
    const auto p2 = jni::read_string(env, a2);
    const auto p3 = a3;
    const auto p4 = jni::read_string(env, a4);
    const auto result = proxy->native_addCustomLine(p0, p1, p2, p3, p4);
    return result;
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_native_1addDroppedPin(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_WorldPoint2(env, a1);
    proxy->native_addDroppedPin(p0, p1);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_native_1getActivityContent(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getActivityContent();
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/ActivityContent");
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_native_1getOsmFilter(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getOsmFilter();
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/OsmFilter");
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_native_1getRecordContent(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getRecordContent();
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/RecordContent");
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_native_1getRouteContent(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getRouteContent();
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/RouteContent");
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_native_1getSegmentsFilter(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getSegmentsFilter();
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/SegmentsFilter");
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_native_1getStravaPoiFilter(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getStravaPoiFilter();
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/StravaPoiFilter");
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_native_1removeAllDroppedPins(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_removeAllDroppedPins();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_native_1removeCircleMarker(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    proxy->native_removeCircleMarker(p0);
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_native_1removeCustomLine(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    int64_t a0) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto p0 = a0;
    const auto result = proxy->native_removeCustomLine(p0);
    return result;
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Content_00024CppProxy_native_1removeDroppedPin(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::Content>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    proxy->native_removeDroppedPin(p0);
}

// ---- ContentSelectionHandle ------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_ContentSelectionHandle_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::ContentSelectionHandle>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}

// ---- DebugApi --------------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_DebugApi_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::DebugApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_DebugApi_00024CppProxy_native_1dumpEngineState(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::DebugApi>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_dumpEngineState();
    return jni::write_string(env, result);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_DebugApi_00024CppProxy_native_1hideFpsStatsOverlay(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::DebugApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_hideFpsStatsOverlay();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_DebugApi_00024CppProxy_native_1showFpsStatsOverlay(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::DebugApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_DebugOverlayCorner(env, a0);
    const auto p1 = jni::read_DebugOverlayPadding(env, a1);
    proxy->native_showFpsStatsOverlay(p0, p1);
}

// ---- DeviceOrientationListener ---------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_DeviceOrientationListener_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::DeviceOrientationListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_DeviceOrientationListener_00024CppProxy_native_1onDeviceOrientationChanged(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::DeviceOrientationListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_DeviceOrientation(env, a0);
    proxy->native_onDeviceOrientationChanged(p0);
}

// ---- EngineConfiguration ---------------------------------------
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_createDefault(
    JNIEnv* env,
    jobject thiz) {
    auto* result = fatmap::static_EngineConfiguration_createDefault();
    if (result == nullptr) {
        return nullptr;
    }
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/EngineConfiguration");
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1getBreadcrumbRecorder(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getBreadcrumbRecorder();
    return jni::write_opaque(env, result);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1getCameraTarget(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getCameraTarget();
    return jni::write_CameraTarget(env, result);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1getClientName(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getClientName();
    return jni::write_string(env, result);
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1getEnableRasterMegaTiles(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_getEnableRasterMegaTiles();
    return result;
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1getLogLevel(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getLogLevel();
    return jni::write_LogLevel(env, result);
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1getLoggingToStdOut(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_getLoggingToStdOut();
    return result;
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1getReindexOnDiskResources(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_getReindexOnDiskResources();
    return result;
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1getUseOnDiskCache(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_getUseOnDiskCache();
    return result;
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1loadFromFile(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto result = proxy->native_loadFromFile(p0);
    return result;
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1setBreadcrumbRecorder(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_setBreadcrumbRecorder(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1setCameraTarget(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_CameraTarget(env, a0);
    proxy->native_setCameraTarget(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1setClientName(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    proxy->native_setClientName(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1setEnableRasterMegaTiles(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setEnableRasterMegaTiles(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1setLogLevel(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_LogLevel(env, a0);
    proxy->native_setLogLevel(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1setLoggingToStdOut(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setLoggingToStdOut(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1setReindexOnDiskResources(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setReindexOnDiskResources(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1setUseOnDiskCache(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::EngineConfiguration>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setUseOnDiskCache(p0);
}

// ---- GestureRecognizerControl ----------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_GestureRecognizerControl_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::GestureRecognizerControl>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_GestureRecognizerControl_00024CppProxy_native_1setAllGesturesEnabled(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::GestureRecognizerControl>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setAllGesturesEnabled(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_GestureRecognizerControl_00024CppProxy_native_1setDoubleTapGesturesEnabled(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::GestureRecognizerControl>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setDoubleTapGesturesEnabled(p0);
}

// ---- LuaAnimationHandler ---------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_LuaAnimationHandler_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::LuaAnimationHandler>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_LuaAnimationHandler_00024CppProxy_native_1onError(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::LuaAnimationHandler>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_LuaScriptError(env, a0);
    proxy->native_onError(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_LuaAnimationHandler_00024CppProxy_native_1onProgress(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0) {
    auto* proxy = fatmap::resolve<Proxy::LuaAnimationHandler>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_onProgress(p0);
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_LuaAnimationHandler_00024CppProxy_native_1shouldAdvanceAnimation(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::LuaAnimationHandler>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_shouldAdvanceAnimation();
    return result;
}

// ---- MREHolder -------------------------------------------------
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_MREHolder_00024CppProxy_create(
    JNIEnv* env,
    jobject thiz,
    jobject a0,
    jobject a1,
    jobject a2,
    jobject a3,
    jobject a4) {
    auto* result = fatmap::static_MREHolder_create();
    if (result == nullptr) {
        return nullptr;
    }
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/MREHolder");
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_MREHolder_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::MREHolder>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_MREHolder_00024CppProxy_native_1handleMemoryWarning(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::MREHolder>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_handleMemoryWarning();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_MREHolder_00024CppProxy_native_1handleOnAppDidEnterBackground(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::MREHolder>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_handleOnAppDidEnterBackground();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_MREHolder_00024CppProxy_native_1handleOnAppDidEnterForeground(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::MREHolder>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_handleOnAppDidEnterForeground();
}

// ---- Offline ---------------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Offline_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Offline>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Offline_00024CppProxy_native_1loadBundles(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::Offline>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_list<std::string>(env, a0, [](JNIEnv* e, jobject o) { return jni::read_string(e, o); });
    const auto p1 = jni::read_opaque(env, a1);
    proxy->native_loadBundles(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Offline_00024CppProxy_native_1setNonBundleResourcesAllowed(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::Offline>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setNonBundleResourcesAllowed(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Offline_00024CppProxy_native_1unloadBundles(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::Offline>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_list<std::string>(env, a0, [](JNIEnv* e, jobject o) { return jni::read_string(e, o); });
    const auto p1 = jni::read_opaque(env, a1);
    proxy->native_unloadBundles(p0, p1);
}

// ---- OsmFilter -------------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_OsmFilter_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::OsmFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_OsmFilter_00024CppProxy_native_1setShow(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::OsmFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setShow(p0);
}

// ---- OverriddenFeatureProperties -------------------------------
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_OverriddenFeatureProperties_00024CppProxy_create(
    JNIEnv* env,
    jobject thiz) {
    const auto result = fatmap::static_OverriddenFeatureProperties_create();
    return jni::write_OverriddenFeatureProperties(env, result);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_OverriddenFeatureProperties_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::OverriddenFeatureProperties>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_OverriddenFeatureProperties_00024CppProxy_native_1addBooleanProperty(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    bool a1) {
    auto* proxy = fatmap::resolve<Proxy::OverriddenFeatureProperties>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = a1;
    proxy->native_addBooleanProperty(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_OverriddenFeatureProperties_00024CppProxy_native_1addDoubleProperty(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    double a1) {
    auto* proxy = fatmap::resolve<Proxy::OverriddenFeatureProperties>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = a1;
    proxy->native_addDoubleProperty(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_OverriddenFeatureProperties_00024CppProxy_native_1addIntegerProperty(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    int64_t a1) {
    auto* proxy = fatmap::resolve<Proxy::OverriddenFeatureProperties>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = a1;
    proxy->native_addIntegerProperty(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_OverriddenFeatureProperties_00024CppProxy_native_1addStringProperty(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::OverriddenFeatureProperties>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_string(env, a1);
    proxy->native_addStringProperty(p0, p1);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_OverriddenFeatureProperties_00024CppProxy_native_1getBooleanProperty(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::OverriddenFeatureProperties>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto result = proxy->native_getBooleanProperty(p0);
    return jni::write_boxed_boolean(env, result);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_OverriddenFeatureProperties_00024CppProxy_native_1getDoubleProperty(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::OverriddenFeatureProperties>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto result = proxy->native_getDoubleProperty(p0);
    return jni::write_boxed_double(env, result);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_OverriddenFeatureProperties_00024CppProxy_native_1getIntegerProperty(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::OverriddenFeatureProperties>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto result = proxy->native_getIntegerProperty(p0);
    return jni::write_boxed_long(env, result);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_OverriddenFeatureProperties_00024CppProxy_native_1getStringProperty(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::OverriddenFeatureProperties>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto result = proxy->native_getStringProperty(p0);
    return jni::write_string(env, result);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_OverriddenFeatureProperties_00024CppProxy_native_1removeProperty(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::OverriddenFeatureProperties>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    proxy->native_removeProperty(p0);
}

// ---- PrivateApi ------------------------------------------------
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_getPerformanceTestsNames(
    JNIEnv* env,
    jobject thiz) {
    const auto result = fatmap::static_PrivateApi_getPerformanceTestsNames();
    return jni::write_list<std::string>(env, result, [](JNIEnv* e, const std::string& v) { return jni::write_string(e, v); });
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1addPerformanceMetricsListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_addPerformanceMetricsListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1clearLuaPersistedStore(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_clearLuaPersistedStore();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1finishTwoPassVideoRendering(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_finishTwoPassVideoRendering();
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1getGivenUpDownloadsInfo(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    int64_t a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = a0;
    const auto result = proxy->native_getGivenUpDownloadsInfo(p0);
    return jni::write_GivenUpDownloadsInfo(env, result);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1getMemoryStats(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getMemoryStats();
    return jni::write_MemoryStats(env, result);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1getPreparedCommandService(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = jni::read_opaque(env, a0);
    const auto result = proxy->native_getPreparedCommandService(p0);
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/CommandService");
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1getVisibleAreaBoundsExt(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    float a1,
    jobject a2) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = jni::read_CameraMargins(env, a0);
    const auto p1 = a1;
    const auto p2 = jni::read_opaque(env, a2);
    const auto result = proxy->native_getVisibleAreaBoundsExt(p0, p1, p2);
    return jni::write_VisibleAreaBounds(env, result);
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1haveGivenUpTileDownloads(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_haveGivenUpTileDownloads();
    return result;
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1haveVisualContentLoadingWork(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto p0 = a0;
    const auto result = proxy->native_haveVisualContentLoadingWork(p0);
    return result;
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1isVsyncEnabled(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_isVsyncEnabled();
    return result;
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1recordResourcesNeededForSecondVideoRenderingPass(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_recordResourcesNeededForSecondVideoRenderingPass();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1refreshScreenInfo(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_refreshScreenInfo();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1reloadTerrain(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_reloadTerrain();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1removePerformanceMetricsListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_removePerformanceMetricsListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1resetOnlineAvailabilityData(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_resetOnlineAvailabilityData();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1resetTileDownloadHistory(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_resetTileDownloadHistory();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1runPerformanceTests(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_list<std::string>(env, a0, [](JNIEnv* e, jobject o) { return jni::read_string(e, o); });
    const auto p1 = jni::read_string(env, a1);
    const auto p2 = jni::read_opaque(env, a2);
    proxy->native_runPerformanceTests(p0, p1, p2);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1runScreenshotTests(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_list<std::string>(env, a0, [](JNIEnv* e, jobject o) { return jni::read_string(e, o); });
    const auto p1 = jni::read_string(env, a1);
    const auto p2 = jni::read_opaque(env, a2);
    proxy->native_runScreenshotTests(p0, p1, p2);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1saveWindowScreenshot(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    proxy->native_saveWindowScreenshot(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setDiskCacheSize(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    int64_t a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setDiskCacheSize(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setDownloadHeightmapsOnly(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setDownloadHeightmapsOnly(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setLodSubdivisionThresholdEnabled(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setLodSubdivisionThresholdEnabled(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setLuaGlobalAnimation(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    bool a2) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_AnimatedImage(env, a1);
    const auto p2 = a2;
    proxy->native_setLuaGlobalAnimation(p0, p1, p2);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setLuaGlobalFloat(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    float a1) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = a1;
    proxy->native_setLuaGlobalFloat(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setLuaGlobalImage(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    bool a2) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_CustomImage(env, a1);
    const auto p2 = a2;
    proxy->native_setLuaGlobalImage(p0, p1, p2);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setLuaGlobalInt(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    int32_t a1) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = a1;
    proxy->native_setLuaGlobalInt(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setLuaGlobalLine(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_WorldLine2(env, a1);
    proxy->native_setLuaGlobalLine(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setLuaGlobalString(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_string(env, a1);
    proxy->native_setLuaGlobalString(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setOfflineMode(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setOfflineMode(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setPixelRatio(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setPixelRatio(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setResourceLoadingFromDiskAllowed(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setResourceLoadingFromDiskAllowed(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setShaderHotReloadingEnabled(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setShaderHotReloadingEnabled(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setShowSymbolBoundingBoxes(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setShowSymbolBoundingBoxes(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setStylingZoomLevelOverride(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_boxed_float(env, a0);
    proxy->native_setStylingZoomLevelOverride(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setSymbolScreenPaddingEnabled(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setSymbolScreenPaddingEnabled(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setSymbolVisibilityStateful(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setSymbolVisibilityStateful(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setTestAccountForPersonalHeatmap(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_PersonalHeatmapTestAccount(env, a0);
    proxy->native_setTestAccountForPersonalHeatmap(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setTileDownloadEventProcessor(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_setTileDownloadEventProcessor(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setVsyncEnabled(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setVsyncEnabled(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1setupDefaultMockTiles(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_setupDefaultMockTiles();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1startDownloadingResourcesForSecondVideoRenderingPass(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_startDownloadingResourcesForSecondVideoRenderingPass();
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1startEnteringTestingContext(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2,
    jobject a3) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = jni::read_opaque(env, a0);
    const auto p1 = jni::read_string(env, a1);
    const auto p2 = jni::read_string(env, a2);
    const auto p3 = jni::read_opaque(env, a3);
    const auto result = proxy->native_startEnteringTestingContext(p0, p1, p2, p3);
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/TestingContextHandle");
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1startLuaAnimationScript(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_boxed_float(env, a1);
    const auto p2 = jni::read_opaque(env, a2);
    proxy->native_startLuaAnimationScript(p0, p1, p2);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1startMockTileServer(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_startMockTileServer();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1startTrackPerfTest(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    float a1,
    int64_t a2,
    bool a3,
    jobject a4) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_list<WorldPoint3>(env, a0, [](JNIEnv* e, jobject o) { return jni::read_WorldPoint3(e, o); });
    const auto p1 = a1;
    const auto p2 = a2;
    const auto p3 = a3;
    const auto p4 = jni::read_opaque(env, a4);
    proxy->native_startTrackPerfTest(p0, p1, p2, p3, p4);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1startVideoRenderingFirstPass(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_startVideoRenderingFirstPass();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1startVideoRenderingSecondPass(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_startVideoRenderingSecondPass();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1stopLuaAnimationScript(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_stopLuaAnimationScript();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1stopMockTileServer(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_stopMockTileServer();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1switchToCustomStyle(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    bool a1,
    bool a2,
    bool a3,
    bool a4,
    bool a5,
    bool a6,
    jobject a7) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = a1;
    const auto p2 = a2;
    const auto p3 = a3;
    const auto p4 = a4;
    const auto p5 = a5;
    const auto p6 = a6;
    const auto p7 = jni::read_boxed_float(env, a7);
    proxy->native_switchToCustomStyle(p0, p1, p2, p3, p4, p5, p6, p7);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1updateLodCalculationControlParams(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::PrivateApi>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = jni::read_LodCalculationControlParams(env, a0);
    const auto result = proxy->native_updateLodCalculationControlParams(p0);
    return jni::write_LodCalculationControlParams(env, result);
}

// ---- RecordContent ---------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RecordContent_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::RecordContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RecordContent_00024CppProxy_native_1displayLiveSegments(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::RecordContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_list<CandidateSegment>(env, a0, [](JNIEnv* e, jobject o) { return jni::read_CandidateSegment(e, o); });
    proxy->native_displayLiveSegments(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RecordContent_00024CppProxy_native_1displayRecordingLine(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::RecordContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_WorldLine2(env, a0);
    proxy->native_displayRecordingLine(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RecordContent_00024CppProxy_native_1removeAllLiveSegments(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::RecordContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_removeAllLiveSegments();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RecordContent_00024CppProxy_native_1removeRecordingLine(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::RecordContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_removeRecordingLine();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RecordContent_00024CppProxy_native_1showRecordingLineFinishMarker(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::RecordContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_showRecordingLineFinishMarker(p0);
}

// ---- RouteContent ----------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RouteContent_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::RouteContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RouteContent_00024CppProxy_native_1addWaypointToRoute(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::RouteContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_RouteWaypoint(env, a1);
    proxy->native_addWaypointToRoute(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RouteContent_00024CppProxy_native_1displayAnimatedRoute(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2,
    jobject a3,
    jobject a4,
    bool a5,
    float a6,
    float a7,
    jobject a8) {
    auto* proxy = fatmap::resolve<Proxy::RouteContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_WorldLine2(env, a1);
    const auto p2 = jni::read_list<RouteSection>(env, a2, [](JNIEnv* e, jobject o) { return jni::read_RouteSection(e, o); });
    const auto p3 = jni::read_list<RouteWaypoint>(env, a3, [](JNIEnv* e, jobject o) { return jni::read_RouteWaypoint(e, o); });
    const auto p4 = jni::read_RouteSelectionStyle(env, a4);
    const auto p5 = a5;
    const auto p6 = a6;
    const auto p7 = a7;
    const auto p8 = jni::read_opaque(env, a8);
    proxy->native_displayAnimatedRoute(p0, p1, p2, p3, p4, p5, p6, p7, p8);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RouteContent_00024CppProxy_native_1displayRoute(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1,
    jobject a2,
    jobject a3,
    jobject a4,
    bool a5) {
    auto* proxy = fatmap::resolve<Proxy::RouteContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_WorldLine2(env, a1);
    const auto p2 = jni::read_list<RouteSection>(env, a2, [](JNIEnv* e, jobject o) { return jni::read_RouteSection(e, o); });
    const auto p3 = jni::read_list<RouteWaypoint>(env, a3, [](JNIEnv* e, jobject o) { return jni::read_RouteWaypoint(e, o); });
    const auto p4 = jni::read_RouteSelectionStyle(env, a4);
    const auto p5 = a5;
    proxy->native_displayRoute(p0, p1, p2, p3, p4, p5);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RouteContent_00024CppProxy_native_1displayRouteScrubber(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    double a0,
    double a1) {
    auto* proxy = fatmap::resolve<Proxy::RouteContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    const auto p1 = a1;
    proxy->native_displayRouteScrubber(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RouteContent_00024CppProxy_native_1fadeInUnselectedRoutes(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0,
    jobject a1,
    jobject a2) {
    auto* proxy = fatmap::resolve<Proxy::RouteContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    const auto p1 = jni::read_boxed_float(env, a1);
    const auto p2 = jni::read_opaque(env, a2);
    proxy->native_fadeInUnselectedRoutes(p0, p1, p2);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RouteContent_00024CppProxy_native_1removeAllRoutes(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::RouteContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_removeAllRoutes();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RouteContent_00024CppProxy_native_1removeRouteScrubber(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::RouteContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_removeRouteScrubber();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RouteContent_00024CppProxy_native_1removeRoutes(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::RouteContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_list<std::string>(env, a0, [](JNIEnv* e, jobject o) { return jni::read_string(e, o); });
    proxy->native_removeRoutes(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RouteContent_00024CppProxy_native_1selectRoute(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::RouteContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_RouteSelectionStyle(env, a1);
    proxy->native_selectRoute(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RouteContent_00024CppProxy_native_1selectWaypointOnRoute(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::RouteContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_string(env, a1);
    proxy->native_selectWaypointOnRoute(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RouteContent_00024CppProxy_native_1stopUnselectedRoutesFadeIn(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::RouteContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_stopUnselectedRoutesFadeIn();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RouteContent_00024CppProxy_native_1unselectRoute(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::RouteContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    proxy->native_unselectRoute(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_RouteContent_00024CppProxy_native_1unselectWaypointOnRoute(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::RouteContent>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto p1 = jni::read_string(env, a1);
    proxy->native_unselectWaypointOnRoute(p0, p1);
}

// ---- SegmentsFilter --------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_SegmentsFilter_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::SegmentsFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_SegmentsFilter_00024CppProxy_native_1getVisibleSegments(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::SegmentsFilter>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getVisibleSegments();
    return jni::write_list<SegmentFeature>(env, result, [](JNIEnv* e, const SegmentFeature& v) { return jni::write_SegmentFeature(e, v); });
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_SegmentsFilter_00024CppProxy_native_1hide(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::SegmentsFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_hide();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_SegmentsFilter_00024CppProxy_native_1selectSegment(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    int64_t a0) {
    auto* proxy = fatmap::resolve<Proxy::SegmentsFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_selectSegment(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_SegmentsFilter_00024CppProxy_native_1show(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::SegmentsFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_SegmentsFilterOptions(env, a0);
    proxy->native_show(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_SegmentsFilter_00024CppProxy_native_1unselectSegment(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::SegmentsFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_unselectSegment();
}

// ---- StravaPoiFilter -------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_StravaPoiFilter_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::StravaPoiFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_StravaPoiFilter_00024CppProxy_native_1disableBikeShares(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::StravaPoiFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_disableBikeShares();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_StravaPoiFilter_00024CppProxy_native_1disableStartPoints(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::StravaPoiFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_disableStartPoints();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_StravaPoiFilter_00024CppProxy_native_1enableBikeShares(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::StravaPoiFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_enableBikeShares();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_StravaPoiFilter_00024CppProxy_native_1enableStartPoints(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::StravaPoiFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_StartPointFilterOptions(env, a0);
    proxy->native_enableStartPoints(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_StravaPoiFilter_00024CppProxy_native_1selectPoi(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::StravaPoiFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_selectPoi(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_StravaPoiFilter_00024CppProxy_native_1setShow(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::StravaPoiFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setShow(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_StravaPoiFilter_00024CppProxy_native_1unselectPoi(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::StravaPoiFilter>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_unselectPoi();
}

// ---- TerrainEngine ---------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1addOnGridChangeListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_addOnGridChangeListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1addOnMouseMoveListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_addOnMouseMoveListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1addOnTapListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_addOnTapListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1addOverlayPolygon(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_OverlayPolygon(env, a0);
    proxy->native_addOverlayPolygon(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1addPostUpdateListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_addPostUpdateListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1addTileLoadingListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_addTileLoadingListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1addViewUpdateListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_addViewUpdateListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1didEnterBackground(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_didEnterBackground();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1didEnterForeground(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_didEnterForeground();
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1getCamera(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getCamera();
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/Camera");
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1getContent(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getContent();
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/Content");
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1getDebugApi(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getDebugApi();
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/DebugApi");
}
extern "C" JNIEXPORT double JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1getElevation(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto p0 = jni::read_WorldPoint2(env, a0);
    const auto result = proxy->native_getElevation(p0);
    return result;
}
extern "C" JNIEXPORT float JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1getFps(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_getFps();
    return result;
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1getGridUnits(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getGridUnits();
    return jni::write_UnitsInfo(env, result);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1getOffline(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getOffline();
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/Offline");
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1getOverriddenFeatureProperties(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getOverriddenFeatureProperties();
    return jni::write_OverriddenFeatureProperties(env, result);
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1getPrivateApi(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto p0 = jni::read_string(env, a0);
    const auto result = proxy->native_getPrivateApi(p0);
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/PrivateApi");
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1getUtils(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_getUtils();
    return jni::wrap_proxy(env, result, "com/fatmap/sdk/api/Utils");
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1receivedMemoryWarning(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_receivedMemoryWarning();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1removeAllOverlayPolygons(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_removeAllOverlayPolygons();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1removeOnGridChangeListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_removeOnGridChangeListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1removeOnMouseMoveListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_removeOnMouseMoveListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1removeOnTapListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_removeOnTapListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1removePostUpdateListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_removePostUpdateListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1removeTileLoadingListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_removeTileLoadingListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1removeViewUpdateListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_removeViewUpdateListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1requestRender(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_requestRender();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setAlwaysRender(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setAlwaysRender(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setAuthenticationErrorListener(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    proxy->native_setAuthenticationErrorListener(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setDesiredLanguage(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_Language(env, a0);
    proxy->native_setDesiredLanguage(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setEnableScreenSymbols(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setEnableScreenSymbols(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setGlobalHeatmapConfig(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_GlobalHeatmapConfig(env, a0);
    proxy->native_setGlobalHeatmapConfig(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setImageryOverlay(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_ImageryOverlay(env, a0);
    proxy->native_setImageryOverlay(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setIsTerrain3dEnabled(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setIsTerrain3dEnabled(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setLogLevel(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_LogLevel(env, a0);
    proxy->native_setLogLevel(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setMapType(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_MapType(env, a0);
    proxy->native_setMapType(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setNightlyHeatmapConfig(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_NightlyHeatmapConfig(env, a0);
    proxy->native_setNightlyHeatmapConfig(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setOverlayPolygons(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_list<OverlayPolygon>(env, a0, [](JNIEnv* e, jobject o) { return jni::read_OverlayPolygon(e, o); });
    proxy->native_setOverlayPolygons(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setOverriddenFeatureProperties(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_OverriddenFeatureProperties(env, a0);
    proxy->native_setOverriddenFeatureProperties(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setPaused(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setPaused(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setPersonalHeatmapConfig(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_PersonalHeatmapConfig(env, a0);
    proxy->native_setPersonalHeatmapConfig(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setRenderingDisabled(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setRenderingDisabled(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setScreenSymbolRefreshInterval(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setScreenSymbolRefreshInterval(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setServerConfig(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    bool a1) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_ServerConfig(env, a0);
    const auto p1 = a1;
    proxy->native_setServerConfig(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setStravaAuthData(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    int64_t a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    const auto p1 = jni::read_list<HttpHeader>(env, a1, [](JNIEnv* e, jobject o) { return jni::read_HttpHeader(e, o); });
    proxy->native_setStravaAuthData(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setStravaSourcesExtraHeaders(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_list<HttpHeader>(env, a0, [](JNIEnv* e, jobject o) { return jni::read_HttpHeader(e, o); });
    proxy->native_setStravaSourcesExtraHeaders(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setSurfaceIdentifier(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    proxy->native_setSurfaceIdentifier(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setUnitSystem(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_MeasurementUnitSystem(env, a0);
    proxy->native_setUnitSystem(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setWeeklyHeatmapConfig(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_WeeklyHeatmapConfig(env, a0);
    proxy->native_setWeeklyHeatmapConfig(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1update(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_update();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1waitToLoadMap(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    bool a1,
    float a2) {
    auto* proxy = fatmap::resolve<Proxy::TerrainEngine>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_opaque(env, a0);
    const auto p1 = a1;
    const auto p2 = a2;
    proxy->native_waitToLoadMap(p0, p1, p2);
}

// ---- TestingContextEventListener -------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TestingContextEventListener_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TestingContextEventListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TestingContextEventListener_00024CppProxy_native_1onEntered(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TestingContextEventListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_onEntered();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TestingContextEventListener_00024CppProxy_native_1onErrorEntering(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TestingContextEventListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    proxy->native_onErrorEntering(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TestingContextEventListener_00024CppProxy_native_1onLeft(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TestingContextEventListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_onLeft();
}

// ---- TestingContextHandle --------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TestingContextHandle_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TestingContextHandle>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT jobject JNICALL Java_com_fatmap_sdk_api_TestingContextHandle_00024CppProxy_native_1status(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TestingContextHandle>(env, handle);
    if (proxy == nullptr) {
        return nullptr;
    }
    const auto result = proxy->native_status();
    return jni::write_TestingContextStatus(env, result);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TestingContextHandle_00024CppProxy_native_1stopEnteringOrInitiateLeaving(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TestingContextHandle>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_stopEnteringOrInitiateLeaving();
}

// ---- TestsListener ---------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TestsListener_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TestsListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TestsListener_00024CppProxy_native_1onFinished(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TestsListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_onFinished();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TestsListener_00024CppProxy_native_1onPerformanceTestResult(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TestsListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_PerformanceTestResult(env, a0);
    proxy->native_onPerformanceTestResult(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TestsListener_00024CppProxy_native_1onTestFinished(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TestsListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_string(env, a0);
    proxy->native_onTestFinished(p0);
}

// ---- TouchListener ---------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TouchListener_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TouchListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TouchListener_00024CppProxy_native_1onDoubleTap(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TouchListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_OsTouch(env, a0);
    proxy->native_onDoubleTap(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TouchListener_00024CppProxy_native_1onLongPress(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TouchListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_OsTouch(env, a0);
    proxy->native_onLongPress(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TouchListener_00024CppProxy_native_1onSingleTap(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TouchListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_OsTouch(env, a0);
    proxy->native_onSingleTap(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TouchListener_00024CppProxy_native_1onTapAndMove(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::TouchListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_OsTouch(env, a0);
    const auto p1 = jni::read_OsTouchAction(env, a1);
    proxy->native_onTapAndMove(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TouchListener_00024CppProxy_native_1onTouches(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TouchListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_list<OsTouch>(env, a0, [](JNIEnv* e, jobject o) { return jni::read_OsTouch(e, o); });
    proxy->native_onTouches(p0);
}

// ---- TrackPerfTestCompletionListener ---------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TrackPerfTestCompletionListener_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::TrackPerfTestCompletionListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_TrackPerfTestCompletionListener_00024CppProxy_native_1onPerfTestFinished(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::TrackPerfTestCompletionListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_TrackPerfTestResults(env, a0);
    proxy->native_onPerfTestFinished(p0);
}

// ---- Utils -----------------------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT int64_t JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1addCustomMarker2(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    double a0,
    double a1,
    jobject a2,
    jobject a3,
    jobject a4) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto p0 = a0;
    const auto p1 = a1;
    const auto p2 = jni::read_string(env, a2);
    const auto p3 = jni::read_string(env, a3);
    const auto p4 = jni::read_string(env, a4);
    const auto result = proxy->native_addCustomMarker2(p0, p1, p2, p3, p4);
    return result;
}
extern "C" JNIEXPORT int64_t JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1addCustomMarker3(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    double a0,
    double a1,
    jobject a2,
    jobject a3,
    jobject a4) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto p0 = a0;
    const auto p1 = a1;
    const auto p2 = jni::read_string(env, a2);
    const auto p3 = jni::read_string(env, a3);
    const auto p4 = jni::read_string(env, a4);
    const auto result = proxy->native_addCustomMarker3(p0, p1, p2, p3, p4);
    return result;
}
extern "C" JNIEXPORT float JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1getUserMarkerLocationAngle(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_getUserMarkerLocationAngle();
    return result;
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1hideTileGrid(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_hideTileGrid();
}
extern "C" JNIEXPORT bool JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1isUserMarkerOnScreen(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return 0;
    }
    const auto result = proxy->native_isUserMarkerOnScreen();
    return result;
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1removeCustomMarker(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    int64_t a0) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_removeCustomMarker(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1removeUserMarker(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->native_removeUserMarker();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1setHighlightedTiles(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_list<HighlightedTile>(env, a0, [](JNIEnv* e, jobject o) { return jni::read_HighlightedTile(e, o); });
    proxy->native_setHighlightedTiles(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1setUserMarker(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    double a0,
    double a1) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    const auto p1 = a1;
    proxy->native_setUserMarker(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1setUserMarkerAnimation(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setUserMarkerAnimation(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1setUserMarkerDirection(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setUserMarkerDirection(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1setUserMarkerPrecision(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setUserMarkerPrecision(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1setUserMarkerState(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    bool a0) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_setUserMarkerState(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1showTileGrid(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    int8_t a0,
    jobject a1) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    const auto p1 = jni::read_list<HighlightedTile>(env, a1, [](JNIEnv* e, jobject o) { return jni::read_HighlightedTile(e, o); });
    proxy->native_showTileGrid(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_Utils_00024CppProxy_native_1updateCustomMarkerPosition(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    int64_t a0,
    double a1,
    double a2) {
    auto* proxy = fatmap::resolve<Proxy::Utils>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    const auto p1 = a1;
    const auto p2 = a2;
    proxy->native_updateCustomMarkerPosition(p0, p1, p2);
}

// ---- VisibleAreaSamplesSink ------------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_VisibleAreaSamplesSink_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::VisibleAreaSamplesSink>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_VisibleAreaSamplesSink_00024CppProxy_native_1processVisiblePoint(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::VisibleAreaSamplesSink>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_WorldPoint2(env, a0);
    proxy->native_processVisiblePoint(p0);
}

// ---- WindowLifecycleListener -----------------------------------
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_WindowLifecycleListener_00024CppProxy_nativeDestroy(
    JNIEnv* env,
    jobject thiz,
    jlong handle) {
    auto* proxy = fatmap::resolve<Proxy::WindowLifecycleListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    proxy->nativeDestroy();
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_WindowLifecycleListener_00024CppProxy_native_1onDisplayChanged(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    float a0) {
    auto* proxy = fatmap::resolve<Proxy::WindowLifecycleListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    proxy->native_onDisplayChanged(p0);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_WindowLifecycleListener_00024CppProxy_native_1onWindowResized(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    int32_t a0,
    int32_t a1) {
    auto* proxy = fatmap::resolve<Proxy::WindowLifecycleListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = a0;
    const auto p1 = a1;
    proxy->native_onWindowResized(p0, p1);
}
extern "C" JNIEXPORT void JNICALL Java_com_fatmap_sdk_api_WindowLifecycleListener_00024CppProxy_native_1onWindowStateChanged(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jobject a0) {
    auto* proxy = fatmap::resolve<Proxy::WindowLifecycleListener>(env, handle);
    if (proxy == nullptr) {
        return;
    }
    const auto p0 = jni::read_WindowState(env, a0);
    proxy->native_onWindowStateChanged(p0);
}

}  // namespace
