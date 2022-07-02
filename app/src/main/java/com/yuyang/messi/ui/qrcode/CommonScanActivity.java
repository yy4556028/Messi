package com.yuyang.messi.ui.qrcode;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.lib_scan.scanPure.BaseScanActivity;

public class CommonScanActivity extends BaseScanActivity {

    public static final String RETURN_DATA = "return_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onScanResult(Result rawResult, ParsedResult parsedResult, Bitmap barcode) {
        if (rawResult == null || rawResult.getText() == null) {
            ToastUtil.showToast("未识别到二维码");
        } else {
            ToastUtil.showToast(rawResult.getText());
        }
    }

    @Override
    protected void onScanPicResult(String result) {
        if (result == null) {
            ToastUtil.showToast("未识别到二维码");
        } else {
            ToastUtil.showToast(result);
        }
    }
}
