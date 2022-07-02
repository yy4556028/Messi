package com.yuyang.messi.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.screen_share.ScreenShareActivity;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.StorageUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScreenRecordService extends Service {

    private static final String KEY_RESULT_CODE = "KEY_RESULT_CODE";
    private static final String KEY_RESULT_DATA = "KEY_RESULT_DATA";
    private static final String KEY_IS_HD = "KEY_IS_HD";
    private static final String KEY_IS_AUDIO = "KEY_IS_AUDIO";

    private int resultCode;
    private Intent resultData = null;

    private NotificationCompat.Builder notificationBuilder;
    private String channelId = "record";
    private int notifyId_Record = 101;

    private MediaProjection mediaProjection = null;
    private MediaRecorder mediaRecorder = null;
    private VirtualDisplay virtualDisplay = null;

    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;

    private String filePath;

    private boolean isVideoHd;//是否为高清视频
    private boolean isAudio = true;//是否录制声音

    public static void startService(Context context, int resultCode, @Nullable Intent data, boolean isVideoHd, boolean isAudio) {
        Intent intent = new Intent(context, ScreenRecordService.class);
        intent.putExtra(KEY_RESULT_CODE, resultCode);
        intent.putExtra(KEY_RESULT_DATA, data);
        intent.putExtra(KEY_IS_HD, isVideoHd);
        intent.putExtra(KEY_IS_AUDIO, isAudio);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initNotification();
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling startService(Intent),
     * providing the arguments it supplied and a unique integer token representing the start request.
     * Do not call this method directly.
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            resultCode = intent.getIntExtra(KEY_RESULT_CODE, -1);
            resultData = intent.getParcelableExtra(KEY_RESULT_DATA);
            isVideoHd = intent.getBooleanExtra(KEY_IS_HD, false);
            isAudio = intent.getBooleanExtra(KEY_IS_AUDIO, false);

            mScreenWidth = CommonUtil.getScreenWidth();
            mScreenHeight = CommonUtil.getScreenHeight();
            mScreenDensity = Resources.getSystem().getDisplayMetrics().densityDpi;

            mediaProjection = createMediaProjection();
            mediaRecorder = createMediaRecorder();
            virtualDisplay = createVirtualDisplay();
            mediaRecorder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * START_NOT_STICKY:
         * Constant to return from onStartCommand(Intent, int, int): if this service's process is
         * killed while it is started (after returning from onStartCommand(Intent, int, int)),
         * and there are no new start intents to deliver to it, then take the service out of the
         * started state and don't recreate until a future explicit call to Context.startService(Intent).
         * The service will not receive a onStartCommand(Intent, int, int) call with a null Intent
         * because it will not be re-started if there are no pending Intents to deliver.
         */
        return Service.START_NOT_STICKY;
    }

    /**
     * 初始化通知栏
     */
    private void initNotification() {
        notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)//设置状态栏的通知图标
                .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.screen_record))//设置通知栏横条的图标
                .setAutoCancel(false)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setShowWhen(true)//右上角的时间显示
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("点击此处停止录制")
                .setContentIntent(PendingIntent.getBroadcast(this, 0, new Intent(ScreenShareActivity.RECORD_STOP), PendingIntent.FLAG_UPDATE_CURRENT))
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT);//设置该通知优先级;

        Notification notification = notificationBuilder.build();
        startForeground(notifyId_Record, notification);
    }

    //createMediaProjection
    public MediaProjection createMediaProjection() {
        /**
         * Use with getSystemService(Class) to retrieve a MediaProjectionManager instance for
         * managing media projection sessions.
         */
        return ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE))
                .getMediaProjection(resultCode, resultData);
        /**
         * Retrieve the MediaProjection obtained from a succesful screen capture request.
         * Will be null if the result from the startActivityForResult() is anything other than RESULT_OK.
         */
    }

    private MediaRecorder createMediaRecorder() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        filePath = StorageUtil.getExternalFilesDir("ScreenRecord").getAbsolutePath();
        if (!new File(filePath).exists()) {
            new File(filePath).mkdirs();
        }
        filePath = filePath + "/" + simpleDateFormat.format(new Date()) + ".mp4";
        //Used to record audio and video. The recording control is based on a simple state machine.
        MediaRecorder mediaRecorder = new MediaRecorder();

        if (isAudio) mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //Set the video source to be used for recording.
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        //Set the format of the output produced during recording.
        //3GPP media file format
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //Sets the video encoder to be used for recording.
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //Sets the width and height of the video to be captured.
        mediaRecorder.setVideoSize(mScreenWidth, mScreenHeight);

        if (isAudio) mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        int bitRate;
        if (isVideoHd) {
            //Sets the video encoding bit rate for recording.
            //param:the video encoding bit rate in bits per second.
            mediaRecorder.setVideoEncodingBitRate(5 * mScreenWidth * mScreenHeight);
            //Sets the frame rate of the video to be captured.
            mediaRecorder.setVideoFrameRate(60); //after setVideoSource(), setOutFormat()
            bitRate = 5 * mScreenWidth * mScreenHeight / 1000;
        } else {
            //Sets the video encoding bit rate for recording.
            //param:the video encoding bit rate in bits per second.
            mediaRecorder.setVideoEncodingBitRate(mScreenWidth * mScreenHeight);
            //Sets the frame rate of the video to be captured.
            mediaRecorder.setVideoFrameRate(30);
            bitRate = mScreenWidth * mScreenHeight / 1000;
        }
        try {
            //Pass in the file object to be written.
            mediaRecorder.setOutputFile(filePath);
            //Prepares the recorder to begin capturing and encoding data.
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaRecorder;
    }

    private VirtualDisplay createVirtualDisplay() {
        /**
         * name    String: The name of the virtual display, must be non-empty.This value must never be null.
         width int: The width of the virtual display in pixels. Must be greater than 0.
         height    int: The height of the virtual display in pixels. Must be greater than 0.
         dpi   int: The density of the virtual display in dpi. Must be greater than 0.
         flags int: A combination of virtual display flags. See DisplayManager for the full list of flags.
         surface   Surface: The surface to which the content of the virtual display should be rendered, or null if there is none initially.
         callback  VirtualDisplay.Callback: Callback to call when the virtual display's state changes, or null if none.
         handler   Handler: The Handler on which the callback should be invoked, or null if the callback should be invoked on the calling thread's main Looper.
         */
        /**
         * DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR
         * Virtual display flag: Allows content to be mirrored on private displays when no content is being shown.
         */
        return mediaProjection.createVirtualDisplay("mediaProjection", mScreenWidth, mScreenHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
        ToastUtil.showToast("录屏文件已保存，路径：" + filePath);
        //取消前台进程
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void release() {
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder = null;
        }
        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }
    }
}
