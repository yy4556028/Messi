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
 * 陀螺仪传感器叫做Gyro-sensor，返回x、y、z三轴的角加速度数据。
 * 角加速度的单位是radians/second。
 * 根据Nexus S手机实测：
 * 水平逆时针旋转，Z轴为正。
 * 水平逆时针旋转，z轴为负。
 * 向左旋转，y轴为负。
 * 向右旋转，y轴为正。
 * 向上旋转，x轴为负。
 * 向下旋转，x轴为正。
 * ST的L3G系列的陀螺仪传感器比较流行，iphone4和google的nexus s中使用该种传感器。
 * 
 * @author yuyang
 * 
 */

public class GyroscopeActivity extends AppBaseActivity implements SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mSensor;

	private TextView textView;
	private TextView textView1;

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
		headerLayout.showTitle("陀螺仪传感器");

		textView = findViewById(R.id.activity_sensor_normal_text0);
		textView1 = findViewById(R.id.activity_sensor_normal_text1);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
	}

	@Override
	protected void onResume() {
		if (mSensor != null) {
			mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			ToastUtil.showToast("此设备没有陀螺仪传感器");
		}
		super.onResume();
	}

	protected void onPause() {

		mSensorManager.unregisterListener(this);
		super.onPause();
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	private float timestamp;

	float[] angle = new float[3];

	private static final float NS2S = 1.0f / 1000000000.0f;

	/*
	 * 代码中通过陀螺仪传感器相邻两次获得数据的时间差（dT）来分别计算在这段时间内手机延X、
	 * Y、Z轴旋转的角度，并将值分别累加到angle数组的不同元素上。
	 */
	//传感器数据变化时调用 
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

			float[] values=event.values;

			textView.setText("X轴旋转的角速度：：" + values[0] + "\nY轴旋转的角速度：：" + values[1] + "\nZ轴旋转的角速度：：" + values[2]);

			if (timestamp != 0) {

				final float dT = (event.timestamp - timestamp) * NS2S;

				angle[0] += event.values[0] * dT;

				angle[1] += event.values[1] * dT;

				angle[2] += event.values[2] * dT;

				textView1.setText("X轴旋转的角速度：" + angle[0] + ":Y轴旋转的角速度:" + angle[1] + ":Z轴旋转的角速度:" + angle[2]);
			}

			timestamp = event.timestamp;// event.timestamp表示当前的时间，单位是纳秒（1百万分之一毫秒）
		}
	}
}
