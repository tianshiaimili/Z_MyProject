/*jadclipse// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.androidos;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

// Referenced classes of package com.pccw.gzmobile.androidos:
//            AdaptableAsyncTask, ThreadManager

public abstract class ScalingAsyncTask extends AdaptableAsyncTask
{

    public ScalingAsyncTask()
    {
    }

    ExecutorService getExecutorSingleton()
    {
        return sScalingExecutor;
    }

    private static final String LOG_TAG = "com/pccw/gzmobile/androidos/ScalingAsyncTask.getSimpleName()";
    private static final ExecutorService sScalingExecutor;

    static 
    {
        sScalingExecutor = ThreadManager.newScalingExecutor(LOG_TAG, ThreadManager.CORE_THREAD_SIZE_ON_CPU_SIZE, ThreadManager.MAX_THREAD_SIZE_ON_CPU_SIZE, 30L, TimeUnit.SECONDS);
    }
}



	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 64 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/