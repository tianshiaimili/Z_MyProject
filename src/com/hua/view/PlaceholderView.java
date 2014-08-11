/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PlaceholderView extends View
{

    public PlaceholderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public PlaceholderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PlaceholderView(Context context)
    {
        super(context);
    }

    public PlaceholderView(Context context, int width, int height)
    {
        super(context);
        setLayoutParams(new android.view.ViewGroup.LayoutParams(width, height));
    }
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 441 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/