//
// Created by pkuzhd on 2022/11/22.
//

#include <jni.h>
#include <android/native_window_jni.h>

void CppMain();

extern "C"
JNIEXPORT void JNICALL
Java_com_example_testnative_CppMain_main(JNIEnv *env, jobject thiz) {
    CppMain();
}