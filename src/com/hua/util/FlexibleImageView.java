/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

@SuppressLint("WrongCall")
public class FlexibleImageView extends ImageView
{
    private class FitRatioLayoutStrategy
        implements LayoutStrategy
    {

        public boolean onLayout(Drawable drawable)
        {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            if(width > 0 && height > 0)
            {
                if(mEnableFitWidth && mFitWidth > 0)
                {
                    getLayoutParams().width = mFitWidth;
                    getLayoutParams().height = FlexibleImageView.getFitHeight(mFitWidth, width, height);
                } else
                if(!mEnableFitWidth && mFitHeight > 0)
                {
                    getLayoutParams().height = mFitHeight;
                    getLayoutParams().width = FlexibleImageView.getFitWidth(mFitHeight, width, height);
                }
            } else
            {
                Log.v(FlexibleImageView.TAG, (new StringBuilder("Drawable is NOT ready to use : ")).append(width).append(", ").append(height).toString());
            }
            return false;
        }

        private boolean mEnableFitWidth;
        private int mFitWidth;
        private int mFitHeight;
        final FlexibleImageView this$0;

        private FitRatioLayoutStrategy(boolean fitWidth, int rawSize)
        {
            this$0 = FlexibleImageView.this;
//            super();
            mEnableFitWidth = fitWidth;
            if(mEnableFitWidth)
                mFitWidth = rawSize;
            else
                mFitHeight = rawSize;
        }

        FitRatioLayoutStrategy(boolean flag, int i, FitRatioLayoutStrategy fitratiolayoutstrategy)
        {
            this(flag, i);
        }
    }

    private class FixedSizeLayoutStrategy
        implements LayoutStrategy
    {

        public boolean onLayout(Drawable drawable)
        {
            boolean resize = !mAfterDrawableReady;
            if(mAfterDrawableReady && FlexibleImageView.isDrawableReady(drawable))
                resize = true;
            if(resize)
            {
                getLayoutParams().width = mFixedWidth;
                getLayoutParams().height = mFixedHeight;
            }
            return false;
        }

        private boolean mAfterDrawableReady;
        private int mFixedWidth;
        private int mFixedHeight;
        final FlexibleImageView this$0;

        private FixedSizeLayoutStrategy(boolean afterDrawableReady, int width, int height)
        {
            this$0 = FlexibleImageView.this;
//            super();
            mAfterDrawableReady = afterDrawableReady;
            mFixedWidth = width;
            mFixedHeight = height;
        }

        FixedSizeLayoutStrategy(boolean flag, int i, int j, FixedSizeLayoutStrategy fixedsizelayoutstrategy)
        {
            this(flag, i, j);
        }
    }

    public static interface ImageEventListener
    {

        public abstract void onDrawableReady(Drawable drawable);
    }

    private static interface LayoutStrategy
    {

        public abstract boolean onLayout(Drawable drawable);
    }


    public FlexibleImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public FlexibleImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FlexibleImageView(Context context)
    {
        super(context);
    }

    private static boolean isDrawableReady(Drawable drawable)
    {
        return drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0;
    }

    public void setImageBitmap(Bitmap bm)
    {
        super.setImageBitmap(bm);
    }

    public void setImageResource(int resId)
    {
        setImageDrawable(getContext().getResources().getDrawable(resId));
    }

    public void setImageDrawable(Drawable drawable)
    {
        if(!mAfterOnLayoutCalled && mImageEventListener != null && isDrawableReady(drawable))
            mImageEventListener.onDrawableReady(drawable);
        if(mLayoutStrategy != null)
        {
            if(!mLayoutStrategy.onLayout(drawable))
                super.setImageDrawable(drawable);
        } else
        {
            super.setImageDrawable(drawable);
        }
        if(mAfterOnLayoutCalled && mImageEventListener != null && isDrawableReady(drawable))
            mImageEventListener.onDrawableReady(drawable);
    }

    public int getLayoutWidth()
    {
        return getLayoutParams().width;
    }

    public int getLayoutHeight()
    {
        return getLayoutParams().height;
    }

    public void setImageEventListener(boolean afterOnLayoutCalled, ImageEventListener listener)
    {
        mAfterOnLayoutCalled = afterOnLayoutCalled;
        mImageEventListener = listener;
    }

    public void setLayout(boolean fitWidth, int rawSize)
    {
        mLayoutStrategy = new FitRatioLayoutStrategy(fitWidth, rawSize, null);
    }

    public void setLayout(boolean afterDrawableReady, int fixedWidth, int fixedHeight)
    {
        mLayoutStrategy = new FixedSizeLayoutStrategy(afterDrawableReady, fixedWidth, fixedHeight, null);
    }

    public static int getFitHeight(int fitWidth, int ratioWidth, int ratioHeight)
    {
        if(ratioWidth == 0)
        {
            Log.w(TAG, "getFitHeight() ratioWidth is 0, return fitHeight 0.");
            return 0;
        } else
        {
            return (fitWidth * ratioHeight) / ratioWidth;
        }
    }

    public static int getFitWidth(int fitHeight, int ratioWidth, int ratioHeight)
    {
        if(ratioHeight == 0)
        {
            Log.w(TAG, "getFitWidth() ratioHeight is 0, return fitWidth 0.");
            return 0;
        } else
        {
            return (fitHeight * ratioWidth) / ratioHeight;
        }
    }

    private static final String TAG = "com/pccw/gzmobile/widget/FlexibleImageView.getSimpleName()";
    private LayoutStrategy mLayoutStrategy;
    private boolean mAfterOnLayoutCalled;
    private ImageEventListener mImageEventListener;



}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 155 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/