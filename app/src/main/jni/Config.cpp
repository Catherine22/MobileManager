//
// Created by Catherine on 2017/6/15.
//
#include <jni.h>
#include "Config.h"
extern "C" {
    JNIEXPORT jstring JNICALL
    Java_com_itheima_mobilesafe_utils_SecurityUtils_getAuthentication(JNIEnv *env, jobject instance) {
        return env->NewStringUTF("V293ISBob3cgY3VyaW91cyBlaD8=");
    }
}