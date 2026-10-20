# The Fatmap-compatible map engine.
#
# Three targets:
#   fatmap_core  - the engine itself, host-testable
#   fatmap_jni   - the shared object the Android app loads
#   fatmap_tests - host tests (not built for Android)
#
# The JNI entry points are generated from the extracted interface contract;
# see tools/fatmap_codegen.py.

cmake_minimum_required(VERSION 3.16)
project(openstride_map_engine LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

if(ANDROID)
    # The NDK supplies jni.h; there is no JDK to find.
    add_library(fatmap_jni SHARED
        ${CMAKE_CURRENT_SOURCE_DIR}/generated/fatmap_jni.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/generated/fatmap_jni_values.cpp
        src/engine.cpp
        src/engine_api.cpp
        src/geo.cpp
        src/sha256.cpp
    )
    target_include_directories(fatmap_jni PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/src
        ${CMAKE_CURRENT_SOURCE_DIR}/generated
    )
    target_link_libraries(fatmap_jni PRIVATE log)
else()
    find_package(JNI REQUIRED)
    find_package(Threads REQUIRED)

    set(GENERATED_DIR ${CMAKE_CURRENT_SOURCE_DIR}/generated)
    set(SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/src)

    add_library(fatmap_core STATIC
        ${SRC_DIR}/geo.cpp
        ${SRC_DIR}/sha256.cpp
        ${SRC_DIR}/engine.cpp
        ${SRC_DIR}/engine_api.cpp
    )
    target_include_directories(fatmap_core PUBLIC
        ${SRC_DIR}
        ${GENERATED_DIR}
        ${JNI_INCLUDE_DIRS}
    )
    target_link_libraries(fatmap_core PUBLIC Threads::Threads)

    add_library(fatmap_jni SHARED
        ${GENERATED_DIR}/fatmap_jni.cpp
        ${GENERATED_DIR}/fatmap_jni_values.cpp
    )
    target_link_libraries(fatmap_jni PRIVATE fatmap_core)
    target_include_directories(fatmap_jni PRIVATE ${JNI_INCLUDE_DIRS})

    option(FATMAP_BUILD_TESTS "Build the host test suite" ON)
    if(FATMAP_BUILD_TESTS)
        enable_testing()
        add_executable(fatmap_tests ${CMAKE_CURRENT_SOURCE_DIR}/tests/test_engine.cpp)
        target_link_libraries(fatmap_tests PRIVATE fatmap_core)
        add_test(NAME fatmap_tests COMMAND fatmap_tests)
    endif()
endif()
