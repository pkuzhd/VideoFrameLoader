cmake_minimum_required(VERSION 3.18)
project(main)

include_directories(../../../include)
link_directories(../../../libs/arm64-v8a)

add_library(VideoLib
        VideoLib/Video2frame.cpp VideoLib/Video2frame.h
        )
target_link_libraries(
        VideoLib
        ijkffmpeg
)

add_library(main
        SHARED
        jni_main.cpp
        main.cpp
        )
find_library(
        log-lib
        log
)

target_link_libraries( # Specifies the target library.
        main
        ijkffmpeg
        VideoLib
        ${log-lib}
        android
        jnigraphics
        EGL
        GLESv3
        )