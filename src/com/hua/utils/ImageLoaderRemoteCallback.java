/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface ImageLoaderRemoteCallback
{

    public abstract Bitmap onBitmapDecoded(String s, Bitmap bitmap);

    public abstract void onDownloadFailed(String s, ImageView imageview);
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 108 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/