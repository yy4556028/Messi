package com.yuyang.messi.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.yuyang.aidl_audioplayer.AudioBean;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.media.audio.aidl.AudioActivity;
import com.yuyang.messi.utils.SharedPreferencesUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 音乐播放服务
 */
public class AudioPlayMessengerService extends Service {

    private static final String TAG = AudioPlayMessengerService.class.getSimpleName();
    public static final String KEY_DATA = "key_data";
    public static final String KEY_CURRENT_BEAN = "key_current_bean";
    public static final String KEY_CURRENT_POS = "key_current_pos";
    public static final int MSG_CLEAR = -1;
    public static final int MSG_INIT = 0;
    public static final int MSG_PLAY = 1;
    public static final int MSG_PAUSE = 2;
    public static final int MSG_RESUME = 3;
    public static final int MSG_PROGRESS = 4;
    public static final int MSG_PREV = 5;
    public static final int MSG_NEXT = 6;

    private static final String ACTION_PLAY = "action.play";
    private static final String ACTION_PAUSE = "action.pause";
    private static final String ACTION_NEXT = "action.next";
    private static final String ACTION_PREV = "action.prev";
    private static final String ACTION_REMOVE = "action.remove";

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private final String channelId = "AudioPlay";//过长会引发震动，我也很无奈
    private final int NOTIFICATION_ID = 30;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Messenger toClientMessenger;
    private Messenger mServerMessenger;

    private PlayReceiver playReceiver;

    private List<AudioBean> audioBeanList;

    private AudioBean currentAudioBean;

    public class PlayReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) return;
            switch (action) {
                case ACTION_PLAY: {
                    resumeAudio();
                    break;
                }
                case ACTION_PAUSE: {
                    pauseAudio();
                    break;
                }
                case ACTION_NEXT: {
                    nextAudio();
                    break;
                }
                case ACTION_PREV: {
                    prevAudio();
                    break;
                }
                case ACTION_REMOVE: {
                    //取消前台进程
                    pauseAudio();
                    stopForeground(true);
                    break;
                }
            }
        }
    }

    private static class AudioServiceHandler extends Handler {
        private WeakReference<AudioPlayMessengerService> mWeakReference;

        public AudioServiceHandler(AudioPlayMessengerService audioPlayMessengerService) {
            this.mWeakReference = new WeakReference<>(audioPlayMessengerService);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            final AudioPlayMessengerService audioPlayMessengerService = mWeakReference.get();
            if (audioPlayMessengerService == null) return;
            switch (msg.what) {
                case MSG_CLEAR: {
                    audioPlayMessengerService.toClientMessenger = null;
                    break;
                }
                case MSG_INIT: {
                    audioPlayMessengerService.toClientMessenger = msg.replyTo;

                    Message toClientMsg;
                    if (audioPlayMessengerService.isPlaying()) {
                        toClientMsg = Message.obtain(null, MSG_PLAY);
                    } else {
                        toClientMsg = Message.obtain(null, MSG_INIT);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(AudioBean.class.getSimpleName(), audioPlayMessengerService.currentAudioBean);
                    bundle.putInt(KEY_CURRENT_POS, audioPlayMessengerService.mediaPlayer.getCurrentPosition());
                    try {
                        toClientMsg.setData(bundle);
                        audioPlayMessengerService.toClientMessenger.send(toClientMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                }
//                case MSG_PLAY: {
//                    audioPlayService.resumeAudio();
//                    break;
//                }
                case MSG_RESUME: {
                    audioPlayMessengerService.resumeAudio();
                    break;
                }
                case MSG_PAUSE: {
                    audioPlayMessengerService.pauseAudio();
                    break;
                }
                case MSG_PROGRESS: {
                    audioPlayMessengerService.seekTo(msg.arg1);
                    break;
                }
                case MSG_PREV: {
                    audioPlayMessengerService.prevAudio();
                    break;
                }
                case MSG_NEXT: {
                    audioPlayMessengerService.nextAudio();
                    break;
                }
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public static void startService(Context context, ArrayList<AudioBean> audioBeanList, AudioBean audioBean, int currentPos) {
        Intent intent = new Intent(context, AudioPlayMessengerService.class);
        intent.putParcelableArrayListExtra(AudioPlayMessengerService.KEY_DATA, audioBeanList);
        intent.putExtra(AudioPlayMessengerService.KEY_CURRENT_BEAN, (Parcelable) audioBean);
        intent.putExtra(AudioPlayMessengerService.KEY_CURRENT_POS, currentPos);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MessiApp.getInstance().startForegroundService(intent);
        } else {
            MessiApp.getInstance().startService(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        initNotification();

        mServerMessenger = new Messenger(new AudioServiceHandler(this));

        playReceiver = new PlayReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_PLAY);
        mFilter.addAction(ACTION_PAUSE);
        mFilter.addAction(ACTION_NEXT);
        mFilter.addAction(ACTION_PREV);
        mFilter.addAction(ACTION_REMOVE);
        registerReceiver(playReceiver, mFilter);

        initEvent();

        audioBeanList = SharedPreferencesUtil.getLastAudioList();
        currentAudioBean = SharedPreferencesUtil.getLastAudioBean();
        int duration = SharedPreferencesUtil.getLastAudioDuration();
        if (currentAudioBean != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(this, currentAudioBean.getAudioUri());
                mediaPlayer.prepare();

                if (duration >= 0) {
                    mediaPlayer.seekTo(duration);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        audioBeanList = intent.getParcelableArrayListExtra(KEY_DATA);
        AudioBean audioBean = intent.getParcelableExtra(KEY_CURRENT_BEAN);
        SharedPreferencesUtil.setLastAudioList(audioBeanList);
        playAudio(audioBean == null ? audioBeanList.get(0) : audioBean, intent.getIntExtra(KEY_CURRENT_POS, -1));
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mServerMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (isPlaying()) {
            SharedPreferencesUtil.setLastAudioBean(currentAudioBean);
            SharedPreferencesUtil.setLastAudioDuration(mediaPlayer.getCurrentPosition());
        }

        release();
        if (playReceiver != null)
            unregisterReceiver(playReceiver);// 解除广播注册

        //取消前台进程
        stopForeground(true);
    }

    private void initNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Music",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("音乐播放器");
            channel.enableLights(false);
            channel.setLightColor(Color.RED);
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{0});
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, AudioActivity.class);

        /**
         * 如果是组件化开发，点击通知跳转的页面在其他module中，则是无法引用到MainActivity的。
         * 因此采用该种方式更合适。只需要在app的清单文件中再次配置一下Activity的关系即可。打包的时候会合并清单文件配置。
         *
         * 作者：小编
         * 链接：https://www.jianshu.com/p/678e2322fd41
         * 来源：简书
         * 简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
         */
        //创建返回栈
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //添加Activity到返回栈
        stackBuilder.addParentStack(AudioActivity.class);
        //添加Intent到栈顶
        stackBuilder.addNextIntent(intent);

        notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)//设置状态栏的通知图标
                .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.douban))//设置通知栏横条的图标
                .setAutoCancel(false)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(true)//禁止滑动删除
                .setShowWhen(true)//右上角的时间显示
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("ContentText")
                .setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT))  //android:parentActivityName=".ui.main.MainActivity"，关闭页面进入MainActivity
//                .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))  //android:excludeFromRecents="true" 所以不会出现在最近任务
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT);//设置该通知优先级
    }

    private void updateNotification() {
        Notification notification = notificationBuilder.build();

        RemoteViews normalRemoteView = new RemoteViews(getPackageName(), R.layout.activity_audio_notification_normal);
        RemoteViews expandRemoteView = new RemoteViews(getPackageName(), R.layout.activity_audio_notification_expand);
        normalRemoteView.setImageViewBitmap(R.id.activity_audio_notification_normal_imageView, BitmapFactory.decodeFile(currentAudioBean.getImage()));
        normalRemoteView.setTextViewText(R.id.activity_audio_notification_normal_titleText, currentAudioBean.getTitle());
        normalRemoteView.setTextViewText(R.id.activity_audio_notification_normal_subtitleText, String.format("%s - %s", currentAudioBean.getTitle(), currentAudioBean.getArtist()));
        if (isPlaying()) {
            normalRemoteView.setImageViewBitmap(R.id.activity_audio_notification_normal_playImage, BitmapFactory.decodeResource(getResources(), R.drawable.activity_audio_play_pause_normal));
            normalRemoteView.setOnClickPendingIntent(R.id.activity_audio_notification_normal_playImage,
                    PendingIntent.getBroadcast(this, 0, new Intent(ACTION_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            normalRemoteView.setImageViewBitmap(R.id.activity_audio_notification_normal_playImage, BitmapFactory.decodeResource(getResources(), R.drawable.activity_audio_play_play_normal));
            normalRemoteView.setOnClickPendingIntent(R.id.activity_audio_notification_normal_playImage,
                    PendingIntent.getBroadcast(this, 0, new Intent(ACTION_PLAY), PendingIntent.FLAG_UPDATE_CURRENT));
        }
        normalRemoteView.setOnClickPendingIntent(R.id.activity_audio_notification_normal_nextImage,
                PendingIntent.getBroadcast(this, 0, new Intent(ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT));
        normalRemoteView.setOnClickPendingIntent(R.id.activity_audio_notification_normal_removeImage,
                PendingIntent.getBroadcast(this, 0, new Intent(ACTION_REMOVE), PendingIntent.FLAG_UPDATE_CURRENT));

        expandRemoteView.setImageViewBitmap(R.id.activity_audio_notification_expand_imageView, BitmapFactory.decodeFile(currentAudioBean.getImage()));
        expandRemoteView.setTextViewText(R.id.activity_audio_notification_expand_titleText, currentAudioBean.getTitle());
        expandRemoteView.setTextViewText(R.id.activity_audio_notification_expand_subtitleText, String.format("%s - %s", currentAudioBean.getTitle(), currentAudioBean.getArtist()));
        if (isPlaying()) {
            normalRemoteView.setImageViewBitmap(R.id.activity_audio_notification_expand_playImage, BitmapFactory.decodeResource(getResources(), R.drawable.activity_audio_play_pause_normal));
            normalRemoteView.setOnClickPendingIntent(R.id.activity_audio_notification_expand_playImage,
                    PendingIntent.getBroadcast(this, 0, new Intent(ACTION_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            normalRemoteView.setImageViewBitmap(R.id.activity_audio_notification_expand_playImage, BitmapFactory.decodeResource(getResources(), R.drawable.activity_audio_play_play_normal));
            normalRemoteView.setOnClickPendingIntent(R.id.activity_audio_notification_expand_playImage,
                    PendingIntent.getBroadcast(this, 0, new Intent(ACTION_PLAY), PendingIntent.FLAG_UPDATE_CURRENT));
        }
        normalRemoteView.setOnClickPendingIntent(R.id.activity_audio_notification_expand_nextImage,
                PendingIntent.getBroadcast(this, 0, new Intent(ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT));
        normalRemoteView.setOnClickPendingIntent(R.id.activity_audio_notification_expand_removeImage,
                PendingIntent.getBroadcast(this, 0, new Intent(ACTION_REMOVE), PendingIntent.FLAG_UPDATE_CURRENT));

        notification.contentView = normalRemoteView;
        notification.bigContentView = expandRemoteView;
        startForeground(NOTIFICATION_ID, notification);
    }

    private void initEvent() {

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextAudio();// 音乐播放完 自动下一曲
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                ToastUtil.showToast("Audio MediaPlayer Error");
                return true;
            }
        });
    }

    /**
     * 更新进度的线程
     */
    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            while (isPlaying()) {
                if (toClientMessenger != null) {

                    // service发送消息给client
                    Message toClientMsg = Message.obtain(null, MSG_PROGRESS, mediaPlayer.getCurrentPosition(), 0);
                    try {
                        toClientMessenger.send(toClientMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                SystemClock.sleep(200);
            }
        }
    };

    /**
     * 播放
     */
    private void playAudio(AudioBean audioBean, int currentPos) {
        currentAudioBean = audioBean;

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, currentAudioBean.getAudioUri());
            mediaPlayer.prepare();

            if (currentPos >= 0) {
                mediaPlayer.seekTo(currentPos);
            }
            mediaPlayer.start();

            // 开始更新进度的线程
            executorService.execute(progressRunnable);

            if (toClientMessenger != null) {
                Message toClientMsg = Message.obtain(null, MSG_PLAY);
                Bundle bundle = new Bundle();
                bundle.putParcelable(AudioBean.class.getSimpleName(), currentAudioBean);
                bundle.putInt(KEY_CURRENT_POS, mediaPlayer.getCurrentPosition());
                try {
                    toClientMsg.setData(bundle);
                    toClientMessenger.send(toClientMsg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateNotification();
    }

    /**
     * 继续播放
     */
    private void resumeAudio() {
        if (!isPlaying()) {
            mediaPlayer.start();

            // 开始更新进度的线程
            executorService.execute(progressRunnable);
        }
        updateNotification();
        if (toClientMessenger != null) {
            Message toClientMsg = Message.obtain(null, MSG_RESUME);
            try {
                toClientMessenger.send(toClientMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 暂停播放
     */
    private void pauseAudio() {
        if (isPlaying()) {
            mediaPlayer.pause();
        }
        updateNotification();
        if (toClientMessenger != null) {
            Message toClientMsg = Message.obtain(null, MSG_PAUSE);
            try {
                toClientMessenger.send(toClientMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        SharedPreferencesUtil.setLastAudioBean(currentAudioBean);
        SharedPreferencesUtil.setLastAudioDuration(mediaPlayer.getCurrentPosition());
    }

    /**
     * 下一曲
     */
    private void nextAudio() {
        int pos = audioBeanList.indexOf(currentAudioBean);
        if (pos >= audioBeanList.size() - 1) {
            playAudio(audioBeanList.get(0), -1);
        } else {
            playAudio(audioBeanList.get(pos + 1), -1);
        }
    }

    /**
     * 上一曲
     */
    private void prevAudio() {
        int pos = audioBeanList.indexOf(currentAudioBean);
        if (pos <= 0) {
            playAudio(audioBeanList.get(audioBeanList.size() - 1), -1);
        } else {
            playAudio(audioBeanList.get(pos - 1), -1);
        }
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    private boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    private void seekTo(int msec) {
        if (mediaPlayer == null) return;
        mediaPlayer.seekTo(msec);
    }

    /**
     * 服务销毁时，释放各种控件
     */
    private void release() {
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        executorService = null;


        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}