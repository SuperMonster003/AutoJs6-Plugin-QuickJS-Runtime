cmake_minimum_required(VERSION 3.22.1)
project(autojs_quickjs_runtime LANGUAGES C CXX)

set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED ON)
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(QUICKJS_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/vendor/quickjs-2026-06-04")
set(QUICKJS_SOURCES
    quickjs.c
    dtoa.c
    libregexp.c
    libunicode.c
    cutils.c
)

foreach(quickjs_source IN LISTS QUICKJS_SOURCES)
    if(NOT EXISTS "${QUICKJS_ROOT}/${quickjs_source}")
        message(FATAL_ERROR "Pinned QuickJS 2026-06-04 source is incomplete: ${quickjs_source}")
    endif()
endforeach()

list(TRANSFORM QUICKJS_SOURCES PREPEND "${QUICKJS_ROOT}/")

add_library(autojs_quickjs_runtime SHARED
    quickjs_runtime_jni.cpp
    ${QUICKJS_SOURCES}
)

target_compile_definitions(autojs_quickjs_runtime PRIVATE
    _GNU_SOURCE
    CONFIG_VERSION="2026-06-04"
)

target_compile_options(autojs_quickjs_runtime PRIVATE
    -Wall
    -Wextra
    -Werror=return-type
    -fwrapv
    -fvisibility=hidden
)

# NDK r28 produces 16 KB compatible ELF alignment by default. Keep the linker
# requirement explicit so a future toolchain change cannot silently regress it.
target_link_options(autojs_quickjs_runtime PRIVATE
    "-Wl,-z,max-page-size=16384"
)

target_link_libraries(autojs_quickjs_runtime PRIVATE log m)
