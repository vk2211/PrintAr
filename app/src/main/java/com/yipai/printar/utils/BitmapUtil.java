package com.yipai.printar.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.yipai.printar.utils.log.CLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by liuchuanliang on 2017/3/16.
 */
public class BitmapUtil {
	private static final String TAG = "BitmapUtil";
	private Context mContext;

	public BitmapUtil(Context context) {

		mContext = context;
	}

	public void saveBitmap(Bitmap bitmap, String path) {
		File file = null;
		try {
			file = new File(path);
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//			bitmap.recycle();
			fos.close();
		} catch (FileNotFoundException e) {
			CLog.e(TAG, "File not found: " + e.getMessage());
			return;
		} catch (IOException e) {
			CLog.e(TAG, "Error accessing file: " + e.getMessage());
			return;
		}
//		String dir = FileUtil.getDirectoryPath(DIR, false);
		mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
	}
}
