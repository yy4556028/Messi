package com.yuyang.messi.ui.category;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.jetpack.workmanager.TestWork;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorkManagerActivity extends AppBaseActivity {

    private String WORK_TAG = "workTag";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_workmanager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("WorkManager");
        initView();
    }

    private void initView() {
        findViewById(R.id.activity_workmanager_createOneTimeWork).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createWork();
            }
        });
        findViewById(R.id.activity_workmanager_getOneTimeWorkState).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<WorkInfo> workInfoList = WorkManager.getInstance().getWorkInfosByTag(WORK_TAG).get();
                    if (workInfoList == null || workInfoList.size() == 0) {
                        ToastUtil.showToast("No Work");
                    } else {
                        switch (workInfoList.get(0).getState()) {
                            case FAILED:
                                ToastUtil.showToast("FAILED");
                                break;
                            case BLOCKED:
                                ToastUtil.showToast("BLOCKED");
                                break;
                            case RUNNING:
                                ToastUtil.showToast("RUNNING");
                                break;
                            case CANCELLED:
                                ToastUtil.showToast("CANCELLED");
                                break;
                            case ENQUEUED:
                                ToastUtil.showToast("ENQUEUED");
                                break;
                            case SUCCEEDED:
                                ToastUtil.showToast("SUCCEEDED");
                                break;
                            default:
                                ToastUtil.showToast("DEFAULT");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void createWork() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)//网络连接设置
                .setRequiresBatteryNotLow(false)//是否为低电量时运行 默认false
                .setRequiresCharging(false)//是否要插入设备（接入电源），默认false
                .setRequiresDeviceIdle(false)//设备是否为空闲，默认false
                .setRequiresStorageNotLow(false)//设备可用存储是否不低于临界阈值
                .build();

//        PeriodicWorkRequest   循环任务
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(TestWork.class)
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .addTag(WORK_TAG)
                .build();

        WorkManager.getInstance().enqueue(oneTimeWorkRequest);

        WorkManager.getInstance().getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.e("TestWorker", workInfo.getState().name());
                        if (workInfo.getState().isFinished()) {
                            Log.e("TestWorker", "Finish");
                        }
                    }
                });

//        WorkManager.getInstance()
//                .beginWith(workA)
//                .then(workB)
//                .then(workC)
//                .enqueue();

//        val configA_B = WorkManager.getInstance().beginWith(workRequest)
//                .then(workRequestB)
//
//        val configC_D = WorkManager.getInstance().beginWith(workRequestC)
//                .then(workRequestD)
//
//        WorkContinuation.combine(configA_B,configC_D)
//                .then(workRequestE)
//                .enqueue()
    }

}