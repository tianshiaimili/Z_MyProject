package com.hua.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hua.activity.R;
import com.hua.network.utils.LogUtils2;

public class PlayHistoryRefreshListAdapter extends BaseAdapter {
	
	private Context context;
	private List<String> mDataList;
	private LayoutInflater mInflater;

	public PlayHistoryRefreshListAdapter(Context context,List<String > list) {
		this.context = context;
		this.mDataList = list;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
//		return contentStr.length;
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

//		TextView contentTv;
		ViewHolder mViewHolder  ;
		if(position == 0){
			
			LogUtils2.d("convertView----"+convertView);
			
//			if(convertView != null && ((ViewHolder)convertView.getTag()).categoryView == null){
//				LogUtils2.d("second---------------");
//				return ((ViewHolder)convertView.getTag()).categoryView;
//				
//			}
			LogUtils2.d("first---------------");
			convertView = mInflater.inflate(R.layout.under_banner_layout, null);
			mViewHolder = new ViewHolder();
			mViewHolder.categoryView = convertView;
			convertView.setTag(mViewHolder);
			return convertView;
			
		}
		
		if (convertView != null && ((ViewHolder)convertView.getTag()).tempTextView != null) {
			LogUtils2.e("text2222222--"+convertView);
			mViewHolder = (ViewHolder) convertView.getTag();
//			return convertView;
		} else {
//			mViewHolder = new TextView(context);
			mViewHolder = new ViewHolder();
			convertView =  new TextView(context);
			mViewHolder.tempTextView = (TextView) convertView;
		}
		mViewHolder.tempTextView.setBackgroundResource(R.drawable.bg_text_iteml);
		mViewHolder.tempTextView.setHeight((int)TypedValue.complexToDimension(30, context.getResources().getDisplayMetrics()));
//		contentTv.setText(contentStr[position]);
		mViewHolder.tempTextView.setText(mDataList.get(position));
		mViewHolder.tempTextView.setTextColor(Color.parseColor("#666666"));
		convertView.setTag(mViewHolder);
		LogUtils2.e("textviiii----"+convertView);
		return convertView;
	}
	
	
	class ViewHolder{
		
		ImageView tempImageView;
		View categoryView;
		TextView tempTextView;
		
		
	}
	
	
}
