package com.yuyang.messi.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.WindowManager;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;

public class GravityViewUtil {

    private final static SensorManager sensorManager = (SensorManager) MessiApp.getInstance().getSystemService(Context.SENSOR_SERVICE);

    public static void addGravityMonitor(LifecycleOwner lifecycleOwner, View view) {
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

                final WindowManager windowManager = (WindowManager) MessiApp.getInstance().getSystemService(Context.WINDOW_SERVICE);
                int uiRot = windowManager.getDefaultDisplay().getRotation();
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

                ObjectAnimator objectAnimator = (ObjectAnimator) view.getTag(R.id.id_gravityviewutil_animator);
                if (objectAnimator != null) {
                    objectAnimator.cancel();
                }

                objectAnimator = ObjectAnimator.ofFloat(view, "rotation", view.getRotation(), degree);
                view.setTag(R.id.id_gravityviewutil_animator, objectAnimator);
                objectAnimator.start();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        lifecycleOwner.getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            public void onDestroy() {
                sensorManager.unregisterListener(mSensorEventListener);
                lifecycleOwner.getLifecycle().removeObserver(this);
            }
        });
        sensorManager.registerListener(mSensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public static void addGravityMonitor2(LifecycleOwner lifecycleOwner, View view) {
        SensorEventListener mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (Sensor.TYPE_GRAVITY != sensorEvent.sensor.getType()) {
                    return;
                }

                float[] values = sensorEvent.values;

                float toRotationX = values[1] * 8;
                float toRotationY = values[0] * 8;

//                AnimatorSet animatorSet = new AnimatorSet();
//                animatorSet.playTogether(
//                        ObjectAnimator.ofFloat(this, "rotationX", view.getRotationX(), toRotationX),
//                        ObjectAnimator.ofFloat(this, "rotationY", view.getRotationY(), toRotationY));
//                animatorSet.start();

                view.setRotationX(toRotationX);
                view.setRotationY(toRotationY);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        lifecycleOwner.getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            public void onDestroy() {
                sensorManager.unregisterListener(mSensorEventListener);
                lifecycleOwner.getLifecycle().removeObserver(this);
            }
        });
        sensorManager.registerListener(mSensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_GAME);
    }
}
