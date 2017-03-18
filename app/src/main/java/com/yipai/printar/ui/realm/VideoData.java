package com.yipai.printar.ui.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by liuchuanliang on 2017/3/17.
 */

public class VideoData extends RealmObject {
	@PrimaryKey
	private String mImagePath;
	private String mVideoPath;
	private long mStartTime;

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
}
