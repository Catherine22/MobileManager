//
// Created by Catherine on 2017/6/15.
//
#include <jni.h>
#include <android/log.h>
#include <string.h>
#include "Config.h"

extern "C" {
    #define TAGNAME "JNI_LOG"

    JNIEXPORT jstring JNICALL
    Java_com_itheima_mobilesafe_utils_SecurityUtils_getAuthentication(JNIEnv *env, jobject instance) {
        return env->NewStringUTF("V293ISBob3cgY3VyaW91cyBlaD8=");
    }



    JNIEXPORT jint JNICALL
    Java_com_itheima_mobilesafe_utils_SecurityUtils_getdynamicID(JNIEnv *env, jobject instance, jint timestamp) {
            return timestamp*5;
     }


    JNIEXPORT jobjectArray JNICALL
    Java_com_itheima_mobilesafe_utils_SecurityUtils_getAuthChain(JNIEnv *env, jobject instance, jstring key) {

    jobjectArray valueArray = (jobjectArray)env->NewObjectArray(5, env->FindClass("java/lang/String"), 0);

    const char *keyChar = env->GetStringUTFChars(key, 0);

     if(strcmp(keyChar, "LOGIN") == 0){
        for (int i=0; i<5; i++)
        {
            char *hash = "Czc0SC";
            jstring value = env->NewStringUTF(hash);
            env->SetObjectArrayElement(valueArray, i, value);
        }
     }else{
        for (int i=0; i<5; i++)
        {
            char *hash = "OVma0x";
            jstring value = env->NewStringUTF(hash);
            env->SetObjectArrayElement(valueArray, i, value);
         }
     }
        return valueArray;
    }
}