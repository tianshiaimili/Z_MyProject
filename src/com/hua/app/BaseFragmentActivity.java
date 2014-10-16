/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   BaseFragmentActivity.java

package com.hua.app;

import java.util.Locale;

import android.content.Context;
import android.os.Bundle;

import com.hua.wedget.slideingactivity.SlidingActivity;

// Referenced classes of package com.pccw.gzmobile.app:
//            AppLocaleAide, AppProgressDialogAide

public class BaseFragmentActivity extends SlidingActivity
    implements AppLocaleAide.AppLocaleAideSupport
{

	private AppLocaleAide mAppLocaleAide;
    private AppProgressDialogAide mAppDialogAide;
	
    public BaseFragmentActivity()
    {
        mAppLocaleAide = new AppLocaleAide(this);
    }

    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        mAppDialogAide = new AppProgressDialogAide(this);
        /**
         * �ı���������
         */
        mAppLocaleAide.syncLocaleWithAppLocaleOnCreate(this);
    }

    protected void onResume()
    {
        super.onResume();
        mAppLocaleAide.syncLocaleWithAppLocaleOnResume(this);
    }

    protected void onDestroy()
    {
        super.onDestroy();
        mAppDialogAide.destroyProgressDialog();
    }

    public void setProgressDialogMessage(CharSequence message)
    {
        mAppDialogAide.setProgressDialogMessage(message);
    }

    public void setProgressDialogDismissListener(AppProgressDialogAide.ProgressDialogDismissListener listener)
    {
        mAppDialogAide.setProgressDialogDismissListener(listener);
    }

    public void setProgressDialogCancelListener(AppProgressDialogAide.ProgressDialogCancelListener listener)
    {
        mAppDialogAide.setProgressDialogCancelListener(listener);
    }

    public final void showProgressDialog(boolean cancelable)
    {
        mAppDialogAide.showProgressDialog(cancelable);
    }

    public final void showProgressDialog(Context context, boolean cancelable)
    {
        mAppDialogAide.showProgressDialog(context, cancelable);
    }

    public final void dismissProgressDialog()
    {
        mAppDialogAide.dismissProgressDialog();
    }

    public void setProgressDialogCancelTag(Object cancelTag)
    {
        mAppDialogAide.setProgressDialogCancelTag(cancelTag);
    }

    public final boolean progressDialogHasCanceled(Object cancelTag)
    {
        return mAppDialogAide.progressDialogHasCanceled(cancelTag);
    }

    public void setAppLocale(Context context, Locale newLocale)
    {
        mAppLocaleAide.setAppLocale(context, newLocale);
    }

    public void onLocaleChanged()
    {
    }

    
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 474 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/