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
 * 加速度传感器
 * onSensorChanged方法只有一个SensorEvent类型的参数event，其中SensorEvent类有一个values变量非常重要，
 * 该变量的类型是float[]。但该变量最多只有3个元素，而且根据传感器的不同，values变量中元素所代表的含义也不同。
 * 该传感器的values变量的3个元素值分别表示X、Y、Z轴的加速值。例如，水平放在桌面上的手机从左侧向右侧移动，
 * values[0]为负值；从右向左移动，values[0]为正值。读者可以通过本节的例子来体会加速传感器中的值的变化。
 * 要想使用相应的传感器，仅实现SensorEventListener接口是不够的，还需要使用下面的代码来注册相应的传感器
 *
 * 加速度传感器又叫G-sensor，返回x、y、z三轴的加速度数值。
 * 该数值包含地心引力的影响，单位是m/s^2。
 * 将手机平放在桌面上，x轴默认为0，y轴默认0，z轴默认9.81。
 * 将手机朝下放在桌面上，z轴为-9.81。
 * 将手机向左倾斜，x轴为正值。
 * 将手机向右倾斜，x轴为负值。
 * 将手机向上倾斜，y轴为负值。
 * 将手机向下倾斜，y轴为正值。
 * 加速度传感器可能是最为成熟的一种mems产品，市场上的加速度传感器种类很多。
 * 手机中常用的加速度传感器有BOSCH（博世）的BMA系列，AMK的897X系列，ST的LIS3X系列等。
 * 这些传感器一般提供±2G至±16G的加速度测量范围，采用I2C或SPI接口和MCU相连，数据精度小于16bit。
 *
 * @author yuyang
 */
public class AccelerometerActivity extends AppBaseActivity implements SensorEventListener {

    private TextView textView;

    private TextView textView1;

    private Sensor mAccelerometer;

    private Sensor mGravity;

    private SensorManager mSensorManager;

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
        headerLayout.showTitle("加速度传感器");

        textView = findViewById(R.id.activity_sensor_normal_text0);
        textView1 = findViewById(R.id.activity_sensor_normal_text1);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    @Override
    protected void onResume() {
        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            ToastUtil.showToast("此设备没有加速度传感器");
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

            float[] values = event.values;
            textView.setText("TYPE_ACCELEROMETER\n" + "X轴加速度的值：：" + values[0] + "\nY轴加速度的值：：" + values[1] + "\nZ轴加速度的值：：" + values[2]);
        }

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            float[] values = event.values;
            textView1.setText("TYPE_GRAVITY\n" + "X轴重力的值：：" + values[0] + "\nY轴重力的值：：" + values[1] + "\nZ轴重力的值：：" + values[2]);
        }
    }

}
