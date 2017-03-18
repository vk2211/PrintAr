/******************************************************************************
 * @project PrintAr
 * @brief
 * @author yaochuan
 * @module com.yipai.printar.ui.view
 * @date 2017/3/18
 * @version 0.1
 * @history v0.1, 2017/3/18, by yaochuan
 * <p>
 * Copyright (C) 2017
 ******************************************************************************/
package com.yipai.printar.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.yipai.printar.adapter.CacheAdapter;
import com.yipai.printar.ui.realm.VideoData;

/**
 * Created by yaochuan on 2017/3/18.
 *
 */
public class ShotImageRecyclerView extends EasyLoadPageRecyclerView<VideoData> {
	private static final String TAG = ShotImageRecyclerView.class.getSimpleName();
	private CacheAdapter mCacheAdapter;

	public ShotImageRecyclerView(Context context) {
		super(context);
	}

	public ShotImageRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void requestPageData(int page) {

	}

	@Override
	protected RecyclerArrayAdapter<VideoData> getAdapter(Context context) {
		if (mCacheAdapter == null) {
			mCacheAdapter = new CacheAdapter(context);
		}
		return mCacheAdapter;
	}
}
