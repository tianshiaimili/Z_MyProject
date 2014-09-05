package com.hua.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hua.activity.R;
import com.hua.util.DisplayUtils;
import com.hua.util.LogUtils2;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

@SuppressLint("NewApi")
public class DownloadFragment extends Fragment implements OnClickListener{

	private static final int SHOW_GRIDVIEW = 2;

	private static final int GONE_GRIDVIEW = -1;
	private static final String EDIT_TEXT = "Edit";
	private static final String DONE_TEXT ="Done";

	private static final float MARGINSPACE = 80;

	private Button editButton;
	private RelativeLayout editLayout;
	private LinearLayout editSubLayout;
	private ListView editListView;
	
	
	private int[] images = {R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher
			,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,
			R.drawable.ic_launcher,R.drawable.ic_launcher,
			R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,
			R.drawable.ic_launcher,R.drawable.ic_launcher};
	
	Button button ;
	TextView textView;
	private final int lineRawCount = 2;
	private boolean  isMore;
	private GridView gridView;
	private MyHandler myHandler;
	
	class MyHandler  extends Handler{
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		
			int what = msg.what;
			
		switch (what) {
		case 2:
			
			gridView.setVisibility(View.VISIBLE);
			gridView.setAdapter(new MGridViewAdapter());
			
			break;
			
			
		case -1:
			gridView.setVisibility(View.GONE);
//			gridView.setAdapter(new MGridViewAdapter());
			
			break;

		default:
			break;
		}
		
		}
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		myHandler = new MyHandler();
	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.download_fragment, null);
	
		textView = (TextView) view.findViewById(R.id.download_textview);
	
		button = (Button) view.findViewById(R.id.download_button);
		button.setOnClickListener(this);
		
		
		DisplayMetrics metrics = new DisplayMetrics();
		 getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		 int screenWidth = metrics.widthPixels;
		 Paint paint = textView.getPaint();
		 int contentLenght = (int) paint.measureText(textView.getText().toString());
		 
		 int tempRawCount = contentLenght /screenWidth ;
		 LogUtils2.d("tempRawCount=="+tempRawCount);
		 
		 if(tempRawCount > lineRawCount){
			 
			 isMore = true;
			 
		 }
		
		 gridView = (GridView) view.findViewById(R.id.download_gridview);
//		 gridView.setAdapter(new MGridViewAdapter());
//		return super.onCreateView(inflater, container, savedInstanceState);
		 editButton = (Button) view.findViewById(R.id.edit_button);
		 editButton.setOnClickListener(this);
		 editLayout = (RelativeLayout) view.findViewById(R.id.edit_layout);
		 
		 editSubLayout = (LinearLayout) view.findViewById(R.id.edit_sublayout);
		 
		 ImageView imageView = new ImageView(getActivity());
		 imageView.setImageResource(R.drawable.xianjian2);
		 
		 HashMap<String, Object> map = new HashMap<String, Object>();
		 map.put("edit_text_item", "asdasdasdasd");
		 map.put("edit_imageview_item", R.drawable.xianjian2);
		 List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		 list.add(map);
		 
		 
		 
		 SimpleAdapter adapter =  new SimpleAdapter(getActivity(), list, 
				 R.layout.edit_listview_item, new String[]{"edit_imageview_item","edit_text_item"}, new int[]{R.id.edit_imageview_item,R.id.edit_text_item});
		 editListView = (ListView) view.findViewById(R.id.edit_listview);
		 /**
		  * 添加ListView的头和尾的话 要在setAdapter之前
		  */
		 View headerView = new View(getActivity());
		 headerView.setBackgroundColor(Color.parseColor("#FFFFFF"));
		 AbsListView.LayoutParams params = 
				 new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 
						 DisplayUtils.dip2px(getActivity(), 30));
		 headerView.setLayoutParams(params);
		 editListView.addHeaderView(headerView);
		 ///////
		 View foodView = new View(getActivity());
		 foodView.setBackgroundColor(Color.parseColor("#CCCCCC"));
		 foodView.setLayoutParams(params);
		 editListView.addFooterView(foodView);
		 
		 LogUtils2.i("adapter=="+adapter);
		 editListView.setAdapter(adapter);
		 editListView.setEmptyView(view.findViewById(R.id.edit_imageview));
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		int id = v.getId();
		if(id == R.id.download_button){
			
			if(isMore){
				isMore = false;
				textView.setEllipsize(null);
				textView.setSingleLine(isMore);
				button.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.btn_more_collapse, 0);
				button.setTextColor(Color.parseColor("#FFFFFF"));
				
				myHandler.sendEmptyMessage(SHOW_GRIDVIEW);
				
			}else {
				
				isMore = true;
				textView.setEllipsize(TextUtils.TruncateAt.END);
				textView.setLines(lineRawCount);
				button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_more_expand, 0);
				button.setTextColor(Color.parseColor("#CCCCCC"));
				
				myHandler.sendEmptyMessage(GONE_GRIDVIEW);
				
			}
			
		}
		
		LogUtils2.i("id=="+id);
		LogUtils2.i("R.id.edit_id=="+R.id.edit_button);
		if(id == R.id.edit_button){
			LogUtils2.i("EDIT_TEXT...............");
			
			LogUtils2.i(".editButton.getText().="+editButton.getText());
			if(editButton.getText() .equals(EDIT_TEXT)){
				
				LogUtils2.i("EDIT_TEXT...............");
				
				editButton.setText(DONE_TEXT);
				editButton.setTextColor(Color.rgb(0, 0, 0));
				editButton.setBackgroundResource(R.drawable.btn_bg_yellow);
				editButton.setPadding(0, 0, 0, 0);
				Animation animation = new TranslateAnimation(0, DisplayUtils.dip2px(getActivity(), MARGINSPACE), 0, 0);
				animation.setFillAfter(false);
				animation.setRepeatCount(0);
				animation.setDuration(250);
				animation.setInterpolator(new AccelerateInterpolator());
				animation.setAnimationListener(new Animation.AnimationListener() {
					
					private ViewGroup.MarginLayoutParams params;
					
					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						params = (ViewGroup.MarginLayoutParams) editSubLayout.getLayoutParams();
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub
						editSubLayout.clearAnimation();
						params.leftMargin = DisplayUtils.dip2px(getActivity(), MARGINSPACE);
						params.rightMargin = -1 * DisplayUtils.dip2px(getActivity(), MARGINSPACE);
						editSubLayout.setLayoutParams(params);
						editSubLayout.requestLayout();
					}
				});
				
				editSubLayout.startAnimation(animation);
				
			}else if(editButton.getText().equals(DONE_TEXT)){
				
				editButton.setText(EDIT_TEXT);
				editButton.setTextColor(Color.rgb(210, 210, 210));
				editButton.setBackgroundResource(R.drawable.btn_bg_grey);
				editButton.setPadding(0, 0, 0, 0);
				
				Animation animation = new TranslateAnimation(0, -1 * DisplayUtils.dip2px(getActivity(), MARGINSPACE), 0, 0);
				animation.setFillAfter(false);
				animation.setRepeatCount(0);
				animation.setDuration(250);
				animation.setInterpolator(new AccelerateInterpolator());
				animation.setAnimationListener(new Animation.AnimationListener() {
					
					private ViewGroup.MarginLayoutParams params;
					
					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						params = (ViewGroup.MarginLayoutParams) editSubLayout.getLayoutParams();
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub
						editSubLayout.clearAnimation();
						params.leftMargin = 0;
						params.rightMargin = 0;
						editSubLayout.setLayoutParams(params);
						editSubLayout.requestLayout();
					}
				});
				
				editSubLayout.startAnimation(animation);
				
			}
		}
		
		
	}
	
	class MGridViewAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			LogUtils2.d("getCount....");
			return images.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView = new ImageView(getActivity());
			imageView.setImageResource(images[position]);
			imageView.setScaleType(ScaleType.FIT_XY);
			return imageView;
		}
		
	}
	
	
	
}
