/*jadclipse// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.androidos;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// Referenced classes of package com.pccw.gzmobile.androidos:
//            ScalingThreadPoolExecutor

public class ThreadManager
{
    public static class AtomicIntegerThreadFactory
        implements ThreadFactory
    {

        public Thread newThread(Runnable r)
        {
            Thread thread = new Thread(r, (new StringBuilder(String.valueOf(name))).append("#").append(mCount.getAndIncrement()).toString());
            System.out.println((new StringBuilder("AtomicIntegerThreadFactory.newThread() : ")).append(thread).toString());
            return thread;
        }

        private final AtomicInteger mCount = new AtomicInteger(1);
        private final String name;

        public AtomicIntegerThreadFactory(String name)
        {
            if(name == null || name.equals(""))
                name = "com/pccw/gzmobile/androidos/ThreadManager$AtomicIntegerThreadFactory.getSimpleName()";
            this.name = name;
            System.err.println((new StringBuilder("new AtomicIntegerThreadFactory named ")).append(name).toString());
        }
    }


    private ThreadManager()
    {
    }

    public static ThreadPoolExecutor newExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue)
    {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new AtomicIntegerThreadFactory(name));
    }

    public static ThreadPoolExecutor newExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue, RejectedExecutionHandler handler)
    {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new AtomicIntegerThreadFactory(name), handler);
    }

    public static ThreadPoolExecutor newSingleExecutor(String name)
    {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), new AtomicIntegerThreadFactory(name));
    }

    public static ThreadPoolExecutor newFixedExecutor(String name, int fixedSize)
    {
        return new ThreadPoolExecutor(fixedSize, fixedSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), new AtomicIntegerThreadFactory(name));
    }

    public static ThreadPoolExecutor newCachedExecutor(String name, long keepAliveTime, TimeUnit unit)
    {
        return new ThreadPoolExecutor(0, 2147483647, keepAliveTime, unit, new SynchronousQueue(), new AtomicIntegerThreadFactory(name));
    }

    public static ThreadPoolExecutor newScalingExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit)
    {
        return null;//new ScalingThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, new AtomicIntegerThreadFactory(name));
    }

    public static ThreadPoolExecutor newFIFOExecutor(String name)
    {
        return newSingleExecutor(name);
    }

    public static long getCurrentThreadId()
    {
        return Thread.currentThread().getId();
    }

    public static String getCurrentThreadName()
    {
        return Thread.currentThread().getName();
    }

    public static boolean tryToCancelTask(ThreadPoolExecutor executor, Runnable task, Future future)
    {
        if(task == null && future == null)
            throw new IllegalArgumentException("Runnable and Future can NOT be both null.");
        boolean success = false;
        if(task != null)
            success = executor.remove(task);
        if(!success && future != null)
            success = future.cancel(true);
        return success;
    }

    public static void execute(Runnable task)
    {
        sScalingExecutor.execute(task);
    }

    public static void execute(Runnable task, Object threadFlagKey, Object threadFlagValue)
    {
        updateThreadFlag(threadFlagKey, threadFlagValue);
        sScalingExecutor.execute(task);
    }

    public static Object getThreadFlag(Object threadFlagKey)
    {
        byte abyte0[] = THREAD_FLAG_LOCK;
        JVM INSTR monitorenter ;
        return sThreadFlag.get(threadFlagKey);
        abyte0;
        JVM INSTR monitorexit ;
        throw ;
    }

    public static void updateThreadFlag(Object threadFlagKey, Object threadFlagValue)
    {
        synchronized(THREAD_FLAG_LOCK)
        {
            sThreadFlag.put(threadFlagKey, threadFlagValue);
        }
    }

    public static Object removeThreadFlag(Object threadFlagKey)
    {
        byte abyte0[] = THREAD_FLAG_LOCK;
        JVM INSTR monitorenter ;
        return sThreadFlag.remove(threadFlagKey);
        abyte0;
        JVM INSTR monitorexit ;
        throw ;
    }

    private static final String TAG = com/pccw/gzmobile/androidos/ThreadManager.getSimpleName();
    private static final int CPU_SIZE;
    public static final int CORE_THREAD_SIZE_ON_CPU_SIZE;
    public static final int MAX_THREAD_SIZE_ON_CPU_SIZE;
    private static final ExecutorService sScalingExecutor;
    private static final HashMap sThreadFlag = new HashMap();
    private static final byte THREAD_FLAG_LOCK[] = new byte[0];

    static 
    {
        CPU_SIZE = Runtime.getRuntime().availableProcessors();
        CORE_THREAD_SIZE_ON_CPU_SIZE = CPU_SIZE < 4 ? ((int) (CPU_SIZE < 2 ? 2 : 4)) : 8;
        MAX_THREAD_SIZE_ON_CPU_SIZE = CPU_SIZE < 4 ? ((int) (CPU_SIZE < 2 ? 12 : 20)) : 30;
        sScalingExecutor = newScalingExecutor((new StringBuilder(String.valueOf(TAG))).append("$ScalingPriorityExecutor").toString(), CORE_THREAD_SIZE_ON_CPU_SIZE, MAX_THREAD_SIZE_ON_CPU_SIZE, 30L, TimeUnit.SECONDS);
    }
}



	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 155 ms
	Jad reported messages/errors:
Overlapped try statements detected. Not all exception handlers will be resolved in the method getThreadFlag
Couldn't fully decompile method getThreadFlag
Couldn't resolve all exception handlers in method getThreadFlag
Overlapped try statements detected. Not all exception handlers will be resolved in the method updateThreadFlag
Overlapped try statements detected. Not all exception handlers will be resolved in the method removeThreadFlag
Couldn't fully decompile method removeThreadFlag
Couldn't resolve all exception handlers in method removeThreadFlag
	Exit status: 0
	Caught exceptions:
*/