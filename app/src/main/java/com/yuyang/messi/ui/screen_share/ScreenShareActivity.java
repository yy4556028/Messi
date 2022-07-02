package com.yuyang.messi.ui.screen_share;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.kotlinui.main.MainActivity;
import com.yuyang.messi.service.ScreenRecordService;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScreenShareActivity extends AppBaseActivity {

    private AppCompatCheckBox cbHD;
    private AppCompatCheckBox cbAudio;
    private MaterialButton btnRecord;

    private MediaProjectionManager mediaProjectionManager;

    public RecordReceiver recordReceiver;
    public static final String RECORD_STOP = "record.stop";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_screen_share;
    }

    public class RecordReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) return;
            switch (action) {
                case RECORD_STOP: {
                    stopScreenRecord();
                    break;
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        initView();
        recordReceiver = new RecordReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(RECORD_STOP);
        registerReceiver(recordReceiver, mFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonUtil.isServiceRunning(ScreenRecordService.class.getName())) {
            btnRecord.setText("停止录屏");
        } else {
            btnRecord.setText("开始录屏");
        }
    }

    private void checkPermission() {
        registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                List<String> deniedAskList = new ArrayList<>();
                List<String> deniedNoAskList = new ArrayList<>();
                for (Map.Entry<String, Boolean> stringBooleanEntry : result.entrySet()) {
                    if (!stringBooleanEntry.getValue()) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), stringBooleanEntry.getKey())) {
                            deniedAskList.add(stringBooleanEntry.getKey());
                        } else {
                            deniedNoAskList.add(stringBooleanEntry.getKey());
                        }
                    }
                }

                if (deniedAskList.size() == 0 && deniedNoAskList.size() == 0) {//全通过
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else if (deniedNoAskList.size() > 0) {
                    finish();
                } else {
                    checkPermission();
                }
            }
        }).launch(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.RECORD_AUDIO});
    }

    private void initView() {
        btnRecord = findViewById(R.id.activity_screen_share_btnRecord);
        cbHD = findViewById(R.id.activity_screen_share_cbHD);
        cbAudio = findViewById(R.id.activity_screen_share_cbAudio);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("开始录屏".equals(btnRecord.getText().toString())) {
                    startScreenRecord();
                } else {
                    stopScreenRecord();
                }
            }
        });
    }

    /**
     * 开始录屏
     */
    private void startScreenRecord() {

        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = null;
        if (mediaProjectionManager != null) {
            captureIntent = mediaProjectionManager.createScreenCaptureIntent();
        }
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    //获得录屏权限，启动Service进行录制
                    ScreenRecordService.startService(getActivity(), result.getResultCode(), result.getData(), cbHD.isChecked(), cbAudio.isChecked());
                    ToastUtil.showToast("录屏开始");
                    btnRecord.setText("停止录屏");
                } else {
                    ToastUtil.showToast("用戶拒绝录制屏幕");
                }
            }
        }).launch(captureIntent);
    }

    private void stopScreenRecord() {
        Intent service = new Intent(this, ScreenRecordService.class);
        stopService(service);
        btnRecord.setText("开始录屏");
    }
}
