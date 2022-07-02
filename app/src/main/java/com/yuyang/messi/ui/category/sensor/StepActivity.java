package com.yuyang.messi.ui.category.sensor;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.service.StepService;
import com.yuyang.messi.ui.base.AppBaseActivity;

/**
 * 在Android4.4 Kitkat 新增的STEP DETECTOR 以及 STEP COUNTER传感器。
 * 但是！Android的这个传感器虽然可以计步，但是所记录的步数是从你开机之时开始计算，不断累加，隔天也不会清零，
 * 并且，一旦关机后，传感器记录的数据也就清空了！
 */
public class StepActivity extends AppBaseActivity {

    private TextView todayText;

    private ServiceConnection conn = new ServiceConnection() {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepService stepService = ((StepService.StepBinder) service).getService();
            todayText.setText(stepService.getStepCount() + "");
            //设置步数监听回调
            stepService.registerCallback(new StepService.UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount) {
                    todayText.setText(stepCount + "");
                }
            });
        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_step;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        startService(new Intent(this, StepService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getActivity(), StepService.class), conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("Step");

        todayText = findViewById(R.id.activity_step_todayText);
        todayText.setText("0");
    }

    /**
     * 判断该设备是否支持计歩
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isSupportStepCountSensor(Context context) {
        // 获取传感器管理器的实例
        SensorManager sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        return countSensor != null || detectorSensor != null;
    }
}