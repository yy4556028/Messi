package com.yuyang.messi.ui.category.sensor;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.SensorRecyclerViewAdapter;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.ui.category.sensor.demo.CompassActivity;
import com.yuyang.messi.ui.category.sensor.demo.PedometerActivity;

import java.util.Arrays;
import java.util.List;

/**
 * SensorManager.SENSOR_DELAY_FASTEST   0ms
 * SensorManager.SENSOR_DELAY_GAME      20ms
 * SensorManager.SENSOR_DELAY_UI        60ms
 * SensorManager.SENSOR_DELAY_NORMAL    200ms
 * <p>
 * 感应检测Sensor的硬件检测组件受不同的厂商提供。你可以采用Sensor的getVendor(),Sensor()的getName()和Sensor的getVersion()方法来取得 厂商的名称、产品和版本
 */

public class SensorActivity extends AppBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sensor;
    }

    @Override
    protected void initTransition() {
        super.initTransition();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            TransitionUtil.setEnterTransition(getActivity(), TransitionUtil.slideEnd, 1500);
//            TransitionUtil.setReturnTransition(getActivity(), TransitionUtil.slideEnd, 1500);
//
//            TransitionUtil.setExitTransition(getActivity(), TransitionUtil.slideStart, 1500);
//            TransitionUtil.setReenterTransition(getActivity(), TransitionUtil.slideStart, 1500);
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("传感器");

        RecyclerView recyclerView = findViewById(R.id.activity_sensor_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String[] titles = new String[]{
                "加速度传感器",
                "重力传感器",
                "线性加速度传感器",
                "陀螺仪传感器",
                "光传感器",
                "磁力传感器",
                "方向传感器",
                "旋转矢量传感器",
                "温度传感器",
                "压力传感器",
                "距离传感器",
                "计步传感器",
        };

        Class[] classes = new Class[]{
                AccelerometerActivity.class,
                GravityActivity.class,
                LinearAccelerometerActivity.class,
                GyroscopeActivity.class,
                LightActivity.class,
                MagneticFieldActivity.class,
                OrientationActivity.class,
                RotationVectorActivity.class,
                AmbientActivity.class,
                PressureActivity.class,
                ProximityActivity.class,
                StepActivity.class
        };

        List<String> titleList = Arrays.asList(titles);
        List<Class> classList = Arrays.asList(classes);
        recyclerView.setAdapter(new SensorRecyclerViewAdapter(getActivity(), titleList, classList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sensor_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_compass) {
            startActivity(new Intent(getActivity(), CompassActivity.class));
            return true;
        } else if (id == R.id.action_pedometer) {
            startActivity(new Intent(getActivity(), PedometerActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

