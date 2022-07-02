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
 * 线性加速度传感器简称LA-sensor。
 * 线性加速度传感器是加速度传感器减去重力影响获取的数据。
 * 单位是m/s^2，坐标系统与加速度传感器相同。
 * 加速度传感器、重力传感器和线性加速度传感器的计算公式如下：
 * 加速度 = 重力 + 线性加速度
 */
public class LinearAccelerometerActivity extends AppBaseActivity implements SensorEventListener {

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
		headerLayout.showTitle("线性加速度传感器");

		textView = findViewById(R.id.activity_sensor_normal_text0);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	}

	@Override
	protected void onResume() {

		if (mSensor != null) {
			mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			ToastUtil.showToast("此设备没有线性加速度传感器");
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

		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

			float[] values = event.values;
			textView.setText("X轴线性加速度的值：："+values[0]+"\nY线性加速度的值：："+values[1]+"\nZ轴线性加速度的值：："+values[2]);
		}
	}
}
