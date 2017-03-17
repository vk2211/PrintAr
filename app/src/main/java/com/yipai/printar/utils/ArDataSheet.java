/******************************************************************************
 * @project CvArDemo
 * @brief
 * @author yaochuan
 * @module com.yipai.ardemo.utils
 * @date 2017/3/8
 * @version 0.1
 * @history v0.1, 2017/3/8, by yaochuan
 * <p>
 * Copyright (C) 2017
 ******************************************************************************/
package com.yipai.printar.utils;

import android.content.Context;
import android.util.Log;

import com.yipai.printar.ui.realm.VideoData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by yaochuan on 2017/3/8.
 */
public class ArDataSheet extends BaseDataShare {
	private static final String TAG = ArDataSheet.class.getSimpleName();
	private static final String PREFIX_FILE = "name";
	private static final String PREFIX_VIDEO = "video";
	Map<String, String> result;

	private  Realm mRealm;

	public ArDataSheet(Context context) {
		super(context);
		RealmUtils realmUtils=RealmUtils.getInstance(context);
		mRealm=realmUtils.getRealm();
	}

	public void add(String path, String url) {
		for (int i = 1; i < 100; i++) {
			if (!contains(PREFIX_FILE + i)) {
				addData(PREFIX_FILE + i, path);
				addData(PREFIX_VIDEO + i, url);
				break;
			}
		}
	}



      //添加數據
	public void realmAdd(String path, String url){
		mRealm.beginTransaction();
		VideoData videoData=mRealm.createObject(VideoData.class);
		videoData.setImagePath(path);
		videoData.setVideoPath(url);
		mRealm.commitTransaction();

	}

	public Map<String, String>  realmRead(){

		List<VideoData> list=new ArrayList<VideoData>();
		list=mRealm.where(VideoData.class).findAll();
		Map<String, String> map=new HashMap<String, String>();
		for (int i=0;i<list.size();i++){
			map.put(list.get(i).getImagePath(),list.get(i).getVideoPath());
			Log.e("############", list.get(i).getImagePath());
			Log.e("############", list.get(i).getVideoPath());
		}
		return  map;
	}


	public Map<String, String> read() {
		if (result == null) {
			result = new HashMap<>();
			for (int i = 1; i < 100; i++) {
				if (contains(PREFIX_FILE + i)) {
					result.put(getData(PREFIX_FILE + i), getData(PREFIX_VIDEO + i));
				} else {
					break;
				}
			}
		}
		return result;
	}

	public String get(String key) {
		read();
		return result.get(key);
	}
}
