package com.yuyang.messi.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Size;

import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.utils.Camera2Util;
import com.yuyang.lib_base.utils.StorageUtil;

import java.io.File;
import java.io.IOException;

public class VideoRecorderHelper {

    private static final String PREFIX = "video";
    private static final String EXTENSION = ".mp4";
    public int ERROR_INVALID_FILE = -1001;
    public static final int LENGTH_TOO_SHORT = -1002;

    private MediaRecorder recorder;

    private boolean isRecording = false;

    private String videoFilePath = null;
    private String videoFileName = null;
    private File file;
    private File outputFile;

    private long startTime;
    private long videoLength;
    private long maxLength = 30 * 1000;

    public MediaRecorder getMediaRecorder() {
        return recorder;
    }

    /**
     * 500k/s
     * Sobrr 生成的视频 size 会小很多
     *
     * @param camera
     * @param currentCamera
     */
    public void initRecorder(Camera camera, int currentCamera) {

        initOutputFile();

        if (recorder == null)
            recorder = new MediaRecorder();

        recorder.reset();
        recorder.setCamera(camera);//设置一个recording的摄像头

        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);//设置用于录制的音源
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//开始捕捉和编码数据到setOutputFile（指定的文件）
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//设置在录制过程中产生的输出文件的格式

        CamcorderProfile profile = getPropCamcorderProfile(camera, currentCamera);

        recorder.setVideoFrameRate(Math.min(profile.videoFrameRate, 30));//设置要捕获的视频帧速率
        recorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);//设置要捕获的视频的宽度和高度
//      recorder.setVideoSize(camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height);

        recorder.setVideoEncodingBitRate(3_700_000);//设置录制的视频编码比特率
//        recorder.setVideoEncodingBitRate(profile.videoBitRate);

        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//设置视频编码器，用于录制

        if (profile.quality >= CamcorderProfile.QUALITY_TIME_LAPSE_LOW &&
                profile.quality <= CamcorderProfile.QUALITY_TIME_LAPSE_QVGA) {
            // setCaptureRate() 设置视频帧的捕获率
            // Nothing needs to be done. Call to setCaptureRate() enables
            // time lapse video recording.
        } else {
            recorder.setAudioEncodingBitRate(profile.audioBitRate);//设置录制的音频编码比特率
            recorder.setAudioChannels(profile.audioChannels);//设置录制的音频通道数
            recorder.setAudioSamplingRate(profile.audioSampleRate);//设置录制的音频采样率
            recorder.setAudioEncoder(profile.audioCodec);//设置audio的编码格式
        }

        if (currentCamera == Camera.CameraInfo.CAMERA_FACING_BACK) {
            recorder.setOrientationHint(90);
        } else {
            recorder.setOrientationHint(270);
        }

        recorder.setOutputFile(file.getAbsolutePath());//设置输出文件的路径

        /**
         * 日常的业务中经常对拍摄视频的时长或者大小有要求，这个可以通过mediaRecorder.setOnInfoListener()来处理
         */
        recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED == what) {
                    //到达最大时长
                } else if (MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED == what) {
                    //到达最大尺寸
                }
            }
        });

        try {
            recorder.prepare();
        } catch (IOException e) {
            recorder = null;
            ToastUtil.showToast("record prepare fail");
        }
    }

    /**
     * 6+M/s
     *
     * @param camera
     * @param currentCamera
     */
    public void initRecorder2(Camera camera, int currentCamera) {

        initOutputFile();

        if (recorder == null)
            recorder = new MediaRecorder();

        recorder.reset();
        recorder.setCamera(camera);

        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        CamcorderProfile profile = getPropCamcorderProfile(camera, currentCamera);

        recorder.setProfile(profile);

        if (currentCamera == Camera.CameraInfo.CAMERA_FACING_BACK) {
            recorder.setOrientationHint(90);
        } else {
            recorder.setOrientationHint(270);
        }

        recorder.setOutputFile(file.getAbsolutePath());

        try {
            recorder.prepare();
        } catch (IOException e) {
            recorder = null;
            ToastUtil.showToast("record prepare fail");
        }
    }

    /**
     * googleCamera2 demo 中设置
     *
     * @param mVideoSize
     */
    public void initRecorder3(Activity activity, Size mVideoSize, Integer facing) {

        initOutputFile();

        if (recorder == null)
            recorder = new MediaRecorder();

        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(file.getAbsolutePath());
        recorder.setVideoEncodingBitRate(10000000);
        recorder.setVideoFrameRate(30);
        recorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int orientation = Camera2Util.ORIENTATIONS.get(rotation);

        if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
            recorder.setOrientationHint(orientation);
        } else if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
            recorder.setOrientationHint(orientation + 180);
        } else {
            recorder.setOrientationHint(0);
        }

        try {
            recorder.prepare();
        } catch (IOException e) {
            recorder = null;
            ToastUtil.showToast("record prepare fail");
        }
    }

    public void setOutputFile(File file) {
        outputFile = file;
        if (!outputFile.getParentFile().exists())
            outputFile.getParentFile().mkdirs();
        if (outputFile.exists())
            outputFile.delete();
    }

    private void initOutputFile() {

        if (outputFile != null) {
            file = outputFile;
            return;
        }

        videoFileName = getVideoFileName(PREFIX + EXTENSION);
        videoFilePath = StorageUtil.getExternalFilesDir(StorageUtil.VIDEO) + "/" + videoFileName;
        file = new File(videoFilePath);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (file.exists())
            file.delete();
    }

    /**
     * start recording to the file
     */
    public String startRecording(Camera camera, int currentCamera) {

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return null;

        isRecording = true;
        if (camera != null)
            camera.unlock();
        try {
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        startTime = System.currentTimeMillis();
        return file == null ? null : file.getAbsolutePath();
    }

    private CamcorderProfile getPropCamcorderProfile(Camera camera, int cameraId) {

        Camera.Size size = camera.getParameters().getPreviewSize();

        int quality;
        CamcorderProfile profile;

        if (equalRate(size, 3840f / 2160) && CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_2160P)) {

            quality = CamcorderProfile.QUALITY_2160P;
        } else if (equalRate(size, 1920f / 1080) && CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_1080P)) {

            quality = CamcorderProfile.QUALITY_1080P;
        } else if (equalRate(size, 1280f / 720) && CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_720P)) {

            quality = CamcorderProfile.QUALITY_720P;
        } else if (equalRate(size, 720f / 480) && CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_480P)) {

            quality = CamcorderProfile.QUALITY_480P;
        } else if (equalRate(size, 352f / 288) && CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_CIF)) {

            quality = CamcorderProfile.QUALITY_CIF;
        } else if (equalRate(size, 320f / 240) && CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_QVGA)) {

            quality = CamcorderProfile.QUALITY_QVGA;
        } else if (equalRate(size, 176f / 144) && CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_QCIF)) {

            quality = CamcorderProfile.QUALITY_QCIF;
        } else {

            quality = CamcorderProfile.QUALITY_HIGH;
        }

        profile = CamcorderProfile.get(cameraId, quality);

        return profile;
    }

    public void stopRecording() {

        if (!isRecording)
            return;

        if (recorder != null) {
            isRecording = false;
            try {
                recorder.stop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            recorder.reset();

            if (file == null || !file.exists() || !file.isFile()) {
                if (listener != null)
                    listener.onComplete("", ERROR_INVALID_FILE);
                videoLength = 0;
                return;
            }
            if (file.length() == 0) {
                file.delete();
                if (listener != null)
                    listener.onComplete("", ERROR_INVALID_FILE);
                videoLength = 0;
                return;
            }
            long length = System.currentTimeMillis() - startTime;
            if (listener != null)
                listener.onComplete(videoFilePath, length > 999 ? length : LENGTH_TOO_SHORT);
            videoLength = length;
            return;
        }
        if (listener != null)
            listener.onComplete("", 0);
        videoLength = 0;
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if (recorder != null) {
            recorder.release();
        }
    }

    private String getVideoFileName(String uid) {
//        Time now = new Time();
//        now.setToNow();
//        return uid + now.toString().substring(0, 15) + EXTENSION;
        return PREFIX + System.currentTimeMillis() + EXTENSION;
    }

    public boolean isRecording() {
        return isRecording;
    }

    private boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

    public String convertTime(long time) {

        if (time <= 0) {
            return "00:00";
        }

        int minute = (int) ((time / 1000) / 60);
        int second = (int) ((time / 1000) % 60);

        String m = ("00" + minute).substring(("00" + minute).length() - 2);

        String s = ("00" + second).substring(("00" + second).length() - 2);

        return m + ":" + s;
    }

    public interface RecorderListener {

        public void onComplete(String videoFilePath, long videoTimeLength);
    }

    private RecorderListener listener;

    public void setRecorderListener(RecorderListener listener) {
        this.listener = listener;
    }
}
