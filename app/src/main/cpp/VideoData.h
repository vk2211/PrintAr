
#ifndef __EASYAR_SAMPLE_VIDEO_DATA_H__
#define __EASYAR_SAMPLE_VIDEO_DATA_H__

class VideoData {
public:
	std::string getImagePath() const {
		return mImagePath;
	}

	void setImagePath(std::string imagePath) {
		mImagePath = imagePath;
	}

	std::string getVideoPath() const {
		return mVideoPath;
	}

	void setVideoPath(std::string videoPath) {
		mVideoPath = videoPath;
	}

	long long getStartTime() const {
		return mStartTime;
	}

	void setStartTime(long long startTime) {
		mStartTime = startTime;
	}

	void log() {
		LOGI("$$$$$$$$$$$$$$$$$ imagePath:%s", mImagePath.c_str());
		LOGI("$$$$$$$$$$$$$$$$$ videoPath:%s", mVideoPath.c_str());
		LOGI("$$$$$$$$$$$$$$$$$ time:%lld", mStartTime);
	}

private:
	std::string mImagePath;
	std::string mVideoPath;
	long long mStartTime;
};

int getVideoDataFromJavaObject(JNIEnv *jenv, const jobject &jVideoData, VideoData &videoData);

jobject convertVideoDataToJavaObject(JNIEnv *jenv, const VideoData &videoData);

#endif
