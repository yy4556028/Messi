package com.yuyang.messi.jetpack.workmanager;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.CoroutineWorker;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.yuyang.messi.ui.category.WorkManagerActivity;

import kotlin.coroutines.Continuation;

public class Test1Work extends CoroutineWorker {

    public Test1Work(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Nullable
    @Override
    public Object doWork(@NonNull Continuation<? super Result> continuation) {
        Intent intent = new Intent(getApplicationContext(), WorkManagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        return Result.success();
    }

    @Nullable
    @Override
    public Object getForegroundInfo(@NonNull Continuation<? super ForegroundInfo> $completion) {
//        return ForegroundInfo(
//                NOTIFICATION_ID, createNotification()
//        );
        return super.getForegroundInfo($completion);
    }
}
