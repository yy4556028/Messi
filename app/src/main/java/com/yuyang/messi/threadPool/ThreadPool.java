package com.yuyang.messi.threadPool;

import android.os.Process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool {

    private final int CPU_COUNT = Runtime.getRuntime().availableProcessors(); //cup内核数
    private final int DEFAULT_THREAD_COUNT = CPU_COUNT + 1; //默认核心线程数

    private final ExecutorService mExecutorService;
    private static ThreadPool mInstance;

    public static ThreadPool getInstance() {
        if (mInstance == null) {
            synchronized (ThreadPool.class) {
                if (mInstance == null) {
                    mInstance = new ThreadPool();
                }
            }
        }
        return mInstance;
    }

    private ThreadPool() {
        mExecutorService = initNormal();
//        mExecutorService = initOhHttp();
    }

    private ExecutorService initNormal() {
        ThreadFactory mBackgroundThreadFactory = new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);
        PriorityBlockingQueue<Runnable> queue = new PriorityBlockingQueue<>(20);
        return new ThreadPoolExecutor(
                DEFAULT_THREAD_COUNT,
                CPU_COUNT * 2,
                3,
                TimeUnit.SECONDS,
                queue
//                , mBackgroundThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy()
        );
    }

    private ExecutorService initOhHttp() {
        return new ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new SynchronousQueue<>());
    }

    public void execute(Runnable runnable) {
        execute(runnable, 0);
    }

    public void execute(Runnable runnable, int priority) {
        ThreadPool.getInstance().mExecutorService.execute(new PriorityRunnable(runnable, priority));
    }

    public static class PriorityThreadFactory implements ThreadFactory {
        private final AtomicInteger mCount = new AtomicInteger(1);
        private final int mThreadPriority;

        public PriorityThreadFactory(int threadPriority) {
            mThreadPriority = threadPriority;
        }

        @Override
        public Thread newThread(final Runnable runnable) {
            Runnable priorityRunnable = new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(mThreadPriority);
                    runnable.run();
                }
            };
            return new Thread(priorityRunnable, "AsyncThreadTask #" + mCount.getAndIncrement());
        }
    }
}
