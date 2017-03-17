package com.yipai.printar.utils;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by liuchuanliang on 2017/3/17.
 */

public class RealmUtils {

            private Context context;

            private static RealmUtils mInstance;

            private String realName = "myRealm.realm";

            public RealmUtils(Context context) {

                        this.context = context;

            }
            /**单例方式*/

            public static RealmUtils getInstance(Context context) {

                        if (mInstance == null) {

                                    synchronized (RealmUtils.class) {

                                                if (mInstance == null) {

                                                            mInstance = new RealmUtils(context);
                                                }
                                    }

                        }

                        return mInstance;

            }
            /***获得realm对象*/

            public Realm getRealm() {

                        return Realm.getInstance(new RealmConfiguration.Builder(context).name(realName).build());

            }
}
