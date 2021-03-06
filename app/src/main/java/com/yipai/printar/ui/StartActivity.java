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
import android.widget.TextView;
import android.widget.Toast;

import com.yipai.printar.R;
import com.yipai.printar.bean.VideoData;
import com.yipai.printar.constant.Path;
import com.yipai.printar.constant.RequestCode;
import com.yipai.printar.ui.dialog.SingleChoiceDialog;
import com.yipai.printar.ui.view.ShotImageRecyclerView;
import com.yipai.printar.utils.BitmapUtil;
import com.yipai.printar.utils.file.FileUtil;
import com.yipai.printar.utils.log.TimeUtil;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
	@BindView(R.id.video)
	JCVideoPlayerStandard mVideo;
	@BindView(R.id.openVideo)
	TextView mOpenVideo;
	@BindView(R.id.printFrame)
	TextView mPrintFrame;
	@BindView(R.id.scanPhoto)
	TextView mScanPhoto;
	@BindView(R.id.shotList)
	ShotImageRecyclerView mShotList;
	private JCVideoPlayer.JCAutoFullscreenListener sensorEventListener;
	private static final long WAIT_TIME = 2000L;
	private long TOUCH_TIME = 0;
	private SensorManager sensorManager;
	private Uri mUri;
	private SingleChoiceDialog mSingleChoiceDialog;
	private String mVideoPath;
	private ShotImageRecyclerView mShotImageRecycleView;
	private BitmapUtil mBitmapUtil;
	private String mDialogTitle = "选择视频";
	private String[] mDialogItems = {"本地视频", "网络视频1", "网络视频2", "网络视频3"};
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
			} else {
				String url = null;
				if (result == 1) {//网络视频
					url = "http://test-epai.oss-cn-shenzhen.aliyuncs.com/video/VR/yangshanhu1002.mp4";
				} else if (result == 2) {
					url = "http://lb.aishang.ctlcdn.com/00000110240002_1/playlist.m3u8?" +
						"CONTENTID=000001110762000_1&start=20170322031801&end=20170322040301&A" +
						"UTHINFO=hEcQRKsVzykWOGchNbZ%2BQiWsN8NZ2AtsPVUsXGQV7tzr9QQhp78%2FgaA9N" +
						"HjiqfbOadtsaf6e7aUU2%2BqckOj9Tw%3D%3D&USERTOKEN=Peu3HyhzwKdNKG%2B5Arg" +
						"z2G07WdB07EMl";
				} else if (result == 3) {
					url = "http://vod.aishang.ctlcdn.com/ceph_201703/static/video/00001" +
						"000110152894000000000000000/playlist.m3u8?CONTENTID=00001000110152894" +
						"000000000000000&AUTHINFO=adl%2F%2Bwn3I%2BwsbQQJ9AeXwM9DbxK1kv1nXMrp%2" +
						"FSQ2ykd2Z5XCc3ZXz1YAJr%2FiDiiOo44EfBEtn33mB4qG8j1WZw%3D%3D&USERTOKEN=" +
						"revqRrdN8LkDfyAIwO6ajG07WdB07EMl";
				}
				mUri = Uri.parse(url);
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
		ButterKnife.bind(this);
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
	public void finish() {
		if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
			super.finish();
		} else {
			TOUCH_TIME = System.currentTimeMillis();
			Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
		}
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
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorEventListener = new JCVideoPlayer.JCAutoFullscreenListener();
		FileUtil.sdcard.createDir(Path.DIR);
		FileUtil.sdcard.createDir(Path.CACHE_DIR);
		mSingleChoiceDialog = new SingleChoiceDialog(this, mSingleSelectInterface, mDialogTitle, mDialogItems);
		mShotImageRecycleView = (ShotImageRecyclerView) this.findViewById(R.id.shotList);
		mBitmapUtil = new BitmapUtil(StartActivity.this);
		LinearLayoutManager m = new LinearLayoutManager(this);
		m.setOrientation(LinearLayoutManager.VERTICAL);
		mShotImageRecycleView.setLayoutManager(m);
		mShotImageRecycleView.enableRefresh(false);
	}

	@OnClick({R.id.openVideo, R.id.printFrame, R.id.scanPhoto})
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.openVideo:
			mSingleChoiceDialog.showSingleChoiceDialog();
			break;
		case R.id.printFrame:
			int time = mVideo.getCurrentPositionWhenPlaying();
			getBitmapsFromVideo(time * 1000);
			break;
		case R.id.scanPhoto:
			Intent it = new Intent(StartActivity.this, ArActivity.class);
			startActivity(it);
			break;
		}
	}
}
