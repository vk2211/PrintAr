package com.yipai.printar.ar;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.yipai.printar.R;
import com.yipai.printar.bean.VideoData;
import com.yipai.printar.bean.VideoDataRealm;
import com.yipai.printar.utils.VideoDataSheet;

import java.util.List;

import cn.easyar.engine.EasyAR;

public class ArCameraActivity extends AppCompatActivity {
	static String key = "ADhPHJpveTwDaLq3oUKxG2bkyhKl9OssyICQZUQuUPFNFBC2sspNjqGRLKgiYVhFqac6w4gZI3h" +
		"TIOvcHejEJzlyvDAjwysnBWPV78834cf88b96469ee05beab0c554856cMccBa7vhF9i0nn3fcmuIxlBmUWZZmwJnaP" +
		"m4OzbktkrUeIRKlM5x1AC7928lmoJACOZA";
	private VideoDataSheet mVideoDataSheet;
	private List<VideoDataRealm> mVideoDataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ar_camera);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mVideoDataSheet = VideoDataSheet.get();
		mVideoDataList = mVideoDataSheet.read();
		for (VideoDataRealm videoDataRealm : mVideoDataList) {
			VideoData videoData = videoDataRealm.get();
			String p = videoData.getImagePath().replace("/storage/emulated/0", "/sdcard");
			String uri = videoData.getVideoPath();
			videoData.setImagePath(p.replace(".jpg", ""));
			videoData.setStartTime(videoData.getStartTime() / 1000);
			NativeAr.add(videoData);
		}
		EasyAR.initialize(this, key);
		NativeAr.init();
		GLView glView = new GLView(this);
		glView.setRenderer(new Renderer());
		glView.setZOrderMediaOverlay(true);
		ViewGroup vg = ((ViewGroup) findViewById(R.id.preview));
		vg.addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		NativeAr.rotationChange(getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0);
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		NativeAr.rotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		NativeAr.destroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EasyAR.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		EasyAR.onPause();
	}
}
