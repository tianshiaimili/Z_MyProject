package com.hua.utils;
/*jadclipse// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;
import com.pccw.gzmobile.androidos.ScalingAsyncTask;
import java.lang.ref.WeakReference;

// Referenced classes of package com.pccw.gzmobile.res:
//            ImageLoader, AbsImageCacheHelper, ImageLoaderRemoteCallback

public class SingleImageLoader extends ImageLoader
{
    private static class AsyncDrawable extends BitmapDrawable
    {

        public BitmapWorkerTask getBitmapWorkerTask()
        {
            return (BitmapWorkerTask)bitmapWorkerTaskReference.get();
        }

        private final WeakReference bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask)
        {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference(bitmapWorkerTask);
        }
    }

    private class BitmapWorkerTask extends ScalingAsyncTask
    {

        protected transient Bitmap doInBackground(String params[])
        {
            url = params[0];
            Bitmap bitmap = null;
            if(mImageCacheHelper != null && !isCancelled() && getAttachedImageView() != null)
                bitmap = mImageCacheHelper.getImage(url);
            if(bitmap == null && !isCancelled() && getAttachedImageView() != null)
                try
                {
                    bitmap = loadBitmap(url, reqMinWidth);
                }
                catch(Exception e)
                {
                    Log.w(SingleImageLoader.TAG, (new StringBuilder(String.valueOf(SingleImageLoader.TAG))).append(" load bitmap failed : ").append(e).append("\nurl : ").append(url).toString());
                }
            if(bitmap != null && getImageLoaderRemoteCallback() != null)
            {
                bitmap = getImageLoaderRemoteCallback().onBitmapDecoded(url, bitmap);
                if(bitmap == null)
                    Log.e(SingleImageLoader.TAG, (new StringBuilder(String.valueOf(SingleImageLoader.TAG))).append(" bitmap null after onBitmapDecoded() callback called.").toString());
            }
            if(bitmap != null && mImageCacheHelper != null)
                mImageCacheHelper.putImage(url, bitmap, true);
            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap)
        {
            if(isCancelled())
            {
                mImageCacheHelper.removeImage(url);
                return;
            }
            if(bitmap == null)
            {
                String failedUrl = url;
                url = null;
                if(getImageLoaderRemoteCallback() != null)
                    getImageLoaderRemoteCallback().onDownloadFailed(failedUrl, getAttachedImageView());
                return;
            }
            ImageView imageView = getAttachedImageView();
            if(bitmap != null && imageView != null)
                setImageBitmapWithFadeIn(imageView, bitmap);
        }

        private ImageView getAttachedImageView()
        {
            ImageView imageView = (ImageView)imageViewReference.get();
            BitmapWorkerTask bitmapWorkerTask = SingleImageLoader.getBitmapWorkerTask(imageView);
            if(this == bitmapWorkerTask)
                return imageView;
            else
                return null;
        }

        protected volatile void onPostExecute(Object obj)
        {
            onPostExecute((Bitmap)obj);
        }

        protected volatile transient Object doInBackground(Object aobj[])
        {
            return doInBackground((String[])aobj);
        }

        private String url;
        private final WeakReference imageViewReference;
        private final int reqMinWidth;
        final SingleImageLoader this$0;


        public BitmapWorkerTask(ImageView imageView, int reqMinWidth)
        {
            this$0 = SingleImageLoader.this;
            super();
            imageViewReference = new WeakReference(imageView);
            this.reqMinWidth = reqMinWidth;
        }
    }


    SingleImageLoader(AbsImageCacheHelper cacheHelper)
    {
        super(cacheHelper);
        mImageCacheHelper = cacheHelper;
    }

    public void setRemoteImage(ImageView imageView, String url, Bitmap loadingBitmap, int reqMinWidth)
    {
        if(imageView == null)
        {
            Log.w(TAG, "setRemoteImage() imageView == null.");
            return;
        }
        if(url == null || url.equals(""))
        {
            Log.w(TAG, "setRemoteImage() url == null or url is empty.");
            imageView.setImageBitmap(loadingBitmap);
            return;
        }
        Bitmap bitmap = null;
        if(mImageCacheHelper != null)
            bitmap = getBitmapFromCache(url);
        if(bitmap != null)
            imageView.setImageBitmap(bitmap);
        else
        if(cancelPotentialWork(url, imageView))
        {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView, reqMinWidth);
            AsyncDrawable asyncDrawable = new AsyncDrawable(imageView.getResources(), loadingBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(new String[] {
                url
            });
        }
    }

    public static void cancelWork(ImageView imageView)
    {
        BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if(bitmapWorkerTask != null)
            bitmapWorkerTask.cancel(true);
    }

    public static boolean cancelPotentialWork(String data, ImageView imageView)
    {
        BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if(bitmapWorkerTask != null)
        {
            String url = bitmapWorkerTask.url;
            if(url == null || !url.equals(data))
            {
                bitmapWorkerTask.cancel(true);
                Log.d(TAG, "setRemoteImage() cancel previous task.");
                return true;
            } else
            {
                Log.d(TAG, "setRemoteImage() the same work is already in progress.");
                return false;
            }
        } else
        {
            Log.d(TAG, "setRemoteImage() no task associated with the ImageView.");
            return true;
        }
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView)
    {
        if(imageView != null)
        {
            android.graphics.drawable.Drawable drawable = imageView.getDrawable();
            if(drawable instanceof AsyncDrawable)
            {
                AsyncDrawable asyncDrawable = (AsyncDrawable)drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public volatile Bitmap getLoadingImage()
    {
        return super.getLoadingImage();
    }

    public volatile android.graphics.BitmapFactory.Options getBitmapOptions()
    {
        return super.getBitmapOptions();
    }

    public volatile void setBitmapOptions(android.graphics.BitmapFactory.Options options)
    {
        super.setBitmapOptions(options);
    }

    public volatile void setRequestMinWidth(int i)
    {
        super.setRequestMinWidth(i);
    }

    public volatile void addEquivalentUrl(String s, String s1)
    {
        super.addEquivalentUrl(s, s1);
    }

    public volatile void setAssetsImage(ImageView imageview, String s, int i, int j)
    {
        super.setAssetsImage(imageview, s, i, j);
    }

    public volatile int getRequestMinWidth()
    {
        return super.getRequestMinWidth();
    }

    public volatile void setLoadingImage(Resources resources, int i)
    {
        super.setLoadingImage(resources, i);
    }

    public volatile void setLoadingImage(Bitmap bitmap)
    {
        super.setLoadingImage(bitmap);
    }

    public volatile Bitmap getBitmapFromCache(String s)
    {
        return super.getBitmapFromCache(s);
    }

    public volatile void setImageLoaderRemoteCallback(ImageLoaderRemoteCallback imageloaderremotecallback)
    {
        super.setImageLoaderRemoteCallback(imageloaderremotecallback);
    }

    public volatile void setStorageImage(ImageView imageview, String s, int i, int j)
    {
        super.setStorageImage(imageview, s, i, j);
    }

    public volatile ImageLoaderRemoteCallback getImageLoaderRemoteCallback()
    {
        return super.getImageLoaderRemoteCallback();
    }

    public volatile void setImageFadeIn(boolean flag)
    {
        super.setImageFadeIn(flag);
    }

    public volatile boolean isImageFadeIn()
    {
        return super.isImageFadeIn();
    }

    public volatile void setResourceImage(ImageView imageview, int i, int j, int k)
    {
        super.setResourceImage(imageview, i, j, k);
    }

    private static final String TAG = com/pccw/gzmobile/res/SingleImageLoader.getSimpleName();
    private AbsImageCacheHelper mImageCacheHelper;




}



	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 679 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/