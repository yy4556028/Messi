package com.yuyang.lib_base;

import android.os.Handler;
import android.os.Looper;

/**
 * Convenient handler for some main thread tasks.
 */
public class MainHandler extends Handler {
    private static volatile MainHandler sInstance;

    private MainHandler() {
        super(Looper.getMainLooper());
    }

    public static MainHandler getInstance() {
        if (sInstance == null) {
            synchronized (MainHandler.class) {
                if (sInstance == null) {
                    sInstance = new MainHandler();
                }
            }
        }
        return sInstance;
    }
}
