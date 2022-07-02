package com.yuyang.messi.ui.category.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.lib_base.utils.ToastUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

/**
 * 接近传感器检测物体与手机的距离，单位是厘米。
 * 一些接近传感器只能返回远和近两个状态，
 * 因此，接近传感器将最大距离返回远状态，小于最大距离返回近状态。
 * 接近传感器可用于接听电话时自动关闭LCD屏幕以节省电量。
 * 一些芯片集成了接近传感器和光线传感器两者功能。
 */
public class ProximityActivity extends AppBaseActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView textView;

    /**
     * 播放一些短的反应速度要求高的声音
     */
    private SoundPool soundPool;

    private HashMap<Integer, Integer> soundPoolMap;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sensor_normal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initSoundPool();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("距离传感器");

        textView = findViewById(R.id.activity_sensor_normal_text0);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    private void initSoundPool() {

        if (Build.VERSION.SDK_INT > 20) {

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).build())
                    .build();
        } else {

        soundPool = new SoundPool(3, AudioManager.STREAM_SYSTEM, 100);
        }

        soundPoolMap = new HashMap<>();

        try {

            soundPoolMap.put(0, soundPool.load(getAssets().openFd("sound/shake_sound_male.mp3"), 1));

            soundPoolMap.put(1, soundPool.load(getAssets().openFd("sound/shake_match.mp3"), 1));

            soundPoolMap.put(2, soundPool.load(getActivity(), R.raw.refreshing_sound, 1));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {

        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            ToastUtil.showToast("此设备没有距离传感器");
        }

        super.onResume();
    }

    protected void onPause() {

        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private long lastTime;

    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

            float[] values = event.values;

            textView.setText(values[0] + "");

            long currentTime = SystemClock.elapsedRealtime();
            long spaceTime = currentTime - lastTime;
            lastTime = currentTime;

            float distance = values[0];

            // 如果距离小于某一个距离阈值，默认是5.0f，说明手机和脸部距离贴近，应该要熄灭屏幕。
            boolean active = (distance >= 0 && distance < 10 && distance < mSensor.getMaximumRange());

            if (spaceTime > 1000) {
                if (active) {

                    /**
                     * soundId
                     * 左音量
                     * 右音量
                     * 优先级
                     * 循环次数
                     * 速率：最低0.5， 最高2， 1代表常速
                     */
                    soundPool.play(soundPoolMap.get(new Random().nextInt(3)), 1, 1, 0, 0, 1);

//                    soundPool.pause(soundPoolMap.get(0));
                }
            }

        }
    }
}