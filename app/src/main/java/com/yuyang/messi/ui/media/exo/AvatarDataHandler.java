package com.yuyang.messi.ui.media.exo;

import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;

import okio.ByteString;

public class AvatarDataHandler {

    private static final AvatarDataHandler INSTANCE = new AvatarDataHandler();
    private final ConcurrentLinkedQueue<ByteString> dataQueue = new ConcurrentLinkedQueue<>();


    private AvatarDataHandler() {
    }

    public static AvatarDataHandler getInstance() {
        return INSTANCE;
    }

    public static native void setClsRef();

    public static void start() {
        Log.d("AvatarDataHandler", "start");
        INSTANCE.clearStream();
        if (mCallback != null) {
            mCallback.onStart();
        }
    }

    public static void end() {
        Log.d("AvatarDataHandler", "end");
        INSTANCE.dataQueue.add(ByteString.EMPTY);
        if (mCallback != null) {
            mCallback.onEnd();
        }
    }

    public static void push(byte[] bytes) {
        Log.d("AvatarDataHandler", "push " + bytes.length);
        INSTANCE.dataQueue.add(ByteString.of(bytes));
    }

    public ByteString getNextStream() {
        return dataQueue.poll();
    }

    void clearStream() {
        dataQueue.clear();
    }

    public boolean isVideoCacheEmpty() {
        return dataQueue.isEmpty();
    }


    private static AvatarCallback mCallback;

    public static void setCallback(AvatarCallback cb) {
        mCallback = cb;
    }

    ///回调
    public interface AvatarCallback {
        void onStart();

        void onEnd();
    }
}
