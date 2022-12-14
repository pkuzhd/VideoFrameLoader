#ifndef MAIN_LOGCAT_H
#define MAIN_LOGCAT_H

#include <android/log.h>

#define  LOG_TAG "VideoFrameLoader"

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,   LOG_TAG, __VA_ARGS__)
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,   LOG_TAG, __VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,    LOG_TAG, __VA_ARGS__)

#endif
