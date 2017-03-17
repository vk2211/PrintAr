/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#include <string>
#include <map>
#include "ar.hpp"
#include "renderer.hpp"
#include <jni.h>
#include <GLES2/gl2.h>
#ifdef ANDROID
#include <android/log.h>
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "EasyAR", __VA_ARGS__)
#else
#define LOGI(...) printf(__VA_ARGS__)
#endif

#define JNIFUNCTION_NATIVE1(sig) Java_cn_easyar_samples_helloarvideo_MainActivity_##sig
#define JNIFUNCTION_NATIVE(sig) Java_com_yipai_printar_ar_NativeAr_##sig

extern "C" {
    JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(init(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(destroy(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(initGL(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(resizeGL(JNIEnv* env, jobject object, jint w, jint h));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(render(JNIEnv* env, jobject obj));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(rotationChange(JNIEnv* env, jobject obj, jboolean portrait));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(add(JNIEnv* env, jobject object, jstring s1, jstring s2));
};
bool jstringToString(JNIEnv* env, jstring jstr, std::string &stlStr);

#define MAX_RECOGNIZE 100
std::string namestr[MAX_RECOGNIZE];
std::map<std::string, std::string> Map;
std::map<std::string, int> IndexMap;


JNIEXPORT jboolean JNICALL
Java_com_yipai_printar_ar_NativeAr_init1(JNIEnv *env, jclass type) {

	// TODO

}

namespace EasyAR {
	namespace samples {


class HelloARVideo : public AR {
public:
	HelloARVideo();

	~HelloARVideo();

	virtual void initGL();

	void addGL();

	virtual void resizeGL(int width, int height);

	virtual void render();

	virtual bool clear();

	int index;
private:
	Vec2I view_size;
	VideoRenderer *renderer[MAX_RECOGNIZE];
	int tracked_target;
	int active_target;
	int texid[MAX_RECOGNIZE];
	ARVideo *video;
	VideoRenderer *video_renderer;
};

HelloARVideo::HelloARVideo() {
	int i;
	view_size[0] = -1;
	tracked_target = 0;
	active_target = 0;
	for (i = 0; i < MAX_RECOGNIZE; ++i) {
		texid[i] = 0;
		renderer[i] = new VideoRenderer;
	}
	video = NULL;
	video_renderer = NULL;
	index = 3;
}

HelloARVideo::~HelloARVideo() {
	int i;
	for (i = 0; i < MAX_RECOGNIZE; ++i) {
		delete renderer[i];
	}
}

void HelloARVideo::initGL() {
	augmenter_ = Augmenter();
	augmenter_.attachCamera(camera_);
	int i;
	for (i = 0; i < MAX_RECOGNIZE; ++i) {
		renderer[i]->init();
		texid[i] = renderer[i]->texId();
	}
}

void HelloARVideo::addGL() {
	renderer[index]->init();
	texid[index] = renderer[index]->texId();
	index++;
}

void HelloARVideo::resizeGL(int width, int height) {
	view_size = Vec2I(width, height);
}

void HelloARVideo::render() {
	glClearColor(0.f, 0.f, 0.f, 1.f);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	Frame frame = augmenter_.newFrame();
	if (view_size[0] > 0) {
		AR::resizeGL(view_size[0], view_size[1]);
		if (camera_ && camera_.isOpened())
			view_size[0] = -1;
	}
	augmenter_.setViewPort(viewport_);
	augmenter_.drawVideoBackground();
	glViewport(viewport_[0], viewport_[1], viewport_[2], viewport_[3]);

	AugmentedTarget::Status status = frame.targets()[0].status();
//  LOGI("############### AugmentedTarget::Status status = frame.targets()[0].status()");
	if (status == AugmentedTarget::kTargetStatusTracked) {
		int id = frame.targets()[0].target().id();
		if (active_target && active_target != id) {
			video->onLost();
			delete video;
			video = NULL;
			tracked_target = 0;
			active_target = 0;
		}
		if (!tracked_target) {
			if (video == NULL) {
				LOGI("############### target=%s", frame.targets()[0].target().name());
				if (frame.targets()[0].target().name() == std::string("argame") && texid[0]) {
					video = new ARVideo;
					video->openVideoFile("video.mp4", texid[0]);
					video_renderer = renderer[0];
				}
				else if (frame.targets()[0].target().name() == std::string("namecard") && texid[1]) {
					video = new ARVideo;
					video->openTransparentVideoFile("transparentvideo.mp4", texid[1]);
					video_renderer = renderer[1];
				}
				else if (frame.targets()[0].target().name() == std::string("idback") && texid[2]) {
					video = new ARVideo;
					video->openStreamingVideo(
						"http://7xl1ve.com5.z0.glb.clouddn.com/sdkvideo/EasyARSDKShow201520.mp4", texid[2]);
					video_renderer = renderer[2];
				} else {
					int i = 3;
                    LOGI("############### else, target=%s, namestr[i]=%s",
                         frame.targets()[0].target().name(), namestr[i].c_str());
					for (i = 3; i < index; i++) {
						if (frame.targets()[0].target().name() == namestr[i] && texid[i]) {
                            LOGI("###################### else, url=%s", Map[namestr[i]].c_str());
							video = new ARVideo;
							video->openStreamingVideo(Map[namestr[i]], texid[i]);
							video_renderer = renderer[i];
							break;
						}
					}
				}
			}
			if (video) {
				video->onFound();
				tracked_target = id;
				active_target = id;
			}
		}
		Matrix44F projectionMatrix = getProjectionGL(camera_.cameraCalibration(), 0.2f, 500.f);
		Matrix44F cameraview = getPoseGL(frame.targets()[0].pose());
		ImageTarget target = frame.targets()[0].target().cast_dynamic<ImageTarget>();
		if (tracked_target) {
			video->update();
			video_renderer->render(projectionMatrix, cameraview, target.size());
		}
	} else {
		if (tracked_target) {
			video->onLost();
			tracked_target = 0;
		}
	}
}

bool HelloARVideo::clear() {
	AR::clear();
	if (video) {
		delete video;
		video = NULL;
		tracked_target = 0;
		active_target = 0;
	}
	return true;
}

	}
}

bool jstringToString(JNIEnv *env, jstring jstr, std::string &stlStr) {
	if (jstr == NULL) {
		stlStr.assign("");
		return true;
	}

	const char *str = env->GetStringUTFChars(jstr, NULL);
	if (str == NULL) {
		LOGI("GetStringUTFChars fail!");
		return false; /* OutOfMemoryError already thrown */
	}
	stlStr.assign(str);
	env->ReleaseStringUTFChars(jstr, str);

	return true;
}

EasyAR::samples::HelloARVideo ar;

JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(init(JNIEnv * , jobject)) {
	int i;
	bool status = ar.initCamera();
	ar.loadAllFromJsonFile("targets.json");
	ar.loadFromImage("namecard.jpg");
	for (i = 3; i < ar.index; i++) {
		LOGI("############# nativeInit, str=%s", namestr[i].c_str());
		ar.loadFromImage(namestr[i] + ".jpg");
	}
//    ar.loadFromImage("/storage/emulated/0/Yipai/ArDemo/1489350086109.jpg");
//    ar.loadAbsoluteImage("/storage/emulated/0/Yipai/ArDemo/1489350086109.jpg");
//    ar.loadFromImage("/sdcard/Yipai/ArDemo/1489350086109.jpg");
//    ar.loadAbsoluteImage("/sdcard//Yipai/ArDemo/1489350086109.jpg");
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

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(add(JNIEnv * env, jobject, jstring s1, jstring s2)) {
	std::string str1;
	std::string str2;
	jstringToString(env, s1, str1);
	jstringToString(env, s2, str2);
	LOGI("%%%%%%%%%%%%%%%%%%%%%%%%%%%%% s1=%s, s2=%s", str1.c_str(), str2.c_str());
	namestr[ar.index] = str1.replace(str1.find(".jpg"), 4, "");
	Map[str1] = str2;
	IndexMap[str1] = ar.index++;
}
