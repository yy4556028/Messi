// IAudioPlayerAidlInterface.aidl
package com.yuyang.aidl_audioplayer;
import com.yuyang.aidl_audioplayer.AudioBean;
import com.yuyang.aidl_audioplayer.IClientCallback;

// Declare any non-default types here with import statements

interface IAudioPlayerAidlInterface {

    oneway void registerCallback(IClientCallback callback);
    oneway void unregisterCallback(IClientCallback callback);

    void playAudioList(in List<AudioBean> audioBeanList, int playIndex);//播放

    void resumeAudio();//继续播放

    void pauseAudio();//暂停

    void nextAudio();//下一曲

    void prevAudio();//上一曲

    boolean isPlaying();//是否正在播放

    void seekTo(int msec);//跳转到
}