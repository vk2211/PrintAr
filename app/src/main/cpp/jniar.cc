/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#include <string>
#include <map>
#include <vector>
#include <jni.h>
#include <GLES2/gl2.h>
#include "ar.hpp"
#include "renderer.hpp"
#include "Log.h"
#include "VideoData.h"
#include "arvideo.hpp"

#define JNIFUNCTION_NATIVE(sig) Java_com_yipai_printar_ar_NativeAr_##sig

extern "C" {
    JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(init(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(destroy(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(initGL(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(resizeGL(JNIEnv* env, jobject object, jint w, jint h));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(render(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(rotationChange(JNIEnv* env, jobject object, jboolean portrait));
	JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(add(JNIEnv *env, jobject object, jobject videoData));
	JNIEXPORT jobject JNICALL JNIFUNCTION_NATIVE(get(JNIEnv *env, jclass type));
};

static EasyAR::samples::HelloARVideo ar;

JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(init(JNIEnv * , jobject)) {
	int i;
	bool status = ar.initCamera();
	ar.init();
	status &= ar.start();
	return status;
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(destroy(JNIEnv * , jobject)) {
	ar.clear();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(initGL(JNIEnv * , jobject)) {
	ar.initGL();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(resizeGL(JNIEnv * , jobject, jint w, jint h)) {
	ar.resizeGL(w, h);
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(render(JNIEnv * , jobject)) {
	ar.render();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(rotationChange(JNIEnv * , jobject, jboolean portrait)) {
	ar.setPortrait(portrait);
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(add(JNIEnv *env, jobject, jobject videoData)) {
	VideoData v;
	getVideoDataFromJavaObject(env, videoData, v);
	ar.push(v);
}

JNIEXPORT jobject JNICALL JNIFUNCTION_NATIVE(get(JNIEnv *env, jclass type)) {
	return convertVideoDataToJavaObject(env, *ar.getCurrentVideoData());
}
