package com.yuyang.messi.ui.qrcode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.Result;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.header.HeaderRightBean;
import com.yuyang.lib_base.utils.BitmapUtil;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.ScreenBrightUtil;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.lib_scan.utils.QrCodeCallback;
import com.yuyang.lib_scan.utils.QrCodeUtils;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class QRCodeActivity extends AppBaseActivity {

    private static final String QRCODE_IMAGE_PATH = StorageUtil.getExternalFile("/qr_code.jpg").getAbsolutePath();

    private EditText inputEdit;
    private ImageView showImageView;

    private Bitmap bitmap;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_qrcode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScreenBrightUtil.setActivityBrightness(getActivity(), 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScreenBrightUtil.setActivityBrightness(getActivity(), -1);
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("二维码生成");

        List<HeaderRightBean> rightBeanList = new ArrayList<>();
        rightBeanList.add(new HeaderRightBean("生成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generationQRCode();
            }
        }));
        rightBeanList.add(new HeaderRightBean("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    boolean isSuccess = BitmapUtil.saveBitmap(bitmap, new File(QRCODE_IMAGE_PATH));

                    if (isSuccess) {
                        ToastUtil.showToast("图片已保存到" + QRCODE_IMAGE_PATH);
                    } else {
                        ToastUtil.showToast("图片保存失败");
                    }
                }
            }
        }));
        headerLayout.setRight(rightBeanList);

        inputEdit = findViewById(R.id.activity_qrcode_edit);
        showImageView = findViewById(R.id.activity_qrcode_image);
    }

    private void initEvent() {
        showImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showImageView.setDrawingCacheEnabled(true);
                QrCodeUtils.parseFromBitmapAsync(null, showImageView.getDrawingCache(), new QrCodeCallback<Result>() {
                    @Override
                    public void onComplete(boolean success, Result result) {
                        showImageView.setDrawingCacheEnabled(false);
                        if (success) {
                            ToastUtil.showToast("Contents = " + result.getText() + ", Format = " + result.getBarcodeFormat().toString());
                        } else {
                            ToastUtil.showToast("Unknown");
                        }
                    }
                });
                return true;
            }
        });
    }

    private void generationQRCode() {
        QrCodeUtils.generateAsync(
                inputEdit.getText().toString(),
                CommonUtil.getScreenWidth() / 2,
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), new QrCodeCallback<Bitmap>() {
                    @Override
                    public void onComplete(boolean success, Bitmap ret) {
                        if (ret != null) {
                            bitmap = ret;
                            showImageView.setImageBitmap(bitmap);
                        }
                    }
                });
    }
}

