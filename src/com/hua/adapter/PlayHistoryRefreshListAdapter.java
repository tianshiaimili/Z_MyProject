package com.hua.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hua.activity.R;
import com.hua.model.AppData.TrueAppData;
import com.hua.network.utils.LogUtils2;

public class PlayHistoryRefreshListAdapter extends BaseAdapter {
	
	private static final String SUFFIX_DOWN = "万人下载 |";
	private static final String SUFFIX_SIZE = "M";
	private Context context;
	private List<TrueAppData> mDataList;
	private LayoutInflater mInflater;
	private final List<String> urlLists = new ArrayList<String>();

	public PlayHistoryRefreshListAdapter(Context context,List<TrueAppData > list) {
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
		ViewHolder mViewHolder;
		TrueAppData trueAppData = null;
		String tempDownUrl= null;
		if(position == 0){
			
			LogUtils2.d("convertView----"+convertView);
			LogUtils2.d("first---------------");
			convertView = mInflater.inflate(R.layout.under_banner_layout, null);
			mViewHolder = new ViewHolder();
			mViewHolder.categoryView = convertView;
			convertView.setTag(mViewHolder);
			return convertView;
			
		}
		
		if(mDataList.size() >0){
			trueAppData = mDataList.get(position);
		}
		
		if (convertView != null && ((ViewHolder)convertView.getTag()).tempAppName != null) {
			LogUtils2.e("text2222222--"+convertView);
			mViewHolder = (ViewHolder) convertView.getTag();
//			return convertView;
		} else {
//			mViewHolder = new TextView(context);
			mViewHolder = new ViewHolder();
//			convertView =  new TextView(context);
			convertView = mInflater.inflate(R.layout.playhistory_item, null);
			mViewHolder.tempImageView = (ImageView) convertView.findViewById(R.id.ph_imageview);
			mViewHolder.tempAppName = (TextView) convertView.findViewById(R.id.playhistory_textview_title);
			mViewHolder.tempAppScoreRatingBar = (RatingBar) convertView.findViewById(R.id.ph_ratingBar1);
			mViewHolder.tempDownNum = (TextView) convertView.findViewById(R.id.ph_downnum);
			mViewHolder.tempAppSize = (TextView) convertView.findViewById(R.id.ph_app_size);
			mViewHolder.tempComment = (TextView) convertView.findViewById(R.id.ph_comment);
			mViewHolder.tempAppDownButton = (Button) convertView.findViewById(R.id.ph_downbutton);
		}
		mViewHolder.tempImageView.setImageBitmap(trueAppData.getAppIco());
		mViewHolder.tempAppName.setText(trueAppData.getAppName());
		mViewHolder.tempAppScoreRatingBar.setRating(Float.parseFloat(trueAppData.getAppScore()));
		mViewHolder.tempDownNum.setText(trueAppData.getAppDownLoadNum()+SUFFIX_DOWN);
		mViewHolder.tempAppSize.setText(trueAppData.getAppSize()+SUFFIX_SIZE);
		mViewHolder.tempComment.setText(trueAppData.getAppComment());
		tempDownUrl = trueAppData.getAppDowmUrl();
		mViewHolder.tempAppDownButton.setOnClickListener(new MyOnClickListener(tempDownUrl));
		convertView.setTag(mViewHolder);
		LogUtils2.e("textviiii----"+convertView);
		return convertView;
	}
	
	
	class ViewHolder{
		
		ImageView tempImageView;
		View categoryView;
//		TextView tempTextView;
		TextView tempAppName;
		TextView tempAppScore;
		TextView tempDownNum;
		TextView tempAppSize;
		TextView tempComment;
		Button tempAppDownButton;
		RatingBar tempAppScoreRatingBar;
		
		
	}
	
	
	class MyOnClickListener implements OnClickListener{

		String temUrl = null;
		
		public MyOnClickListener(String url){
			temUrl = url;
		}
		
		@Override
		public void onClick(View v) {
			
			Toast.makeText(context, "Url=="+temUrl, 300).show();
			
		}
		
	}
	
	
}
