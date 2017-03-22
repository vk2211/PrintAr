/******************************************************************************
 * @project PrintAr
 * @brief
 * @author yaochuan
 * @module com.yipai.printar.ar
 * @date 2017/3/15
 * @version 0.1
 * @history v0.1, 2017/3/15, by yaochuan
 * <p>
 * Copyright (C) 2017
 ******************************************************************************/
package com.yipai.printar.ar;

import com.yipai.printar.bean.VideoData;

/**
 * Created by yaochuan on 2017/3/15.
 */
public class NativeAr {
	static {
		System.loadLibrary("ar");
	}

	private static final String TAG = NativeAr.class.getSimpleName();

	public static native void initGL();

	public static native void resizeGL(int w, int h);

	public static native void render();

	public static native boolean init();

	public static native void destroy();

	public static native void rotationChange(boolean portrait);

	public static native void add(VideoData videoData);
}
