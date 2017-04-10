package com.yipai.printar.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yipai.printar.R;
import com.yipai.printar.bean.VideoData;
import com.yipai.printar.constant.Path;
import com.yipai.printar.utils.VideoDataSheet;
import com.yipai.printar.utils.file.AssetsUtil;
import com.yipai.printar.utils.file.FileUtil;

public class YipaiDemoActivity extends AppCompatActivity {
	private static final String URL = "http://125.76.235.72:8080/tongzhouli.mp4";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yipai_demo);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		VideoDataSheet.get().clear();

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				VideoDataSheet mVideoDataSheet = VideoDataSheet.get();
				String fileName = "tongzhouli.jpg";
				String path = FileUtil.sdcard.getFullPath(Path.DIR, fileName);
				if (!mVideoDataSheet.contains(path)) {
					AssetsUtil.copyAsset(YipaiDemoActivity.this, fileName, FileUtil.sdcard.createDir(Path.DIR));
					VideoData itemCacheData = new VideoData();
					itemCacheData.setImagePath(path);
					itemCacheData.setVideoPath(Uri.parse(URL).toString());
					itemCacheData.setStartTime(0);
					mVideoDataSheet.add(itemCacheData);
				}
				Intent it = new Intent(YipaiDemoActivity.this, ArActivity.class);
				startActivity(it);
			}
		});
	}
}
