package com.yuyang.messi.ui.category.sensor.demo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.service.PedometerService;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.view.GifView;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 创建者: yuyang
 * 创建日期: 2015-08-19
 * 创建时间: 09:22
 * PedometerActivity: 计步器
 *
 * @author yuyang
 * @version 1.0
 */

public class PedometerActivity extends AppBaseActivity {

    /**
     * 用时
     */
    private TextView timeText;

    /**
     * 行程
     */
    private TextView distanceText;

    /**
     * 热量
     */
    private TextView energyText;

    /**
     * 速度
     */
    private TextView velocityText;

    /**
     * 步数
     */
    private TextView stepCountText;

    private GifView gifView;

    private Button startBtn;
    private Button stopBtn;
    private Button clearBtn;

    private EditText heightEdit;
    private EditText weightEdit;
    private Button tvSure;

    // 一步的长度
    private static double STEP_LENGTH = 0.7; // m
    private static double WEIGHT = 60;// kg

    private final DecimalFormat format = new DecimalFormat("####.##");

    private double distance; // m

    private PedometerService pedometerService;

    private Intent serviceIntent;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pedometerService = ((PedometerService.PedometerBinder) service).getService();
            if (PedometerDetector.CURRENT_STEP >= 0) {
                if (pedometerService.isStepping())
                    startBtn.performClick();
                else {
                    // 通知更新UI
                    Message msg = new Message();
                    handler.sendMessage(msg);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            pedometerService = null;
        }
    };

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // 消耗的时间
            int consumeTime = pedometerService.getTime();

            // 计算距离
            calculateDistance();

            // 计算卡路里:   跑步热量（kcal）＝体重（kg）×距离（公里）×1.036
            double calories = WEIGHT * distance * 0.001036;

            // 计算速度
            double velocity = distance * 1000 / consumeTime;

            // 显示消耗时间
            timeText.setText(convertTime(consumeTime));

            // 显示距离
            distanceText.setText(format.format(distance));

            // 显示卡路里
            energyText.setText(format.format(calories));

            // 显示速度
            velocityText.setText(format.format(velocity));

            stepCountText.setText(PedometerDetector.CURRENT_STEP + "");
        }
    };

    private Timer getStepTimer;

    private TimerTask getStepTimerTask = new TimerTask() {
        @Override
        public void run() {

            if (pedometerService != null && pedometerService.isStepping()) {

                Message msg = new Message();
                handler.sendMessage(msg);// 通知主线程
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sensor_pedometer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();

        serviceIntent = new Intent(this, PedometerService.class);

        startService(serviceIntent);
        bindService(serviceIntent,
                serviceConnection,
                BIND_AUTO_CREATE);

        getStepTimer = new Timer();
        getStepTimer.schedule(getStepTimerTask, 0, 250);
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("计步器");

        timeText = findViewById(R.id.activity_sensor_pedometer_time);
        distanceText = findViewById(R.id.activity_sensor_pedometer_distance);
        energyText = findViewById(R.id.activity_sensor_pedometer_energy);
        velocityText = findViewById(R.id.activity_sensor_pedometer_velocity);
        stepCountText = findViewById(R.id.activity_sensor_pedometer_step_count);

        timeText.setText("00:00:00");
        distanceText.setText("0");
        energyText.setText("0");
        velocityText.setText("0");
        stepCountText.setText("0");

        gifView = (GifView) findViewById(R.id.activity_sensor_pedometer_gif);

        startBtn = (Button) findViewById(R.id.activity_sensor_pedometer_start);
        stopBtn = (Button) findViewById(R.id.activity_sensor_pedometer_stop);
        clearBtn = (Button) findViewById(R.id.activity_sensor_pedometer_clear);

        gifView.setMovieResource(R.drawable.gif_walk);
        gifView.setPaused(true);

        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        clearBtn.setEnabled(false);

        heightEdit = (EditText) findViewById(R.id.activity_sensor_pedometer_edit_height);
        weightEdit = (EditText) findViewById(R.id.activity_sensor_pedometer_edit_weight);
        tvSure = (Button) findViewById(R.id.tvSure);

    }

    private void initEvent() {

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn.setEnabled(false);
                stopBtn.setEnabled(true);
                clearBtn.setEnabled(false);
                gifView.setPaused(false);

                pedometerService.start();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                clearBtn.setEnabled(true);
                gifView.setPaused(true);

                pedometerService.stop();
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearBtn.setEnabled(false);
                pedometerService.clear();

                timeText.setText("00:00:00");
                distanceText.setText("0");
                energyText.setText("0");
                velocityText.setText("0");
                stepCountText.setText("0");
            }
        });

        heightEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String editable = s.toString();

                if (editable == null || editable.equals("")) return;

                String str = editable;

                // 小数点后2位
                if (str.indexOf(".") >= 0 && str.indexOf(".") + 3 < str.length()) {
                    str = str.substring(0, str.indexOf(".") + 3);
                }

                if (str.startsWith(".")) {
                    str = "0" + str;
                }

                if (!editable.equals(str)) {
                    heightEdit.setText(str);
                }

                heightEdit.setSelection(heightEdit.length());
            }
        });

        weightEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String editable = s.toString();

                if (editable == null || editable.equals("")) return;

                String str = editable;

                // 小数点后2位
                if (str.indexOf(".") >= 0 && str.indexOf(".") + 3 < str.length()) {
                    str = str.substring(0, str.indexOf(".") + 3);
                }

                if (str.startsWith(".")) {
                    str = "0" + str;
                }

                if (!editable.equals(str)) {
                    weightEdit.setText(str);
                }

                weightEdit.setSelection(weightEdit.length());
            }
        });

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String height = heightEdit.getText().toString();
                String weight = weightEdit.getText().toString();

                if (height.equals("") || weight.equals("")) {
                } else {
                    STEP_LENGTH = 0.0045 * Double.parseDouble(height);
                    WEIGHT = Double.parseDouble(weight);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getStepTimer.cancel();
        getStepTimer = null;
        unbindService(serviceConnection);
        if (pedometerService.getTime() == 0) {
            stopService(serviceIntent);
        }
        serviceConnection = null;
        pedometerService = null;
        serviceIntent = null;
    }

    //        //  获得声音服务
//
//        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//
//        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

    /**
     * 时间转换
     *
     * @param time ms
     * @return 00:00:00
     */
    private String convertTime(int time) {

        if (time <= 0) {
            return "00:00:00";
        }

        int hour = (time / 1000) / 3600;
        int minute = ((time / 1000) % 3600) / 60;
        int second = (time / 1000) % 60;

        String h = ("00" + hour).substring(("00" + hour).length() - 2);

        String m = ("00" + minute).substring(("00" + minute).length() - 2);

        String s = ("00" + second).substring(("00" + second).length() - 2);

        return h + ":" + m + ":" + s;
    }

    /**
     * 根据走的步数计算走的路程 具体算法含义不清楚
     *
     * @return
     */
    private void calculateDistance() {

        if (PedometerDetector.CURRENT_STEP % 2 == 0) {
            distance = (PedometerDetector.CURRENT_STEP / 2) * 3 * STEP_LENGTH;
        } else {
            distance = ((PedometerDetector.CURRENT_STEP / 2) * 3 + 1) * STEP_LENGTH;
        }
    }

}
