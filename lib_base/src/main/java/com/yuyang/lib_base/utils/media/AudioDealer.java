package com.yuyang.lib_base.utils.media;

import android.os.Environment;
import android.util.Log;

import com.yuyang.lib_base.BaseApp;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class AudioDealer {

    private static final String TAG = AudioDealer.class.getSimpleName();

    private FileOutputStream fosAudio;

    public static final String AUDIO_DIR = BaseApp.getInstance().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + File.separator + "Record";
    public static final String AUDIO_FILE_NAME = "AudioFull";// 整段录制 文件名

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());

    private PcmUtil mPcmUtil;

    private boolean isDealing;

    public void startDeal(int sampleRateInHz, int channelConfig, int audioFormat) {
        isDealing = true;
        mPcmUtil = new PcmUtil(sampleRateInHz, channelConfig, audioFormat);

        File pcmFile = new File(AUDIO_DIR, AUDIO_FILE_NAME + ".pcm");
        if (pcmFile.exists()) {
            pcmFile.delete();
        }
        if (!pcmFile.getParentFile().exists()) {
            pcmFile.getParentFile().mkdirs();
        }

        try {
            pcmFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fosAudio = new FileOutputStream(pcmFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void endDeal() {
        isDealing = false;

        if (fosAudio != null) {
            try {
                fosAudio.close();
                fosAudio = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            convertPcm2Wav(new File(AUDIO_DIR, AUDIO_FILE_NAME + ".pcm"),
                    new File(AUDIO_DIR, AUDIO_FILE_NAME + dateFormat.format(Calendar.getInstance().getTime()) + ".wav"));
        }
    }

    public void dealData(int readSize, @NotNull byte[] byteData) {
        if (!isDealing) return;

        try {
            fosAudio.write(byteData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void convertPcm2Wav(File pcmFile, File wavFile) {
        mPcmUtil.pcmToWav(
                pcmFile.getAbsolutePath(),
                wavFile.getPath(),
                true);
        Log.d(TAG, "Generate wav file " + wavFile.getAbsolutePath());
    }

    public static double getVolume(int readSize, byte[] srcByteArr) {
        byte[] byteData = Arrays.copyOf(srcByteArr, readSize);

        long v = 0L;
        short[] shortBuffer = DataConvertUtil.byteToShort(byteData, ByteOrder.LITTLE_ENDIAN);
        for (short i : shortBuffer) {
            v += i * i;
        }
        double mean = v / (double) readSize;
        if (mean == 0) {
            return 0;
        } else {
            return 10 * Math.log10(mean);
        }
    }

    private final short SHRT_MAX = (short) 0x7F00;
    private final short SHRT_MIN = (short) -0x7F00;

    byte[] changeVolume(byte[] byteArr, float multiple) {
        byte[] ret = new byte[byteArr.length];

        int nCur = 0;
        while (nCur < byteArr.length) {
            short volume = (short) ((byteArr[nCur] & 0xFF) | (byteArr[nCur + 1] << 8));
            volume = (short) (volume * multiple);
            if (volume < SHRT_MIN) {
                volume = SHRT_MIN;
            } else if (volume > SHRT_MAX)//爆音的处理
            {
                volume = SHRT_MAX;
            }

            ret[nCur] = (byte) (volume & 0xFF);
            ret[nCur + 1] = (byte) ((volume >> 8) & 0xFF);
            nCur += 2;
        }
        return ret;
    }

    byte[] changeVolumeAdd(byte[] byteArr, float add) {
        byte[] ret = new byte[byteArr.length];

        int nCur = 0;
        while (nCur < byteArr.length) {
            short volume = (short) ((byteArr[nCur] & 0xFF) | (byteArr[nCur + 1] << 8));
            volume = (short) (volume + add);
            if (volume < SHRT_MIN) {
                volume = SHRT_MIN;
            } else if (volume > SHRT_MAX)//爆音的处理
            {
                volume = SHRT_MAX;
            }

            ret[nCur] = (byte) (volume & 0xFF);
            ret[nCur + 1] = (byte) ((volume >> 8) & 0xFF);
            nCur += 2;
        }
        return ret;
    }

    public static class AudioDealerListener {
        public void onStartRecord() {
        }

        public void onEndRecord() {
        }

        public void onDetectSnore() {

        }

        public void onGenerateWav() {
        }
    }

    private AudioDealerListener mListener;

    public void setListener(AudioDealerListener listener) {
        this.mListener = listener;
    }
}
