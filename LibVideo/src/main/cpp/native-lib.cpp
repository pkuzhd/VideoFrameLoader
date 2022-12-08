//
// Created by pkuzhd on 2022/11/24.
//
#include <jni.h>

#include "VideoFrameLoader.h"
#include "logcat.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_netvideo_libvideo_VideoFrameLoader_nInit(JNIEnv *env, jobject thiz,
                                                 jstring videoFilename,
                                                 jstring depthFilename) {
    LOGD("@@@ init");
    const char *video = env->GetStringUTFChars(videoFilename, nullptr);
    const char *depth = env->GetStringUTFChars(depthFilename, nullptr);
    VideoFrameLoader *videoFrameLoader = new VideoFrameLoader(video, depth);
    env->ReleaseStringUTFChars(videoFilename, video);
    env->ReleaseStringUTFChars(depthFilename, depth);
    return reinterpret_cast<jlong>(videoFrameLoader);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_netvideo_libvideo_VideoFrameLoader_nInit2(JNIEnv *env, jobject thiz,
                                                  jstring videoFilename,
                                                  jstring depthFilename) {
    LOGD("@@@ init");
    const char *video = env->GetStringUTFChars(videoFilename, nullptr);
    const char *depth = env->GetStringUTFChars(depthFilename, nullptr);
    VideoFrameLoader *videoFrameLoader = new VideoFrameLoader(video, depth, 2);
    env->ReleaseStringUTFChars(videoFilename, video);
    env->ReleaseStringUTFChars(depthFilename, depth);
    return reinterpret_cast<jlong>(videoFrameLoader);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_netvideo_libvideo_VideoFrameLoader_nGetTextureID(JNIEnv *env, jobject thiz, jlong ptr,
                                                         jint textureType) {
    LOGD("@@@ get texture id");
    auto video = reinterpret_cast<VideoFrameLoader *>(ptr);
    return video->getTextureId(textureType);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_netvideo_libvideo_VideoFrameLoader_nRun(JNIEnv *env, jobject thiz, jlong ptr) {
    LOGD("@@@ run");
    auto video = reinterpret_cast<VideoFrameLoader *>(ptr);
    return video->run();
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_netvideo_libvideo_VideoFrameLoader_nUpdate(JNIEnv *env, jobject thiz, jlong ptr) {
    LOGD("@@@ update");
    auto video = reinterpret_cast<VideoFrameLoader *>(ptr);
    return video->update();
}
extern "C"
JNIEXPORT jdouble JNICALL
Java_com_netvideo_libvideo_VideoFrameLoader_nGetNextPTS(JNIEnv *env, jobject thiz, jlong ptr) {
    LOGD("@@@ getNextPTS");
    auto video = reinterpret_cast<VideoFrameLoader *>(ptr);
    return video->getNextPTS();

}
extern "C"
JNIEXPORT jdouble JNICALL
Java_com_netvideo_libvideo_VideoFrameLoader_nGetLastPTS(JNIEnv *env, jobject thiz, jlong ptr) {
    LOGD("@@@ getLastPTS");
    auto video = reinterpret_cast<VideoFrameLoader *>(ptr);
    return video->getLastPTS();
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_netvideo_libvideo_VideoFrameLoader_nUpdate2(JNIEnv *env, jobject thiz, jlong ptr,
                                                    jint type) {
    LOGD("@@@ update2 %d", type);
    auto video = reinterpret_cast<VideoFrameLoader *>(ptr);
    return video->update2(type);
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_netvideo_libvideo_VideoFrameLoader_nGetInt(JNIEnv *env, jobject thiz, jlong ptr,
                                                   jstring _name, jint idx) {

    const char *__name = env->GetStringUTFChars(_name, nullptr);
    std::string name = __name;
    env->ReleaseStringUTFChars(_name, __name);

    auto video = reinterpret_cast<VideoFrameLoader *>(ptr);

    return video->getInt(name, idx);
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_netvideo_libvideo_VideoFrameLoader_nGetByteBuffer(JNIEnv *env, jobject thiz, jlong ptr,
                                                          jint idx, jobject byteBuffer) {
    if (idx == -1) {
        uint8_t *start = static_cast<uint8_t *>(env->GetDirectBufferAddress(byteBuffer));
        int capacity = env->GetDirectBufferCapacity(byteBuffer);
        if (start) {
            for (int i = 0; i < capacity / 4; ++i) {
//                if (i % 3 == 0)
                start[i] = 0xff;
            }
            for (int i = capacity / 4; i < capacity / 2; ++i) {
//                if (i % 3 == 0)
                start[i] = 0xef;
            }
            return 1;
        }
    }
    auto video = reinterpret_cast<VideoFrameLoader *>(ptr);
    int width = video->getInt("width", idx);
    int height = video->getInt("height", idx);
    uint8_t *buffer = video->getBuffer(idx);

    uint8_t *start = static_cast<uint8_t *>(env->GetDirectBufferAddress(byteBuffer));
    int capacity = env->GetDirectBufferCapacity(byteBuffer);
    if (start && buffer) {
        memcpy(start, buffer, capacity);
        return 1;
    }
    return 0;
}