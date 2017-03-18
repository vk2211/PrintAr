/******************************************************************************
 * @project NjSeed
 * @brief
 * @author yaochuan
 * @module com.ndfw.njseed.ui.view
 * @date 2017/2/17
 * @version 0.1
 * @history v0.1, 2017/2/17, by yaochuan
 * <p>
 * Copyright (C) 2017
 ******************************************************************************/
package com.yipai.printar.ui.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.yipai.printar.R;

import java.util.List;

/**
 * Created by yaochuan on 2017/2/17.
 */
public abstract class EasyLoadPageRecyclerView<T> extends EasyRecyclerView {
	private static final String TAG = EasyLoadPageRecyclerView.class.getSimpleName();
	private RecyclerArrayAdapter<T> mAdapter;
	private int mLoadedPage = 1;
	private int mTotalPage = 1;
	private Handler mHandler = new Handler();

	public EasyLoadPageRecyclerView(Context context) {
		super(context);
		init(context);
	}

	public EasyLoadPageRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	protected void init(Context context) {
		mAdapter = getAdapter(context);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
		this.setLayoutManager(linearLayoutManager);
		this.setAdapter(mAdapter);
		initRefresh();
		loadDataList();
	}

	private void initRefresh() {
		mAdapter.setMore(R.layout.layout_erv_more, new RecyclerArrayAdapter.OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
			}
		});
		mAdapter.setMore(R.layout.layout_erv_more, new RecyclerArrayAdapter.OnMoreListener() {
			@Override
			public void onMoreShow() {
				loadDataList();
			}

			@Override
			public void onMoreClick() {

			}
		});
		mAdapter.setNoMore(R.layout.layout_erv_nomore, new RecyclerArrayAdapter.OnNoMoreListener() {
			@Override
			public void onNoMoreShow() {
				mAdapter.resumeMore();
			}

			@Override
			public void onNoMoreClick() {
				mAdapter.resumeMore();
			}
		});
		mAdapter.setError(R.layout.layout_erv_error, new RecyclerArrayAdapter.OnErrorListener() {
			@Override
			public void onErrorShow() {
				mAdapter.resumeMore();
			}

			@Override
			public void onErrorClick() {
				mAdapter.resumeMore();
			}
		});
		this.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						refresh();
					}
				}, 500);
			}
		});
	}

	public RecyclerView.LayoutManager getLayoutManager() {
		return mRecycler.getLayoutManager();
	}

	public void refresh() {
		mLoadedPage = 1;
		mAdapter.clear();
		loadDataList();
	}

	public void enableRefresh(boolean isEnabled) {
		mPtrLayout.setEnabled(isEnabled);
	}

	private void loadDataList() {
		if (mLoadedPage > mTotalPage) {
			mAdapter.stopMore();
			return;
		}
		requestPageData(mLoadedPage++);
	}

	public void notifyDataSetChanged() {
		mAdapter.notifyDataSetChanged();
	}

	protected void notifyPageDataChanged(int totalPage, List<T> dataList) {
		if (totalPage > 0) {
			mTotalPage = totalPage;
		}
		if (dataList != null && dataList.size() > 0) {
			mAdapter.addAll(dataList);
			mAdapter.notifyDataSetChanged();
		}
		mAdapter.stopMore();
	}

	public void setOnItemClickListener(RecyclerArrayAdapter.OnItemClickListener listener) {
		mAdapter.setOnItemClickListener(listener);
	}

	protected abstract void requestPageData(int page);

	protected abstract RecyclerArrayAdapter<T> getAdapter(Context context);

	public void add(T obj) {
		mAdapter.add(obj);
	}

	public int getItemCount() {
		return mAdapter.getCount();
	}

	public T getItem(int position) {
		return mAdapter.getItem(position);
	}
}
