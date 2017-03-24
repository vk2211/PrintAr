/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#ifndef __EASYAR_SAMPLE_UTILITY_SIMPLERENDERER_H__
#define __EASYAR_SAMPLE_UTILITY_SIMPLERENDERER_H__

#include <jni.h>
#include <string>
#include "Log.h"
#include "Common.h"
#include "VideoData.h"

int getVideoDataFromJavaObject(JNIEnv *jenv, const jobject &jVideoData, VideoData &videoData) {
	if ((NULL == jenv) || (NULL == jVideoData)) {
		LOGI("enter function getReceiveOptionFromObject --> (NULL == jenv) || (NULL == jReceiveOption)");
		return 2;
	}

	//get values from java for ReceiveOption
	jclass jVideoDataClass = jenv->FindClass(JNI_CLASS(JNI_DATA_PACKAGE, VideoData));
	if (NULL == jVideoDataClass) {
		LOGI("FindClass("
				 JNI_CLASS(JNI_DATA_PACKAGE, VideoData)
				 ") --> NULL == jVideoData");
		return 2;
	}
	jmethodID mid = jenv->GetMethodID(jVideoDataClass, "<init>", "("JNI_CLASS_PARAM(JNI_DATA_PACKAGE, VideoData)")V");
	if (NULL == mid) {
		STR(L##JNI_PACKAGE);
		JNI_CLASS_PARAM(JNI_DATA_PACKAGE, VideoData);
		LOGI("GetMethodID(("
				 JNI_CLASS_PARAM(JNI_DATA_PACKAGE, VideoData)
				 ")V) --> NULL == mid");
		return 2;
	}
	jobject videoDataObject = jenv->NewObject(jVideoDataClass, mid, jVideoData);
	if (NULL == videoDataObject) {
		LOGI("NewObject --> NULL == videoDataObject");
		return 2;
	}

	mid = jenv->GetMethodID(jVideoDataClass, "getImagePath", "()Ljava/lang/String;");
	if (NULL == mid) {
		LOGI("GetMethodID(getDirectory) --> NULL == mid");
		return 2;
	}
	jstring imagePath = (jstring) jenv->CallObjectMethod(videoDataObject, mid);
	if (NULL == imagePath) {
		LOGI("CallObjectMethod --> NULL == imagePath");
		return 2;
	}
	const char *pImagePath = jenv->GetStringUTFChars(imagePath, 0);
	if (NULL == pImagePath) {
		LOGI("GetStringUTFChars --> NULL == pImagePath");
		return 2;
	}
	videoData.setImagePath(pImagePath);
	jenv->ReleaseStringUTFChars(imagePath, pImagePath);

	mid = jenv->GetMethodID(jVideoDataClass, "getVideoPath", "()Ljava/lang/String;");
	if (NULL == mid) {
		LOGI("GetMethodID(isRename) --> NULL == mid");
		return 2;
	}
	jstring videoPath = (jstring) jenv->CallObjectMethod(videoDataObject, mid);
	if (NULL == videoPath) {
		LOGI("CallObjectMethod --> NULL == videoPath");
		return 2;
	}
	const char *pVideoPath = jenv->GetStringUTFChars(videoPath, 0);
	if (NULL == pVideoPath) {
		LOGI("GetStringUTFChars --> NULL == pVideoPath");
		return 2;
	}
	videoData.setVideoPath(pVideoPath);
	jenv->ReleaseStringUTFChars(videoPath, pVideoPath);

	mid = jenv->GetMethodID(jVideoDataClass, "getStartTime", "()J");
	if (NULL == mid) {
		LOGI("GetMethodID(getPercent) --> NULL == mid");
		return 2;
	}
	jlong nPercent = jenv->CallLongMethod(videoDataObject, mid);
	videoData.setStartTime(nPercent);
//	videoData.log();
	return 0;
}

jobject convertVideoDataToJavaObject(JNIEnv *jenv, const VideoData &videoData) {
	if (NULL == jenv) {
		LOGI("enter function getReceiveOptionFromObject --> (NULL == jenv) || (NULL == jReceiveOption)");
		return NULL;
	}

	//get values from java for ReceiveOption
	jclass jVideoDataClass = jenv->FindClass(JNI_CLASS(JNI_DATA_PACKAGE, VideoData));
	if (NULL == jVideoDataClass) {
		LOGI("FindClass("
				 JNI_CLASS(JNI_DATA_PACKAGE, VideoData)
				 ") --> NULL == jVideoData");
		return NULL;
	}
	jmethodID mid = jenv->GetMethodID(jVideoDataClass, "<init>", "()V");
	if (NULL == mid) {
		STR(L##JNI_PACKAGE);
		JNI_CLASS_PARAM(JNI_DATA_PACKAGE, VideoData);
		LOGI("GetMethodID(("
				 JNI_CLASS_PARAM(JNI_DATA_PACKAGE, VideoData)
				 ")V) --> NULL == mid");
		return NULL;
	}
	jobject videoDataObject = jenv->NewObject(jVideoDataClass, mid);
	if (NULL == videoDataObject) {
		LOGI("NewObject --> NULL == videoDataObject");
		return NULL;
	}

	jfieldID field = jenv->GetFieldID(jVideoDataClass, "mImagePath", "Ljava/lang/String;");
	if (NULL == field) {
		LOGI("GetFieldID(mImagePath) --> NULL == field");
		return NULL;
	}
	jstring jImagePath = jenv->NewStringUTF(videoData.getImagePath().c_str());
	jenv->SetObjectField(videoDataObject, field, jImagePath);

	field = jenv->GetFieldID(jVideoDataClass, "mVideoPath", "Ljava/lang/String;");
	if (NULL == field) {
		LOGI("GetFieldID(mVideoPath) --> NULL == field");
		return NULL;
	}
	jstring jVideoPath = jenv->NewStringUTF(videoData.getVideoPath().c_str());
	jenv->SetObjectField(videoDataObject, field, jVideoPath);

	field = jenv->GetFieldID(jVideoDataClass, "mStartTime", "J");
	if (NULL == field) {
		LOGI("GetFieldID(mStartTime) --> NULL == field");
		return NULL;
	}
	jenv->SetLongField(videoDataObject, field, videoData.getStartTime());

	return videoDataObject;
}

#endif
