cmake_minimum_required(VERSION 3.18)
project(main)

include_directories(../../../include)
link_directories(../../../libs/${ANDROID_ABI})

add_library(VideoLib
        video/Video2frame.cpp video/Video2frame.h
        )
target_link_libraries(
        VideoLib
        ijkffmpeg
)

add_library(video
        SHARED
        native-lib.cpp
        logcat.h
        VideoFrameLoader.cpp
        VideoFrameLoader.h
        )
find_library(
        log-lib
        log
)

target_link_libraries( # Specifies the target library.
        video
        ijkffmpeg
        VideoLib
        ${log-lib}
        android
        jnigraphics
        EGL
        GLESv3
        m)