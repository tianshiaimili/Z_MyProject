/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.widget.ImageView;

// Referenced classes of package com.pccw.gzmobile.res:
//            AbsImageCacheHelper, ImageLoaderRemoteCallback

class ImageLoader
{
	

    private static final String TAG = "com/pccw/gzmobile/res/ImageLoader.getSimpleName()";
    private Bitmap mLoadingBitmap;
    private static final int FADE_IN_TIME = 800;
    private boolean mFadeInBitmap;
    private static ColorDrawable mTransparentDrawable;
    private AbsImageCacheHelper helper;
    private int mMinWidthPixel;
    private android.graphics.BitmapFactory.Options mOptions;
    private ImageLoaderRemoteCallback mRemoteCallback;
    private Map mEquivalentUrls;

    ImageLoader(AbsImageCacheHelper helper)
    {
        this.helper = helper;
        Log.e(TAG, (new StringBuilder(String.valueOf(TAG))).append(" memory cache map size is ").append(this.helper.size()).toString());
        mTransparentDrawable = new ColorDrawable(17170445);
        mEquivalentUrls = new HashMap();
    }

    public void setLoadingImage(Bitmap bitmap)
    {
        mLoadingBitmap = bitmap;
    }

    public void setLoadingImage(Resources res, int resId)
    {
        mLoadingBitmap = null;
        try
        {
            mLoadingBitmap = BitmapFactory.decodeResource(res, resId);
        }
        catch(Exception e)
        {
            Log.w(TAG, (new StringBuilder("setLoadingImage failed ")).append(e).toString());
        }
    }

    public Bitmap getLoadingImage()
    {
        return mLoadingBitmap;
    }

    public void setImageFadeIn(boolean fadeIn)
    {
        mFadeInBitmap = fadeIn;
    }

    public boolean isImageFadeIn()
    {
        return mFadeInBitmap;
    }

    protected void setImageBitmapWithFadeIn(ImageView imageView, Bitmap bitmap)
    {
        if(mFadeInBitmap)
        {
            TransitionDrawable td = new TransitionDrawable(new Drawable[] {
                mTransparentDrawable, new BitmapDrawable(imageView.getResources(), bitmap)
            });
            imageView.setImageDrawable(td);
            td.startTransition(800);
        } else
        {
            imageView.setImageBitmap(bitmap);
        }
    }

    public ImageLoaderRemoteCallback getImageLoaderRemoteCallback()
    {
        return mRemoteCallback;
    }

    public void setImageLoaderRemoteCallback(ImageLoaderRemoteCallback callback)
    {
        mRemoteCallback = callback;
    }

    void setLocalImage(ImageView imageView, Object toBeDecode, String key, int reqMinWidth, int defaultResId, boolean isFling)
    {
        if(imageView == null)
        {
            Log.w(TAG, "ImageView is null.");
            return;
        }
        imageView.setTag(key);
        Bitmap bitmap = helper.getImage(key);
        if(bitmap != null)
        {
            Log.d(TAG, "setLocalImage() found image from cache.");
            imageView.setImageBitmap(bitmap);
            return;
        }
        if(isFling)
        {
            Log.v(TAG, "setLocalImage() enable lazy-load and fling is true now, no need to load image.");
            return;
        }
        try
        {
            bitmap = helper.decodeScaledDownBitmap(toBeDecode, imageView.getResources(), reqMinWidth, getBitmapOptions());
        }
        catch(Exception e)
        {
            Log.w(TAG, (new StringBuilder("decodeScaledDownBitmap failed : ")).append(e).toString());
        }
        if(bitmap == null)
        {
            try
            {
                imageView.setImageResource(defaultResId);
            }
            catch(Exception exception) { }
        } else
        {
            imageView.setImageBitmap(bitmap);
            helper.putImage(key, bitmap, false);
        }
    }

    public void setResourceImage(ImageView imageView, int resId, int reqMinWidth, int defaultResId)
    {
        setLocalImage(imageView, Integer.valueOf(resId), (new StringBuilder(String.valueOf(resId))).append(imageView.getContext().getResources().getConfiguration().toString()).toString(), reqMinWidth, defaultResId, false);
    }

    public void setAssetsImage(ImageView imageView, String assetFileName, int reqMinWidth, int defaultResId)
    {
        InputStream is = null;
        try
        {
            is = imageView.getContext().getAssets().open(assetFileName);
        }
        catch(Exception e)
        {
            Log.w(TAG, (new StringBuilder("setAssetsImage failed : ")).append(e).toString());
        }
        setLocalImage(imageView, is, assetFileName, reqMinWidth, defaultResId, false);
    }

    public void setStorageImage(ImageView imageView, String filePath, int reqMinWidth, int defaultResId)
    {
        setLocalImage(imageView, filePath, filePath, reqMinWidth, defaultResId, false);
    }

    public void setRequestMinWidth(int minWidthPixel)
    {
        mMinWidthPixel = minWidthPixel;
    }

    public int getRequestMinWidth()
    {
        return mMinWidthPixel;
    }

    public void setBitmapOptions(android.graphics.BitmapFactory.Options opt)
    {
        mOptions = opt;
    }

    public android.graphics.BitmapFactory.Options getBitmapOptions()
    {
        return mOptions;
    }

    Bitmap loadBitmap(String url, int reqMinWidth)
        throws Exception
    {
        byte data[] = NetUtils.getByteArray(url);
        return helper.decodeScaledDownBitmap(data, null, reqMinWidth, getBitmapOptions());
    }

    public void addEquivalentUrl(String urlOriginal, String urlEquivalent)
    {
        if(urlOriginal != null && urlEquivalent != null)
            mEquivalentUrls.put(urlOriginal, urlEquivalent);
    }

    public Bitmap getBitmapFromCache(String url)
    {
        Bitmap bitmap;
        for(bitmap = helper.getImage(url); bitmap == null; bitmap = helper.getImage(url))
        {
            url = (String)mEquivalentUrls.get(url);
            if(url == null)
                break;
        }

        if(bitmap != null)
            Log.d(TAG, (new StringBuilder("setRemoteImage() found image from cache with key : ")).append(url).toString());
        return bitmap;
    }


}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 157 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/