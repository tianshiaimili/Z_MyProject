/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.utils;

import android.os.*;
import android.util.Log;

// Referenced classes of package com.pccw.gzmobile.androidos:
//            CustomHandlerThread

public class HandlerTimer
{

    public HandlerTimer(boolean runOnUiThread)
    {
        mRunning = false;
        mQuit = false;
        if(runOnUiThread)
        {
            mHandler = new Handler(Looper.getMainLooper());
        } else
        {
            mHandlerThread = new CustomHandlerThread((new StringBuilder(String.valueOf(TAG))).append("$HandlerThread").toString());
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }
        Log.d(TAG, (new StringBuilder("Handler associate with thread ")).append(mHandler.getLooper().getThread().getName()).toString());
        Log.d(TAG, "HandlerTimer is ready.");
    }

    public HandlerTimer(Handler handler)
    {
        mRunning = false;
        mQuit = false;
        mHandler = handler;
        Log.d(TAG, (new StringBuilder("Handler associate with thread ")).append(mHandler.getLooper().getThread().getName()).toString());
        Log.d(TAG, "HandlerTimer is ready.");
    }

    /**
     * 检查是否已关闭 
     * @param task 子线程
     * @param delay  延迟的时间
     * @param interval 间隔时间
     */
    private void checkTimer(Runnable task, long delay, long interval)
    {
        if(mQuit)
            throw new IllegalStateException("Timer has been quit. Can not run task in a quit timer.");
        if(task == null)
            throw new IllegalArgumentException("Timer task can not be null.");
        if(delay < 0L)
            throw new IllegalArgumentException("delay must be >= 0.");
        if(interval <= 0L)
            throw new IllegalArgumentException("interval must be > 0.");
        else
            return;
    }

    public synchronized void scheduleRepeatExecution(Runnable task, long delay, long interval)
        throws IllegalStateException, IllegalArgumentException
    {
        Log.d(TAG, (new StringBuilder("scheduleRepeatExecution(), delay = ")).append(delay).append(", interval = ").append(interval).append("ms").toString());
        checkTimer(task, delay, interval);
        if(mRunning)
            throw new IllegalStateException("Timer is running a repeating execution. You should stop it before schedule a new one.");
        mRepeatTask = task;
        mInterval = interval;
        mRunning = true;
        if(delay == 0L)
            mHandler.post(mOnTick);
        else
            mHandler.postDelayed(mOnTick, delay);
    }

    public synchronized void cancelRepeatExecution()
    {
        Log.d(TAG, "cancelRepeatExecution()");
        mHandler.removeCallbacks(mOnTick);
        mRunning = false;
    }

    public synchronized boolean isRepeatExecutionRunning()
    {
        return mRunning;
    }

    public void scheduleSingleExecution(Runnable task, long delay)
        throws IllegalStateException, IllegalArgumentException
    {
        Log.d(TAG, (new StringBuilder("scheduleSingleExecution(), delay = ")).append(delay).toString());
        checkTimer(task, delay, 2147483647L);
        if(delay == 0L)
            mHandler.post(task);
        else
            mHandler.postDelayed(task, delay);
    }

    public void cancelSingleExecution(Runnable task)
    {
        Log.d(TAG, "cancelSingleExecution()");
        mHandler.removeCallbacks(task);
    }

    public void cancelAll()
    {
        Log.d(TAG, "cancelAll()");
        mHandler.removeCallbacksAndMessages(null);
    }

    public void quit()
    {
        cancelAll();
        if(mHandlerThread != null)
        {
            mHandlerThread.quit();
            mHandlerThread.interrupt();
            mHandlerThread = null;
        }
        mQuit = true;
        Log.w(TAG, "Quit timer.");
    }

    public boolean isQuit()
    {
        return mQuit;
    }

    private static final String TAG =" com/pccw/gzmobile/androidos/HandlerTimer.getSimpleName()";
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Runnable mRepeatTask;
    private long mInterval;
    private boolean mRunning;
    private boolean mQuit;
    private Runnable mOnTick = new Runnable() {

        public void run()
        {
            mRepeatTask.run();
            if(mRunning && !mQuit)
                mHandler.postDelayed(mOnTick, mInterval);
        }

//        final HandlerTimer this$0;
//
//            
//            {
//                this$0 = HandlerTimer.this;
////                super();
//            }
    }
;







}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 72 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/