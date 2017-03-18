package com.yipai.printar.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuchuanliang on 2017/3/18.
 */

public class SingleChoiceDialog {
	private Context mContext;
	private int yourChoice = 0;
	private SingleSelectInterface mSingleSelectInterface;
	private String mTitle;
	private List<String> mList;
	private String[] mItems = null;

	public SingleChoiceDialog(Context context, SingleSelectInterface singleSelectInterface) {
		mContext = context;
		mSingleSelectInterface = singleSelectInterface;
		mList = new ArrayList<>();
	}

	public SingleChoiceDialog(Context context, SingleSelectInterface singleSelectInterface, String title, String[] items) {
		mContext = context;
		mSingleSelectInterface = singleSelectInterface;
		mTitle = title;
		mItems = items;
		if (mList == null) {
			mList = new ArrayList<>();
		}
		for (int i = 0; i < items.length; i++) {
			mList.add(items[i]);
		}
	}

	public void addItems(String item) {
		mList.add(item);
		mItems = new String[mList.size()];
		for (int i = 0; i < mList.size(); i++) {
			mItems[i] = mList.get(i);
		}
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public List<String> getList() {
		return mList;
	}

	public void setList(List<String> list) {
		mList = list;
	}

	public void showSingleChoiceDialog() {
		AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(mContext);
		singleChoiceDialog.setTitle(getTitle());
		// 第二个参数是默认选项，此处设置为0
		singleChoiceDialog.setSingleChoiceItems(mItems, 0,
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					yourChoice = which;
				}
			});
		singleChoiceDialog.setPositiveButton("确定",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSingleSelectInterface.OnSingleSelect(yourChoice);
					yourChoice = 0;
				}
			});
		singleChoiceDialog.show();
	}

	public interface SingleSelectInterface {
		public void OnSingleSelect(int result);
	}

}
