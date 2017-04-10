/******************************************************************************
 * @project YipaiArlib
 * @brief
 * @author yaochuan
 * @module com.yipai.yipaiarlib.utils.file
 * @date 2016/9/29
 * @version 0.1
 * @history v0.1, 2016/9/29, by yaochuan
 * <p/>
 * Copyright (C) 2016
 ******************************************************************************/
package com.yipai.printar.utils.file;

import android.content.Context;


import com.yipai.printar.utils.log.CLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yaochuan on 2016/9/29.
 */
public class AssetsUtil {
	private static final String TAG = AssetsUtil.class.getSimpleName();

	public static String copyAsset(Context context, String strAssetFileName, String strTargetPath) {
		byte[] buffer = new byte[1024];
		InputStream assetStream;
		OutputStream fileStream = null;
		String targetFilePath = strTargetPath + File.separator + strAssetFileName;
		boolean ret = false;
		try {
			assetStream = context.getAssets().open(strAssetFileName);
			fileStream = new FileOutputStream(targetFilePath);
			int length = assetStream.read(buffer);
			while (length > 0) {
				fileStream.write(buffer, 0, length);
				length = assetStream.read(buffer);
			}

			fileStream.flush();
			assetStream.close();
			fileStream.close();
			return targetFilePath;
		} catch (FileNotFoundException e) {
			CLog.e(TAG, e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			CLog.e(TAG, e.toString());
			e.printStackTrace();
		}
		return null;
	}
}
