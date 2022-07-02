package com.yuyang.messi.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;

/**
 * http://www.bubuko.com/infodetail-2018479.html
 */
public class NotificationUtil {

    private static final int FLAG = Notification.FLAG_INSISTENT;
    private static final String channelId = "messi_notify";//过长会引发震动，我也很无奈
    private static int notifyID = 1;
    private static int foregroundNotifyID = 0555;

    private static NotificationManager notificationManager;
    private static NotificationManagerCompat mNotificationManager;

    private static AudioManager audioManager;
    private static Vibrator vibrator;

    private long lastNotifiyTime;

    private Ringtone ringtone = null;

    private static void init(Context ctx) {

        notificationManager = (NotificationManager) MessiApp.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager = NotificationManagerCompat.from(MessiApp.getInstance());

        audioManager = (AudioManager) MessiApp.getInstance().getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) MessiApp.getInstance().getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                channelId,
                "Messi",
                NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("MessiDescription");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{0});
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showNotification(Context context, Class activityClass, String message, boolean isForeground) {

        if (notificationManager == null) {
            init(context);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)// ticker icon 24 * 24
            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))// 通知图标 默认是应用图标
            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
            .setTicker("ticker")// 状态栏提示
            .setContentTitle(context.getString(R.string.app_name))// 通知 title
            .setContentText("ContentText")// 通知内容
            .setLights(Color.argb(255, 218, 165, 32), 400, 2000)
            .setProgress(100, 50, false)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(
                // CATEGORY_ALARM(alarms or timers), REMINDER(user requested reminder),EVENT,MESSAGE,CALL,EMAIL,SOCIAL,RECOMMENDATION(TV?)
                /**
                 *  NotificationCompat.CATEGORY_ALARM : alarms or timers
                 *  NotificationCompat.CATEGORY_REMINDER : user requested reminder
                 *  NotificationCompat.CATEGORY_MESSAGE
                 *  NotificationCompat.CATEGORY_...
                 */
                NotificationCompat.CATEGORY_ALARM
            )
            .setOnlyAlertOnce(true);//同id的notification, 声音、振动、ticker 仅第一次

        if (activityClass != null) {
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, activityClass), PendingIntent.FLAG_ONE_SHOT);
            mBuilder.setContentIntent(contentIntent);
        }

        Notification notification = mBuilder.build();

        if (isForeground) {
            notificationManager.notify(foregroundNotifyID, notification);
            notificationManager.cancel(foregroundNotifyID);
        } else {
            notificationManager.notify(notifyID, notification);
        }
//
//        context.getSystemService(Context.ALARM_SERVICE)
//                .set(AlarmManager.ELAPSED_REALTIME,
//                        SystemClock.elapsedRealtime() + 60 * 60 * 1000,
//                        makeCancelAllPendingIntent(ctxt));
    }

    /**
     * 手机震动和声音提示
     */
    public void viberateAndPlayTone() {

        if (System.currentTimeMillis() - lastNotifiyTime < 1000) {
            // received new messages within 1 seconds, skip play ringtone
            return;
        }

        lastNotifiyTime = System.currentTimeMillis();

        // 判断是否处于静音模式
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            return;
        }
        if (true) {
            long[] pattern = new long[]{0, 180, 80, 120};
            vibrator.vibrate(pattern, -1);
        }

        if (ringtone == null) {
            Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            ringtone = RingtoneManager.getRingtone(MessiApp.getInstance(), notificationUri);
            if (ringtone == null) {
                return;
            }
        }

        if (!ringtone.isPlaying()) {
            String vendor = Build.MANUFACTURER;

            ringtone.play();
            // for samsung S3, we meet a bug that the phone will
            // continue ringtone without stop
            // so add below special handler to stop it after 3s if
            // needed
            if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                Thread ctlThread = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            if (ringtone.isPlaying()) {
                                ringtone.stop();
                            }
                        } catch (Exception e) {
                        }
                    }
                };
                ctlThread.run();
            }
        }

    }

    /**
     * 通过反射获取通知的开关状态
     */
    public static boolean isNotificationEnabled(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();

//        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//        ApplicationInfo appInfo = context.getApplicationInfo();
//        String pkg = context.getApplicationContext().getPackageName();
//        int uid = appInfo.uid;
//        Class appOpsClass = null; /* Context.APP_OPS_MANAGER */
//        try {
//            appOpsClass = Class.forName(AppOpsManager.class.getName());
//            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
//            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
//            int value = (int)opPostNotificationValue.get(Integer.class);
//            return ((int)checkOpNoThrowMethod.invoke(mAppOps,value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return true;
    }

}
