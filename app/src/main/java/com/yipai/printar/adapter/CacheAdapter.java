package com.yipai.printar.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.yipai.printar.App;
import com.yipai.printar.R;
import com.yipai.printar.constant.Path;
import com.yipai.printar.ui.dialog.SingleChoiceDialog;
import com.yipai.printar.ui.realm.VideoData;
import com.yipai.printar.utils.BitmapUtil;
import com.yipai.printar.utils.ImageLoader;
import com.yipai.printar.utils.PackageUtil;
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
	private SingleChoiceDialog mSingleChoiceDialog;
	private String mDialogTitle = "选择打印机";
	private String[] mDialogItems = {"Pringo", "Prinhome"};
	private String mToastString = "未安装打印程序";
	private SingleChoiceDialog.SingleSelectInterface mSingleSelectInterface = new SingleChoiceDialog
		.SingleSelectInterface() {
		@Override
		public void OnSingleSelect(int result) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ComponentName cn = null;
			if (result == 0) {
				cn = new ComponentName("com.hiti.pringo", "com.hiti.pringo.MainActivity");
				if (PackageUtil.isAppExist(mContext, "com.hiti.pringo")) {
					intent.setComponent(cn);
					App.getContext().startActivity(intent);
				} else {
					Toast.makeText(mContext, mToastString, Toast.LENGTH_SHORT).show();
				}
			} else if (result == 1) {
				cn = new ComponentName("com.hiti.prinhome", "com.hiti.prinhome.SwitchNode");
				if (PackageUtil.isAppExist(mContext, "com.hiti.prinhome")) {
					intent.setComponent(cn);
					App.getContext().startActivity(intent);
				} else {
					Toast.makeText(mContext, mToastString, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	public CacheAdapter(Context context) {
		super(context);
		mContext = context;
		mBitmapUtil = new BitmapUtil(mContext);
		mVideoDataSheet = VideoDataSheet.get();
		mSingleChoiceDialog = new SingleChoiceDialog(mContext, mSingleSelectInterface, mDialogTitle, mDialogItems);
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
					mSingleChoiceDialog.showSingleChoiceDialog();
				}
			});
		}

		@Override
		public void setData(VideoData data) {
			super.setData(data);
				ImageLoader.showImageView(mContext, Uri.fromFile(new File(data.getImagePath())).toString(), cacheIamge);
			//tv_print.setText("打印");
		}
	}
}
