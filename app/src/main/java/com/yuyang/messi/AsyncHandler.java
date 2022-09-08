package com.yuyang.messi;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class AsyncHandler extends Handler {
    private static volatile AsyncHandler sInstance;

    private AsyncHandler() {
        super(createLooper());
    }

    private static Looper createLooper() {
        HandlerThread thread = new HandlerThread("AsyncHandler");
        thread.start();
        return thread.getLooper();
    }

    public static AsyncHandler getInstance() {
        if (sInstance == null) {
            synchronized (AsyncHandler.class) {
                if (sInstance == null) {
                    sInstance = new AsyncHandler();
                }
            }
        }
        return sInstance;
    }
}

