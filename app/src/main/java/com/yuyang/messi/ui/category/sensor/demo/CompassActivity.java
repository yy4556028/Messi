package com.yuyang.messi.ui.category.sensor.demo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.lib_base.utils.ToastUtil;

/**
 * 创建者: yuyang
 * 创建日期: 2015-08-19
 * 创建时间: 09:22
 * CompassActivity: 指南针
 *
 * @author yuyang
 * @version 1.0
 */

public class CompassActivity extends AppBaseActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor aSensor;
    private Sensor mSensor;

    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];
    float[] values = new float[3];
    float[] r = new float[9];

    private TextView textView;
    private ImageView compass;

    // 记录指南针图片转过的角度
    private float currentDegree = 0f;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sensor_compass;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("指南针");

        textView = findViewById(R.id.activity_sensor_compass_text);
        compass = findViewById(R.id.activity_sensor_compass_image);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        aSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {

        if (aSensor != null && mSensor != null) {

            mSensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            ToastUtil.showToast("此设备没有加速度传感器或磁力传感器");
        }
        super.onResume();
    }

    protected void onPause() {

        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = event.values;
        }

        calculateOrientation();
    }

    private void calculateOrientation() {

        // 调用 getRotationMatrix 获取变换矩阵R[]
        SensorManager.getRotationMatrix(r, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(r, values);

        float degree = (float) Math.toDegrees(values[0]);

        // 创建旋转动画（反向转过degree度）
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        // 设置动画的持续时间
        ra.setDuration(200);
        // 设置动画结束后的保留状态
        // ra.setFillAfter(true);
        // 启动动画
        compass.startAnimation(ra);
        currentDegree = -degree;

        values[0] = (degree + 360) % 360;

        if (values[0] >= 355 || values[0] <= 5) {
            textView.setText("正北");
        } else if (values[0] > 5 && values[0] < 85) {
            textView.setText("东北");
        } else if (values[0] >= 85 && values[0] <= 95) {
            textView.setText("正东");
        } else if (values[0] > 95 && values[0] < 175) {
            textView.setText("东南");
        } else if (values[0] >= 175 && values[0] <= 185) {
            textView.setText("正南");
        } else if (values[0] > 185 && values[0] < 265) {
            textView.setText("西南");
        } else if (values[0] >= 265 && values[0] <= 275) {
            textView.setText("正西");
        } else if (values[0] > 275 && values[0] < 355) {
            textView.setText("西北");
        }

//        setTitle(values[0] + "");
    }
}
