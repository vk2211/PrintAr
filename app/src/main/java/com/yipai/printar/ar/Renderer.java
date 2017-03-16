/**
 * Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
 * EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
 * and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
 */

package com.yipai.printar.ar;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Renderer implements GLSurfaceView.Renderer {

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		NativeAr.initGL();
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		NativeAr.resizeGL(w, h);
	}

	public void onDrawFrame(GL10 gl) {
		NativeAr.render();
	}

}
