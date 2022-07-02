package com.yuyang.messi.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import com.yuyang.messi.ui.category.sensor.demo.PedometerDetector;

/**
 * 计步器 service
 */
public class PedometerService extends Service {

	private SensorManager sensorManager;
	private PedometerDetector detector;// 传感器监听对象

	private long startTime; // s
	private long stopTime; // 停止时间 兼 判断是否在监听

//	private PowerManager mPowerManager;// 电源管理服务
//	private WakeLock mWakeLock;// 屏幕灯

	public class PedometerBinder extends Binder {

		public PedometerService getService() {
			return PedometerService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new PedometerBinder();
	}

//		if (mWakeLock != null) {
//			mWakeLock.release();
//		}

	@Override
	public void onCreate() {
		super.onCreate();
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
	}

//		// 电源管理服务
//		mPowerManager = (PowerManager) this
//				.getSystemService(Context.POWER_SERVICE);
//		mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
//				| PowerManager.ACQUIRE_CAUSES_WAKEUP, "S");
//		mWakeLock.acquire();


	public void start() {

		if (detector == null) {
			// 创建监听器类，实例化监听对象
			detector = new PedometerDetector();
		}

		// 注册传感器，注册监听器
		sensorManager.registerListener(detector,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);

		// 如果暂停
		if (stopTime != 0) {
			startTime = System.currentTimeMillis() - (stopTime - startTime);
			stopTime = 0;
		// 如果已开始
		} else if (startTime == 0){
			startTime = System.currentTimeMillis();
			detector.CURRENT_STEP = 0;
		}

	}

	public void stop() {

		if (detector != null) {
			sensorManager.unregisterListener(detector);
		}

		if (stopTime == 0)
			stopTime = System.currentTimeMillis();

	}

	public void clear() {

		detector.CURRENT_STEP = -1;
		detector = null;
		startTime = 0;
		stopTime = 0;
	}

	public int getTime() {

		if (stopTime == 0) {
			return (int) (System.currentTimeMillis() - startTime);
		} else {
			return (int) (stopTime - startTime);
		}
	}

	public boolean isStepping() {
		return startTime != 0 && stopTime == 0;
	}

}
