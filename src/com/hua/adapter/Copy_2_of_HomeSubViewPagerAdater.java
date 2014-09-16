package com.hua.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hua.util.LogUtils2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 首页商家广告适配器
 *
 */
public class Copy_2_of_HomeSubViewPagerAdater extends PagerAdapter implements OnClickListener{
	
	private List<ImageView> mDatas;
	private List<ImageView> mViews;
	private ImageView mImageView;
	private Context mContext;
	private Bundle mBundle;
	private Intent mIntent;
	
	public Copy_2_of_HomeSubViewPagerAdater(Context mContext,List<ImageView> mDatas)
	{
		mIntent = new Intent();
		mBundle = new Bundle();
		this.mContext = mContext;
		mViews = new ArrayList<ImageView>();
		this.mDatas = mDatas;
		int length = mDatas == null ? 0 : mDatas.size();
		
		for(int i=0;i<length;i++)
		{
			ImageView mImageView = new ImageView(mContext);
			mViews.add(mImageView);
		}
		
		length = 0;
	}

	@Override
	public int getCount() {
		
		return mDatas == null ? 0 : mDatas.size();
//		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		
		return arg0==(arg1);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		
		LogUtils2.e("instantiateItem------=="+position);
		////////////
		mImageView = mDatas.get(position % mDatas.size());
		mImageView.setAdjustViewBounds(true);
		mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		mImageView.setOnClickListener(this);
		
		(container).addView(mImageView);
		
		return mImageView;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
//		LogUtils2.e("position % (mViews.size())=="+position % (mViews.size())+ "   position=="+position);
////		mImageView = mViews.get(position % (mViews.size()));
//		(container).removeView((View)object);
		LogUtils2.d("destroyItem************=="+position);
		(container).removeView((View)object);
		
	}

	@Override
	public void onClick(View v) {
		
	
	}
}

