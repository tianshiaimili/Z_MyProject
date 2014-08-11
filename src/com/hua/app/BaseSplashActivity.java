/*jadclipse// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   BaseSplashActivity.java

package com.hua.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.pccw.gzmobile.androidos.ThreadManager;
import com.pccw.gzmobile.utils.NetUtils;

// Referenced classes of package com.pccw.gzmobile.app:
//            BaseActivity, WeakHandler

public abstract class BaseSplashActivity extends BaseActivity
{
    private static class InnerStaticHandler extends WeakHandler
    {

        public void handleWeakHandlerMessage(BaseSplashActivity contextObject, Message msg)
        {
            switch(msg.what)
            {
            default:
                break;

            case 1: // '\001'
                Log.w(BaseSplashActivity.TAG, "Static splash enter home page.");
                contextObject.startActivity(contextObject.mSplashSetting.homePageIntent);
                contextObject.finish();
                break;

            case 2: // '\002'
                Log.e(BaseSplashActivity.TAG, "Network unavailable.");
                contextObject.removeAllHandlerMessage();
                if(!contextObject.onSplashTaskFailed(2) && !contextObject.isFinishing())
                    contextObject.showDialog(2);
                break;

            case 5: // '\005'
            case 6: // '\006'
                if(msg.what == 5)
                    Log.e(BaseSplashActivity.TAG, (new StringBuilder("Execute splash task failed : ")).append(msg.obj).toString());
                else
                    Log.e(BaseSplashActivity.TAG, (new StringBuilder("Execute splash task failed : timeout ")).append(contextObject.mSplashSetting.maxDynamicSplashTime).append(" ms.").toString());
                contextObject.removeAllHandlerMessage();
                if(!contextObject.onSplashTaskFailed(5) && !contextObject.isFinishing())
                    contextObject.showDialog(5);
                break;

            case 4: // '\004'
                contextObject.mIsTaskFinished = true;
                // fall through

            case 3: // '\003'
                if(contextObject.mIsTaskFinished)
                {
                    if(System.currentTimeMillis() - contextObject.mStartTime >= 1000L)
                    {
                        if(msg.what == 4)
                            Log.i(BaseSplashActivity.TAG, "Execute splash task success exceed MIN_DYNAMIC_SPLASH_TIME. About to close splash page and show home page.");
                        else
                            Log.i(BaseSplashActivity.TAG, "Execute splash task success within min time and min time reaches. About to close splash page and show home page.");
                        contextObject.removeAllHandlerMessage();
                        contextObject.startActivity(contextObject.mSplashSetting.homePageIntent);
                        contextObject.finish();
                    } else
                    {
                        Log.w(BaseSplashActivity.TAG, "Execute splash task success within MIN_DYNAMIC_SPLASH_TIME. Wait till MIN_DYNAMIC_SPLASH_TIME reaches.");
                    }
                } else
                {
                    Log.w(BaseSplashActivity.TAG, "Splash task not yet finish at MIN_DYNAMIC_SPLASH_TIME. Wait till task done or timeout.");
                }
                break;
            }
        }

        public volatile void handleWeakHandlerMessage(Object obj, Message message)
        {
            handleWeakHandlerMessage((BaseSplashActivity)obj, message);
        }

        InnerStaticHandler(BaseSplashActivity act)
        {
            super(act);
        }
    }

    public static class SplashSetting
    {

        public boolean isDynamicSplashTime()
        {
            return isDynamicSplashTime;
        }

        public void setHomePageIntent(Intent intent)
        {
            homePageIntent = intent;
        }

        public void setMaxStaticSplashTime(int maxStaticSplashTime)
        {
            if(maxStaticSplashTime > 5000)
                maxStaticSplashTime = 5000;
            else
            if(maxStaticSplashTime < 1000)
                maxStaticSplashTime = 1000;
            this.maxStaticSplashTime = maxStaticSplashTime;
        }

        public void setMaxDynamicSplashTime(int maxDynamicSplashTime)
        {
            if(maxDynamicSplashTime > 2147483647)
                maxDynamicSplashTime = 2147483647;
            else
            if(maxDynamicSplashTime < 1000)
                maxDynamicSplashTime = 1000;
            this.maxDynamicSplashTime = maxDynamicSplashTime;
        }

        public void setSplashTask(Runnable splashTask, boolean runOnUiThread)
        {
            this.splashTask = splashTask;
            isSplashTaskRunOnUiThread = runOnUiThread;
        }

        public void setNetworkUnavailableTitle(String splashTaskErrorTitle)
        {
            networkUnavailableTitle = splashTaskErrorTitle;
        }

        public void setNetworkUnavailableMsg(String networkUnavailableMsg)
        {
            this.networkUnavailableMsg = networkUnavailableMsg;
        }

        public void setSplashTaskErrorTitle(String splashTaskErrorTitle)
        {
            this.splashTaskErrorTitle = splashTaskErrorTitle;
        }

        public void setSplashTaskErrorMsg(String splashTaskErrorMsg)
        {
            this.splashTaskErrorMsg = splashTaskErrorMsg;
        }

        public void setSplashDialogPositiveButtonText(String positiveButtonText)
        {
            splashDialogPositiveButtonText = positiveButtonText;
        }

        public void setIgnoreNetworkConnectivity(boolean ignoreNetworkConnectivity)
        {
            isIgnoreNetworkConnectivity = ignoreNetworkConnectivity;
        }

        private static final int MIN_DYNAMIC_SPLASH_TIME = 1000;
        private static final int MAX_DYNAMIC_SPLASH_TIME = 2147483647;
        private static final int MIN_STATIC_SPLASH_TIME = 1000;
        private static final int MAX_STATIC_SPLASH_TIME = 5000;
        private int maxDynamicSplashTime;
        private int maxStaticSplashTime;
        private Intent homePageIntent;
        private boolean isDynamicSplashTime;
        private Runnable splashTask;
        private boolean isSplashTaskRunOnUiThread;
        private String networkUnavailableTitle;
        private String networkUnavailableMsg;
        private String splashTaskErrorTitle;
        private String splashTaskErrorMsg;
        private String splashDialogPositiveButtonText;
        private boolean isIgnoreNetworkConnectivity;













        public SplashSetting(boolean isDynamicSplashTime)
        {
            maxDynamicSplashTime = 2147483647;
            maxStaticSplashTime = 5000;
            networkUnavailableTitle = "Sorry";
            networkUnavailableMsg = "System busy. Please try again later.";
            splashTaskErrorTitle = "Sorry";
            splashTaskErrorMsg = "System busy. Please try again later.";
            splashDialogPositiveButtonText = "Confirm";
            this.isDynamicSplashTime = isDynamicSplashTime;
        }
    }


    public BaseSplashActivity()
    {
        mHandler = new InnerStaticHandler(this);
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mSplashSetting = viewWillCreate();
        if(mSplashSetting == null)
            throw new RuntimeException("SplashSetting should initialize in viewWillCreate().");
        super.setContentView(getSplashContent());
        mStartTime = System.currentTimeMillis();
        if(mSplashSetting.isDynamicSplashTime)
        {
            boolean connected = mSplashSetting.isIgnoreNetworkConnectivity ? true : NetUtils.checkConnectivity(this);
            if(connected)
            {
                mHandler.sendEmptyMessageDelayed(3, 1000L);
                mHandler.sendEmptyMessageDelayed(6, mSplashSetting.maxDynamicSplashTime);
                Runnable splashTask = new Runnable() {

                    public void run()
                    {
                        try
                        {
                            Log.d(BaseSplashActivity.TAG, "Begin splash task.");
                            mSplashSetting.splashTask.run();
                            Log.d(BaseSplashActivity.TAG, "Finish splash task.");
                        }
                        catch(Exception e)
                        {
                            mHandler.obtainMessage(5, e.toString()).sendToTarget();
                        }
                    }

                    final BaseSplashActivity this$0;

            
            {
                this$0 = BaseSplashActivity.this;
                super();
            }
                }
;
                if(mSplashSetting.isSplashTaskRunOnUiThread)
                    splashTask.run();
                else
                    ThreadManager.execute(splashTask);
            } else
            {
                mHandler.sendEmptyMessage(2);
            }
        } else
        {
            Log.d(TAG, (new StringBuilder("No need to do splash task. Show splash page in ")).append(mSplashSetting.maxStaticSplashTime).append(" ms.").toString());
            mHandler.sendEmptyMessageDelayed(1, mSplashSetting.maxStaticSplashTime);
        }
        Log.w(TAG, (new StringBuilder(String.valueOf(TAG))).append(" onCreate() completed.").toString());
    }

    public final void setContentView(int layoutResID)
    {
        String errorInfo = "You should implement getSplashContent() to set window feature and return content view id rather than calling setContentView() directly.";
        throw new RuntimeException(errorInfo);
    }

    protected abstract SplashSetting viewWillCreate();

    protected abstract int getSplashContent();

    protected boolean onSplashTaskFailed(int reason)
    {
        return false;
    }

    public void setSplashTaskSuccessful()
    {
        Log.i(TAG, "setSplashTaskSuccessful()");
        mHandler.sendEmptyMessage(4);
    }

    public void setSplashTaskFailded()
    {
        Log.w(TAG, "setSplashTaskFailded()");
        mHandler.obtainMessage(5, "setSplashTaskFailded().").sendToTarget();
    }

    protected Dialog onCreateDialog(int id)
    {
        switch(id)
        {
        case 2: // '\002'
            return (new android.app.AlertDialog.Builder(this)).setTitle(mSplashSetting.networkUnavailableTitle).setMessage(mSplashSetting.networkUnavailableMsg).setCancelable(false).setPositiveButton(mSplashSetting.splashDialogPositiveButtonText, new android.content.DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which)
                {
                    finish();
                }

                final BaseSplashActivity this$0;

            
            {
                this$0 = BaseSplashActivity.this;
                super();
            }
            }
).create();

        case 5: // '\005'
            return (new android.app.AlertDialog.Builder(this)).setTitle(mSplashSetting.splashTaskErrorTitle).setMessage(mSplashSetting.splashTaskErrorMsg).setCancelable(false).setPositiveButton(mSplashSetting.splashDialogPositiveButtonText, new android.content.DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which)
                {
                    finish();
                }

                final BaseSplashActivity this$0;

            
            {
                this$0 = BaseSplashActivity.this;
                super();
            }
            }
).create();

        case 3: // '\003'
        case 4: // '\004'
        default:
            return null;
        }
    }

    private void removeAllHandlerMessage()
    {
        mHandler.removeMessages(1);
        mHandler.removeMessages(2);
        mHandler.removeMessages(3);
        mHandler.removeMessages(4);
        mHandler.removeMessages(5);
        mHandler.removeMessages(6);
        mHandler.stopHandlingMessage();
    }

    protected void onDestroy()
    {
        super.onDestroy();
        removeAllHandlerMessage();
        Log.w(TAG, (new StringBuilder(String.valueOf(TAG))).append(" onDestroy() completed.").toString());
    }

    private static final String TAG = com/pccw/gzmobile/app/BaseSplashActivity.getSimpleName();
    private static final int HANDLER_STATIC_SPLASH = 1;
    private static final int HANDLER_NETWORK_UNAVAILABLE = 2;
    private static final int HANDLER_CHECK_SPLASH_TASK_FINISHED_WITHIN_MIN_DYNAMIC_SPLASH_TIME = 3;
    private static final int HANDLER_SPLASH_TASK_FINISHED = 4;
    private static final int HANDLER_SPLASH_TASK_ERROR = 5;
    private static final int HANDLER_SPLASH_TASK_TIMEOUT = 6;
    public static final int ERROR_REASON_NETWORK_UNAVAILABLE = 2;
    public static final int ERROR_REASON_SPLASH_TASK_ERROR = 5;
    private InnerStaticHandler mHandler;
    private SplashSetting mSplashSetting;
    private boolean mIsTaskFinished;
    private long mStartTime;








}



	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 515 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/