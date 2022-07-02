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
 * 重力传感器
 * <p/>
 * 加速度传感器的类型常量是Sensor.TYPE_GRAVITY。重力传感器与加速度传感器使用同一套坐标系。
 * values数组中三个元素分别表示了X、Y、Z轴的重力大小。Android SDK定义了一些常量，
 * 用于表示星系中行星、卫星和太阳表面的重力。下面就来温习一下天文知识，将来如果在地球以外用Android手机，也许会用得上。
 * <p/>
 * public static final float GRAVITY_SUN= 275.0f;
 * <p/>
 * public static final float GRAVITY_MERCURY= 3.70f;
 * <p/>
 * public static final float GRAVITY_VENUS= 8.87f;
 * <p/>
 * public static final float GRAVITY_EARTH= 9.80665f;
 * <p/>
 * public static final float GRAVITY_MOON= 1.6f;
 * <p/>
 * public static final float GRAVITY_MARS= 3.71f;
 * <p/>
 * public static final float GRAVITY_JUPITER= 23.12f;
 * <p/>
 * public static final float GRAVITY_SATURN= 8.96f;
 * <p/>
 * public static final float GRAVITY_URANUS= 8.69f;
 * <p/>
 * public static final float GRAVITY_NEPTUNE= 11.0f;
 * <p/>
 * public static final float GRAVITY_PLUTO= 0.6f;
 * <p/>
 * public static final float GRAVITY_DEATH_STAR_I= 0.000000353036145f;
 * <p/>
 * public static final float GRAVITY_THE_ISLAND= 4.815162342f;
 *
 *
 * 重力传感器简称GV-sensor，输出重力数据。
 * 在地球上，重力数值为9.8，单位是m/s^2。
 * 坐标系统与加速度传感器相同。
 * 当设备复位时，重力传感器的输出与加速度传感器相同。
 *
 * @author yuyang
 */

public class GravityActivity extends AppBaseActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mGravity;

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
        headerLayout.showTitle("重力传感器");

        textView = findViewById(R.id.activity_sensor_normal_text0);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    @Override
    protected void onResume() {

        if (mGravity != null) {
            mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            ToastUtil.showToast("此设备没有重力传感器");
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

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {

            float[] values = event.values;

            textView.setText("X轴重力的值：" + values[0] + "\nY轴重力的值：" + values[1] + "\nZ轴重力的值：" + values[2]);
        }
    }

    /**
     * 当 x=y=0 时，手机处于水平放置状态。
     * 当 x=0 并且 y>0 时，手机顶部的水平位置要大于底部，也就是一般接听电话时手机所处的状态。
     * 当 x=0 并且 y<0 时，手机顶部的水平位置要小于底部。手机一般很少处于这种状态。
     * 当 y=0 并且 x>0 时，手机右侧的水平位置要大于左侧，也就是右侧被抬起。
     * 当 y=0 并且 x<0 时，手机右侧的水平位置要小于左侧，也就是左侧被抬起。
     * 当 z=0 时，手机平面与水平面垂直。
     * 当 z>0 时，手机屏幕朝上。
     * 当 z<0 时，手机屏幕朝下
     */
}
