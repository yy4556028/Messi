package com.yuyang.messi.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.yuyang.messi.MessiApp;

public class GravityViewUtil {

    private final static SensorManager sensorManager = (SensorManager) MessiApp.getInstance().getSystemService(Context.SENSOR_SERVICE);

    public static void addGravityMonitor(AppCompatActivity activity, View view) {
        SensorEventListener mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (Sensor.TYPE_ACCELEROMETER != sensorEvent.sensor.getType()) {
                    return;
                }

                float[] values = sensorEvent.values;
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

                int uiRot = activity.getWindowManager().getDefaultDisplay().getRotation();
                double uiRad = Math.PI / 2 * uiRot;
                rad -= uiRad;

                float degree = (float) Math.toDegrees(rad);
                float currentDegree = view.getRotation();

                while (degree - currentDegree >= 180) {
                    degree -= 360;
                }
                while (degree - currentDegree <= -180) {
                    degree += 360;
                }

                ObjectAnimator ra = ObjectAnimator.ofFloat(view, "rotation", currentDegree, degree);

                ra.start();
                currentDegree = degree;
                view.setRotation(currentDegree = currentDegree % 360);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        activity.getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            public void onDestroy() {
                sensorManager.unregisterListener(mSensorEventListener);
                activity.getLifecycle().removeObserver(this);
            }
        });
        sensorManager.registerListener(mSensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }
}
