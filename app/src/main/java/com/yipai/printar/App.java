package com.yipai.printar;

import android.app.Application;
import android.content.Context;
import android.util.Log;


/**
 * Created by yaochuan on 2016/9/15.
 */
public class App extends Application {
	private static final String TAG = App.class.getSimpleName();
	private static App sInstance;// = new App();

	protected static App getInstance() {
		if (sInstance == null) {
			Log.e(TAG, "sInstance == null will not enter!!!");


		}
		return sInstance;
	}

	public static Context getContext() {
		return sInstance.getApplicationContext();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
	}
}
