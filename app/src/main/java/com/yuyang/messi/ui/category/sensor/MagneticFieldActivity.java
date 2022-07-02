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
 * 磁力传感器
 *
 * 磁力传感器简称为M-sensor，返回x、y、z三轴的环境磁场数据。
 * 该数值的单位是微特斯拉（micro-Tesla），用uT表示。
 * 单位也可以是高斯（Gauss），1Tesla=10000Gauss。
 * 硬件上一般没有独立的磁力传感器，磁力数据由电子罗盘传感器提供（E-compass）。
 * 电子罗盘传感器同时提供下文的方向传感器数据。
 */
public class MagneticFieldActivity extends AppBaseActivity implements SensorEventListener {

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
		headerLayout.showTitle("磁力传感器");

		textView = findViewById(R.id.activity_sensor_normal_text0);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}

	@Override
	protected void onResume() {

		if (mSensor != null) {
			mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			ToastUtil.showToast("此设备没有磁力传感器");
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

		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

			float[] values = event.values;
			textView.setText("X轴磁力传感器的值：：" + values[0] + "\nY磁力传感器的值：：" + values[1] + "\nZ轴磁力传感器的值：：" + values[2]);
		}
	}
}
