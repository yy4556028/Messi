package com.yamap.lib_chat.utils;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AudioRecorderHelper {

    public static int ERROR_INVALID_FILE = -1001;//Recording_without_permission
    public static final int LENGTH_TOO_SHORT = -1002;//The_recording_time_is_too_short

    private MediaRecorder recorder;

    private boolean isRecording = false;

    private String voiceFilePath = null;
    private String voiceFileName = null;

    private long startTime;
    private long maxLength = 30 * 1000;

    private MediaPlayer mediaPlayer;

    public String getVoiceFilePath() {
        return voiceFilePath;
    }

    public void setVoiceFilePath(String voiceFilePath) {
        this.voiceFilePath = voiceFilePath;
    }

    public String getVoiceFileName() {
        return voiceFileName;
    }

    public void setVoiceFileName(String voiceFileName) {
        this.voiceFileName = voiceFileName;
    }

    public long getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(long maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * 开始录音
     */
    public void startRecording() {

        try {
//            initRecorderSobrr();
            initRecorderLeanCloud();
            isRecording = true;
            recorder.start();
            startTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isRecording) {
                        SystemClock.sleep(100);
                        if (recorder == null || !isRecording) {
                            break;
                        }
                        if (listener != null) {

                            long length = System.currentTimeMillis() - startTime;

                            /**
                             * 参考
                             * http://blog.csdn.net/greatpresident/article/details/38402147
                             * 分贝值正常值域为0 dB 到90.3 dB
                             */

                            double ratio = (double) recorder.getMaxAmplitude();

                            double db = 0;// 分贝
                            if (ratio > 1)
                                db = 20 * Math.log10(ratio);

                            listener.onDb((int) db);
                            listener.onProgress(length);

                            if (length >= maxLength) {
                                stopRecording();
                            }
                        }

                    }
                } catch (Exception e) {
                    // from the crash report website, found one NPE crash from
                    // one android 4.0.4 htc phone
                    // maybe handler is null for some reason
                    if (listener != null) {
                        listener.onDb(0);
                        listener.onProgress(0);
                    }
                }
            }
        }).start();
    }

    /**
     * 取消录音
     */
    public void discardRecording() {

        if (recorder != null) {
            isRecording = false;

            try {
                recorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                recorder.release();
                recorder = null;
            }

            File file = new File(voiceFilePath, voiceFileName);
            if (file.exists() && !file.isDirectory()) {
                file.delete();
            }
        }
    }

    /**
     * 完成录音
     */
    public void stopRecording() {

        if (!isRecording)
            return;

        isRecording = false;

        if (recorder != null) {
            try {
                recorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                recorder.release();
                recorder = null;
            }

            File file = new File(voiceFilePath, voiceFileName);

            if (file.length() == 0) {
                file.delete();
                if (listener != null)
                    listener.onComplete("", ERROR_INVALID_FILE);
                return;
            }
            long length = System.currentTimeMillis() - startTime;
            if (listener != null)
                listener.onComplete(file.getAbsolutePath(), length > 999 ? length : LENGTH_TOO_SHORT);
            return;
        }
        if (listener != null)
            listener.onComplete("", 0);
    }

    private MediaRecorder initRecorderSobrr() throws Exception {

//        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
//            return null;

        if (TextUtils.isEmpty(voiceFilePath)) {
            voiceFilePath = Environment.getDownloadCacheDirectory() + "/" + voiceFileName;
        }
        if (TextUtils.isEmpty(voiceFileName)) {
            Calendar calendar = Calendar.getInstance();
            voiceFileName = "record_" + new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(calendar.getTime()) + ".m4a";
        }

        File file = new File(voiceFilePath, voiceFileName);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (!file.exists())
            file.createNewFile();

        if (recorder == null) {
            recorder = new MediaRecorder();
//                recorder.setMaxDuration();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioChannels(1); // MONO
            recorder.setAudioSamplingRate(8000); // 8000Hz
            recorder.setAudioEncodingBitRate(64); // seems if change this to 128, still got same file size.
            // one easy way is to use temp file
            // file = File.createTempFile(PREFIX + userId, EXTENSION,
            // User.getVoicePath());
            recorder.setOutputFile(file.getAbsolutePath());
            recorder.prepare();
        } else {
            recorder.reset();
            recorder.setOutputFile(file.getAbsolutePath());
        }
        return recorder;
    }

    private void initRecorderLeanCloud() throws Exception {
        if (TextUtils.isEmpty(voiceFilePath)) {
            voiceFilePath = Environment.getDownloadCacheDirectory() + "/" + voiceFileName;
        }
        if (TextUtils.isEmpty(voiceFileName)) {
            Calendar calendar = Calendar.getInstance();
            voiceFileName = "record_" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(calendar.getTime()) + ".m4a";
        }

        File file = new File(voiceFilePath, voiceFileName);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (!file.exists())
            file.createNewFile();

        if (recorder == null) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(file.getAbsolutePath());

            recorder.prepare();

        } else {
            recorder.reset();
            recorder.setOutputFile(file.getAbsolutePath());
        }
    }

    public void stopPlayVoice() {

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if (recorder != null) {
            recorder.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public interface RecorderListener {
        public void onProgress(long currnetLength);

        public void onDb(int amplitude);

        public void onComplete(String voiceFilePath, long voiceTimeLength);
    }

    private RecorderListener listener;

    public void setRecorderListener(RecorderListener listener) {
        this.listener = listener;
    }
}
