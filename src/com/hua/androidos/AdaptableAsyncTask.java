/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.androidos;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

abstract class AdaptableAsyncTask
{
    private static class AsyncTaskResult
    {

        final AdaptableAsyncTask mTask;
        final Object mData[];

         AsyncTaskResult(AdaptableAsyncTask task, Object data[])
        {
            mTask = task;
            mData = data;
        }
    }

    private static class InternalHandler extends Handler
    {

        public void handleMessage(Message msg)
        {
            AsyncTaskResult result = (AsyncTaskResult)msg.obj;
            switch(msg.what)
            {
            case 1: // '\001'
                result.mTask.finish(result.mData[0]);
                break;

            case 2: // '\002'
                result.mTask.onProgressUpdate(result.mData);
                break;

            case 3: // '\003'
                result.mTask.onCancelled();
                break;
            }
        }

        private InternalHandler()
        {
        }

        InternalHandler(InternalHandler internalhandler)
        {
            this();
        }
    }

    public static final class Status 
    {

        public static Status[] values()
        {
            Status astatus[];
            int i;
            Status astatus1[];
            System.arraycopy(astatus = ENUM$VALUES, 0, astatus1 = new Status[i = astatus.length], 0, i);
            return astatus1;
        }

        public static Status valueOf(String s)
        {
            return null;//(Status)Enum.valueOf(AdaptableAsyncTask.Status.class, s);
        }

        public static final Status PENDING;
        public static final Status RUNNING;
        public static final Status FINISHED;
        private static final Status ENUM$VALUES[];

        static 
        {
            PENDING = new Status("PENDING", 0);
            RUNNING = new Status("RUNNING", 1);
            FINISHED = new Status("FINISHED", 2);
            ENUM$VALUES = (new Status[] {
                PENDING, RUNNING, FINISHED
            });
        }

        private Status(String s, int i)
        {
//            super(s, i);
        }
    }

    private static abstract class WorkerRunnable
        implements Callable
    {

        Object mParams[];

        private WorkerRunnable()
        {
        }

        WorkerRunnable(WorkerRunnable workerrunnable)
        {
            this();
        }
    }


    abstract ExecutorService getExecutorSingleton();

    public AdaptableAsyncTask()
    {
        mStatus = Status.PENDING;
        mFuture = new FutureTask(mWorker) {

            protected void done()
            {
                Object result = null;
                Message message;
                try
                {
                    result = get();
                }
                catch(InterruptedException e)
                {
                    Log.w(AdaptableAsyncTask.LOG_TAG, e);
                }
                catch(ExecutionException e)
                {
                    throw new RuntimeException("An error occured while executing doInBackground()", e.getCause());
                }
                catch(CancellationException e)
                {
                    message = AdaptableAsyncTask.sHandler.obtainMessage(3, new AsyncTaskResult(AdaptableAsyncTask.this, null));
                    message.sendToTarget();
                    return;
                }
                catch(Throwable t)
                {
                    throw new RuntimeException("An error occured while executing doInBackground()", t);
                }
                message = AdaptableAsyncTask.sHandler.obtainMessage(1, new AsyncTaskResult(AdaptableAsyncTask.this, new Object[] {
                    result
                }));
                message.sendToTarget();
            }

            final AdaptableAsyncTask this$0;

            
            {
                this$0 = AdaptableAsyncTask.this;
//                super($anonymous0);
            }
        }
;
    }

    public final Status getStatus()
    {
        return mStatus;
    }

    protected  abstract Object doInBackground(Object aobj[]);

    protected void onPreExecute()
    {
    }

    protected void onPostExecute(Object obj)
    {
    }

    protected  void onProgressUpdate(Object aobj[])
    {
    }

    protected void onCancelled()
    {
    }

    public final boolean isCancelled()
    {
        return mFuture.isCancelled();
    }

    public final boolean cancel(boolean mayInterruptIfRunning)
    {
        return mFuture.cancel(mayInterruptIfRunning);
    }

    public final Object get()
        throws InterruptedException, ExecutionException
    {
        return mFuture.get();
    }

    public final Object get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException
    {
        return mFuture.get(timeout, unit);
    }

    public final  AdaptableAsyncTask execute(Object params[])
    {
        if(mStatus != Status.PENDING)
            switch(0)
            {
            case 2: // '\002'
                throw new IllegalStateException("Cannot execute task: the task is already running.");

            case 3: // '\003'
                throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
            }
        mStatus = Status.RUNNING;
        onPreExecute();
        mWorker.mParams = params;
        getExecutorSingleton().execute(mFuture);
        return this;
    }

    protected final  void publishProgress(Object values[])
    {
        sHandler.obtainMessage(2, new AsyncTaskResult(this, values)).sendToTarget();
    }

    private void finish(Object result)
    {
        if(isCancelled())
            result = null;
        onPostExecute(result);
        mStatus = Status.FINISHED;
    }

   /* static int[] $SWITCH_TABLE$com$pccw$gzmobile$androidos$AdaptableAsyncTask$Status()
    {
//        $SWITCH_TABLE$com$pccw$gzmobile$androidos$AdaptableAsyncTask$Status;
        if($SWITCH_TABLE$com$pccw$gzmobile$androidos$AdaptableAsyncTask$Status == null) goto _L2; else goto _L1
_L1:
        return;
_L2:
        JVM INSTR pop ;
        int ai[] = new int[Status.values().length];
        try
        {
            ai[Status.FINISHED.ordinal()] = 3;
        }
        catch(NoSuchFieldError _ex) { }
        try
        {
            ai[Status.PENDING.ordinal()] = 1;
        }
        catch(NoSuchFieldError _ex) { }
        try
        {
            ai[Status.RUNNING.ordinal()] = 2;
        }
        catch(NoSuchFieldError _ex) { }
        return $SWITCH_TABLE$com$pccw$gzmobile$androidos$AdaptableAsyncTask$Status = ai;
    }*/

    private static final String LOG_TAG = "com/pccw/gzmobile/androidos/AdaptableAsyncTask.getSimpleName()";
    private static final int MESSAGE_POST_RESULT = 1;
    private static final int MESSAGE_POST_PROGRESS = 2;
    private static final int MESSAGE_POST_CANCEL = 3;
    private static final InternalHandler sHandler = new InternalHandler(null);
    private final WorkerRunnable mWorker = new WorkerRunnable() {

        public Object call()
            throws Exception
        {
            Process.setThreadPriority(10);
            Log.v(AdaptableAsyncTask.LOG_TAG, (new StringBuilder("Run task in thread ")).append(Thread.currentThread().getName()).toString());
            return doInBackground(mParams);
        }

        final AdaptableAsyncTask this$0;

            
            {
                this$0 = AdaptableAsyncTask.this;
//                super(null);
            }
    }
;
    private final FutureTask mFuture;
    private volatile Status mStatus;
    private static int $SWITCH_TABLE$com$pccw$gzmobile$androidos$AdaptableAsyncTask$Status[];




}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 35 ms
	Jad reported messages/errors:
Couldn't fully decompile method $SWITCH_TABLE$com$pccw$gzmobile$androidos$AdaptableAsyncTask$Status
	Exit status: 0
	Caught exceptions:
*/