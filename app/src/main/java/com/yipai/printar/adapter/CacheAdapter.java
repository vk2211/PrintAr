package com.yipai.printar.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.yipai.printar.R;
import com.yipai.printar.constant.Path;
import com.yipai.printar.ui.realm.VideoData;
import com.yipai.printar.utils.BitmapUtil;
import com.yipai.printar.utils.ImageLoader;
import com.yipai.printar.utils.VideoDataSheet;
import com.yipai.printar.utils.file.FileUtil;
import com.yipai.printar.utils.log.TimeUtil;
import java.io.File;

/**
 * Created by liuchuanliang on 2017/3/16.
 */
public class CacheAdapter extends RecyclerArrayAdapter<VideoData> {
	private Context mContext;
	private VideoDataSheet mVideoDataSheet;
	private BitmapUtil mBitmapUtil;

	public CacheAdapter(Context context) {
		super(context);
		mContext = context;
		mBitmapUtil = new BitmapUtil(mContext);
		mVideoDataSheet = VideoDataSheet.get();
	}

	@Override
	public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
		return new CacheDataViewHolder(parent);
	}

	class CacheDataViewHolder extends BaseViewHolder<VideoData> {
		private ImageView cacheIamge;
		private TextView tv_print;

		public CacheDataViewHolder(ViewGroup itemView) {
			super(itemView, R.layout.item_shotview);
			cacheIamge = $(R.id.img_catch);
			tv_print = $(R.id.tv_print);
			tv_print.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int pos = CacheDataViewHolder.this.getDataPosition();
					VideoData videoData = CacheAdapter.this.getItem(pos);
					Bitmap bitmap = BitmapFactory.decodeFile(videoData.getImagePath());
					String arpath = FileUtil.sdcard.getFullPath(Path.DIR, "" + TimeUtil.getCurrentTime() + ".jpg");
					videoData.setImagePath(arpath);
					mVideoDataSheet.add(videoData);
					mBitmapUtil.saveBitmap(bitmap, arpath);
				}
			});
		}

		@Override
		public void setData(VideoData data) {
			super.setData(data);
			ImageLoader.showImageView(mContext, Uri.fromFile(new File(data.getImagePath())).toString(), cacheIamge);
			tv_print.setText("打印");
		}
	}
}
