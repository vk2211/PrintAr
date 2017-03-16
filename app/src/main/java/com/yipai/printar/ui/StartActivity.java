package com.yipai.printar.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.yipai.printar.R;
import com.yipai.printar.adapter.CacheAdapter;
import com.yipai.printar.adapter.itemcachedata.ItemCacheData;
import com.yipai.printar.ar.ArCameraActivity;
import com.yipai.printar.constant.Path;
import com.yipai.printar.constant.RequestCode;
import com.yipai.printar.utils.ArDataSheet;
import com.yipai.printar.utils.BitmapUtil;
import com.yipai.printar.utils.dialog.DialogServer;
import com.yipai.printar.utils.file.FileUtil;
import com.yipai.printar.utils.log.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;


public class StartActivity extends AppCompatActivity implements View.OnClickListener {
	private static final String TAG = "StartActivity";
	public static final String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";
	private JCVideoPlayer.JCAutoFullscreenListener sensorEventListener;
	private SensorManager sensorManager;


	JCVideoPlayerStandard mVideo;
	View mOpenVideo;
	View mPrintFrame;
	View mScanPhoto;

	public static final String DIR = "Yipai/ArDemo";
	private Uri mUri;
	private ArDataSheet mArDataSheet;
	private DialogServer mDialogServer;
	private String VideoPath;

	private CacheAdapter mCacheAdapter;
	private EasyRecyclerView mShotImageRecycleView;
	private Bitmap mShotBitmap;
	private List<ItemCacheData> mShotImageList;
	private List<Bitmap> mBitmapList;
	private BitmapUtil mBitmapUtil;


	/**
	 * 选择对话框接口
	 */
	private DialogServer.MydialogInterface mydialogInterface = new DialogServer.MydialogInterface() {
		@Override
		public void OnDoalogDismiss(String result) {
			if (result.equals("本地视频")) {
				Intent it = new Intent(Intent.ACTION_GET_CONTENT);
				it.setType("video/*");
				startActivityForResult(it, RequestCode.RC_VIDEO);
			} else if (result.equals("网络视频")) {

				mUri = Uri.parse("http://test-epai.oss-cn-shenzhen.aliyuncs.com/video/VR/yangshanhu1002.mp4");

				mVideo.setUp(mUri.toString(), JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL);
				mVideo.backButton.setVisibility(View.INVISIBLE);
				mVideo.setVisibility(View.VISIBLE);
				mVideo.startButton.performClick();
				mUri = null;

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
						VideoPath = c.getString(columnIndex);
						c.close();
					} else {
						VideoPath = uri.toString();
					}

					mVideo.release();
					mVideo.setUp(VideoPath, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL);
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


	public void getBitmapsFromVideo(long timeUs) {
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		if (mUri == null && VideoPath == null) {

		} else {
			if (mUri == null) {
				retriever.setDataSource(VideoPath, new HashMap<String, String>());

			} else {
				retriever.setDataSource(mUri.toString(), new HashMap<String, String>());
			}
			//Bitmap bitmap = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
			Bitmap bitmap = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST);
			String path = FileUtil.sdcard.getFullPath(Path.CACHE_DIR, "" + TimeUtil.getCurrentTime() + ".jpg");

			Log.e("#######", path);
			mBitmapUtil.saveBitmap(bitmap, path);
			ItemCacheData itemCacheData = new ItemCacheData();
			itemCacheData.setImagePath(path);
			mCacheAdapter.add(itemCacheData);
			mCacheAdapter.notifyDataSetChanged();

//			mShot.setImageBitmap(bitmap);
//			String path = FileUtil.sdcard.getFullPath(DIR, "" + TimeUtil.getCurrentTime() + ".jpg");
//			saveBitmap(bitmap, path);
//			if(mUri==null){
//				mArDataSheet.add(path,VideoPath);
//			}
//			else {
//				mArDataSheet.add(path, mUri.toString());
//			}

		}

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
		mDialogServer = new DialogServer(StartActivity.this, mydialogInterface);

		mShotImageRecycleView = (EasyRecyclerView) this.findViewById(R.id.shotList);
		mBitmapUtil = new BitmapUtil(StartActivity.this);
		mCacheAdapter = new CacheAdapter(StartActivity.this);
		mShotImageList = new ArrayList<ItemCacheData>();
		mCacheAdapter.addAll(mShotImageList);
		LinearLayoutManager m = new LinearLayoutManager(this);
		m.setOrientation(LinearLayoutManager.VERTICAL);
		mShotImageRecycleView.setLayoutManager(m);
		mShotImageRecycleView.setAdapter(mCacheAdapter);

		mArDataSheet = new ArDataSheet(this);
		mOpenVideo.setOnClickListener(this);
		mPrintFrame.setOnClickListener(this);
		mScanPhoto.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.openVideo:
			mDialogServer.showSingleChoiceDialog();
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
