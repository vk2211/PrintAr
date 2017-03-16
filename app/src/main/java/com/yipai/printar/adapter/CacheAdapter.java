package com.yipai.printar.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.yipai.printar.R;
import com.yipai.printar.adapter.itemcachedata.ItemCacheData;
import com.yipai.printar.utils.ImageLoader;

import java.io.File;


/**
 * Created by liuchuanliang on 2017/3/16.
 */
public class CacheAdapter extends RecyclerArrayAdapter<ItemCacheData> {
	private Context mContext;

	public CacheAdapter(Context context) {
		super(context);
		mContext = context;
	}


	@Override
	public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
		return new CacheDataViewHolder(parent);
	}

	class CacheDataViewHolder extends BaseViewHolder<ItemCacheData> {

		private ImageView cacheIamge;
		private TextView tv_print;


		public CacheDataViewHolder(ViewGroup itemView) {
			super(itemView, R.layout.item_shotview);
			cacheIamge = $(R.id.img_catch);
			tv_print = $(R.id.tv_print);
		}

		@Override
		public void setData(ItemCacheData data) {
			super.setData(data);
			ImageLoader.showImageView(mContext, Uri.fromFile(new File(data.getImagePath())).toString(), cacheIamge);
			tv_print.setText("打印");
		}
	}
}
