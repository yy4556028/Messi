package com.yuyang.lib_base.utils.media;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import org.jetbrains.annotations.NotNull;

public class AudioRecorderHelper {

    private static final String TAG = AudioRecorderHelper.class.getSimpleName();

    private AudioRecord audioRecord = null;
    private int recordBufSize = 0;

    private Handler workerHandler;
    private HandlerThread workerThread;
    private boolean isRecording = false;

    /**
     * @param sampleRateInHz 官方文档说44100 为目前所有设备兼容，但是如果用模拟器测试的话会有问题，如果部分手机有问题，替换为16000 或 8000
     * @param channelConfig  声道数。CHANNEL_IN_MONO 单声道 and CHANNEL_IN_STEREO 双声道. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的
     * @param audioFormat    返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT 等
     */
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private void initAudioRecord(int sampleRateInHz, int channelConfig, int audioFormat) {
        recordBufSize = AudioRecord
                .getMinBufferSize(sampleRateInHz,
                        channelConfig,
                        audioFormat);
        if (recordBufSize != AudioRecord.ERROR_BAD_VALUE) {
            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
//                    MediaRecorder.AudioSource.VOICE_COMMUNICATION,// 降噪去回声
                    sampleRateInHz,
                    channelConfig,
                    audioFormat,
                    recordBufSize);
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    public void startRecordAudio() {
        startRecordAudio(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    public void startRecordAudio(int sampleRateInHz, int channelConfig, int audioFormat) {
        initAudioRecord(sampleRateInHz, channelConfig, audioFormat);

        isRecording = true;
        audioRecord.startRecording();

        workerThread = new HandlerThread(TAG);
        workerThread.start();
        Looper workerLooper = workerThread.getLooper();
        workerHandler = new Handler(workerLooper);
        workerHandler.post(this::readAudioData);
    }

    public void stopRecording() {
        try {
            this.isRecording = false;
            if (this.audioRecord != null) {
                this.audioRecord.stop();
                this.audioRecord.release();
                this.audioRecord = null;
                workerThread.quit();
            }
        } catch (Exception e) {
            Log.w(TAG, e.getLocalizedMessage());
        }
    }

    private void readAudioData() {
        byte[] byteData = new byte[recordBufSize];
        while (isRecording) {
            int readSize = audioRecord.read(byteData, 0, recordBufSize);
            if (mListener != null) {
                mListener.onReadByteData(readSize, byteData);
            }
        }
    }

    public interface RecorderListener {
        void onReadByteData(int readSize, @NotNull byte[] byteData);
    }

    private RecorderListener mListener;

    public void setRecorderListener(RecorderListener listener) {
        this.mListener = listener;
    }

    ////////////////////////////////////////////

    private short getShort(byte[] src, int start) {
        return (short) ((src[start] & 0xFF) | (src[start + 1] << 8));
    }

    short SHRT_MAX = (short) 0x7F00;
    short SHRT_MIN = (short) -0x7F00;

    /**
     * 调节PCM数据音量
     * src  :
     * nLen :
     * dest :
     * nBitsPerSample: 16/8
     * multiple: 放大倍数，如1.5
     */
    byte[] amplifyPCMData(byte[] src, int nLen, int nBitsPerSample, float multiple) {
        byte[] ret = new byte[nLen];

        int nCur = 0;
        if (16 == nBitsPerSample) {
            while (nCur < nLen) {
                short volum = getShort(src, nCur);
                //Log.d(TAG, "volum="+volum);
                volum = (short) (volum * multiple);
                if (volum < SHRT_MIN) {
                    volum = SHRT_MIN;
                } else if (volum > SHRT_MAX)//爆音的处理
                {
                    volum = SHRT_MAX;
                }

                ret[nCur] = (byte) (volum & 0xFF);
                ret[nCur + 1] = (byte) ((volum >> 8) & 0xFF);
                nCur += 2;
            }

        }
        /*else if (8 == nBitsPerSample)
        {
            while (nCur < nLen)
            {
                BYTE* volum = src + nCur;
                *volum = (*volum) * multiple;
                if (*volum > 255)//爆音的处理
                {
                    *volum = 255;
                }
                *src  = *volum  ;
                nCur ++;
            }

        }   */
        return ret;
    }
}
