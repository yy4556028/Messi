package com.yuyang.lib_base.utils.media;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.yuyang.lib_base.BaseApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AudioRecorderHelper {

    private static final String TAG = AudioRecorderHelper.class.getSimpleName();

    private AudioRecord audioRecord = null;
    private int recordBufSize = 0;

    private Thread recordingAudioThread;// 录音的工作线程
    private boolean isRecording = false;// mark if is recording

    private static final long TIME_INTERVAL = TimeUnit.SECONDS.toMillis(5);// 超过5秒音量不超过阈值 写入文件结束
    private static final long TIME_MAX = TimeUnit.SECONDS.toMillis(60);// 最长不超过60s
    private static final long MIN_VOLUME = 50;// 超过50分贝记录

    public static final String FILE_DIR = BaseApp.getInstance().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath();

    //录音文件
    public final List<String> wavList = new ArrayList<>();

    private File currentWriteFile;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());

    private long currentStartMills;
    private long preHighVolumeMills;

    private PcmUtil ptwUtil;

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
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    sampleRateInHz,
                    channelConfig,
                    audioFormat,
                    recordBufSize);

            ptwUtil = new PcmUtil(sampleRateInHz, channelConfig, audioFormat);
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

        recordingAudioThread = new Thread(new Runnable() {
            @Override
            public void run() {
                dealRecordData();
            }
        });
        recordingAudioThread.start();
    }

    /**
     * 完成录音
     */
    public void stopRecording() {
        try {
            this.isRecording = false;
            if (this.audioRecord != null) {
                this.audioRecord.stop();
                this.audioRecord.release();
                this.audioRecord = null;
                this.recordingAudioThread.interrupt();
                this.recordingAudioThread = null;
            }
        } catch (Exception e) {
            Log.w(TAG, e.getLocalizedMessage());
        }
    }

    private void dealRecordData() {
        byte[] byteData = new byte[recordBufSize];
        FileOutputStream fos = null;
        while (isRecording && !recordingAudioThread.isInterrupted()) {
            int readSize = audioRecord.read(byteData, 0, recordBufSize);


            double volume = getVolume(readSize, byteData);
            Log.d(TAG, "Volume -> " + volume);
//            byteData = amplifyPCMData(byteData, readSize, 16, 1);
//            volume = getVolume(readSize, byteData);

            if (listener != null) {
                listener.onVolume(volume);
            }

            if (System.currentTimeMillis() - currentStartMills > TIME_MAX && fos != null && currentWriteFile != null) {
                try {
                    // 关闭数据流
                    fos.close();
                    fos = null;
                    convertPcm2Wav();
                    currentWriteFile = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (volume >= MIN_VOLUME) {
                preHighVolumeMills = System.currentTimeMillis();

                if (currentWriteFile == null) {
                    currentWriteFile = generateFile();
                    try {
                        fos = new FileOutputStream(currentWriteFile);
                        currentStartMills = System.currentTimeMillis();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        currentWriteFile = null;
                        Log.d(TAG, "File not found");
                    }
                }
            } else {
                if (fos != null && System.currentTimeMillis() - preHighVolumeMills > TIME_INTERVAL) {
                    try {
                        // 关闭数据流
                        fos.close();
                        fos = null;
                        convertPcm2Wav();
                        currentWriteFile = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (fos != null && AudioRecord.ERROR_INVALID_OPERATION != readSize) {
                try {
                    fos.write(byteData);
//                    Log.i(TAG, "写录音数据->" + readSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (fos != null) {
            try {
                // 关闭数据流
                fos.close();
                fos = null;
                convertPcm2Wav();
                currentWriteFile = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File generateFile() {
        File toBeWriteFile = new File(FILE_DIR, dateFormat.format(new Date()) + ".pcm");
        if (toBeWriteFile.exists()) {
            toBeWriteFile.delete();
        }

        try {
            toBeWriteFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Generate pcm file " + toBeWriteFile);
        return toBeWriteFile;
    }

    private void convertPcm2Wav() {
        String pcmPath = currentWriteFile.getAbsolutePath();
        String wavPath = pcmPath.substring(0, pcmPath.lastIndexOf(".")) + ".wav";
        ptwUtil.pcmToWav(
                pcmPath,
                wavPath,
                true);
        wavList.add(wavPath);
        Log.d(TAG, "Generate wav file " + wavPath);
        if (listener != null) {
            listener.onGenerateWav(wavPath);
        }
    }

    private short[] bytesToShort(byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            short[] shorts = new short[bytes.length / 2];
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            return shorts;
        }
    }

    private static short[] toShortArray(byte[] src) {

        int count = src.length >> 1;
        short[] dest = new short[count];
        for (int i = 0; i < count; i++) {
            dest[i] = (short) (src[i * 2] << 8 | src[2 * i + 1] & 0xff);
        }
        return dest;
    }

    private static byte[] toByteArray(short[] src) {

        int count = src.length;
        byte[] dest = new byte[count << 1];
        for (int i = 0; i < count; i++) {
            dest[i * 2] = (byte) (src[i] >> 8);
            dest[i * 2 + 1] = (byte) (src[i] >> 0);
        }
        return dest;
    }

    private double getVolume(int readSize, byte[] byteData) {
        long v = 0L;
        short[] shortBuffer = bytesToShort(byteData);
        for (short i : shortBuffer) {
            v += i * i;
        }
        double mean = v / (double) readSize;
        double volume = 10 * Math.log10(mean);
        return volume;
    }

    public interface RecorderListener {
        void onGenerateWav(String wavFilePath);

        void onVolume(double volume);
    }

    private RecorderListener listener;

    public void setRecorderListener(RecorderListener listener) {
        this.listener = listener;
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
