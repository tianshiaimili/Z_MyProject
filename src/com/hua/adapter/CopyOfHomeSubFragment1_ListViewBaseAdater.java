/*package com.hua.adapter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.androidos.HandlerTimer;
import com.hua.contants.Constant;
import com.hua.util.LogUtils2;
import com.hua.view.MyViewPager;

public class CopyOfHomeSubFragment1_ListViewBaseAdater extends BaseAdapter {
	protected static final int START_BAR = 9;
	private Context mContext;
	private List<ImageView> mImageList;

	private View topViewPager; // 顶部View
	private MyViewPager viewPager; // 顶部View 中的ViewPager
	// private MyPagerAdapter pageAdaper; // Viewpager adapter
	private HomeSubViewPagerAdater pagerAdater;
	private List<View> images; // 上方viewpager的图片
	private List<Integer> mPagerList;
	private ImageView[] mImageViews;
	private int currentPosition = 0;
	private TextView tv_title;
	private HandlerTimer handlerTimer;
	
	*//**
	 * 设计广告副 走动
	 *//*
	private int currentItem=0;
	private Timer timer;
	private TimerTask task;
	private boolean isCanel;

	Handler handler = new Handler(){
		public void handleMessage(Message message) {
			int what = message.what;
    		int numchange = what;
    		LogUtils2.i("what=="+what);
    		
    		switch (what) {
			
				case 9:
					if(viewPager !=null){
						LogUtils2.d("999999999utyuiyiyiuyui");
						viewPager.setCurrentItem(currentItem);
						
					}
					
			default:
				break;
			}
			
		};
	};
	
	
	public CopyOfHomeSubFragment1_ListViewBaseAdater(Context mContext,
			List<ImageView> mList,List<Integer> pagerList) {
		this.mContext = mContext;
		this.mImageList = mList;
		this.mPagerList = pagerList;
		pagerAdater = new HomeSubViewPagerAdater(mContext, mImageList);
	}

	@Override
	public int getCount() {
		return mImageList.size();
	}

	@Override
	public View getItem(int position) {
		return null;//mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		View categoryInfo = getItem(position);

		if (position == 0) {

			// 第一行 viewpager的layout
			return setTopView(convertView);

		}

//		if (convertView == null) {
//			convertView = LayoutInflater.from(mContext).inflate(
//					R.layout.h_s_fragment01_item_category, null);
//			mHolder = new ViewHolder();
//			mHolder.icon = (ImageView) convertView.findViewById(R.id.img_type);
//			mHolder.msg = (TextView) convertView.findViewById(R.id.tv_typeName);
//			convertView.setTag(mHolder);
//		} else {
//			mHolder = (ViewHolder) convertView.getTag();
			
			TextView textView = new TextView(mContext);
			textView.setText("123456");
			textView.setBackgroundColor(Color.parseColor("#FFFCCC"));
			convertView = textView;
			
//		}

		// mHolder.icon.setImageResource(categoryInfo.getIcon());
		// mHolder.msg.setText(categoryInfo.getMsg());

		return textView;
	}

	private View setTopView(View convertView) {
		if (topViewPager == null) {
			// 加载顶部View
			topViewPager = LayoutInflater.from(mContext).inflate(
					R.layout.home_subfragment02_header_item, null);
			// 设置Viewpager
			viewPager = (MyViewPager) topViewPager
					.findViewById(R.id.vp_ad2);
			// 塞入adapter
			viewPager.setAdapter(pagerAdater);
			// 默认选中第一个
			viewPager.setCurrentItem(0);
			viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					super.onPageSelected(position);
					setCurPoint(position % 5);
				}
			});

			
			tv_title = (TextView) topViewPager.findViewById(R.id.tv_title2);
			createPoint(topViewPager);
			LogUtils2.d("XXXXXXXXXXXXX");
//			startViewPagerTimer();
		}else {
			
			
			
		}
		return topViewPager;
	}


	*//**
	 * 创建指示点
	 * @param view
	 *//*
	private void createPoint(View view) {
		// six index round point
		LinearLayout ll = (LinearLayout) view.findViewById(R.id.llayout2);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
//		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(10,10);
		lp.setMargins(0, 0, 12, 0);
		LogUtils2.d("lalal==="+mPagerList.size());
		mImageViews = new ImageView[mPagerList.size()];
		for (int i = 0; i < mImageViews.length; i++) {
			mImageViews[i] = new ImageView(mContext);
			mImageViews[i].setImageResource(R.drawable.guide_round);
			mImageViews[i].setEnabled(true);
			mImageViews[i].setLayoutParams(lp);
			ll.addView(mImageViews[i]);
		}
		mImageViews[currentPosition].setEnabled(false);
	}
	
	
	*//**
	 * 改变显示的文字
	 * @param position
	 *//*
	public void setCurPoint(int position) {
		// TODO Auto-generated method stub
		if(position < 0 || position > mImageViews.length || position == currentPosition){
			return;
		}
		
		mImageViews[currentPosition].setEnabled(true);
		mImageViews[position].setEnabled(false);
		////
		
		switch (position) {
		case 0:
			tv_title.setText(Constant.ad_text[position]);
			break;
		case 1:
			tv_title.setText(Constant.ad_text[position]);
			break;
		case 2:
			tv_title.setText(Constant.ad_text[position]);
			break;
		case 3:
			tv_title.setText(Constant.ad_text[position]);
			break;
		case 4:
			tv_title.setText(Constant.ad_text[position]);
			break;
		}
		
		currentPosition = position;
	}
	
	
	*//**
	 * start viewPager's timer
	 *//*
	public void startViewPagerTimer() {
		 *//**
		    * 设置广告横幅走动
		    *//*
			//自动跳转广告
			task = new TimerTask() {

				@Override
				public void run() {

					synchronized (viewPager) {
						if(viewPager != null){
//							currentItem = (currentItem + 1) % ad_text.length;
							currentItem = viewPager.getCurrentItem() + 1;
							LogUtils2.d("currentItem==="+currentItem);
						}
						
						Message message = handler.obtainMessage(START_BAR);
//						handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
						handler.sendMessage(message);
					}

				}
			};
			// /
			timer = new Timer();
			timer.schedule(task, 2000, 3000);
//			startViewPagerTimer();
			isCanel = false;
	}
	
	*//**
	 * stop viewPager's timer
	 *//*
	public void stopViewPagerTimer() {

		timer.cancel();
		isCanel = true;

	}
	
	private class ViewHolder {
		private ImageView icon;
		private TextView msg;
	}

	public boolean isCanel() {
		return isCanel;
	}

	public void setCanel(boolean isCanel) {
		this.isCanel = isCanel;
	}

	
	
}
*/