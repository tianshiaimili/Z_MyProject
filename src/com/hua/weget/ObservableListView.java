/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.weget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class ObservableListView extends ListView
{
    public static interface OnScrollChangedListener
    {

        public abstract void onScrollChanged(ObservableListView observablelistview, int i, int j, int k, int l);
    }


    public ObservableListView(Context context)
    {
        super(context);
        mInterceptTouchInterested = null;
        mTouchInterested = null;
    }

    public ObservableListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mInterceptTouchInterested = null;
        mTouchInterested = null;
    }

    public ObservableListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mInterceptTouchInterested = null;
        mTouchInterested = null;
    }

    public void setOnScrollChangedListener(OnScrollChangedListener l)
    {
        mOnScrollChangedListener = l;
    }

    protected void onScrollChanged(int x, int y, int oldx, int oldy)
    {
        super.onScrollChanged(x, y, oldx, oldy);
        if(mOnScrollChangedListener != null)
            mOnScrollChangedListener.onScrollChanged(this, x, y, oldx, oldy);
    }

    public void setInterceptTouchInterested(boolean interested)
    {
        mInterceptTouchInterested = Boolean.valueOf(interested);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if(mInterceptTouchInterested != null && !mInterceptTouchInterested.booleanValue())
            return false;
        else
            return super.onInterceptTouchEvent(ev);
    }

    public void setTouchInterested(boolean interested)
    {
        mTouchInterested = Boolean.valueOf(interested);
    }

    public boolean onTouchEvent(MotionEvent ev)
    {
        Boolean touchInterested = null;
        boolean superRetval;
        switch(ev.getAction())
        {
        case MotionEvent.ACTION_DOWN: // '\0'
            touchInterested = mTouchInterested;
            // fall through

        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
        default:
            superRetval = super.onTouchEvent(ev);
            break;
        }
        if(touchInterested == null)
            return superRetval;
        else
            return touchInterested.booleanValue();
    }

    private OnScrollChangedListener mOnScrollChangedListener;
    private Boolean mInterceptTouchInterested;
    private Boolean mTouchInterested;
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 329 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/