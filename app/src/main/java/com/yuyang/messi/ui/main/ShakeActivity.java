package com.yuyang.messi.ui.main;

import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class ShakeActivity extends AppBaseActivity {

    private ImageView shakeImage;

    // 上次摇一摇的时间
    private static long lastShakeTime = 0;

    private final SensorManager mSensorManager = (SensorManager) MessiApp.getInstance().getSystemService(Service.SENSOR_SERVICE);

    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastShakeTime < 5000)//控制摇动间隔时间 5000 ms
                return;

            int sensorType = event.sensor.getType();

            // values[0]: X轴, values[1]:Y轴, values[2]:Z轴
            float[] values = event.values;

            // 加速器
            if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                /**
                 * 正常情况下，任意轴数值最大就在9.8~10之间，只有在突然摇动手机
                 * 的时候，瞬间加速度才会突然增大或减少.
                 * 监听任意轴的加速度大于17即可
                 */
                if (Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math.abs(values[2]) > 17) {

                    lastShakeTime = System.currentTimeMillis();

                    if (Build.VERSION.SDK_INT >= 26) {
//						((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
                        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createWaveform(new long[]{500, 200, 500, 200}, -1));
                    } else {
                        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(new long[]{500, 200, 500, 200}, -1);
                    }
                    shakeImage.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //当传感器精度改变时回调该方法, do nothing
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shake;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * SensorManager.SENSOR_DELAY_NORMAL:默认，标准延迟，一般益智类游戏或EASY级别的游戏可用
         * SensorManager.SENSOR_DELAY_UI:使用传感器更新UI
         * SensorManager.SENSOR_DELAY_FASTEST:最低延迟，一般不特别灵敏的处理不推荐使用，电力消耗大,传递大量原始数据
         * SensorManager.SENSOR_DELAY_GAME:用传感器开发游戏, 实时性较高
         * 根据不同应用，需要的反应速度不同，具体视实际情况设定
         */
        mSensorManager.registerListener(mSensorEventListener
                , mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("摇一摇");
        shakeImage = findViewById(R.id.activity_shake_image);
    }
}
