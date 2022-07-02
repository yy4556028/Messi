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
 * 光线传感器
 * 
 * 光线传感器的类型常量是Sensor.TYPE_LIGHT。values数组只有第一个元素（values[0]）有意义。表示光线的强度。
 * 最大的值是120000.0f。Android SDK将光线强度分为不同的等级，每一个等级的最大值由一个常量表示，
 * 这些常量都定义在SensorManager类中，代码如下：
 * 
 * public static final float LIGHT_SUNLIGHT_MAX =120000.0f;
 * 
 * public static final float LIGHT_SUNLIGHT=110000.0f;
 * 
 * public static final float LIGHT_SHADE=20000.0f;
 * 
 * public static final float LIGHT_OVERCAST= 10000.0f;
 * 
 * public static final float LIGHT_SUNRISE= 400.0f;
 * 
 * public static final float LIGHT_CLOUDY= 100.0f;
 * 
 * public static final float LIGHT_FULLMOON= 0.25f;
 * 
 * public static final float LIGHT_NO_MOON= 0.001f;
 * 
 * 上面的八个常量只是临界值。读者在实际使用光线传感器时要根据实际情况确定一个范围。
 * 例如，当太阳逐渐升起时，values[0]的值很可能会超过LIGHT_SUNRISE，当values[0]的值逐渐增大时，
 * 就会逐渐越过LIGHT_OVERCAST，而达到LIGHT_SHADE，当然，如果天特别好的话，也可能会达到LIGHT_SUNLIGHT，甚至更高
 *
 * 光线感应传感器检测实时的光线强度，光强单位是lux，其物理意义是照射到单位面积上的光通量。
 * 光线感应传感器主要用于Android系统的LCD自动亮度功能。
 * 可以根据采样到的光强数值实时调整LCD的亮度。
 *
 * @author yuyang
 * 
 */
public class LightActivity extends AppBaseActivity implements SensorEventListener {

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
		headerLayout.showTitle("光传感器");

		textView = findViewById(R.id.activity_sensor_normal_text0);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
	}

	@Override
	protected void onResume() {

		if (mSensor != null) {
			mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			ToastUtil.showToast("此设备没有光传感器");
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

		if (event.sensor.getType() == Sensor.TYPE_LIGHT) {

			float[] values = event.values;
			textView.setText("亮度 : " + values[0]);
		}
	}
}
