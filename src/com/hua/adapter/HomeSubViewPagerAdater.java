package com.hua.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hua.util.LogUtils2;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 首页商家广告适配器
 * 
 */
public class HomeSubViewPagerAdater extends PagerAdapter implements
		OnClickListener {

	private List<ImageView> mImageViewsDatas;
	private List<ImageView> mViews;
	private List<Bitmap> mBitmaps;
	private ImageView mImageView;
	private Context mContext;
	private Bundle mBundle;
	private Intent mIntent;
	private int mImagePosition;

	public HomeSubViewPagerAdater(Context mContext, List<ImageView> mDatas,
			List<Bitmap> mBitmapList) {
		mIntent = new Intent();
		mBundle = new Bundle();
		this.mContext = mContext;
		mViews = new ArrayList<ImageView>();
		this.mImageViewsDatas = mDatas;
		this.mBitmaps = mBitmapList;
		int length = mDatas == null ? 0 : mDatas.size();

		for (int i = 0; i < length; i++) {
			ImageView mImageView = new ImageView(mContext);
			// mViews.add(mImageView);
			mViews.add(mImageViewsDatas.get(i));
		}

		length = 0;
	}

	@Override
	public int getCount() {

		// return mDatas == null ? 0 : mDatas.size();
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {

		return arg0 == (arg1);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		Bitmap bitmap = mBitmaps.get(position % (mViews.size()));
		mImagePosition = position % (mViews.size());
		mImageView = new ImageView(mContext);

		mImageView.setAdjustViewBounds(true);
		mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		// mImageView.setTag(pos);
		mImageView.setOnClickListener(this);

		mImageView.setImageBitmap(bitmap);
		mImageView.setTag(mImagePosition);
		(container).addView(mImageView);

		return mImageView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		LogUtils2.e("position % (mViews.size())==" + position % (mViews.size())
				+ "   position==" + position);
		(container).removeView((View) object);
	}

	@Override
	public void onClick(View v) {

		switch ((Integer.valueOf(mImageView.getTag().toString()))) {
		case 0:
			Toast.makeText(mContext, "----"+mImagePosition, 300).show();
			LogUtils2.d("你点钟了 图片"+mImagePosition);
			break;

		case 1:
			LogUtils2.d("你点钟了 图片"+mImagePosition);
			Toast.makeText(mContext, "----"+mImagePosition, 300).show();
			break;

		case 2:
			LogUtils2.d("你点钟了 图片"+mImagePosition);
			break;

		case 3:
			LogUtils2.d("你点钟了 图片"+mImagePosition);
			Toast.makeText(mContext, "----"+mImagePosition, 300).show();
			break;

		case 4:
			LogUtils2.d("你点钟了 图片"+mImagePosition);
			Toast.makeText(mContext, "----"+mImagePosition, 300).show();
			break;

		default:
			break;
		}

	}
}
