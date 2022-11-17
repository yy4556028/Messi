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
 * 方向传感器
 * <p/>
 * 1.1方向传感器
 * <p/>
 * 在方向传感器中values变量的3个值都表示度数，它们的含义如下：
 * <p/>
 * values[0]：该值表示方位，也就是手机绕着Z轴旋转的角度。 0表示北（North）；90表示东（East）；180表示南（South）；
 * 270表示西（West）。如果values[0]的值正好是这4个值，并且手机是水平放置，
 * 表示手机的正前方就是这4个方向。可以利用这个特性来实现电子罗盘，实例76将详细介绍电子罗盘的实现过程。
 * <p/>
 * <p/>
 * values[1]：该值表示倾斜度，或手机翘起的程度。当手机绕X轴倾斜时该值发生变化。 values[1]的取值范围是-180≤values[1]
 * ≤180。假设将手机屏幕朝上水平放在桌子上，这时如果桌子是完全水平的，
 * values[1]的值应该是0（由于很少有桌子是绝对水平的，因此，该值很可能不为0，但一般都是-5和5之间的某个值）。
 * 这时从手机顶部开始抬起，直到将手机沿X轴旋转180度（屏幕向下水平放在桌面上）。
 * 在这个旋转过程中，values[1]会在0到-180之间变化，也就是说，从手机顶部抬起时，values[1]的值会逐渐变小，直到等于-180。
 * 如果从手机底部开始抬起，直到将手机沿X轴旋转180度，这时values[1]会在0到180之间变化。
 * 也就是values[1]的值会逐渐增大，直到等于180。可以利用values[1]和下面要介绍的values[2]来测量桌子等物体的倾斜度。
 * <p/>
 * <p/>
 * values[2]：表示手机沿着Y轴的滚动角度。取值范围是-90≤values[2]≤90。假设将手机屏幕朝上水平放在桌面上，
 * 这时如果桌面是平的，values[2]的值应为0。将手机左侧逐渐抬起时，values[2]的值逐渐变小，直到手机垂直于桌面放置，
 * 这时values[2]的值是-90。将手机右侧逐渐抬起时，values[2]的值逐渐增大，直到手机垂直于桌面放置，这时values[2]的值是90。
 * 在垂直位置时继续向右或向左滚动，values[2]的值会继续在-90至90之间变化。
 *
 * @author Windows
 * <p/>
 * 以上为通过 TYPE_ORIENTATION 过得的 value
 * google 已不推荐使用，本 demo 方法为最新
 * @author yuyang
 */

public class OrientationActivity extends AppBaseActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor aSensor;
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
        headerLayout.showTitle("方向传感器");

        textView = findViewById(R.id.activity_sensor_normal_text0);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        aSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (aSensor != null && mSensor != null) {

            mSensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            ToastUtil.showToast("此设备没有加速度传感器或磁力传感器或方向传感器");
        }
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    float[] mGravity;
    float[] mGeomagnetic;

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }

        if (mGravity != null && mGeomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                // orientation contains: azimut, pitch and roll 方位角、俯仰和滚转
                textView.setText("X：" + Math.toDegrees(orientation[0]) + " 方向角东南西北"+
                        "\nY：" + Math.toDegrees(orientation[1]) + " 俯仰" +
                        "\nZ：" + Math.toDegrees(orientation[2]) + " 滚转");
            }
        }
    }
}
