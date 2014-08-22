package com.hua.adapter;


import java.util.List;

import android.R.integer;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.model.CategoryInfo;
import com.hua.util.LogUtils2;

public class HF01_RecommendAdapter extends BaseAdapter{
	private Context mContext;
	private List<CategoryInfo> mList;
	private int count; 
	
	public HF01_RecommendAdapter(Context mContext,List<CategoryInfo> mList){
		this.mContext = mContext;
		this.mList = mList;
		count = mList.size();
	}

	@Override
	public int getCount() {
		return count;//mList.size() + 2;
	}

	@Override
	public CategoryInfo getItem(int position) {
		LogUtils2.i("getItem------position------------- =="+position);
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		LogUtils2.i("getView-----position-------------=="+position);
		
		if(convertView == null){
			
			convertView = LayoutInflater.from(mContext).inflate(R.layout.h_s_fragment01_item_recommend, null);
			mHolder = new ViewHolder();
			mHolder.icon = (ImageView) convertView.findViewById(R.id.img_type);
			mHolder.msg = (TextView) convertView.findViewById(R.id.tv_typeName);
			convertView.setTag(mHolder);
		}else{
			mHolder = (ViewHolder) convertView.getTag();
		}
		
//		if(position == 0 || position == 1){
//			
//			mHolder.icon.setVisibility(View.GONE);
//			mHolder.msg.setBackgroundColor(Color.parseColor("#FFCCCC"));
//			mHolder.msg.setText("");
//			mHolder.msg.setLayoutParams(
//					new FrameLayout.LayoutParams
//					(100,10));
//			
//			
//			return convertView;
//		}
		CategoryInfo categoryInfo = mList.get(position);
		mHolder.icon.setImageResource(categoryInfo.getIcon());
		mHolder.icon.setVisibility(View.VISIBLE);
		mHolder.msg.setText(categoryInfo.getMsg());
		mHolder.msg.setBackgroundColor(Color.parseColor("#89000000"));
		mHolder.msg.setLayoutParams(
				new FrameLayout.LayoutParams
				(FrameLayout.LayoutParams.MATCH_PARENT,70));
		
		return convertView;
	}
	
	private class ViewHolder{
		private ImageView icon;
		private TextView msg;
	}

}
