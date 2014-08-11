/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.app;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import java.lang.ref.WeakReference;

public abstract class WeakHandler<VODFragment> extends Handler
{


    public abstract void handleWeakHandlerMessage(VODFragment obj, Message message);

    private static final String TAG = "com/pccw/gzmobile/app/WeakHandler.getSimpleName()";
    private final WeakReference mContextObject;
    private boolean mstopped;
	
    public WeakHandler(Object contextObject)
    {
    	/**
    	 * WeakReference<T>：弱引用-->随时可能会被垃圾回收器回收，不一定要等到虚拟机内存不足时才强制回收。要获取对象时，同样可以调用get方法。
    	 */
        mContextObject = new WeakReference(contextObject);
    }

    public void stopHandlingMessage()
    {
        mstopped = true;
    }

    public void restartHandlingMessage()
    {
        mstopped = false;
    }

    public final void handleMessage(Message msg)
    {
        if(mstopped)
        {
            Log.v(TAG, "Handler stop handling message.");
            return;
        }
        Object obj = mContextObject.get();
        if(obj != null)
        {
            if(obj instanceof Fragment)
            {
                Fragment f = (Fragment)obj;
                if(f.getActivity() == null)
                    Log.e(TAG, (new StringBuilder(String.valueOf(f.getClass().getSimpleName()))).append(".getActivity()=null.").toString());
                if(f.getView() == null)
                    Log.e(TAG, (new StringBuilder(String.valueOf(f.getClass().getSimpleName()))).append(".getView()=null.").toString());
            }
            handleWeakHandlerMessage((VODFragment)obj, msg);
        }
    }


}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 293 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/