package com.yipai.printar.ar;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Surface;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.yipai.printar.R;
import com.yipai.printar.ui.realm.VideoData;
import com.yipai.printar.utils.VideoDataSheet;

import java.util.List;

import cn.easyar.engine.EasyAR;

public class ArCameraActivity extends Activity {
	static String key = "ADhPHJpveTwDaLq3oUKxG2bkyhKl9OssyICQZUQuUPFNFBC2sspNjqGRLKgiYVhFqac6w4gZI3hTIOvcHejEJzlyvDAjwysnBWPV78834cf88b96469ee05beab0c554856cMccBa7vhF9i0nn3fcmuIxlBmUWZZmwJnaPm4OzbktkrUeIRKlM5x1AC7928lmoJACOZA";
	private VideoDataSheet mVideoDataSheet;
	private List<VideoData> mVideoDataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ar_camera);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mVideoDataSheet = VideoDataSheet.get();
		mVideoDataList = mVideoDataSheet.read();
		for (VideoData videoData : mVideoDataList) {
			String uri = videoData.getVideoPath();
			String p = videoData.getImagePath().replace("/storage/emulated/0", "/sdcard");
			NativeAr.add(p, uri);
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
