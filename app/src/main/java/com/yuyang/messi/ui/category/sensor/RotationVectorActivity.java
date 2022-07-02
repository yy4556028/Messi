package com.yuyang.messi.ui.category.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.lib_base.utils.ToastUtil;

/**
 * 旋转矢量传感器简称RV-sensor。
 * 旋转矢量代表设备的方向，是一个将坐标轴和角度混合计算得到的数据。
 * RV-sensor输出三个数据：
 * x*sin(theta/2)
 * y*sin(theta/2)
 * z*sin(theta/2)
 * sin(theta/2)是RV的数量级。
 * RV的方向与轴旋转的方向相同。
 * RV的三个数值，与cos(theta/2)组成一个四元组。
 * RV的数据没有单位，使用的坐标系与加速度相同。
 * 举例：
 * sensors_event_t.data[0] = x*sin(theta/2)
 * sensors_event_t.data[1] = y*sin(theta/2)
 * sensors_event_t.data[2] = z*sin(theta/2)
 * sensors_event_t.data[3] = cos(theta/2)
 * GV、LA和RV的数值没有物理传感器可以直接给出，
 * 需要G-sensor、O-sensor和Gyro-sensor经过算法计算后得出。
 * 算法一般是传感器公司的私有产权
 */
public class RotationVectorActivity extends AppBaseActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private TextView textView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sensor_normal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("旋转矢量传感器");

        textView = findViewById(R.id.activity_sensor_normal_text0);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    @Override
    protected void onResume() {

        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            ToastUtil.showToast("此设备没有旋转矢量传感器");
        }

        super.onResume();
    }

    protected void onPause() {

        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            float[] values = event.values;
            textView.setText("X轴旋转矢量的值：："+values[0]+"\nY旋转矢量的值：："+values[1]+"\nZ轴旋转矢量的值：："+values[2]);
        }
    }
}