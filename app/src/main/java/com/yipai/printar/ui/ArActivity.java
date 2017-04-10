package com.yipai.printar.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yipai.printar.R;
import com.yipai.printar.ar.ArCameraActivity;
import com.yipai.printar.ar.NativeAr;
import com.yipai.printar.bean.VideoData;

import butterknife.BindView;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class ArActivity extends ArCameraActivity {
	private static final long WAIT_TIME = 2000L;
	private long TOUCH_TIME = 0;
	@BindView(R.id.idTakeOff)
	TextView idTakeOff;
	@BindView(R.id.idTakeOffJCVideo)
	JCVideoPlayerStandard idTakeOffJCVideo;
	boolean mIsPlaying = false;
//	private JCVideoPlayer.JCAutoFullscreenListener sensorEventListener;
//	private SensorManager sensorManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_ar_camera);
		ButterKnife.bind(this);
//		idTakeOff.bringToFront();
//		idTakeOffJCVideo.bringToFront();
		JCVideoPlayer.NORMAL_ORIENTATION= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		idTakeOff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mIsPlaying = !mIsPlaying;
				VideoData videoData = NativeAr.get();
				String videoPath = videoData.getVideoPath();
				if (!TextUtils.isEmpty(videoPath)) {
					if (mIsPlaying) {
						idTakeOffJCVideo.setVisibility(View.VISIBLE);
						playVideo(videoPath);
					} else {
						idTakeOffJCVideo.setVisibility(View.GONE);
						idTakeOffJCVideo.startButton.performClick();
					}
				}
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		JCVideoPlayer.releaseAllVideos();
	}

	private void playVideo(String vp) {
		idTakeOffJCVideo.release();
		idTakeOffJCVideo.setUp(vp, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL);
		idTakeOffJCVideo.backButton.setVisibility(View.INVISIBLE);
		idTakeOffJCVideo.setVisibility(View.VISIBLE);
		idTakeOffJCVideo.startButton.performClick();
		idTakeOffJCVideo.fullscreenButton.setVisibility(View.GONE);
		idTakeOffJCVideo.tinyBackImageView.setVisibility(View.GONE);
	}

	public void onBackPressed() {
		if (JCVideoPlayer.backPress()) {
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void finish() {
		if (mIsPlaying) {
			idTakeOffJCVideo.startButton.performClick();
			idTakeOffJCVideo.setVisibility(View.GONE);
			mIsPlaying = false;
			return;
		}
		if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
			super.finish();
		} else {
			TOUCH_TIME = System.currentTimeMillis();
			Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
		}
	}
}
