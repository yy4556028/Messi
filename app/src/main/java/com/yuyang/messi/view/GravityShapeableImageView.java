package com.yuyang.messi.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;

import com.google.android.material.imageview.ShapeableImageView;

/**
 * 创建者: yuyang
 * 创建日期: 2015-10-21
 * 创建时间: 12:22
 * GravityRoundImageView: 重力感应圆形图片
 *
 * @author yuyang
 * @version 1.0
 */
public class GravityShapeableImageView extends ShapeableImageView implements SensorEventListener {

    private SensorManager sensorManager;

    // 记录图片转过的角度
    private float currentDegree = 0f;

    private boolean isStart;

    public GravityShapeableImageView(Context context) {
        super(context);
    }

    public GravityShapeableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void toggle() {
        if (!isStart) {
            start();
        } else {
            stop();
        }
    }

    public void start() {
        if (sensorManager == null)
            sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        isStart = true;
    }

    public void stop() {
        sensorManager.unregisterListener(this);
        isStart = false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {
            return;
        }

        float[] values = event.values;
        float gx = values[0];
        float gy = values[1];

        double g = Math.sqrt(gx * gx + gy * gy);

        if (g < 0.5)
            return;

        double cos = gy / g;
        if (cos > 1) {
            cos = 1;
        } else if (cos < -1) {
            cos = -1;
        }
        double rad = Math.acos(cos);
        if (gx < 0) {
            rad = 2 * Math.PI - rad;
        }

        int uiRot = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation();
        double uiRad = Math.PI / 2 * uiRot;
        rad -= uiRad;

        float degree = (float) Math.toDegrees(rad);

        while (degree - currentDegree >= 180) {
            degree -= 360;
        }
        while (degree - currentDegree <= -180) {
            degree += 360;
        }

        ObjectAnimator ra = ObjectAnimator.ofFloat(this, "rotation", currentDegree, degree);

        ra.start();
        currentDegree = degree;
        setRotation(currentDegree = currentDegree % 360);
    }
}
