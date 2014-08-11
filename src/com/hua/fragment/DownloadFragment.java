package com.hua.fragment;

import com.hua.activity.R;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

@SuppressLint("NewApi")
public class DownloadFragment extends Fragment implements OnClickListener{

	private static final int SHOW_GRIDVIEW = 2;

	private static final int GONE_GRIDVIEW = -1;

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
