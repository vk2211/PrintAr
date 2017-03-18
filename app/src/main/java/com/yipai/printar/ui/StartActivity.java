package com.yipai.printar.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.yipai.printar.R;
import com.yipai.printar.ar.ArCameraActivity;
import com.yipai.printar.constant.Path;
import com.yipai.printar.constant.RequestCode;
import com.yipai.printar.ui.dialog.SingleChoiceDialog;
import com.yipai.printar.ui.realm.VideoData;
import com.yipai.printar.ui.view.ShotImageRecyclerView;
import com.yipai.printar.utils.BitmapUtil;
import com.yipai.printar.utils.file.FileUtil;
import com.yipai.printar.utils.log.TimeUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
	private JCVideoPlayer.JCAutoFullscreenListener sensorEventListener;
	private SensorManager sensorManager;
	JCVideoPlayerStandard mVideo;
	View mOpenVideo;
	View mPrintFrame;
	View mScanPhoto;
	private Uri mUri;
	private SingleChoiceDialog mSingleChoiceDialog;
	private String mVideoPath;
	private ShotImageRecyclerView mShotImageRecycleView;
	private List<VideoData> mShotImageList;
	private BitmapUtil mBitmapUtil;
	private String mDialogTitle = "选择视频";
	private String[] mDialogItems = {"本地视频", "网络视频"};
	private Handler mHandler = new Handler();

	/**
	 * 选择对话框接口
	 */
	private SingleChoiceDialog.SingleSelectInterface mSingleSelectInterface = new SingleChoiceDialog.SingleSelectInterface() {
		@Override
		public void OnSingleSelect(int result) {
			if (result == 0) {//本地视频
				Intent it = new Intent(Intent.ACTION_GET_CONTENT);
				it.setType("video/*");
				startActivityForResult(it, RequestCode.RC_VIDEO);
			} else if (result == 1) {//网络视频
				mUri = Uri.parse("http://test-epai.oss-cn-shenzhen.aliyuncs.com/video/VR/yangshanhu1002.mp4");
				mVideoPath = null;
				mVideo.setUp(mUri.toString(), JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL);
				mVideo.backButton.setVisibility(View.INVISIBLE);
				mVideo.setVisibility(View.VISIBLE);
				mVideo.startButton.performClick();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		InitStartActivity();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (data != null) {
				if (requestCode == RequestCode.RC_VIDEO) {
					Uri uri = data.getData();
					String[] filePathColumns = {MediaStore.Images.Media.DATA};
					Cursor c = getContentResolver().query(uri, null, null, null, null);
					if (c != null) {
						c.moveToFirst();
						int columnIndex = c.getColumnIndex(filePathColumns[0]);
						if (columnIndex >= 0) {
							mVideoPath = c.getString(columnIndex);
						}
						c.close();
					} else {
						mVideoPath = uri.toString();
					}
					mUri = null;
					mVideo.release();
					mVideo.setUp(mVideoPath, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL);
					mVideo.backButton.setVisibility(View.INVISIBLE);
					mVideo.setVisibility(View.VISIBLE);
					mVideo.startButton.performClick();
				}
			}
		}
	}

	public void onBackPressed() {
		if (JCVideoPlayer.backPress()) {
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(sensorEventListener);
		JCVideoPlayer.releaseAllVideos();
	}

	/**
	 * 退出程序时，删掉缓存图片
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		FileUtil.deleteAllFiles(new File(Environment.getExternalStorageDirectory().toString() + "/" + Path.CACHE_DIR));
	}

	public void getBitmapsFromVideo(final long timeUs) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				MediaMetadataRetriever retriever = new MediaMetadataRetriever();
				if (mVideoPath != null) {
					retriever.setDataSource(mVideoPath, new HashMap<String, String>());
				} else if (mUri != null) {
					retriever.setDataSource(mUri.toString(), new HashMap<String, String>());
				}
				Bitmap bitmap = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST);
				if (bitmap != null) {
					String fileName = "" + TimeUtil.getCurrentTime() + ".jpg";
					final String path = FileUtil.sdcard.getFullPath(Path.CACHE_DIR, fileName);
					mBitmapUtil.saveBitmap(bitmap, path);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							VideoData itemCacheData = new VideoData();
							itemCacheData.setImagePath(path);
							itemCacheData.setVideoPath(mVideoPath);
							itemCacheData.setStartTime(timeUs);
							mShotImageRecycleView.add(itemCacheData);
							mShotImageRecycleView.notifyDataSetChanged();
							int count = mShotImageRecycleView.getItemCount();
							LinearLayoutManager m = (LinearLayoutManager) mShotImageRecycleView.getLayoutManager();
							m.scrollToPositionWithOffset(count, 0);
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 初始化
	 */
	private void InitStartActivity() {
		mVideo = (JCVideoPlayerStandard) findViewById(R.id.video);
		mOpenVideo = findViewById(R.id.openVideo);
		mPrintFrame = findViewById(R.id.printFrame);
		mScanPhoto = findViewById(R.id.scanPhoto);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorEventListener = new JCVideoPlayer.JCAutoFullscreenListener();
		FileUtil.sdcard.createDir(Path.DIR);
		FileUtil.sdcard.createDir(Path.CACHE_DIR);
		mSingleChoiceDialog = new SingleChoiceDialog(this, mSingleSelectInterface, mDialogTitle, mDialogItems);
		mShotImageRecycleView = (ShotImageRecyclerView) this.findViewById(R.id.shotList);
		mBitmapUtil = new BitmapUtil(StartActivity.this);
//		mCacheAdapter = new CacheAdapter(StartActivity.this);
//		mShotImageList = new ArrayList<>();
//		mCacheAdapter.addAll(mShotImageList);
		LinearLayoutManager m = new LinearLayoutManager(this);
		m.setOrientation(LinearLayoutManager.VERTICAL);
		mShotImageRecycleView.setLayoutManager(m);
//		mShotImageRecycleView.setAdapter(mCacheAdapter);
		mShotImageRecycleView.enableRefresh(false);
		mOpenVideo.setOnClickListener(this);
		mPrintFrame.setOnClickListener(this);
		mScanPhoto.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.openVideo:
			mSingleChoiceDialog.showSingleChoiceDialog();
			break;
		case R.id.printFrame:
			int time = mVideo.getCurrentPositionWhenPlaying();
			getBitmapsFromVideo(time * 1000);
			break;
		case R.id.scanPhoto:
			Intent it = new Intent(StartActivity.this, ArCameraActivity.class);
			startActivity(it);
			break;
		}
	}

}
