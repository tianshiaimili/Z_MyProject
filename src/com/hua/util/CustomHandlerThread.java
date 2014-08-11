/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.util;

import android.os.HandlerThread;
import android.util.Log;

public class CustomHandlerThread extends HandlerThread
{

    public CustomHandlerThread(String name)
    {
        super(name);
    }

    public CustomHandlerThread(String name, int priority)
    {
        super(name, priority);
    }

    protected void onLooperPrepared()
    {
        super.onLooperPrepared();
        Log.w(TAG, "HandlerThread onLooperPrepared().");
    }

    public synchronized void start()
    {
        Log.w(TAG, "HandlerThread calls start().");
        super.start();
    }

    public void run()
    {
        Log.w(TAG, "HandlerThread start running.");
        super.run();
    }

    public boolean quit()
    {
        boolean retval = super.quit();
        Log.w(TAG, "HandlerThread quit.");
        return retval;
    }

    private static final String TAG =" com/pccw/gzmobile/androidos/CustomHandlerThread.getSimpleName()";

}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 613 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/