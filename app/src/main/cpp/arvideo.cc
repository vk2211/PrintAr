/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#include <string>
#include <map>
#include <vector>
#include "ar.hpp"
#include "renderer.hpp"
#include <jni.h>
#include <GLES2/gl2.h>
#ifdef ANDROI1D
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

//#define MAX_RECOGNIZE 100
//std::string namestr[MAX_RECOGNIZE];


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
private:
	Vec2I view_size;
	std::vector<VideoRenderer *> renderer;
	int tracked_target;
	int active_target;
	std::vector<int> texid;
	ARVideo *video;
	VideoRenderer *video_renderer;
public:
	std::vector<std::string> namestr;
	std::map<std::string, std::string> Map;
};

HelloARVideo::HelloARVideo() {
	int i;
	view_size[0] = -1;
	tracked_target = 0;
	active_target = 0;
	video = NULL;
	video_renderer = NULL;
}

HelloARVideo::~HelloARVideo() {
	std::vector<VideoRenderer *>::iterator iter;
	for (iter = renderer.begin(); iter != renderer.end(); iter++) {
		delete *iter;
	}
}

void HelloARVideo::initGL() {
	augmenter_ = Augmenter();
	augmenter_.attachCamera(camera_);
	std::vector<std::string>::iterator iter;
	for (iter = namestr.begin(); iter != namestr.end(); iter++) {
		addGL();
	}
}

void HelloARVideo::addGL() {
	VideoRenderer *r = new VideoRenderer;
	r->init();
	renderer.push_back(r);
	texid.push_back(r->texId());
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
  LOGI("############### AugmentedTarget::Status status = frame.targets()[0].status()");
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

				int i = 0;
				LOGI("############### else, target=%s, namestr[i]=%s",
					 frame.targets()[0].target().name(), namestr[i].c_str());
				size_t size = namestr.size();
				for (i = 0; i < size; i++) {
					if (frame.targets()[0].target().name() == namestr.at(i) && texid[i]) {
						LOGI("###################### else, url=%s", Map[namestr[i]].c_str());
						video = new ARVideo;
						video->openStreamingVideo(Map[namestr[i]], texid[i]);
						video_renderer = renderer[i];
						break;
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

static EasyAR::samples::HelloARVideo ar;

JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(init(JNIEnv * , jobject)) {
	int i;
	bool status = ar.initCamera();
	std::vector<std::string>::iterator iter;
	for (iter = ar.namestr.begin(); iter != ar.namestr.end(); iter++) {
		LOGI("############# nativeInit, str=%s", (*iter).c_str());
		ar.loadFromImage(*iter + ".jpg");
	}
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
	LOGI("$$$$$$$$$$$$$$$$$ s1=%s, s2=%s", str1.c_str(), str2.c_str());
	ar.namestr.push_back(str1.replace(str1.find(".jpg"), 4, ""));
	ar.Map[str1] = str2;
}
