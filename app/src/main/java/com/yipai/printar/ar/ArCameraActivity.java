package com.yipai.printar.ar;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.yipai.printar.R;
import com.yipai.printar.bean.VideoData;
import com.yipai.printar.bean.VideoDataRealm;
import com.yipai.printar.constant.RequestCode;
import com.yipai.printar.utils.VideoDataSheet;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.easyar.engine.EasyAR;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class ArCameraActivity extends AppCompatActivity{
	static String key = "ADhPHJpveTwDaLq3oUKxG2bkyhKl9OssyICQZUQuUPFNFBC2sspNjqGRLKgiYVhFqac6w4gZI3hTIOvcHejEJzlyvDAjwysnBWPV78834cf88b96469ee05beab0c554856cMccBa7vhF9i0nn3fcmuIxlBmUWZZmwJnaPm4OzbktkrUeIRKlM5x1AC7928lmoJACOZA";
	@BindView(R.id.idTakeOff)
	TextView idTakeOff;
	@BindView(R.id.idTakeOffJCVideo)
	JCVideoPlayerStandard idTakeOffJCVideo;
	private VideoDataSheet mVideoDataSheet;
	private List<VideoDataRealm> mVideoDataList;
	boolean mIsPlaying = false;
//	private JCVideoPlayer.JCAutoFullscreenListener sensorEventListener;
//	private SensorManager sensorManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ar_camera);
		ButterKnife.bind(this);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
			WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
		vg.addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT));
		idTakeOff.bringToFront();
		idTakeOffJCVideo.bringToFront();
		idTakeOff.setOnClickListener(takeOffListener);
		RequestCode.BACKNUMBER = 0;
		NativeAr.rotationChange(getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0);
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		NativeAr.rotationChange(getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0);
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
//		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//		sensorEventListener = new JCVideoPlayer.JCAutoFullscreenListener();
//		Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EasyAR.onPause();
		JCVideoPlayer.releaseAllVideos();
	}

	private View.OnClickListener takeOffListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mIsPlaying=!mIsPlaying;
			if(mIsPlaying){
				idTakeOffJCVideo.setVisibility(View.VISIBLE);
				VideoData videoData = NativeAr.get();
				String videoPath = videoData.getVideoPath();
				playVideo(videoPath);
			}
			else {
				idTakeOffJCVideo.setVisibility(View.GONE);
				idTakeOffJCVideo.release();
			}
		}
	};

	private void playVideo(String vp) {
		idTakeOffJCVideo.release();
		idTakeOffJCVideo.setUp(vp, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL);
		idTakeOffJCVideo.backButton.setVisibility(View.INVISIBLE);
		idTakeOffJCVideo.setVisibility(View.VISIBLE);
		idTakeOffJCVideo.startButton.performClick();
	}

	public void onBackPressed() {
		if (JCVideoPlayer.backPress()) {
			return;
		}
		RequestCode.BACKNUMBER++;
		if (RequestCode.BACKNUMBER == 1) {
			Toast.makeText(this, "再按一次退出程序！", Toast.LENGTH_SHORT).show();
		} else if (RequestCode.BACKNUMBER >= 2) {
			super.onBackPressed();
		}
	}
}
