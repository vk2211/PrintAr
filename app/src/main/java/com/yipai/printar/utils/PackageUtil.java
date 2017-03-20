/******************************************************************************
 * @file PackageUtil.java
 * @brief
 * @author YaoChuan (vk2211@gmail.com)
 * @module com.trustworthy.wineplatformapp.util
 * @date 2014.11月30日
 * @version 0.1
 * @history v0.1, 2014.11月30日, by YaoChuan (vk2211@gmail.com)
 * <p>
 * <p>
 * Copyright (C) 2014 YaoChuan.
 ******************************************************************************/

package com.yipai.printar.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.yipai.printar.utils.log.CLog;
import com.yipai.printar.utils.log.TimeUtil;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class PackageUtil {
	public static boolean install(Context context, File apk) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		// 安装完成时会结束当前应用，导致安装界面退出，所以需要启动一个NewTask来升级应用
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
		context.startActivity(intent);
		return true;
	}

	public static boolean install(Context context, String apkpath) {
		return install(context, new File(apkpath));
	}

	public static boolean isNewerUpdateVersion(Context context, String apk) {
		String this_version = getVersion(context);
		String apk_version = getVersion(context, apk);
		long this_release_date = getApkReleaseDate(this_version);
		long apk_release_date = getApkReleaseDate(apk_version);
		return this_release_date < apk_release_date;
	}

	public static String getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "0.9.20141128.1";
		}
	}

	public static int getVersionCode(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			int version = info.versionCode;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static String getVersion(Context context, String apk) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apk, PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			String appName = pm.getApplicationLabel(appInfo).toString();
			String packageName = appInfo.packageName;
			String version = info.versionName;
			CLog.v(TAG, "appName:" + appName + "packageName:" + packageName + ",version:" + version);
			return version;
		}
		return null;
	}

	public static long getApkReleaseDate(String version) {
		String[] split_version = version.split("\\.");
		long release_time = TimeUtil.parseTimeStr(split_version[2], "yyyyMMdd");
		return release_time;
	}

	private static final String TAG = "PackageUtil";

	public static boolean isAppExist(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> installedList = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		Iterator<PackageInfo> iterator = installedList.iterator();
		PackageInfo info;
		String name;
		while (iterator.hasNext()) {
			info = iterator.next();
			name = info.packageName;
			if (name.equals(packageName)) {
				return true;
			}
		}
		return false;
	}
}
