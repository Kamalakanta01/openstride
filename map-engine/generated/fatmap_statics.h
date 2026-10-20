// Generated from the Fatmap SDK interface contract. Do not edit by hand.
#pragma once

#include <jni.h>

#include <string>
#include <vector>

#include "fatmap_api.h"
#include "fatmap_jni_types.h"

namespace fatmap {

EngineConfigurationImpl* static_EngineConfiguration_createDefault(JNIEnv* env);
MREHolderImpl* static_MREHolder_create(JNIEnv* env, jni::Opaque p0, jni::Opaque p1, std::string p2, std::string p3, jni::Opaque p4);
OverriddenFeatureProperties static_OverriddenFeatureProperties_create(JNIEnv* env);
std::vector<std::string> static_PrivateApi_getPerformanceTestsNames(JNIEnv* env);

}  // namespace fatmap
