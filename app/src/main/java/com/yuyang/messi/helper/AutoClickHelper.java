package com.yuyang.messi.helper;

import com.yuyang.messi.threadPool.ThreadPool;

public class AutoClickHelper {

    public void autoClickPos(final float x1, final float y1, final float x2, final float y2) {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String[] order = {"input", "swipe", "" + x1, "" + y1, "" + x2, "" + y2,};
                try {
                    new ProcessBuilder(order).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
