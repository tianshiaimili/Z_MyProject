/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.weget;

import android.content.Context;
import android.content.res.Resources;
import android.util.*;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageIndexer extends LinearLayout
    implements android.view.View.OnClickListener
{
	

    private static final String TAG = "com/pccw/gzmobile/widget/PageIndexer.getSimpleName()";
    private Context mContext;
    private ImageView mImageViews[];
    private int mViewCount;
    private int mDefaultSelected;
    private int mCurSelected;
    private float density;
    private PageClickListener listener;
	
    public static interface PageClickListener
    {

        public abstract void onPageClick(int i);
    }


    public PageIndexer(Context context)
    {
        super(context);
        mDefaultSelected = 0;
        mContext = context;
        density = context.getResources().getDisplayMetrics().density;
    }

    public PageIndexer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mDefaultSelected = 0;
        mContext = context;
        density = context.getResources().getDisplayMetrics().density;
    }

    public void generateViews(int count, int indexImageResId, int leftPadding, int topPadding, int rightPadding, int bottomPadding)
    {
        if(count <= 0)
            return;
        mViewCount = count;
        removeAllViews();
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(-2, -2);
        params.gravity = 16;
        ImageView img = null;
        mImageViews = new ImageView[mViewCount];
        for(int i = 0; i < mViewCount; i++)
        {
            img = new ImageView(mContext);
            img.setImageResource(indexImageResId);
            img.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
            img.setClickable(true);
            img.setEnabled(true);
            img.setTag(Integer.valueOf(i));
            img.setOnClickListener(this);
            mImageViews[i] = img;
            addView(img, params);
        }

        mCurSelected = mDefaultSelected;
        mImageViews[mCurSelected].setEnabled(false);
    }

    public void updateSelected(int position)
    {
        if(position < 0 || position > mViewCount - 1 || position == mCurSelected)
        {
            return;
        } else
        {
            mImageViews[mCurSelected].setEnabled(true);
            mImageViews[position].setEnabled(false);
            mCurSelected = position;
            return;
        }
    }

    public final int dip2px(float dipValue)
    {
        return (int)(dipValue * density + 0.5F);
    }

    public final int px2dip(float pxValue)
    {
        return (int)(pxValue / density + 0.5F);
    }

    public void onClick(View v)
    {
        int position = ((Integer)v.getTag()).intValue();
        Log.d(TAG, (new StringBuilder("onClick position ")).append(position).toString());
        if(listener != null)
            listener.onPageClick(position);
    }

    public void setPageClickListener(PageClickListener listener)
    {
        this.listener = listener;
    }


}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 664 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/