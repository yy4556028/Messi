package com.yuyang.lib_scan.monitor;

import android.os.Bundle;
import android.util.Log;

import com.yuyang.lib_base.ui.base.BaseActivity;
import com.yuyang.lib_scan.R;

import cn.bingoogolapple.qrcode.core.QRCodeView;

/**
 * 监控，不断上传扫描到的图像
 */
public class MonitorActivity extends BaseActivity {

    private MonitorView mMonitorView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_activity_monitor);
        mMonitorView = findViewById(R.id.activity_monitor_monitorView);
        mMonitorView.setDelegate(new QRCodeView.Delegate() {
            @Override
            public void onScanQRCodeSuccess(String result) {
                Log.e("MonitorActivity", result);
                mMonitorView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMonitorView.startSpot();
                    }
                }, 1000);
            }

            @Override
            public void onCameraAmbientBrightnessChanged(boolean b) {

            }

            @Override
            public void onScanQRCodeOpenCameraError() {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMonitorView.startCamera();
        mMonitorView.startSpotAndShowRect();
    }

    @Override
    protected void onStop() {
        mMonitorView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mMonitorView.onDestroy();
        super.onDestroy();
    }
}
