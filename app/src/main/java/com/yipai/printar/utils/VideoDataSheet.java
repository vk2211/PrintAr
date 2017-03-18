package com.yipai.printar.utils;

import com.yipai.printar.App;
import com.yipai.printar.ui.realm.VideoData;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by liuchuanliang on 2017/3/17.
 */

public class VideoDataSheet {

	private static final String TAG = ArDataSheet.class.getSimpleName();
	private static final String RealName = "VideoMap";
	private Realm mRealm;
	private static VideoDataSheet sVideoDataSheet;

	public static VideoDataSheet get() {
		if (sVideoDataSheet == null) {
			sVideoDataSheet = new VideoDataSheet();
		}
		return sVideoDataSheet;
	}

	private VideoDataSheet() {
		mRealm = Realm.getInstance(new RealmConfiguration.Builder(App.getContext()).name(RealName).build());
	}

	//添加數據
	public void add(String imagePath, String videoUri,int startTime) {
		mRealm.beginTransaction();
		VideoData videoData = mRealm.createObject(VideoData.class);
		videoData.setImagePath(imagePath);
		videoData.setVideoPath(videoUri);
		videoData.setStartTime(startTime);
		mRealm.commitTransaction();
	}

	public void add(VideoData videoData) {
		mRealm.beginTransaction();
		mRealm.copyToRealm(videoData);
		mRealm.commitTransaction();
	}

	public List<VideoData> read() {
		List<VideoData> list = mRealm.where(VideoData.class).findAll();
		return list;
	}

}
