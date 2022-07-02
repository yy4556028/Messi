package com.yuyang.lib_scan.scanZxing;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.yuyang.lib_base.helper.SelectPhotoHelper;
import com.yuyang.lib_base.ui.base.BaseActivity;
import com.yuyang.lib_scan.R;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;


public class ZXingActivity extends BaseActivity implements QRCodeView.Delegate {

    private static final String TAG = ZXingActivity.class.getSimpleName();

    private ZXingView mZXingView;

    private SelectPhotoHelper selectPhotoHelper;

    private final ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
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
                    //auto call resume
                } else if (deniedNoAskList.size() > 0) {
                    finish();
                } else {
                    finish();
                }
            });

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BGAQRCodeUtil.setDebug(true);
        setContentView(R.layout.scan_activity_scan_zxing);
        mZXingView = findViewById(R.id.zxingview);
        mZXingView.setDelegate(this);
        permissionsLauncher.launch(new String[]{Manifest.permission.CAMERA});
    }

    @Override
    protected void onResume() {
        super.onResume();
        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onPause() {
        super.onPause();
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        setTitle("扫描结果为：" + result);
        vibrate();

        mZXingView.startSpot(); // 开始识别
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mZXingView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.start_preview) {
            mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        } else if (id == R.id.stop_preview) {
            mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        } else if (id == R.id.start_spot) {
            mZXingView.startSpot(); // 开始识别
        } else if (id == R.id.stop_spot) {
            mZXingView.stopSpot(); // 停止识别
        } else if (id == R.id.start_spot_showrect) {
            mZXingView.startSpotAndShowRect(); // 显示扫描框，并且开始识别
        } else if (id == R.id.stop_spot_hiddenrect) {
            mZXingView.stopSpotAndHiddenRect(); // 停止识别，并且隐藏扫描框
        } else if (id == R.id.show_scan_rect) {
            mZXingView.showScanRect(); // 显示扫描框
        } else if (id == R.id.hidden_scan_rect) {
            mZXingView.hiddenScanRect(); // 隐藏扫描框
        } else if (id == R.id.decode_scan_box_area) {
            mZXingView.getScanBoxView().setOnlyDecodeScanBoxArea(true); // 仅识别扫描框中的码
        } else if (id == R.id.decode_full_screen_area) {
            mZXingView.getScanBoxView().setOnlyDecodeScanBoxArea(false); // 识别整个屏幕中的码
        } else if (id == R.id.open_flashlight) {
            mZXingView.openFlashlight(); // 打开闪光灯
        } else if (id == R.id.close_flashlight) {
            mZXingView.closeFlashlight(); // 关闭闪光灯
        } else if (id == R.id.scan_one_dimension) {
            mZXingView.changeToScanBarcodeStyle(); // 切换成扫描条码样式
            mZXingView.setType(BarcodeType.ONE_DIMENSION, null); // 只识别一维条码
            mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.scan_two_dimension) {
            mZXingView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
            mZXingView.setType(BarcodeType.TWO_DIMENSION, null); // 只识别二维条码
            mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.scan_qr_code) {
            mZXingView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
            mZXingView.setType(BarcodeType.ONLY_QR_CODE, null); // 只识别 QR_CODE
            mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.scan_code128) {
            mZXingView.changeToScanBarcodeStyle(); // 切换成扫描条码样式
            mZXingView.setType(BarcodeType.ONLY_CODE_128, null); // 只识别 CODE_128
            mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.scan_ean13) {
            mZXingView.changeToScanBarcodeStyle(); // 切换成扫描条码样式
            mZXingView.setType(BarcodeType.ONLY_EAN_13, null); // 只识别 EAN_13
            mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.scan_high_frequency) {
            mZXingView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
            mZXingView.setType(BarcodeType.HIGH_FREQUENCY, null); // 只识别高频率格式，包括 QR_CODE、UPC_A、EAN_13、CODE_128
            mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.scan_all) {
            mZXingView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
            mZXingView.setType(BarcodeType.ALL, null); // 识别所有类型的码
            mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.scan_custom) {
            mZXingView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式

            Map<DecodeHintType, Object> hintMap = new EnumMap<>(DecodeHintType.class);
            List<BarcodeFormat> formatList = new ArrayList<>();
            formatList.add(BarcodeFormat.QR_CODE);
            formatList.add(BarcodeFormat.UPC_A);
            formatList.add(BarcodeFormat.EAN_13);
            formatList.add(BarcodeFormat.CODE_128);
            hintMap.put(DecodeHintType.POSSIBLE_FORMATS, formatList); // 可能的编码格式
            hintMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); // 花更多的时间用于寻找图上的编码，优化准确性，但不优化速度
            hintMap.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 编码字符集
            mZXingView.setType(BarcodeType.CUSTOM, hintMap); // 自定义识别的类型

            mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.choose_qrcde_from_gallery) {
            if (selectPhotoHelper == null) {
                selectPhotoHelper = new SelectPhotoHelper(getActivity());
                selectPhotoHelper.setOnResultListener(new SelectPhotoHelper.OnResultListener() {
                    @Override
                    public void onPhotoResult(Bitmap bitmap, Uri uri, String path) {
                        mZXingView.decodeQRCode(bitmap);
//                            QRCodeDecoder.syncDecodeQRCode(picturePath);
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }
}
