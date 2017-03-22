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

#define JNIFUNCTION_NATIVE(sig) Java_com_yipai_printar_ar_NativeAr_##sig

extern "C" {
    JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(init(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(destroy(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(initGL(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(resizeGL(JNIEnv* env, jobject object, jint w, jint h));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(render(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(rotationChange(JNIEnv* env, jobject object, jboolean portrait));
	JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(add(JNIEnv *env, jobject object, jobject videoData));
};


namespace EasyAR {
	namespace samples {


class HelloARVideo : public AR {
public:
	HelloARVideo();

	~HelloARVideo();

	void push(VideoData v);

	void init();

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
	std::vector<VideoData> m_vVideoData;
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

void HelloARVideo::push(VideoData v) {
	m_vVideoData.push_back(v);
}

void HelloARVideo::init() {
	std::vector<VideoData>::iterator iter;
	for (iter = m_vVideoData.begin(); iter != m_vVideoData.end(); iter++) {
		LOGI("############# nativeInit, str=%s", (*iter).getImagePath().c_str());
		loadFromImage((*iter).getImagePath()+".jpg");
	}
}

void HelloARVideo::initGL() {
	augmenter_ = Augmenter();
	augmenter_.attachCamera(camera_);
	std::vector<VideoData>::iterator iter;
	for (iter = m_vVideoData.begin(); iter != m_vVideoData.end(); iter++) {
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

	AugmentedTargetList targets = frame.targets();
	AugmentedTarget::Status status = targets[0].status();
	LOGI("############### AugmentedTarget::Status status = frame.targets()[0].status()=%d", status);
	if (status == AugmentedTarget::kTargetStatusTracked) {
		int id = targets[0].target().id();
		if (active_target && active_target != id) {
			video->onLost();
			delete video;
			video = NULL;
			tracked_target = 0;
			active_target = 0;
		}
		if (!tracked_target) {
			if (video == NULL) {
				LOGI("############### target=%s", targets[0].target().name());
				int i = 0;
				size_t size = m_vVideoData.size();
				for (i = 0; i < size; i++) {
					if (targets[0].target().name() == m_vVideoData[i].getImagePath() && texid[i]) {
						LOGI("###################### else, url=%s", m_vVideoData[i].getVideoPath().c_str());
						video = new ARVideo;
						video->openStreamingVideo(m_vVideoData[i].getVideoPath().c_str(), texid[i]);
						video_renderer = renderer[i];
						video->seek((int) m_vVideoData[i].getStartTime());
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
		Matrix44F cameraview = getPoseGL(targets[0].pose());
		ImageTarget target = targets[0].target().cast_dynamic<ImageTarget>();
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
	getVideoDataFromObject(env, videoData, v);
	ar.push(v);
}
