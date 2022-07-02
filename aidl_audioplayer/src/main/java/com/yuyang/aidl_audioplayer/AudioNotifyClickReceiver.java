package com.yuyang.aidl_audioplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yuyang.lib_base.utils.AppUtils;
import com.yuyang.lib_base.utils.LogUtil;

public class AudioNotifyClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppUtils.isAppRunning(context, "com.yuyang.messi")) {
            LogUtil.i("AudioNotifyClickReceiver", "App is Running");

            Class mainClass = null;
            Class audioClass = null;
            try {
                mainClass = context.getClassLoader().loadClass(
                        "com.yuyang.messi.kotlinui.main.MainActivity");
                audioClass = context.getClassLoader().loadClass(
                        "com.yuyang.messi.ui.media.audio.aidl.AudioActivity");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Intent mainIntent = new Intent(context, mainClass);
            Intent audioIntent = new Intent(context, audioClass);

            Intent[] intents = {
//                    mainIntent,
                    audioIntent};
            intents[0].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivities(intents);
        } else {
            LogUtil.i("AudioNotifyClickReceiver", "App is not Running");
            //如果app进程已经被杀死，先重新启动app，将DetailActivity的启动参数传入Intent中，参数经过
            //SplashActivity传入MainActivity，此时app的初始化已经完成，在MainActivity中就可以根据传入
            // 参数跳转到AudioActivity中去了
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.yuyang.messi");
            launchIntent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

            Class audioClass = null;
            try {
                audioClass = context.getClassLoader().loadClass(
                        "com.yuyang.messi.ui.media.audio.aidl.AudioActivity");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Intent audioIntent = new Intent(context, audioClass);
            launchIntent.putExtra("key_launch_intent", audioIntent);
            context.startActivity(launchIntent);
        }
    }
}
