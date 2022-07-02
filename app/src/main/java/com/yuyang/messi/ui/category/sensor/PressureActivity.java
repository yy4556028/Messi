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
 * 压力传感器返回当前的压强，单位是百帕斯卡 hectopascal（hPa）
 */
public class PressureActivity extends AppBaseActivity implements SensorEventListener {

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
		headerLayout.showTitle("压力传感器");

		textView = findViewById(R.id.activity_sensor_normal_text0);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
	}

	@Override
	protected void onResume() {

		if(mSensor!=null){
			mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}else{
			ToastUtil.showToast("此设备没有压力传感器");
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

		if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {

			float[] values = event.values;
			textView.setText("X轴压力传感器的值：："+values[0]+"\nY压力传感器的值：："+values[1]+"\nZ轴压力传感器的值：："+values[2]);
		}
	}
}
