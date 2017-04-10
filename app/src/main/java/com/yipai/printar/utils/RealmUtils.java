package com.yipai.printar.utils;

import com.yipai.printar.App;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by liuchuanliang on 2017/3/17.
 */

public class RealmUtils {
	private static RealmUtils mInstance;
	private String realName = "myRealm.realm";

	public RealmUtils() {
	}

	/**
	 * 单例方式
	 */
	public static RealmUtils get() {
		if (mInstance == null) {
			synchronized (RealmUtils.class) {
				if (mInstance == null) {
					mInstance = new RealmUtils();
				}
			}
		}
		return mInstance;
	}

	/***获得realm对象*/
	public Realm getRealm() {
		return Realm.getInstance(new RealmConfiguration.Builder(App.getContext()).name(realName).build());
	}
}
