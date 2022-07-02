// IClientCallback.aidl
package com.yuyang.aidl_audioplayer;
import com.yuyang.aidl_audioplayer.AudioBean;

// Declare any non-default types here with import statements

interface IClientCallback {
    oneway void onPlayChange(in AudioBean audioBean, int currentPosition, boolean isPlaying);
    oneway void onProgress(int second);
}