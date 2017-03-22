package com.yipai.printar.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yaochuan on 2017/3/22.
 */

public class VideoDataRealm extends RealmObject {
	@PrimaryKey
	private String mImagePath;
	private String mVideoPath;
	private long mStartTime;

	public VideoDataRealm() {}

	public VideoDataRealm(VideoData videoData) {
		mImagePath = videoData.getImagePath();
		mVideoPath = videoData.getVideoPath();
		mStartTime = videoData.getStartTime();
	}

	public String getImagePath() {
		return mImagePath;
	}

	public void setImagePath(String imagePath) {
		mImagePath = imagePath;
	}

	public String getVideoPath() {
		return mVideoPath;
	}

	public void setVideoPath(String videoPath) {
		mVideoPath = videoPath;
	}

	public long getStartTime() {
		return mStartTime;
	}

	public void setStartTime(long startTime) {
		mStartTime = startTime;
	}

	public VideoData get() {
		VideoData videoData = new VideoData();
		videoData.setImagePath(mImagePath);
		videoData.setVideoPath(mVideoPath);
		videoData.setStartTime(mStartTime);
		return videoData;
	}
}
