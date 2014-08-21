package com.hua.view;

import com.hua.activity.R;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MyViewPager extends LinearLayout{

	private ViewPager pager;
	
	
	private int [] imageViews = {R.drawable.xianjian1,R.drawable.xianjian2,
			R.drawable.xianjian1,R.drawable.xianjian2};
	public MyViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		intitView(context);
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		intitView(context);
		
	}

	public MyViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		intitView(context);
	}


	/**
	 * ≥ı ºªØ
	 */
	private void intitView(Context context) {
		// TODO Auto-generated method stub
		
		setOrientation(LinearLayout.VERTICAL);
	
		///
		LinearLayout.LayoutParams params = 
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 
						LinearLayout.LayoutParams.WRAP_CONTENT);
		setLayoutParams(params);
		setBackgroundColor(Color.parseColor("#CCCCCC"));
		//
		pager = new ViewPager(context);
		pager.setLayoutParams(params);
		pager.setAdapter(new MyViewPagerAdater());
		
		
	}
	
	class MyViewPagerAdater extends PagerAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 4;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			// TODO Auto-generated method stub
			return view == (View)obj;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			
			ImageView imageView = new ImageView(getContext());
			imageView.setImageResource(imageViews[position]);
			container.addView(imageView);
			return imageView;
//			return super.instantiateItem(container, position);
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
//			super.destroyItem(container, position, object);
			container.removeView((View)object);
			
		}
		
		
	}
	
	
}
