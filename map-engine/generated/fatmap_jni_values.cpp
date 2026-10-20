// Generated from the Fatmap SDK interface contract. Do not edit by hand.

#include <jni.h>

#include "fatmap_api.h"
#include "fatmap_jni_types.h"
#include "fatmap_jni_values.h"

using namespace fatmap;

namespace jni {

ActivitySection read_ActivitySection(JNIEnv* env, jobject obj) {
    ActivitySection v;
    jclass cls = env->GetObjectClass(obj);
    v.numPolylineEdges = env->CallLongMethod(obj, env->GetMethodID(cls, "getNumPolylineEdges", "()J"));
    v.privateSection = env->CallBooleanMethod(obj, env->GetMethodID(cls, "getPrivateSection", "()Z"));
    v.surfaceType = jni::read_RoadSurfaceType(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getSurfaceType", "()Lcom/fatmap/sdk/api/RoadSurfaceType;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_ActivitySection(JNIEnv* env, const ActivitySection& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/ActivitySection");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(JLcom/fatmap/sdk/api/RoadSurfaceType;Z)V");
    const jobject a1 = jni::write_RoadSurfaceType(env, value.surfaceType);
    return env->NewObject(cls, ctor, static_cast<int64_t>(value.numPolylineEdges), a1, static_cast<jboolean>(value.privateSection ? JNI_TRUE : JNI_FALSE));
}

AnimatedImage read_AnimatedImage(JNIEnv* env, jobject obj) {
    AnimatedImage v;
    jclass cls = env->GetObjectClass(obj);
    v.fps = env->CallFloatMethod(obj, env->GetMethodID(cls, "getFps", "()F"));
    v.frames = jni::read_list<CustomImage>(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getFrames", "()Ljava/util/ArrayList;")), [](JNIEnv* e, jobject o) { return jni::read_CustomImage(e, o); });
    v.shouldLoop = env->CallBooleanMethod(obj, env->GetMethodID(cls, "getShouldLoop", "()Z"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_AnimatedImage(JNIEnv* env, const AnimatedImage& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/AnimatedImage");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/util/ArrayList;FZ)V");
    const jobject a0 = jni::write_list<CustomImage>(env, value.frames, [](JNIEnv* e, const CustomImage& v) { return jni::write_CustomImage(e, v); });
    return env->NewObject(cls, ctor, a0, static_cast<float>(value.fps), static_cast<jboolean>(value.shouldLoop ? JNI_TRUE : JNI_FALSE));
}

CameraMargins read_CameraMargins(JNIEnv* env, jobject obj) {
    CameraMargins v;
    jclass cls = env->GetObjectClass(obj);
    v.bottom = env->CallFloatMethod(obj, env->GetMethodID(cls, "getBottom", "()F"));
    v.left = env->CallFloatMethod(obj, env->GetMethodID(cls, "getLeft", "()F"));
    v.right = env->CallFloatMethod(obj, env->GetMethodID(cls, "getRight", "()F"));
    v.top = env->CallFloatMethod(obj, env->GetMethodID(cls, "getTop", "()F"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_CameraMargins(JNIEnv* env, const CameraMargins& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/CameraMargins");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(FFFF)V");
    return env->NewObject(cls, ctor, static_cast<float>(value.bottom), static_cast<float>(value.left), static_cast<float>(value.right), static_cast<float>(value.top));
}

CameraStateFlags read_CameraStateFlags(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<CameraStateFlags>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<CameraStateFlags>(value);
}

jobject write_CameraStateFlags(JNIEnv* env, CameraStateFlags value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/CameraStateFlags");
    const char* names[] = {"TARGET", "VIEW_TYPE", "ZOOM_LEVEL"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/CameraStateFlags;");
    return env->GetStaticObjectField(cls, field);
}

CameraTarget read_CameraTarget(JNIEnv* env, jobject obj) {
    CameraTarget v;
    jclass cls = env->GetObjectClass(obj);
    v.bearing = env->CallFloatMethod(obj, env->GetMethodID(cls, "getBearing", "()F"));
    v.distance = env->CallFloatMethod(obj, env->GetMethodID(cls, "getDistance", "()F"));
    v.lookAtPoint = jni::read_WorldPoint3(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getLookAtPoint", "()Lcom/fatmap/sdk/api/WorldPoint3;")));
    v.pitch = env->CallFloatMethod(obj, env->GetMethodID(cls, "getPitch", "()F"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_CameraTarget(JNIEnv* env, const CameraTarget& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/CameraTarget");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Lcom/fatmap/sdk/api/WorldPoint3;FFF)V");
    const jobject a0 = jni::write_WorldPoint3(env, value.lookAtPoint);
    return env->NewObject(cls, ctor, a0, static_cast<float>(value.bearing), static_cast<float>(value.distance), static_cast<float>(value.pitch));
}

CameraTargetPlus read_CameraTargetPlus(JNIEnv* env, jobject obj) {
    CameraTargetPlus v;
    jclass cls = env->GetObjectClass(obj);
    v.avoidMountains = env->CallBooleanMethod(obj, env->GetMethodID(cls, "getAvoidMountains", "()Z"));
    v.cameraTarget = jni::read_CameraTarget(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getCameraTarget", "()Lcom/fatmap/sdk/api/CameraTarget;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_CameraTargetPlus(JNIEnv* env, const CameraTargetPlus& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/CameraTargetPlus");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Lcom/fatmap/sdk/api/CameraTarget;Z)V");
    const jobject a0 = jni::write_CameraTarget(env, value.cameraTarget);
    return env->NewObject(cls, ctor, a0, static_cast<jboolean>(value.avoidMountains ? JNI_TRUE : JNI_FALSE));
}

CameraViewType read_CameraViewType(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<CameraViewType>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<CameraViewType>(value);
}

jobject write_CameraViewType(JNIEnv* env, CameraViewType value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/CameraViewType");
    const char* names[] = {"CAMERA2D", "CAMERA3D"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/CameraViewType;");
    return env->GetStaticObjectField(cls, field);
}

CandidateSegment read_CandidateSegment(JNIEnv* env, jobject obj) {
    CandidateSegment v;
    jclass cls = env->GetObjectClass(obj);
    v.identifier = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getIdentifier", "()Ljava/lang/String;")));
    v.isStarred = env->CallBooleanMethod(obj, env->GetMethodID(cls, "getIsStarred", "()Z"));
    v.line = jni::read_WorldLine2(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getLine", "()Lcom/fatmap/sdk/api/WorldLine2;")));
    v.title = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getTitle", "()Ljava/lang/String;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_CandidateSegment(JNIEnv* env, const CandidateSegment& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/CandidateSegment");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;Lcom/fatmap/sdk/api/WorldLine2;ZLjava/lang/String;)V");
    const jobject a0 = jni::write_string(env, value.identifier);
    const jobject a1 = jni::write_WorldLine2(env, value.line);
    const jobject a3 = jni::write_string(env, value.title);
    return env->NewObject(cls, ctor, a0, a1, static_cast<jboolean>(value.isStarred ? JNI_TRUE : JNI_FALSE), a3);
}

ClickEventType read_ClickEventType(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<ClickEventType>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<ClickEventType>(value);
}

jobject write_ClickEventType(JNIEnv* env, ClickEventType value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/ClickEventType");
    const char* names[] = {"CONTENT", "MAP", "USERMARKER"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/ClickEventType;");
    return env->GetStaticObjectField(cls, field);
}

ClickSourceType read_ClickSourceType(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<ClickSourceType>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<ClickSourceType>(value);
}

jobject write_ClickSourceType(JNIEnv* env, ClickSourceType value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/ClickSourceType");
    const char* names[] = {"MOUSE", "TOUCH"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/ClickSourceType;");
    return env->GetStaticObjectField(cls, field);
}

Color read_Color(JNIEnv* env, jobject obj) {
    Color v;
    jclass cls = env->GetObjectClass(obj);
    v.a = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getA", "()D"));
    v.b = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getB", "()D"));
    v.g = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getG", "()D"));
    v.r = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getR", "()D"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_Color(JNIEnv* env, const Color& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/Color");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(DDDD)V");
    return env->NewObject(cls, ctor, static_cast<double>(value.a), static_cast<double>(value.b), static_cast<double>(value.g), static_cast<double>(value.r));
}

ContentClickEvent read_ContentClickEvent(JNIEnv* env, jobject obj) {
    ContentClickEvent v;
    jclass cls = env->GetObjectClass(obj);
    v.button = jni::read_MouseButtonCode(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getButton", "()Lcom/fatmap/sdk/api/MouseButtonCode;")));
    v.objects = jni::read_list<ContentClickEventObject>(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getObjects", "()Ljava/util/ArrayList;")), [](JNIEnv* e, jobject o) { return jni::read_ContentClickEventObject(e, o); });
    v.source = jni::read_ClickSourceType(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getSource", "()Lcom/fatmap/sdk/api/ClickSourceType;")));
    v.type = jni::read_ClickEventType(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getType", "()Lcom/fatmap/sdk/api/ClickEventType;")));
    v.worldPoint = jni::read_WorldPoint3(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getWorldPoint", "()Lcom/fatmap/sdk/api/WorldPoint3;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_ContentClickEvent(JNIEnv* env, const ContentClickEvent& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/ContentClickEvent");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Lcom/fatmap/sdk/api/ClickEventType;Ljava/util/ArrayList;Lcom/fatmap/sdk/api/ClickSourceType;Lcom/fatmap/sdk/api/MouseButtonCode;Lcom/fatmap/sdk/api/WorldPoint3;)V");
    const jobject a0 = jni::write_ClickEventType(env, value.type);
    const jobject a1 = jni::write_list<ContentClickEventObject>(env, value.objects, [](JNIEnv* e, const ContentClickEventObject& v) { return jni::write_ContentClickEventObject(e, v); });
    const jobject a2 = jni::write_ClickSourceType(env, value.source);
    const jobject a3 = jni::write_MouseButtonCode(env, value.button);
    const jobject a4 = jni::write_WorldPoint3(env, value.worldPoint);
    return env->NewObject(cls, ctor, a0, a1, a2, a3, a4);
}

ContentClickEventObject read_ContentClickEventObject(JNIEnv* env, jobject obj) {
    ContentClickEventObject v;
    jclass cls = env->GetObjectClass(obj);
    v.contentGroup = jni::read_ContentGroup(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getContentGroup", "()Lcom/fatmap/sdk/api/ContentGroup;")));
    v.contentSelectionHandle = jni::read_opaque(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getContentSelectionHandle", "()Lcom/fatmap/sdk/api/ContentSelectionHandle;")));
    v.customTapId = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getCustomTapId", "()Ljava/lang/String;")));
    v.featureCategory = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getFeatureCategory", "()Ljava/lang/String;")));
    v.id = env->CallLongMethod(obj, env->GetMethodID(cls, "getId", "()J"));
    v.layerName = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getLayerName", "()Ljava/lang/String;")));
    v.name = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getName", "()Ljava/lang/String;")));
    v.position = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getPosition", "()Ljava/lang/String;")));
    v.worldPoint = jni::read_WorldPoint3(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getWorldPoint", "()Lcom/fatmap/sdk/api/WorldPoint3;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_ContentClickEventObject(JNIEnv* env, const ContentClickEventObject& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/ContentClickEventObject");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Lcom/fatmap/sdk/api/ContentGroup;JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/fatmap/sdk/api/WorldPoint3;Lcom/fatmap/sdk/api/ContentSelectionHandle;)V");
    const jobject a0 = jni::write_ContentGroup(env, value.contentGroup);
    const jobject a2 = jni::write_string(env, value.customTapId);
    const jobject a3 = jni::write_string(env, value.featureCategory);
    const jobject a4 = jni::write_string(env, value.layerName);
    const jobject a5 = jni::write_string(env, value.name);
    const jobject a6 = jni::write_string(env, value.position);
    const jobject a7 = jni::write_WorldPoint3(env, value.worldPoint);
    const jobject a8 = jni::write_opaque(env, value.contentSelectionHandle);
    return env->NewObject(cls, ctor, a0, static_cast<int64_t>(value.id), a2, a3, a4, a5, a6, a7, a8);
}

ContentGroup read_ContentGroup(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<ContentGroup>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<ContentGroup>(value);
}

jobject write_ContentGroup(JNIEnv* env, ContentGroup value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/ContentGroup");
    const char* names[] = {"ACTIVITIES", "CUSTOMLINE", "CUSTOMMARKER", "OSM", "RECORD", "ROUTES", "SEGMENTS", "STRAVAPOI"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/ContentGroup;");
    return env->GetStaticObjectField(cls, field);
}

CustomImage read_CustomImage(JNIEnv* env, jobject obj) {
    CustomImage v;
    jclass cls = env->GetObjectClass(obj);
    v.data = jni::read_byte_array(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getData", "()[B")));
    v.format = jni::read_ImageFormat(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getFormat", "()Lcom/fatmap/sdk/api/ImageFormat;")));
    v.height = env->CallIntMethod(obj, env->GetMethodID(cls, "getHeight", "()I"));
    v.pixelRatio = env->CallFloatMethod(obj, env->GetMethodID(cls, "getPixelRatio", "()F"));
    v.width = env->CallIntMethod(obj, env->GetMethodID(cls, "getWidth", "()I"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_CustomImage(JNIEnv* env, const CustomImage& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/CustomImage");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Lcom/fatmap/sdk/api/ImageFormat;IIF[B)V");
    const jobject a0 = jni::write_ImageFormat(env, value.format);
    const jobject a4 = jni::write_byte_array(env, value.data);
    return env->NewObject(cls, ctor, a0, static_cast<int32_t>(value.height), static_cast<int32_t>(value.width), static_cast<float>(value.pixelRatio), a4);
}

DebugOverlayCorner read_DebugOverlayCorner(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<DebugOverlayCorner>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<DebugOverlayCorner>(value);
}

jobject write_DebugOverlayCorner(JNIEnv* env, DebugOverlayCorner value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/DebugOverlayCorner");
    const char* names[] = {"LOWERLEFT", "LOWERRIGHT", "UPPERLEFT", "UPPERRIGHT"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/DebugOverlayCorner;");
    return env->GetStaticObjectField(cls, field);
}

DebugOverlayPadding read_DebugOverlayPadding(JNIEnv* env, jobject obj) {
    DebugOverlayPadding v;
    jclass cls = env->GetObjectClass(obj);
    v.horizontal = env->CallFloatMethod(obj, env->GetMethodID(cls, "getHorizontal", "()F"));
    v.vertical = env->CallFloatMethod(obj, env->GetMethodID(cls, "getVertical", "()F"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_DebugOverlayPadding(JNIEnv* env, const DebugOverlayPadding& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/DebugOverlayPadding");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(FF)V");
    return env->NewObject(cls, ctor, static_cast<float>(value.horizontal), static_cast<float>(value.vertical));
}

DeviceOrientation read_DeviceOrientation(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<DeviceOrientation>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<DeviceOrientation>(value);
}

jobject write_DeviceOrientation(JNIEnv* env, DeviceOrientation value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/DeviceOrientation");
    const char* names[] = {"LANDSCAPE_LEFT", "LANDSCAPE_RIGHT", "PORTRAIT", "PORTRAIT_UPSIDE_DOWN"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/DeviceOrientation;");
    return env->GetStaticObjectField(cls, field);
}

FollowMode read_FollowMode(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<FollowMode>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<FollowMode>(value);
}

jobject write_FollowMode(JNIEnv* env, FollowMode value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/FollowMode");
    const char* names[] = {"HEADING", "POSITION"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/FollowMode;");
    return env->GetStaticObjectField(cls, field);
}

GivenUpDownloadsInfo read_GivenUpDownloadsInfo(JNIEnv* env, jobject obj) {
    GivenUpDownloadsInfo v;
    jclass cls = env->GetObjectClass(obj);
    v.currentSequenceNumber = env->CallLongMethod(obj, env->GetMethodID(cls, "getCurrentSequenceNumber", "()J"));
    v.haveGivenUpDownloads = env->CallBooleanMethod(obj, env->GetMethodID(cls, "getHaveGivenUpDownloads", "()Z"));
    v.newGivenUpDownloads = env->CallLongMethod(obj, env->GetMethodID(cls, "getNewGivenUpDownloads", "()J"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_GivenUpDownloadsInfo(JNIEnv* env, const GivenUpDownloadsInfo& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/GivenUpDownloadsInfo");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(JJZ)V");
    return env->NewObject(cls, ctor, static_cast<int64_t>(value.currentSequenceNumber), static_cast<int64_t>(value.newGivenUpDownloads), static_cast<jboolean>(value.haveGivenUpDownloads ? JNI_TRUE : JNI_FALSE));
}

GlobalHeatmapConfig read_GlobalHeatmapConfig(JNIEnv* env, jobject obj) {
    GlobalHeatmapConfig v;
    jclass cls = env->GetObjectClass(obj);
    v.activityType = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getActivityType", "()Ljava/lang/String;")));
    v.colorScheme = jni::read_HeatmapColorScheme(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getColorScheme", "()Lcom/fatmap/sdk/api/HeatmapColorScheme;")));
    v.opacityMultiplier = jni::read_boxed_float(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getOpacityMultiplier", "()Ljava/lang/Float;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_GlobalHeatmapConfig(JNIEnv* env, const GlobalHeatmapConfig& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/GlobalHeatmapConfig");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;Lcom/fatmap/sdk/api/HeatmapColorScheme;Ljava/lang/Float;)V");
    const jobject a0 = jni::write_string(env, value.activityType);
    const jobject a1 = jni::write_HeatmapColorScheme(env, value.colorScheme);
    const jobject a2 = jni::write_boxed_float(env, value.opacityMultiplier);
    return env->NewObject(cls, ctor, a0, a1, a2);
}

HeatmapColorScheme read_HeatmapColorScheme(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<HeatmapColorScheme>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<HeatmapColorScheme>(value);
}

jobject write_HeatmapColorScheme(JNIEnv* env, HeatmapColorScheme value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/HeatmapColorScheme");
    const char* names[] = {"BLUE", "BLUE_RED", "ORANGE", "PINK", "PURPLE", "SUNSET", "WEEKLY_RAINBOW"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/HeatmapColorScheme;");
    return env->GetStaticObjectField(cls, field);
}

HighlightedTile read_HighlightedTile(JNIEnv* env, jobject obj) {
    HighlightedTile v;
    jclass cls = env->GetObjectClass(obj);
    v.color = jni::read_Color(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getColor", "()Lcom/fatmap/sdk/api/Color;")));
    v.index = jni::read_QuadIndex(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getIndex", "()Lcom/fatmap/sdk/api/QuadIndex;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_HighlightedTile(JNIEnv* env, const HighlightedTile& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/HighlightedTile");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Lcom/fatmap/sdk/api/QuadIndex;Lcom/fatmap/sdk/api/Color;)V");
    const jobject a0 = jni::write_QuadIndex(env, value.index);
    const jobject a1 = jni::write_Color(env, value.color);
    return env->NewObject(cls, ctor, a0, a1);
}

HttpHeader read_HttpHeader(JNIEnv* env, jobject obj) {
    HttpHeader v;
    jclass cls = env->GetObjectClass(obj);
    v.name = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getName", "()Ljava/lang/String;")));
    v.value = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getValue", "()Ljava/lang/String;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_HttpHeader(JNIEnv* env, const HttpHeader& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/HttpHeader");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;Ljava/lang/String;)V");
    const jobject a0 = jni::write_string(env, value.name);
    const jobject a1 = jni::write_string(env, value.value);
    return env->NewObject(cls, ctor, a0, a1);
}

ImageFormat read_ImageFormat(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<ImageFormat>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<ImageFormat>(value);
}

jobject write_ImageFormat(JNIEnv* env, ImageFormat value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/ImageFormat");
    const char* names[] = {"JPG", "PNG", "RGB", "RGBA"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/ImageFormat;");
    return env->GetStaticObjectField(cls, field);
}

ImageryOverlay read_ImageryOverlay(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<ImageryOverlay>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<ImageryOverlay>(value);
}

jobject write_ImageryOverlay(JNIEnv* env, ImageryOverlay value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/ImageryOverlay");
    const char* names[] = {"ASPECT", "AVALANCHE", "GRADIENT", "GRID", "NONE"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/ImageryOverlay;");
    return env->GetStaticObjectField(cls, field);
}

IntervalFloat read_IntervalFloat(JNIEnv* env, jobject obj) {
    IntervalFloat v;
    jclass cls = env->GetObjectClass(obj);
    v.from = env->CallFloatMethod(obj, env->GetMethodID(cls, "getFrom", "()F"));
    v.to = env->CallFloatMethod(obj, env->GetMethodID(cls, "getTo", "()F"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_IntervalFloat(JNIEnv* env, const IntervalFloat& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/IntervalFloat");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(FF)V");
    return env->NewObject(cls, ctor, static_cast<float>(value.from), static_cast<float>(value.to));
}

IntervalInt read_IntervalInt(JNIEnv* env, jobject obj) {
    IntervalInt v;
    jclass cls = env->GetObjectClass(obj);
    v.from = env->CallLongMethod(obj, env->GetMethodID(cls, "getFrom", "()J"));
    v.to = env->CallLongMethod(obj, env->GetMethodID(cls, "getTo", "()J"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_IntervalInt(JNIEnv* env, const IntervalInt& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/IntervalInt");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(JJ)V");
    return env->NewObject(cls, ctor, static_cast<int64_t>(value.from), static_cast<int64_t>(value.to));
}

Language read_Language(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<Language>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<Language>(value);
}

jobject write_Language(JNIEnv* env, Language value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/Language");
    const char* names[] = {"ES", "FR", "IT", "LOCAL", "PT", "RU", "ZH_HANS", "ZH_HANT", "DE", "EN", "JA"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/Language;");
    return env->GetStaticObjectField(cls, field);
}

LodCalculationControlParams read_LodCalculationControlParams(JNIEnv* env, jobject obj) {
    LodCalculationControlParams v;
    jclass cls = env->GetObjectClass(obj);
    v.maxLodDifferenceForOffset = env->CallIntMethod(obj, env->GetMethodID(cls, "getMaxLodDifferenceForOffset", "()I"));
    v.tileSplitThresholdOffset = env->CallFloatMethod(obj, env->GetMethodID(cls, "getTileSplitThresholdOffset", "()F"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_LodCalculationControlParams(JNIEnv* env, const LodCalculationControlParams& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/LodCalculationControlParams");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(FI)V");
    return env->NewObject(cls, ctor, static_cast<float>(value.tileSplitThresholdOffset), static_cast<int32_t>(value.maxLodDifferenceForOffset));
}

LogLevel read_LogLevel(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<LogLevel>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<LogLevel>(value);
}

jobject write_LogLevel(JNIEnv* env, LogLevel value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/LogLevel");
    const char* names[] = {"DEBUG", "DISABLED", "ERROR", "INFO", "WARNING"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/LogLevel;");
    return env->GetStaticObjectField(cls, field);
}

LookAtPoint read_LookAtPoint(JNIEnv* env, jobject obj) {
    LookAtPoint v;
    jclass cls = env->GetObjectClass(obj);
    v.isPointingAtMap = env->CallBooleanMethod(obj, env->GetMethodID(cls, "getIsPointingAtMap", "()Z"));
    v.worldPoint = jni::read_WorldPoint3(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getWorldPoint", "()Lcom/fatmap/sdk/api/WorldPoint3;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_LookAtPoint(JNIEnv* env, const LookAtPoint& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/LookAtPoint");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Lcom/fatmap/sdk/api/WorldPoint3;Z)V");
    const jobject a0 = jni::write_WorldPoint3(env, value.worldPoint);
    return env->NewObject(cls, ctor, a0, static_cast<jboolean>(value.isPointingAtMap ? JNI_TRUE : JNI_FALSE));
}

LuaScriptError read_LuaScriptError(JNIEnv* env, jobject obj) {
    LuaScriptError v;
    jclass cls = env->GetObjectClass(obj);
    v.what = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getWhat", "()Ljava/lang/String;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_LuaScriptError(JNIEnv* env, const LuaScriptError& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/LuaScriptError");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;)V");
    const jobject a0 = jni::write_string(env, value.what);
    return env->NewObject(cls, ctor, a0);
}

MapClickEvent read_MapClickEvent(JNIEnv* env, jobject obj) {
    MapClickEvent v;
    jclass cls = env->GetObjectClass(obj);
    v.button = jni::read_MouseButtonCode(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getButton", "()Lcom/fatmap/sdk/api/MouseButtonCode;")));
    v.source = jni::read_ClickSourceType(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getSource", "()Lcom/fatmap/sdk/api/ClickSourceType;")));
    v.type = jni::read_ClickEventType(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getType", "()Lcom/fatmap/sdk/api/ClickEventType;")));
    v.worldPoint = jni::read_WorldPoint3(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getWorldPoint", "()Lcom/fatmap/sdk/api/WorldPoint3;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_MapClickEvent(JNIEnv* env, const MapClickEvent& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/MapClickEvent");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Lcom/fatmap/sdk/api/ClickEventType;Lcom/fatmap/sdk/api/WorldPoint3;Lcom/fatmap/sdk/api/ClickSourceType;Lcom/fatmap/sdk/api/MouseButtonCode;)V");
    const jobject a0 = jni::write_ClickEventType(env, value.type);
    const jobject a1 = jni::write_WorldPoint3(env, value.worldPoint);
    const jobject a2 = jni::write_ClickSourceType(env, value.source);
    const jobject a3 = jni::write_MouseButtonCode(env, value.button);
    return env->NewObject(cls, ctor, a0, a1, a2, a3);
}

MapType read_MapType(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<MapType>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<MapType>(value);
}

jobject write_MapType(JNIEnv* env, MapType value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/MapType");
    const char* names[] = {"STRAVA_HYBRID", "STRAVA_PLANET_HYBRID", "STRAVA_PLANET_HYBRID_EXPERIMENT", "STRAVA_PLANET_SATELLITE_SUMMER", "STRAVA_PLANET_SATELLITE_SUMMER_EXPERIMENT", "STRAVA_PLANET_TOPO_DARK", "STRAVA_PLANET_TOPO_DARK_EXPERIMENT", "STRAVA_PLANET_TOPO_LIGHT", "STRAVA_PLANET_TOPO_LIGHT_EXPERIMENT", "STRAVA_PLANET_TOPO_WINTER", "STRAVA_PLANET_TOPO_WINTER_EXPERIMENT", "STRAVA_PLANET_TOPO_WINTER_HYBRID", "STRAVA_PLANET_TOPO_WINTER_HYBRID_EXPERIMENT", "STRAVA_SATELLITE_SUMMER", "STRAVA_SATELLITE_WINTER", "STRAVA_TOPO_DARK", "STRAVA_TOPO_LIGHT", "STRAVA_TOPO_WINTER", "STRAVA_TOPO_WINTER_HYBRID"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/MapType;");
    return env->GetStaticObjectField(cls, field);
}

MeasurementUnit read_MeasurementUnit(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<MeasurementUnit>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<MeasurementUnit>(value);
}

jobject write_MeasurementUnit(JNIEnv* env, MeasurementUnit value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/MeasurementUnit");
    const char* names[] = {"FEET", "KILOMETER", "METER", "MILE"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/MeasurementUnit;");
    return env->GetStaticObjectField(cls, field);
}

MeasurementUnitSystem read_MeasurementUnitSystem(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<MeasurementUnitSystem>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<MeasurementUnitSystem>(value);
}

jobject write_MeasurementUnitSystem(JNIEnv* env, MeasurementUnitSystem value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/MeasurementUnitSystem");
    const char* names[] = {"IMPERIAL", "METRIC"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/MeasurementUnitSystem;");
    return env->GetStaticObjectField(cls, field);
}

MemoryStats read_MemoryStats(JNIEnv* env, jobject obj) {
    MemoryStats v;
    jclass cls = env->GetObjectClass(obj);
    v.currentAllocatedMb = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getCurrentAllocatedMb", "()D"));
    v.currentAllocationCount = env->CallLongMethod(obj, env->GetMethodID(cls, "getCurrentAllocationCount", "()J"));
    v.totalAllocatedMb = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getTotalAllocatedMb", "()D"));
    v.totalAllocationCount = env->CallLongMethod(obj, env->GetMethodID(cls, "getTotalAllocationCount", "()J"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_MemoryStats(JNIEnv* env, const MemoryStats& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/MemoryStats");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(JDJD)V");
    return env->NewObject(cls, ctor, static_cast<int64_t>(value.currentAllocationCount), static_cast<double>(value.currentAllocatedMb), static_cast<int64_t>(value.totalAllocationCount), static_cast<double>(value.totalAllocatedMb));
}

MouseButtonCode read_MouseButtonCode(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<MouseButtonCode>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<MouseButtonCode>(value);
}

jobject write_MouseButtonCode(JNIEnv* env, MouseButtonCode value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/MouseButtonCode");
    const char* names[] = {"LEFT", "MIDDLE", "RIGHT"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/MouseButtonCode;");
    return env->GetStaticObjectField(cls, field);
}

MouseMoveEvent read_MouseMoveEvent(JNIEnv* env, jobject obj) {
    MouseMoveEvent v;
    jclass cls = env->GetObjectClass(obj);
    v.objects = jni::read_list<ContentClickEventObject>(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getObjects", "()Ljava/util/ArrayList;")), [](JNIEnv* e, jobject o) { return jni::read_ContentClickEventObject(e, o); });
    v.x = env->CallFloatMethod(obj, env->GetMethodID(cls, "getX", "()F"));
    v.y = env->CallFloatMethod(obj, env->GetMethodID(cls, "getY", "()F"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_MouseMoveEvent(JNIEnv* env, const MouseMoveEvent& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/MouseMoveEvent");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(FFLjava/util/ArrayList;)V");
    const jobject a2 = jni::write_list<ContentClickEventObject>(env, value.objects, [](JNIEnv* e, const ContentClickEventObject& v) { return jni::write_ContentClickEventObject(e, v); });
    return env->NewObject(cls, ctor, static_cast<float>(value.x), static_cast<float>(value.y), a2);
}

NightlyHeatmapConfig read_NightlyHeatmapConfig(JNIEnv* env, jobject obj) {
    NightlyHeatmapConfig v;
    jclass cls = env->GetObjectClass(obj);
    v.activityType = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getActivityType", "()Ljava/lang/String;")));
    v.opacityMultiplier = jni::read_boxed_float(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getOpacityMultiplier", "()Ljava/lang/Float;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_NightlyHeatmapConfig(JNIEnv* env, const NightlyHeatmapConfig& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/NightlyHeatmapConfig");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;Ljava/lang/Float;)V");
    const jobject a0 = jni::write_string(env, value.activityType);
    const jobject a1 = jni::write_boxed_float(env, value.opacityMultiplier);
    return env->NewObject(cls, ctor, a0, a1);
}

OsTouch read_OsTouch(JNIEnv* env, jobject obj) {
    OsTouch v;
    jclass cls = env->GetObjectClass(obj);
    v.currentInfo = jni::read_OsTouchInfo(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getCurrentInfo", "()Lcom/fatmap/sdk/api/OsTouchInfo;")));
    v.id = env->CallLongMethod(obj, env->GetMethodID(cls, "getId", "()J"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_OsTouch(JNIEnv* env, const OsTouch& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/OsTouch");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(JLcom/fatmap/sdk/api/OsTouchInfo;)V");
    const jobject a1 = jni::write_OsTouchInfo(env, value.currentInfo);
    return env->NewObject(cls, ctor, static_cast<int64_t>(value.id), a1);
}

OsTouchAction read_OsTouchAction(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<OsTouchAction>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<OsTouchAction>(value);
}

jobject write_OsTouchAction(JNIEnv* env, OsTouchAction value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/OsTouchAction");
    const char* names[] = {"CANCEL", "END", "MOVE", "START"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/OsTouchAction;");
    return env->GetStaticObjectField(cls, field);
}

OsTouchInfo read_OsTouchInfo(JNIEnv* env, jobject obj) {
    OsTouchInfo v;
    jclass cls = env->GetObjectClass(obj);
    v.position = jni::read_OsTouchPosition(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getPosition", "()Lcom/fatmap/sdk/api/OsTouchPosition;")));
    v.timestamp = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getTimestamp", "()D"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_OsTouchInfo(JNIEnv* env, const OsTouchInfo& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/OsTouchInfo");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(DLcom/fatmap/sdk/api/OsTouchPosition;)V");
    const jobject a1 = jni::write_OsTouchPosition(env, value.position);
    return env->NewObject(cls, ctor, static_cast<double>(value.timestamp), a1);
}

OsTouchPosition read_OsTouchPosition(JNIEnv* env, jobject obj) {
    OsTouchPosition v;
    jclass cls = env->GetObjectClass(obj);
    v.normalizedX = env->CallFloatMethod(obj, env->GetMethodID(cls, "getNormalizedX", "()F"));
    v.normalizedY = env->CallFloatMethod(obj, env->GetMethodID(cls, "getNormalizedY", "()F"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_OsTouchPosition(JNIEnv* env, const OsTouchPosition& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/OsTouchPosition");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(FF)V");
    return env->NewObject(cls, ctor, static_cast<float>(value.normalizedX), static_cast<float>(value.normalizedY));
}

OverlayPolygon read_OverlayPolygon(JNIEnv* env, jobject obj) {
    OverlayPolygon v;
    jclass cls = env->GetObjectClass(obj);
    v.aspect = jni::read_list<IntervalFloat>(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getAspect", "()Ljava/util/ArrayList;")), [](JNIEnv* e, jobject o) { return jni::read_IntervalFloat(e, o); });
    v.boundary = jni::read_WorldLine2(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getBoundary", "()Lcom/fatmap/sdk/api/WorldLine2;")));
    v.fillColor = jni::read_Color(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getFillColor", "()Lcom/fatmap/sdk/api/Color;")));
    v.gradientRange = jni::read_IntervalFloat(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getGradientRange", "()Lcom/fatmap/sdk/api/IntervalFloat;")));
    v.heightRange = jni::read_IntervalFloat(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getHeightRange", "()Lcom/fatmap/sdk/api/IntervalFloat;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_OverlayPolygon(JNIEnv* env, const OverlayPolygon& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/OverlayPolygon");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Lcom/fatmap/sdk/api/WorldLine2;Lcom/fatmap/sdk/api/IntervalFloat;Lcom/fatmap/sdk/api/IntervalFloat;Ljava/util/ArrayList;Lcom/fatmap/sdk/api/Color;)V");
    const jobject a0 = jni::write_WorldLine2(env, value.boundary);
    const jobject a1 = jni::write_IntervalFloat(env, value.gradientRange);
    const jobject a2 = jni::write_IntervalFloat(env, value.heightRange);
    const jobject a3 = jni::write_list<IntervalFloat>(env, value.aspect, [](JNIEnv* e, const IntervalFloat& v) { return jni::write_IntervalFloat(e, v); });
    const jobject a4 = jni::write_Color(env, value.fillColor);
    return env->NewObject(cls, ctor, a0, a1, a2, a3, a4);
}

OverriddenFeatureProperties read_OverriddenFeatureProperties(JNIEnv* env, jobject obj) {
    (void)env;
    (void)obj;
    return OverriddenFeatureProperties{};
}

jobject write_OverriddenFeatureProperties(JNIEnv* env, const OverriddenFeatureProperties& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/OverriddenFeatureProperties");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "()V");
    return env->NewObject(cls, ctor);
}

PerformanceMetrics read_PerformanceMetrics(JNIEnv* env, jobject obj) {
    PerformanceMetrics v;
    jclass cls = env->GetObjectClass(obj);
    v.frameCountFromStart = env->CallLongMethod(obj, env->GetMethodID(cls, "getFrameCountFromStart", "()J"));
    v.frameTime = env->CallFloatMethod(obj, env->GetMethodID(cls, "getFrameTime", "()F"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_PerformanceMetrics(JNIEnv* env, const PerformanceMetrics& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/PerformanceMetrics");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(FJ)V");
    return env->NewObject(cls, ctor, static_cast<float>(value.frameTime), static_cast<int64_t>(value.frameCountFromStart));
}

PerformanceTestResult read_PerformanceTestResult(JNIEnv* env, jobject obj) {
    PerformanceTestResult v;
    jclass cls = env->GetObjectClass(obj);
    v.name = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getName", "()Ljava/lang/String;")));
    v.unit = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getUnit", "()Ljava/lang/String;")));
    v.value = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getValue", "()D"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_PerformanceTestResult(JNIEnv* env, const PerformanceTestResult& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/PerformanceTestResult");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;Ljava/lang/String;D)V");
    const jobject a0 = jni::write_string(env, value.name);
    const jobject a1 = jni::write_string(env, value.unit);
    return env->NewObject(cls, ctor, a0, a1, static_cast<double>(value.value));
}

PersonalHeatmapConfig read_PersonalHeatmapConfig(JNIEnv* env, jobject obj) {
    PersonalHeatmapConfig v;
    jclass cls = env->GetObjectClass(obj);
    v.colorScheme = jni::read_HeatmapColorScheme(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getColorScheme", "()Lcom/fatmap/sdk/api/HeatmapColorScheme;")));
    v.filterEnd = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getFilterEnd", "()Ljava/lang/String;")));
    v.filterStart = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getFilterStart", "()Ljava/lang/String;")));
    v.filterType = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getFilterType", "()Ljava/lang/String;")));
    v.includeActivitiesVisibleOnlyToMe = env->CallBooleanMethod(obj, env->GetMethodID(cls, "getIncludeActivitiesVisibleOnlyToMe", "()Z"));
    v.includeActivitiesVisibleToEveryone = env->CallBooleanMethod(obj, env->GetMethodID(cls, "getIncludeActivitiesVisibleToEveryone", "()Z"));
    v.includeActivitiesVisibleToFollowersOnly = env->CallBooleanMethod(obj, env->GetMethodID(cls, "getIncludeActivitiesVisibleToFollowersOnly", "()Z"));
    v.includeCommutes = env->CallBooleanMethod(obj, env->GetMethodID(cls, "getIncludeCommutes", "()Z"));
    v.opacityMultiplier = jni::read_boxed_float(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getOpacityMultiplier", "()Ljava/lang/Float;")));
    v.respectPrivacyZones = env->CallBooleanMethod(obj, env->GetMethodID(cls, "getRespectPrivacyZones", "()Z"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_PersonalHeatmapConfig(JNIEnv* env, const PersonalHeatmapConfig& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/PersonalHeatmapConfig");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/fatmap/sdk/api/HeatmapColorScheme;Ljava/lang/Float;ZZZZZ)V");
    const jobject a0 = jni::write_string(env, value.filterEnd);
    const jobject a1 = jni::write_string(env, value.filterStart);
    const jobject a2 = jni::write_string(env, value.filterType);
    const jobject a3 = jni::write_HeatmapColorScheme(env, value.colorScheme);
    const jobject a4 = jni::write_boxed_float(env, value.opacityMultiplier);
    return env->NewObject(cls, ctor, a0, a1, a2, a3, a4, static_cast<jboolean>(value.includeActivitiesVisibleOnlyToMe ? JNI_TRUE : JNI_FALSE), static_cast<jboolean>(value.includeActivitiesVisibleToEveryone ? JNI_TRUE : JNI_FALSE), static_cast<jboolean>(value.includeActivitiesVisibleToFollowersOnly ? JNI_TRUE : JNI_FALSE), static_cast<jboolean>(value.includeCommutes ? JNI_TRUE : JNI_FALSE), static_cast<jboolean>(value.respectPrivacyZones ? JNI_TRUE : JNI_FALSE));
}

PersonalHeatmapTestAccount read_PersonalHeatmapTestAccount(JNIEnv* env, jobject obj) {
    PersonalHeatmapTestAccount v;
    jclass cls = env->GetObjectClass(obj);
    v.accessToken = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getAccessToken", "()Ljava/lang/String;")));
    v.stravaAthleteId = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getStravaAthleteId", "()Ljava/lang/String;")));
    v.tileEndpointHostName = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getTileEndpointHostName", "()Ljava/lang/String;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_PersonalHeatmapTestAccount(JNIEnv* env, const PersonalHeatmapTestAccount& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/PersonalHeatmapTestAccount");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    const jobject a0 = jni::write_string(env, value.accessToken);
    const jobject a1 = jni::write_string(env, value.stravaAthleteId);
    const jobject a2 = jni::write_string(env, value.tileEndpointHostName);
    return env->NewObject(cls, ctor, a0, a1, a2);
}

PointScreenPosition read_PointScreenPosition(JNIEnv* env, jobject obj) {
    PointScreenPosition v;
    jclass cls = env->GetObjectClass(obj);
    v.isOccluded = env->CallBooleanMethod(obj, env->GetMethodID(cls, "getIsOccluded", "()Z"));
    v.x = env->CallFloatMethod(obj, env->GetMethodID(cls, "getX", "()F"));
    v.y = env->CallFloatMethod(obj, env->GetMethodID(cls, "getY", "()F"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_PointScreenPosition(JNIEnv* env, const PointScreenPosition& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/PointScreenPosition");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(FFZ)V");
    return env->NewObject(cls, ctor, static_cast<float>(value.x), static_cast<float>(value.y), static_cast<jboolean>(value.isOccluded ? JNI_TRUE : JNI_FALSE));
}

PolylineHighlightSection read_PolylineHighlightSection(JNIEnv* env, jobject obj) {
    PolylineHighlightSection v;
    jclass cls = env->GetObjectClass(obj);
    v.endIndex = env->CallLongMethod(obj, env->GetMethodID(cls, "getEndIndex", "()J"));
    v.icon = jni::read_PolylineHighlightSectionIcon(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getIcon", "()Lcom/fatmap/sdk/api/PolylineHighlightSectionIcon;")));
    v.iconText = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getIconText", "()Ljava/lang/String;")));
    v.identifier = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getIdentifier", "()Ljava/lang/String;")));
    v.startIndex = env->CallLongMethod(obj, env->GetMethodID(cls, "getStartIndex", "()J"));
    v.type = jni::read_PolylineHighlightSectionType(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getType", "()Lcom/fatmap/sdk/api/PolylineHighlightSectionType;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_PolylineHighlightSection(JNIEnv* env, const PolylineHighlightSection& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/PolylineHighlightSection");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;JJLcom/fatmap/sdk/api/PolylineHighlightSectionType;Lcom/fatmap/sdk/api/PolylineHighlightSectionIcon;Ljava/lang/String;)V");
    const jobject a0 = jni::write_string(env, value.iconText);
    const jobject a3 = jni::write_PolylineHighlightSectionType(env, value.type);
    const jobject a4 = jni::write_PolylineHighlightSectionIcon(env, value.icon);
    const jobject a5 = jni::write_string(env, value.identifier);
    return env->NewObject(cls, ctor, a0, static_cast<int64_t>(value.endIndex), static_cast<int64_t>(value.startIndex), a3, a4, a5);
}

PolylineHighlightSectionIcon read_PolylineHighlightSectionIcon(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<PolylineHighlightSectionIcon>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<PolylineHighlightSectionIcon>(value);
}

jobject write_PolylineHighlightSectionIcon(JNIEnv* env, PolylineHighlightSectionIcon value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/PolylineHighlightSectionIcon");
    const char* names[] = {"BEST_EFFORT_CLIMB", "BEST_EFFORT_PACE", "BEST_EFFORT_POWER", "BRONZE_PR", "GOLD_PR", "LOCAL_LEGEND", "SILVER_PR", "TOP10_1", "TOP10_10", "TOP10_2", "TOP10_3", "TOP10_4", "TOP10_5", "TOP10_6", "TOP10_7", "TOP10_8", "TOP10_9"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/PolylineHighlightSectionIcon;");
    return env->GetStaticObjectField(cls, field);
}

PolylineHighlightSectionType read_PolylineHighlightSectionType(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<PolylineHighlightSectionType>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<PolylineHighlightSectionType>(value);
}

jobject write_PolylineHighlightSectionType(JNIEnv* env, PolylineHighlightSectionType value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/PolylineHighlightSectionType");
    const char* names[] = {"BLUE", "BRONZE", "GOLD", "PURPLE", "SILVER", "TEAL"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/PolylineHighlightSectionType;");
    return env->GetStaticObjectField(cls, field);
}

QuadIndex read_QuadIndex(JNIEnv* env, jobject obj) {
    QuadIndex v;
    jclass cls = env->GetObjectClass(obj);
    v.x = env->CallIntMethod(obj, env->GetMethodID(cls, "getX", "()I"));
    v.y = env->CallIntMethod(obj, env->GetMethodID(cls, "getY", "()I"));
    v.z = env->CallByteMethod(obj, env->GetMethodID(cls, "getZ", "()B"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_QuadIndex(JNIEnv* env, const QuadIndex& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/QuadIndex");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(BII)V");
    return env->NewObject(cls, ctor, static_cast<int8_t>(value.z), static_cast<int32_t>(value.x), static_cast<int32_t>(value.y));
}

RoadSurfaceType read_RoadSurfaceType(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<RoadSurfaceType>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<RoadSurfaceType>(value);
}

jobject write_RoadSurfaceType(JNIEnv* env, RoadSurfaceType value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/RoadSurfaceType");
    const char* names[] = {"PAVED", "UNPAVED", "UNSPECIFIED"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/RoadSurfaceType;");
    return env->GetStaticObjectField(cls, field);
}

RouteSection read_RouteSection(JNIEnv* env, jobject obj) {
    RouteSection v;
    jclass cls = env->GetObjectClass(obj);
    v.numPolylineEdges = env->CallLongMethod(obj, env->GetMethodID(cls, "getNumPolylineEdges", "()J"));
    v.surfaceType = jni::read_RoadSurfaceType(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getSurfaceType", "()Lcom/fatmap/sdk/api/RoadSurfaceType;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_RouteSection(JNIEnv* env, const RouteSection& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/RouteSection");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(JLcom/fatmap/sdk/api/RoadSurfaceType;)V");
    const jobject a1 = jni::write_RoadSurfaceType(env, value.surfaceType);
    return env->NewObject(cls, ctor, static_cast<int64_t>(value.numPolylineEdges), a1);
}

RouteSelectionStyle read_RouteSelectionStyle(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<RouteSelectionStyle>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<RouteSelectionStyle>(value);
}

jobject write_RouteSelectionStyle(JNIEnv* env, RouteSelectionStyle value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/RouteSelectionStyle");
    const char* names[] = {"CANONICAL", "GRADIENT"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/RouteSelectionStyle;");
    return env->GetStaticObjectField(cls, field);
}

RouteWaypoint read_RouteWaypoint(JNIEnv* env, jobject obj) {
    RouteWaypoint v;
    jclass cls = env->GetObjectClass(obj);
    v.category = jni::read_RouteWaypointCategory(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getCategory", "()Lcom/fatmap/sdk/api/RouteWaypointCategory;")));
    v.identifier = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getIdentifier", "()Ljava/lang/String;")));
    v.point = jni::read_WorldPoint2(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getPoint", "()Lcom/fatmap/sdk/api/WorldPoint2;")));
    v.title = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getTitle", "()Ljava/lang/String;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_RouteWaypoint(JNIEnv* env, const RouteWaypoint& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/RouteWaypoint");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;Lcom/fatmap/sdk/api/WorldPoint2;Ljava/lang/String;Lcom/fatmap/sdk/api/RouteWaypointCategory;)V");
    const jobject a0 = jni::write_string(env, value.identifier);
    const jobject a1 = jni::write_WorldPoint2(env, value.point);
    const jobject a2 = jni::write_string(env, value.title);
    const jobject a3 = jni::write_RouteWaypointCategory(env, value.category);
    return env->NewObject(cls, ctor, a0, a1, a2, a3);
}

RouteWaypointCategory read_RouteWaypointCategory(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<RouteWaypointCategory>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<RouteWaypointCategory>(value);
}

jobject write_RouteWaypointCategory(JNIEnv* env, RouteWaypointCategory value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/RouteWaypointCategory");
    const char* names[] = {"AID_STATION", "ALERT", "ART", "ATTRACTION", "BAR", "BEACH", "BIKE_SHOP", "CAMP_SITE", "CHECKPOINT", "COFFEE", "DISTANCE_MARKER", "GAS_STATION", "GENERIC", "GROCERY", "LODGING", "MEETING_SPOT", "MONUMENT", "PARK", "PARKING", "PEAK", "PHARMACY", "RESTAURANT", "RESTROOM", "REST_AREA", "ROUTE_PLANNED_FINISH", "ROUTE_PLANNED_START", "ROUTE_PLANNED_WAYPOINT", "SEGMENT_END", "SEGMENT_START", "TRAILHEAD", "TRANSITION_ZONE", "VIEWPOINT", "WATER_SOURCE"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/RouteWaypointCategory;");
    return env->GetStaticObjectField(cls, field);
}

SegmentFeature read_SegmentFeature(JNIEnv* env, jobject obj) {
    SegmentFeature v;
    jclass cls = env->GetObjectClass(obj);
    v.activityType = env->CallLongMethod(obj, env->GetMethodID(cls, "getActivityType", "()J"));
    v.athletesAllTime = jni::read_boxed_long(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getAthletesAllTime", "()Ljava/lang/Long;")));
    v.attemptsAllTime = jni::read_boxed_long(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getAttemptsAllTime", "()Ljava/lang/Long;")));
    v.averageGrade = jni::read_boxed_float(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getAverageGrade", "()Ljava/lang/Float;")));
    v.darkSparklineUrl = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getDarkSparklineUrl", "()Ljava/lang/String;")));
    v.darkThumbnailUrl = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getDarkThumbnailUrl", "()Ljava/lang/String;")));
    v.distance = jni::read_boxed_double(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getDistance", "()Ljava/lang/Double;")));
    v.id = env->CallLongMethod(obj, env->GetMethodID(cls, "getId", "()J"));
    v.name = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getName", "()Ljava/lang/String;")));
    v.score = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getScore", "()D"));
    v.sparklineUrl = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getSparklineUrl", "()Ljava/lang/String;")));
    v.startXYLat = jni::read_boxed_float(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getStartXYLat", "()Ljava/lang/Float;")));
    v.startXYLng = jni::read_boxed_float(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getStartXYLng", "()Ljava/lang/Float;")));
    v.thumbnailUrl = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getThumbnailUrl", "()Ljava/lang/String;")));
    v.verifiedStatus = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getVerifiedStatus", "()Ljava/lang/String;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_SegmentFeature(JNIEnv* env, const SegmentFeature& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/SegmentFeature");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(JLjava/lang/String;DJLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Float;Ljava/lang/Double;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Float;Ljava/lang/Float;)V");
    const jobject a1 = jni::write_string(env, value.darkSparklineUrl);
    const jobject a4 = jni::write_string(env, value.darkThumbnailUrl);
    const jobject a5 = jni::write_string(env, value.name);
    const jobject a6 = jni::write_string(env, value.sparklineUrl);
    const jobject a7 = jni::write_string(env, value.thumbnailUrl);
    const jobject a8 = jni::write_string(env, value.verifiedStatus);
    const jobject a9 = jni::write_boxed_float(env, value.averageGrade);
    const jobject a10 = jni::write_boxed_double(env, value.distance);
    const jobject a11 = jni::write_boxed_long(env, value.athletesAllTime);
    const jobject a12 = jni::write_boxed_long(env, value.attemptsAllTime);
    const jobject a13 = jni::write_boxed_float(env, value.startXYLat);
    const jobject a14 = jni::write_boxed_float(env, value.startXYLng);
    return env->NewObject(cls, ctor, static_cast<int64_t>(value.activityType), a1, static_cast<double>(value.score), static_cast<int64_t>(value.id), a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14);
}

SegmentsFilterOptions read_SegmentsFilterOptions(JNIEnv* env, jobject obj) {
    SegmentsFilterOptions v;
    jclass cls = env->GetObjectClass(obj);
    v.activityTypes = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getActivityTypes", "()Ljava/lang/String;")));
    v.creator = jni::read_boxed_boolean(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getCreator", "()Ljava/lang/Boolean;")));
    v.distanceMax = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getDistanceMax", "()Ljava/lang/String;")));
    v.distanceMin = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getDistanceMin", "()Ljava/lang/String;")));
    v.elevationFilter = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getElevationFilter", "()Ljava/lang/String;")));
    v.overall = jni::read_boxed_boolean(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getOverall", "()Ljava/lang/Boolean;")));
    v.starred = jni::read_boxed_boolean(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getStarred", "()Ljava/lang/Boolean;")));
    v.surfaceTypes = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getSurfaceTypes", "()Ljava/lang/String;")));
    v.top10 = jni::read_boxed_boolean(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getTop10", "()Ljava/lang/Boolean;")));
    v.verified = jni::read_boxed_boolean(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getVerified", "()Ljava/lang/Boolean;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_SegmentsFilterOptions(JNIEnv* env, const SegmentsFilterOptions& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/SegmentsFilterOptions");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;)V");
    const jobject a0 = jni::write_string(env, value.activityTypes);
    const jobject a1 = jni::write_string(env, value.distanceMax);
    const jobject a2 = jni::write_string(env, value.distanceMin);
    const jobject a3 = jni::write_string(env, value.elevationFilter);
    const jobject a4 = jni::write_string(env, value.surfaceTypes);
    const jobject a5 = jni::write_boxed_boolean(env, value.creator);
    const jobject a6 = jni::write_boxed_boolean(env, value.overall);
    const jobject a7 = jni::write_boxed_boolean(env, value.starred);
    const jobject a8 = jni::write_boxed_boolean(env, value.top10);
    const jobject a9 = jni::write_boxed_boolean(env, value.verified);
    return env->NewObject(cls, ctor, a0, a1, a2, a3, a4, a5, a6, a7, a8, a9);
}

ServerConfig read_ServerConfig(JNIEnv* env, jobject obj) {
    ServerConfig v;
    jclass cls = env->GetObjectClass(obj);
    v.mapboxServerSecret = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getMapboxServerSecret", "()Ljava/lang/String;")));
    v.mapboxServerURL = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getMapboxServerURL", "()Ljava/lang/String;")));
    v.segmentsServerURL = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getSegmentsServerURL", "()Ljava/lang/String;")));
    v.stravaPoiServerURL = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getStravaPoiServerURL", "()Ljava/lang/String;")));
    v.stravaTileServerURL = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getStravaTileServerURL", "()Ljava/lang/String;")));
    v.tileSourceConfig = jni::read_TileSourceConfig(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getTileSourceConfig", "()Lcom/fatmap/sdk/api/TileSourceConfig;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_ServerConfig(JNIEnv* env, const ServerConfig& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/ServerConfig");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/fatmap/sdk/api/TileSourceConfig;)V");
    const jobject a0 = jni::write_string(env, value.mapboxServerSecret);
    const jobject a1 = jni::write_string(env, value.mapboxServerURL);
    const jobject a2 = jni::write_string(env, value.segmentsServerURL);
    const jobject a3 = jni::write_string(env, value.stravaPoiServerURL);
    const jobject a4 = jni::write_string(env, value.stravaTileServerURL);
    const jobject a5 = jni::write_TileSourceConfig(env, value.tileSourceConfig);
    return env->NewObject(cls, ctor, a0, a1, a2, a3, a4, a5);
}

StartPointFilterOptions read_StartPointFilterOptions(JNIEnv* env, jobject obj) {
    StartPointFilterOptions v;
    jclass cls = env->GetObjectClass(obj);
    v.difficulty = env->CallLongMethod(obj, env->GetMethodID(cls, "getDifficulty", "()J"));
    v.distance = env->CallLongMethod(obj, env->GetMethodID(cls, "getDistance", "()J"));
    v.elevation = env->CallFloatMethod(obj, env->GetMethodID(cls, "getElevation", "()F"));
    v.routeType = env->CallLongMethod(obj, env->GetMethodID(cls, "getRouteType", "()J"));
    v.surfaceType = env->CallLongMethod(obj, env->GetMethodID(cls, "getSurfaceType", "()J"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_StartPointFilterOptions(JNIEnv* env, const StartPointFilterOptions& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/StartPointFilterOptions");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(JJFJJ)V");
    return env->NewObject(cls, ctor, static_cast<int64_t>(value.difficulty), static_cast<int64_t>(value.distance), static_cast<float>(value.elevation), static_cast<int64_t>(value.routeType), static_cast<int64_t>(value.surfaceType));
}

TestingContextStatus read_TestingContextStatus(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<TestingContextStatus>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<TestingContextStatus>(value);
}

jobject write_TestingContextStatus(JNIEnv* env, TestingContextStatus value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/TestingContextStatus");
    const char* names[] = {"ENTERED", "ENTERING", "ENTERINGFAILED", "LEAVING", "NOCONTEXT"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/TestingContextStatus;");
    return env->GetStaticObjectField(cls, field);
}

TileSourceConfig read_TileSourceConfig(JNIEnv* env, jobject obj) {
    TileSourceConfig v;
    jclass cls = env->GetObjectClass(obj);
    v.globalHeatmapURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getGlobalHeatmapURLTemplate", "()Ljava/lang/String;")));
    v.gradientURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getGradientURLTemplate", "()Ljava/lang/String;")));
    v.heightmapURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getHeightmapURLTemplate", "()Ljava/lang/String;")));
    v.mapboxStreetsTerrainURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getMapboxStreetsTerrainURLTemplate", "()Ljava/lang/String;")));
    v.mapboxStreetsURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getMapboxStreetsURLTemplate", "()Ljava/lang/String;")));
    v.nightlyHeatmapURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getNightlyHeatmapURLTemplate", "()Ljava/lang/String;")));
    v.personalHeatmapURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getPersonalHeatmapURLTemplate", "()Ljava/lang/String;")));
    v.segmentsTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getSegmentsTemplate", "()Ljava/lang/String;")));
    v.stravaContoursURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getStravaContoursURLTemplate", "()Ljava/lang/String;")));
    v.stravaPlanetSportURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getStravaPlanetSportURLTemplate", "()Ljava/lang/String;")));
    v.stravaPlanetURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getStravaPlanetURLTemplate", "()Ljava/lang/String;")));
    v.stravaPoiURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getStravaPoiURLTemplate", "()Ljava/lang/String;")));
    v.summerImageryURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getSummerImageryURLTemplate", "()Ljava/lang/String;")));
    v.weeklyHeatmapURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getWeeklyHeatmapURLTemplate", "()Ljava/lang/String;")));
    v.winterImageryURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getWinterImageryURLTemplate", "()Ljava/lang/String;")));
    v.winterOverlayImageryURLTemplate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getWinterOverlayImageryURLTemplate", "()Ljava/lang/String;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_TileSourceConfig(JNIEnv* env, const TileSourceConfig& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/TileSourceConfig");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    const jobject a0 = jni::write_string(env, value.globalHeatmapURLTemplate);
    const jobject a1 = jni::write_string(env, value.gradientURLTemplate);
    const jobject a2 = jni::write_string(env, value.heightmapURLTemplate);
    const jobject a3 = jni::write_string(env, value.mapboxStreetsTerrainURLTemplate);
    const jobject a4 = jni::write_string(env, value.mapboxStreetsURLTemplate);
    const jobject a5 = jni::write_string(env, value.nightlyHeatmapURLTemplate);
    const jobject a6 = jni::write_string(env, value.personalHeatmapURLTemplate);
    const jobject a7 = jni::write_string(env, value.segmentsTemplate);
    const jobject a8 = jni::write_string(env, value.stravaContoursURLTemplate);
    const jobject a9 = jni::write_string(env, value.stravaPlanetSportURLTemplate);
    const jobject a10 = jni::write_string(env, value.stravaPlanetURLTemplate);
    const jobject a11 = jni::write_string(env, value.stravaPoiURLTemplate);
    const jobject a12 = jni::write_string(env, value.summerImageryURLTemplate);
    const jobject a13 = jni::write_string(env, value.weeklyHeatmapURLTemplate);
    const jobject a14 = jni::write_string(env, value.winterImageryURLTemplate);
    const jobject a15 = jni::write_string(env, value.winterOverlayImageryURLTemplate);
    return env->NewObject(cls, ctor, a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15);
}

TrackPerfTestResults read_TrackPerfTestResults(JNIEnv* env, jobject obj) {
    TrackPerfTestResults v;
    jclass cls = env->GetObjectClass(obj);
    v.avgFrameTimeMs = env->CallLongMethod(obj, env->GetMethodID(cls, "getAvgFrameTimeMs", "()J"));
    v.maxFrameTimeMs = env->CallLongMethod(obj, env->GetMethodID(cls, "getMaxFrameTimeMs", "()J"));
    v.minFrameTimeMs = env->CallLongMethod(obj, env->GetMethodID(cls, "getMinFrameTimeMs", "()J"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_TrackPerfTestResults(JNIEnv* env, const TrackPerfTestResults& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/TrackPerfTestResults");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(JJJ)V");
    return env->NewObject(cls, ctor, static_cast<int64_t>(value.avgFrameTimeMs), static_cast<int64_t>(value.maxFrameTimeMs), static_cast<int64_t>(value.minFrameTimeMs));
}

UnitsInfo read_UnitsInfo(JNIEnv* env, jobject obj) {
    UnitsInfo v;
    jclass cls = env->GetObjectClass(obj);
    v.meters = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getMeters", "()D"));
    v.size = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getSize", "()D"));
    v.unit = jni::read_MeasurementUnit(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getUnit", "()Lcom/fatmap/sdk/api/MeasurementUnit;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_UnitsInfo(JNIEnv* env, const UnitsInfo& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/UnitsInfo");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(DDLcom/fatmap/sdk/api/MeasurementUnit;)V");
    const jobject a2 = jni::write_MeasurementUnit(env, value.unit);
    return env->NewObject(cls, ctor, static_cast<double>(value.meters), static_cast<double>(value.size), a2);
}

UserMarkerClickEvent read_UserMarkerClickEvent(JNIEnv* env, jobject obj) {
    UserMarkerClickEvent v;
    jclass cls = env->GetObjectClass(obj);
    v.button = jni::read_MouseButtonCode(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getButton", "()Lcom/fatmap/sdk/api/MouseButtonCode;")));
    v.source = jni::read_ClickSourceType(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getSource", "()Lcom/fatmap/sdk/api/ClickSourceType;")));
    v.type = jni::read_ClickEventType(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getType", "()Lcom/fatmap/sdk/api/ClickEventType;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_UserMarkerClickEvent(JNIEnv* env, const UserMarkerClickEvent& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/UserMarkerClickEvent");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Lcom/fatmap/sdk/api/ClickEventType;Lcom/fatmap/sdk/api/ClickSourceType;Lcom/fatmap/sdk/api/MouseButtonCode;)V");
    const jobject a0 = jni::write_ClickEventType(env, value.type);
    const jobject a1 = jni::write_ClickSourceType(env, value.source);
    const jobject a2 = jni::write_MouseButtonCode(env, value.button);
    return env->NewObject(cls, ctor, a0, a1, a2);
}

VersionInfo read_VersionInfo(JNIEnv* env, jobject obj) {
    VersionInfo v;
    jclass cls = env->GetObjectClass(obj);
    v.branchName = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getBranchName", "()Ljava/lang/String;")));
    v.buildDate = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getBuildDate", "()Ljava/lang/String;")));
    v.buildHash = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getBuildHash", "()Ljava/lang/String;")));
    v.buildId = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getBuildId", "()Ljava/lang/String;")));
    v.buildVersion = env->CallIntMethod(obj, env->GetMethodID(cls, "getBuildVersion", "()I"));
    v.environment = env->CallByteMethod(obj, env->GetMethodID(cls, "getEnvironment", "()B"));
    v.majorVersion = env->CallShortMethod(obj, env->GetMethodID(cls, "getMajorVersion", "()S"));
    v.minorVersion = env->CallShortMethod(obj, env->GetMethodID(cls, "getMinorVersion", "()S"));
    v.patchVersion = env->CallShortMethod(obj, env->GetMethodID(cls, "getPatchVersion", "()S"));
    v.versionString = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getVersionString", "()Ljava/lang/String;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_VersionInfo(JNIEnv* env, const VersionInfo& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/VersionInfo");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;SSSILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;B)V");
    const jobject a0 = jni::write_string(env, value.branchName);
    const jobject a5 = jni::write_string(env, value.buildDate);
    const jobject a6 = jni::write_string(env, value.buildHash);
    const jobject a7 = jni::write_string(env, value.buildId);
    const jobject a8 = jni::write_string(env, value.versionString);
    return env->NewObject(cls, ctor, a0, static_cast<int16_t>(value.majorVersion), static_cast<int16_t>(value.minorVersion), static_cast<int16_t>(value.patchVersion), static_cast<int32_t>(value.buildVersion), a5, a6, a7, a8, static_cast<int8_t>(value.environment));
}

VisibleAreaBounds read_VisibleAreaBounds(JNIEnv* env, jobject obj) {
    VisibleAreaBounds v;
    jclass cls = env->GetObjectClass(obj);
    v.bottomLeftCorner = jni::read_WorldPoint2(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getBottomLeftCorner", "()Lcom/fatmap/sdk/api/WorldPoint2;")));
    v.bottomRightCorner = jni::read_WorldPoint2(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getBottomRightCorner", "()Lcom/fatmap/sdk/api/WorldPoint2;")));
    v.topLeftCorner = jni::read_WorldPoint2(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getTopLeftCorner", "()Lcom/fatmap/sdk/api/WorldPoint2;")));
    v.topRightCorner = jni::read_WorldPoint2(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getTopRightCorner", "()Lcom/fatmap/sdk/api/WorldPoint2;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_VisibleAreaBounds(JNIEnv* env, const VisibleAreaBounds& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/VisibleAreaBounds");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Lcom/fatmap/sdk/api/WorldPoint2;Lcom/fatmap/sdk/api/WorldPoint2;Lcom/fatmap/sdk/api/WorldPoint2;Lcom/fatmap/sdk/api/WorldPoint2;)V");
    const jobject a0 = jni::write_WorldPoint2(env, value.bottomLeftCorner);
    const jobject a1 = jni::write_WorldPoint2(env, value.bottomRightCorner);
    const jobject a2 = jni::write_WorldPoint2(env, value.topLeftCorner);
    const jobject a3 = jni::write_WorldPoint2(env, value.topRightCorner);
    return env->NewObject(cls, ctor, a0, a1, a2, a3);
}

WeeklyHeatmapConfig read_WeeklyHeatmapConfig(JNIEnv* env, jobject obj) {
    WeeklyHeatmapConfig v;
    jclass cls = env->GetObjectClass(obj);
    v.activityType = jni::read_string(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getActivityType", "()Ljava/lang/String;")));
    v.colorScheme = jni::read_HeatmapColorScheme(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getColorScheme", "()Lcom/fatmap/sdk/api/HeatmapColorScheme;")));
    v.opacityMultiplier = jni::read_boxed_float(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getOpacityMultiplier", "()Ljava/lang/Float;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_WeeklyHeatmapConfig(JNIEnv* env, const WeeklyHeatmapConfig& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/WeeklyHeatmapConfig");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;Lcom/fatmap/sdk/api/HeatmapColorScheme;Ljava/lang/Float;)V");
    const jobject a0 = jni::write_string(env, value.activityType);
    const jobject a1 = jni::write_HeatmapColorScheme(env, value.colorScheme);
    const jobject a2 = jni::write_boxed_float(env, value.opacityMultiplier);
    return env->NewObject(cls, ctor, a0, a1, a2);
}

WindowState read_WindowState(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return static_cast<WindowState>(0);
    }
    jclass cls = env->GetObjectClass(obj);
    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");
    const int value = env->CallIntMethod(obj, ordinal);
    return static_cast<WindowState>(value);
}

jobject write_WindowState(JNIEnv* env, WindowState value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/WindowState");
    const char* names[] = {"ACTIVE", "CREATED", "DESTROYED", "INACTIVE", "NONE"};
    const int index = static_cast<int>(value);
    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {
        return nullptr;
    }
    jfieldID field = env->GetStaticFieldID(cls, names[index], "com/fatmap/sdk/api/WindowState;");
    return env->GetStaticObjectField(cls, field);
}

WorldBounds2 read_WorldBounds2(JNIEnv* env, jobject obj) {
    WorldBounds2 v;
    jclass cls = env->GetObjectClass(obj);
    v.northEastCorner = jni::read_WorldPoint2(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getNorthEastCorner", "()Lcom/fatmap/sdk/api/WorldPoint2;")));
    v.southWestCorner = jni::read_WorldPoint2(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getSouthWestCorner", "()Lcom/fatmap/sdk/api/WorldPoint2;")));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_WorldBounds2(JNIEnv* env, const WorldBounds2& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/WorldBounds2");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Lcom/fatmap/sdk/api/WorldPoint2;Lcom/fatmap/sdk/api/WorldPoint2;)V");
    const jobject a0 = jni::write_WorldPoint2(env, value.northEastCorner);
    const jobject a1 = jni::write_WorldPoint2(env, value.southWestCorner);
    return env->NewObject(cls, ctor, a0, a1);
}

WorldLine2 read_WorldLine2(JNIEnv* env, jobject obj) {
    WorldLine2 v;
    jclass cls = env->GetObjectClass(obj);
    v.points = jni::read_list<WorldPoint2>(env, env->CallObjectMethod(obj, env->GetMethodID(cls, "getPoints", "()Ljava/util/ArrayList;")), [](JNIEnv* e, jobject o) { return jni::read_WorldPoint2(e, o); });
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_WorldLine2(JNIEnv* env, const WorldLine2& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/WorldLine2");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Ljava/util/ArrayList;)V");
    const jobject a0 = jni::write_list<WorldPoint2>(env, value.points, [](JNIEnv* e, const WorldPoint2& v) { return jni::write_WorldPoint2(e, v); });
    return env->NewObject(cls, ctor, a0);
}

WorldPoint2 read_WorldPoint2(JNIEnv* env, jobject obj) {
    WorldPoint2 v;
    jclass cls = env->GetObjectClass(obj);
    v.latitude = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getLatitude", "()D"));
    v.longitude = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getLongitude", "()D"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_WorldPoint2(JNIEnv* env, const WorldPoint2& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/WorldPoint2");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(DD)V");
    return env->NewObject(cls, ctor, static_cast<double>(value.latitude), static_cast<double>(value.longitude));
}

WorldPoint3 read_WorldPoint3(JNIEnv* env, jobject obj) {
    WorldPoint3 v;
    jclass cls = env->GetObjectClass(obj);
    v.elevation = env->CallFloatMethod(obj, env->GetMethodID(cls, "getElevation", "()F"));
    v.latitude = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getLatitude", "()D"));
    v.longitude = env->CallDoubleMethod(obj, env->GetMethodID(cls, "getLongitude", "()D"));
    env->DeleteLocalRef(cls);
    return v;
}

jobject write_WorldPoint3(JNIEnv* env, const WorldPoint3& value) {
    jclass cls = env->FindClass("com/fatmap/sdk/api/WorldPoint3");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(DDF)V");
    return env->NewObject(cls, ctor, static_cast<double>(value.latitude), static_cast<double>(value.longitude), static_cast<float>(value.elevation));
}

}  // namespace jni
