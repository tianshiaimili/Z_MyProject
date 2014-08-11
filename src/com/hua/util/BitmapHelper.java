/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.*;

public class BitmapHelper
{

    private BitmapHelper()
    {
    }

    public static void writeBitmapToFile(String filename, Bitmap bitmap, android.graphics.Bitmap.CompressFormat format, int quality)
    {
        BufferedOutputStream out;
        File file;
        out = null;
        file = new File(filename);
        try
        {
            out = new BufferedOutputStream(new FileOutputStream(file), 8192);
            bitmap.compress(format, quality, out);
            out.flush();
            Log.d(TAG, "writeBitmapToFile() success.");
//            break MISSING_BLOCK_LABEL_124;
        }
        catch(Exception e)
        {
            Log.e(TAG, (new StringBuilder("saveImage failed : ")).append(e).toString());
        }
        if(out != null)
            try
            {
                out.close();
            }
            catch(IOException ioexception) { }
//        break MISSING_BLOCK_LABEL_139;
        Exception exception;
//        exception;
        if(out != null)
            try
            {
                out.close();
            }
            catch(IOException ioexception1) { }
//        throw exception;
        if(out != null)
            try
            {
                out.close();
            }
            catch(IOException ioexception2) { }
    }

    public static Bitmap decodeBitmap(Object toBeDecode, Resources res, android.graphics.BitmapFactory.Options opts)
    {
        if(toBeDecode instanceof byte[])
        {
            byte data[] = (byte[])toBeDecode;
            return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        }
        if(toBeDecode instanceof String)
            return BitmapFactory.decodeFile((String)toBeDecode, opts);
        if(toBeDecode instanceof InputStream)
            return BitmapFactory.decodeStream((InputStream)toBeDecode, null, opts);
        if(toBeDecode instanceof Integer)
            return BitmapFactory.decodeResource(res, ((Integer)toBeDecode).intValue(), opts);
        try
        {
            if(toBeDecode instanceof FileDescriptor)
                return BitmapFactory.decodeFileDescriptor((FileDescriptor)toBeDecode, null, opts);
        }
        catch(Exception e)
        {
            Log.w(TAG, (new StringBuilder(String.valueOf(TAG))).append(".decodeBitmap() failed with toBeDecode : ").append(toBeDecode).toString(), e);
        }
        catch(OutOfMemoryError oom)
        {
            Log.e(TAG, (new StringBuilder(String.valueOf(TAG))).append(".decodeBitmap() failed with OutOfMemoryError.").toString(), oom);
        }
        return null;
    }

    public static android.graphics.BitmapFactory.Options decodeBounds(Object toBeDecode, Resources res)
    {
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decodeBitmap(toBeDecode, res, options);
        return options;
    }

    public static int calculateInSampleSize(android.graphics.BitmapFactory.Options options, int reqMinWidth)
    {
        int inSampleSize = 1;
        int rawHeight = options.outHeight;
        int rawWidth = options.outWidth;
        if(reqMinWidth > 0 && rawHeight > 0 && rawWidth > 0)
        {
            int reqMinHeight = (reqMinWidth * rawHeight) / rawWidth;
            if(rawHeight > reqMinHeight || rawWidth > reqMinWidth)
                if(rawWidth > rawHeight)
                    inSampleSize = Math.round((float)rawHeight / (float)reqMinHeight);
                else
                    inSampleSize = Math.round((float)rawWidth / (float)reqMinWidth);
        } else
        {
            Log.i(TAG, (new StringBuilder("Calculate inSampleSize failed: rawWidth = ")).append(rawWidth).append(", rawHeight = ").append(rawHeight).append(", reqMinWidth = ").append(reqMinWidth).toString());
        }
        return inSampleSize;
    }

    public static Bitmap decodeScaledDownBitmap(Object toBeDecode, Resources res, int reqMinWidth, android.graphics.BitmapFactory.Options opts)
    {
        if(reqMinWidth <= 0)
            return decodeBitmap(toBeDecode, res, opts);
        android.graphics.BitmapFactory.Options options = decodeBounds(toBeDecode, res);
        if(toBeDecode instanceof InputStream)
            try
            {
                ((InputStream)toBeDecode).reset();
            }
            catch(IOException e)
            {
                Log.w(TAG, (new StringBuilder("decodeScaledDownBitmap() reset InputStream failed : ")).append(e).toString());
            }
        int inSampleSize = calculateInSampleSize(options, reqMinWidth);
        if(opts != null)
            options = opts;
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = decodeBitmap(toBeDecode, res, options);
        if(toBeDecode instanceof InputStream)
            try
            {
                ((InputStream)toBeDecode).close();
            }
            catch(IOException e)
            {
                Log.w(TAG, (new StringBuilder("decodeScaledDownBitmap() close InputStream failed : ")).append(e).toString());
            }
        return bitmap;
    }

    private static final String TAG = "com/pccw/gzmobile/res/BitmapHelper.getSimpleName()";
    private static final int IO_BUFFER_SIZE = 8192;

}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 28 ms
	Jad reported messages/errors:
Couldn't fully decompile method writeBitmapToFile
Couldn't resolve all exception handlers in method writeBitmapToFile
Couldn't resolve all exception handlers in method decodeBitmap
	Exit status: 0
	Caught exceptions:
*/