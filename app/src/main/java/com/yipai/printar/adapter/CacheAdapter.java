package com.yipai.printar.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.yipai.printar.R;
import com.yipai.printar.adapter.itemcachedata.ItemCacheData;
import com.yipai.printar.constant.Path;
import com.yipai.printar.utils.ArDataSheet;
import com.yipai.printar.utils.BitmapUtil;
import com.yipai.printar.utils.ImageLoader;

import java.io.File;


/**
 * Created by liuchuanliang on 2017/3/16.
 */
public class CacheAdapter extends RecyclerArrayAdapter<ItemCacheData> {
	private Context mContext;
	private ArDataSheet mArDataSheet;
	private BitmapUtil mBitmapUtil;


	public CacheAdapter(Context context) {
		super(context);
		mContext = context;
		mArDataSheet=new ArDataSheet(mContext);
		mBitmapUtil=new BitmapUtil(mContext);

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
			tv_print.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!Path.ImagePath.equals("")&&!Path.VideoPath.equals("")){

						mArDataSheet.realmAdd(Path.ImagePath,Path.VideoPath);
						if(Path.ImageBitmap!=null){
							mBitmapUtil.saveBitmap(Path.ImageBitmap,Path.ImagePath);
							Path.ImagePath="";
							Path.VideoPath="";

//							Intent intent=new Intent();
//							intent.setClassName(mContext,"com.hiti.prinhome.SwitchNode");
//							mContext.startActivity(intent);

							Toast.makeText(mContext, "start print", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
		}

		@Override
		public void setData(ItemCacheData data) {
			super.setData(data);
			ImageLoader.showImageView(mContext, Uri.fromFile(new File(data.getImagePath())).toString(), cacheIamge);
			tv_print.setText("打印");
		}
	}



}
