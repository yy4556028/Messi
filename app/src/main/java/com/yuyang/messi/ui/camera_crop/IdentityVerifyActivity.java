package com.yuyang.messi.ui.camera_crop;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.BitmapUtil;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class IdentityVerifyActivity extends AppBaseActivity {

    private ImageView frontImage;
    private ImageView backImage;

    private TextView frontWatermarkText;
    private TextView backWatermarkText;

    private TextView tvNextStep;

    private boolean frontFlag;

    private String frontPhotoPath;
    private String backPhotoPath;

    private final ActivityResultLauncher<Intent> frontResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() != RESULT_OK) return;
        frontImage.setImageBitmap(BitmapFactory.decodeFile((result.getData().getStringExtra(CameraCropHActivity.RETURN_PATH))));
        frontWatermarkText.setVisibility(View.VISIBLE);
        frontFlag = true;
    });

    private final ActivityResultLauncher<Intent> backResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() != RESULT_OK) return;
        backImage.setImageBitmap(BitmapFactory.decodeFile((result.getData().getStringExtra(CameraCropHActivity.RETURN_PATH))));
        backWatermarkText.setVisibility(View.VISIBLE);
        tvNextStep.setEnabled(true);
    });

    @Override
    protected int getLayoutId() {
        return R.layout.activity_identity_verify;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("身份验证");

        frontPhotoPath = StorageUtil.getPrivateCache() + "/front.jpg";
        backPhotoPath = StorageUtil.getPrivateCache() + "/back.jpg";

        frontImage = findViewById(R.id.activity_identity_verify_frontImage);
        backImage = findViewById(R.id.activity_identity_verify_backImage);
        frontWatermarkText = findViewById(R.id.activity_identity_verify_frontWatermarkText);
        backWatermarkText = findViewById(R.id.activity_identity_verify_backWatermarkText);
        tvNextStep = findViewById(R.id.tvNextStep);
    }

    private void initEvent() {
        frontImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                frontResultLauncher.launch(CameraCropHActivity.getLaunchIntent(getActivity(), CameraCropHActivity.TYPE_IDCARD_FRONT, frontPhotoPath));
            }
        });
        backImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!frontFlag) {
                    ToastUtil.showToast("请先拍摄头像面");
                    return;
                }
                backResultLauncher.launch(CameraCropHActivity.getLaunchIntent(getActivity(), CameraCropHActivity.TYPE_IDCARD_BACK, backPhotoPath));
            }
        });
        tvNextStep.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPhoto();
            }
        });
    }

    private void checkPhoto() {
        final String front_base64Image = BitmapUtil.bitmapToBase64(BitmapFactory.decodeFile((frontPhotoPath)));
        final String back_base64Image = BitmapUtil.bitmapToBase64(BitmapFactory.decodeFile((frontPhotoPath)));
    }
}
