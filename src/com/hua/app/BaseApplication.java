package com.hua.app;

/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.


import android.app.Application;
import android.util.Log;
import java.io.*;

public class BaseApplication extends Application
{

	private boolean isDownload;
	
    public BaseApplication()
    {
    }

    public void onCreate()
    {
        super.onCreate();
//        Log.e(TAG, (new StringBuilder(String.valueOf(com/pccw/gzmobile/app/BaseApplication.getName()))).append(" onCreate().").toString());
        initFilesPath();
        isDownload = false;
    }

    private void initFilesPath()
    {/*
        filesDir = getFilesDir().getAbsolutePath();
        if(filesDir == null || filesDir.length() <= 0 || filesDir.substring(filesDir.length() - 1).equals("/")) goto _L2; else goto _L1
_L1:
        filesDir;
        JVM INSTR new #29  <Class StringBuilder>;
        JVM INSTR dup_x1 ;
        JVM INSTR swap ;
        String.valueOf();
        StringBuilder();
        "/";
        append();
        toString();
        filesDir;
_L2:
        Log.w(TAG, (new StringBuilder("Init files dir ")).append(filesDir).toString());
        return;*/
    }

    public static String getFilesPath()
    {
        return filesDir;
    }

    public static InputStream openFilesInputStream(String name)
        throws FileNotFoundException
    {
        return new FileInputStream((new StringBuilder(String.valueOf(getFilesPath()))).append(name).toString());
    }

//    private static final String TAG = com/pccw/gzmobile/app/BaseApplication.getSimpleName();
    private static String filesDir;

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}
    
    
}

