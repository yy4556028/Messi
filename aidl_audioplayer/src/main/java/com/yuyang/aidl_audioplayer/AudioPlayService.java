package com.yuyang.aidl_audioplayer;

import android.annotation.SuppressLint;
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
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;
import com.yuyang.lib_base.BaseApp;
import com.yuyang.lib_base.gsonSerializer.UriSerializer;
import com.yuyang.lib_base.utils.LogUtil;
import com.yuyang.lib_base.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 音乐播放服务
 */
public class AudioPlayService extends Service {

    private static final String TAG = AudioPlayService.class.getSimpleName();

    private final RemoteCallbackList<IClientCallback> callbackList = new RemoteCallbackList<>();

    public static final String KEY_AUDIO_LIST = "key_audio_list";
    public static final String KEY_CURRENT_BEAN = "key_current_bean";
    public static final String KEY_CURRENT_POS = "key_current_pos";

    private static final String ACTION_PLAY = "action.play";
    private static final String ACTION_PAUSE = "action.pause";
    private static final String ACTION_NEXT = "action.next";
    private static final String ACTION_PREV = "action.prev";
    private static final String ACTION_REMOVE = "action.remove";

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private final String channelId = "AudioPlayer";//过长会引发震动，我也很无奈
    private final int NOTIFICATION_ID = 33;

    private MediaPlayer mMediaPlayer = new MediaPlayer();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private PlayReceiver playReceiver;

    private List<AudioBean> audioBeanList;

    private AudioBean currentAudioBean;

    private final Object threadLock = new Object();

    private final Thread progressThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {

                while (!isPlaying()) {//在while循环里而不是if语句下使用wait，这样，会在线程暂停恢复后都检查wait的条件，并在条件实际上并未改变的情况下处理唤醒通知
                    synchronized (threadLock) {
                        try {
                            threadLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                LogUtil.d("progressThread", progressThread.getId() + " - " + System.currentTimeMillis());
                synchronized (callbackList) {
                    int num = callbackList.beginBroadcast();
                    while (num > 0) {
                        num--;
                        try {
                            callbackList.getBroadcastItem(num).onProgress(mMediaPlayer.getCurrentPosition());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    callbackList.finishBroadcast();
                }
                SystemClock.sleep(200);
            }
        }
    });

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

    public static void startService(Context context, ArrayList<AudioBean> audioBeanList, AudioBean audioBean, int currentPos) {
        Intent intent = new Intent(context, AudioPlayService.class);
        intent.putParcelableArrayListExtra(KEY_AUDIO_LIST, audioBeanList);
        intent.putExtra(KEY_CURRENT_BEAN, (Parcelable) audioBean);
        intent.putExtra(KEY_CURRENT_POS, currentPos);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            BaseApp.getInstance().startForegroundService(intent);
        } else {
            BaseApp.getInstance().startService(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        String rootDir = MMKV.initialize(this);
        LogUtil.i("MMKV", "rootDir = " + rootDir);

        initNotification();

        playReceiver = new PlayReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_PLAY);
        mFilter.addAction(ACTION_PAUSE);
        mFilter.addAction(ACTION_NEXT);
        mFilter.addAction(ACTION_PREV);
        mFilter.addAction(ACTION_REMOVE);
        registerReceiver(playReceiver, mFilter);

        initEvent();

        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriSerializer()).create();
        audioBeanList = gson.fromJson(MMKV.defaultMMKV().decodeString(KEY_AUDIO_LIST), new TypeToken<List<AudioBean>>() {
        }.getType());
        currentAudioBean = MMKV.defaultMMKV().decodeParcelable(KEY_CURRENT_BEAN, AudioBean.class);
        int duration = MMKV.defaultMMKV().decodeInt(KEY_CURRENT_POS);
        if (currentAudioBean != null) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(this, currentAudioBean.getAudioUri());
                mMediaPlayer.prepare();

                if (duration >= 0) {
                    mMediaPlayer.seekTo(duration);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executorService.execute(progressThread);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        AudioBean audioBean = null;
        if (intent != null) {
            audioBeanList = intent.getParcelableArrayListExtra(KEY_AUDIO_LIST);
            audioBean = intent.getParcelableExtra(KEY_CURRENT_BEAN);
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriSerializer()).create();
        MMKV.defaultMMKV().encode(KEY_AUDIO_LIST, gson.toJson(audioBeanList));
        playAudio(audioBean == null ? audioBeanList.get(0) : audioBean, intent.getIntExtra(KEY_CURRENT_POS, -1));
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return new MyBinder();
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
            MMKV.defaultMMKV().encode(KEY_CURRENT_BEAN, currentAudioBean);
            MMKV.defaultMMKV().encode(KEY_CURRENT_POS, mMediaPlayer.getCurrentPosition());
        }

        release();
        if (playReceiver != null)
            unregisterReceiver(playReceiver);// 解除广播注册

        //取消前台进程
        stopForeground(true);
    }

    @SuppressLint("NotificationTrampoline")
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

        notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.douban)//设置状态栏的通知图标
                .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.douban))//设置通知栏横条的图标
                .setAutoCancel(false)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(true)//禁止滑动删除
                .setShowWhen(true)//右上角的时间显示
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("ContentText")
                .setContentIntent(createContentIntent())
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT);//设置该通知优先级
    }

    private PendingIntent createContentIntent() {
        String mode = "广播启动";
        if ("广播启动".equals(mode)) {
            Intent broadcastIntent = new Intent(this, AudioNotifyClickReceiver.class);
            return PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else if ("创建返回栈".equals(mode)) {//https://www.jianshu.com/p/678e2322fd41
            Class audioClass = null;
            try {
                audioClass = getClassLoader().loadClass(
                        "com.yuyang.messi.ui.media.audio.aidl.AudioActivity");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            //添加Activity到返回栈
            stackBuilder.addParentStack(audioClass);
            //添加Intent到栈顶
            stackBuilder.addNextIntent(new Intent(this, audioClass));
            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            //android:parentActivityName=".ui.main.MainActivity"，关闭页面进入MainActivity
        } else {
            return PendingIntent.getActivity(
                    this,
                    0,
                    getPackageManager().getLaunchIntentForPackage("com.yuyang.messi"),
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
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

        mMediaPlayer.setOnCompletionListener(mp -> {
            nextAudio();// 音乐播放完 自动下一曲
        });

        mMediaPlayer.setOnErrorListener((mp, what, extra) -> {
            ToastUtil.showToast("媒体文件错误");
            return true;
        });
    }

    /**
     * 播放
     */
    private void playAudio(AudioBean audioBean, int currentPos) {
        currentAudioBean = audioBean;

        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(this, currentAudioBean.getAudioUri());
            mMediaPlayer.prepare();

            if (currentPos >= 0) {
                mMediaPlayer.seekTo(currentPos);
            }
            mMediaPlayer.start();

            synchronized (threadLock) {
                threadLock.notify();
            }

            synchronized (callbackList) {
                int num = callbackList.beginBroadcast();
                while (num > 0) {
                    num--;
                    try {
                        callbackList.getBroadcastItem(num).onPlayChange(currentAudioBean, mMediaPlayer.getCurrentPosition(), true);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                callbackList.finishBroadcast();
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
            mMediaPlayer.start();

            synchronized (threadLock) {
                threadLock.notify();
            }
        }
        updateNotification();
        synchronized (callbackList) {
            int num = callbackList.beginBroadcast();
            while (num > 0) {
                num--;
                try {
                    callbackList.getBroadcastItem(num).onPlayChange(currentAudioBean, mMediaPlayer.getCurrentPosition(), true);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            callbackList.finishBroadcast();
        }
    }

    /**
     * 暂停播放
     */
    private void pauseAudio() {
        if (isPlaying()) {
            mMediaPlayer.pause();
        }
        updateNotification();

        synchronized (callbackList) {
            int num = callbackList.beginBroadcast();
            while (num > 0) {
                num--;
                try {
                    callbackList.getBroadcastItem(num).onPlayChange(currentAudioBean, mMediaPlayer.getCurrentPosition(), false);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            callbackList.finishBroadcast();
        }

        MMKV.defaultMMKV().encode(KEY_CURRENT_BEAN, currentAudioBean);
        MMKV.defaultMMKV().encode(KEY_CURRENT_POS, mMediaPlayer.getCurrentPosition());
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
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    private void seekTo(int msec) {
        if (mMediaPlayer == null) return;
        mMediaPlayer.seekTo(msec);
    }

    /**
     * 服务销毁时，释放各种控件
     */
    private void release() {
        progressThread.interrupt();
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        executorService = null;


        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    class MyBinder extends IAudioPlayerAidlInterface.Stub {

        @Override
        public void registerCallback(IClientCallback callback) throws RemoteException {
            callbackList.register(callback);
            callback.onPlayChange(currentAudioBean, mMediaPlayer.getCurrentPosition(), isPlaying());
        }

        @Override
        public void unregisterCallback(IClientCallback callback) throws RemoteException {
            callbackList.unregister(callback);
        }

        @Override
        public void playAudioList(List<AudioBean> beanList, int playIndex) throws RemoteException {
            audioBeanList = beanList;
            Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriSerializer()).create();
            MMKV.defaultMMKV().encode(KEY_AUDIO_LIST, gson.toJson(audioBeanList));
            AudioPlayService.this.playAudio(audioBeanList.get(playIndex), -1);
        }

        @Override
        public void resumeAudio() throws RemoteException {
            AudioPlayService.this.resumeAudio();
        }

        @Override
        public void pauseAudio() throws RemoteException {
            AudioPlayService.this.pauseAudio();
        }

        @Override
        public void nextAudio() throws RemoteException {
            AudioPlayService.this.nextAudio();
        }

        @Override
        public void prevAudio() throws RemoteException {
            AudioPlayService.this.prevAudio();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return AudioPlayService.this.isPlaying();
        }

        @Override
        public void seekTo(int msec) throws RemoteException {
            AudioPlayService.this.seekTo(msec);
        }
    }
}