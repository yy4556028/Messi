package com.yuyang.lib_scan.scanPure;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.mylhyl.zxing.scanner.OnScannerCompletionListener;
import com.mylhyl.zxing.scanner.ScannerOptions;
import com.mylhyl.zxing.scanner.ScannerView;
import com.yuyang.lib_base.helper.SelectPhotoHelper;
import com.yuyang.lib_base.ui.base.BaseActivity;
import com.yuyang.lib_scan.R;

import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;


/**
 * 扫码类基础Activity，所有扫码页面从此页面派生
 * <p>
 * 目前只需要 2个依赖
 * implementation 'com.mylhyl:zxingscanner:2.1.1'
 * implementation 'com.google.zxing:core:3.3.3'
 * <p>
 * https://www.wanandroid.com/blog/show/2174
 */
public abstract class BaseScanActivity extends BaseActivity {

    private static final String TAG = BaseScanActivity.class.getSimpleName();

    protected ScannerView mScannerView;

    private final SelectPhotoHelper selectPhotoHelper = new SelectPhotoHelper(getActivity());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_activity_base_scan);
        initView();
    }

    protected void initView() {
        TextView tvTitle = findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText("扫码");
        }
        ImageView ivFlash = findViewById(R.id.ivFlash);
        if (ivFlash != null) {
            ivFlash.setOnClickListener(v -> {
                if (ivFlash.getTag() != null && (Boolean) ivFlash.getTag()) {
                    ivFlash.setTag(false);
                    ivFlash.setImageResource(R.mipmap.scan_flash_close);
                    mScannerView.toggleLight(false);
                } else {
                    ivFlash.setTag(true);
                    ivFlash.setImageResource(R.mipmap.scan_flash_open);
                    mScannerView.toggleLight(true);
                }
            });
        }
        ImageView ivPic = findViewById(R.id.ivPic);
        if (ivPic != null) {
            selectPhotoHelper.setOnResultListener(new SelectPhotoHelper.OnResultListener() {
                @Override
                public void onPhotoResult(Bitmap bitmap, Uri uri, String photoPath) {
                    String result = QRCodeDecoder.syncDecodeQRCode(bitmap);
//                    Rect[] rects = QrCodeUtils.parsesMultiFromBitmap(bitmap);
                    onScanPicResult(result);
                }
            });
            ivPic.setOnClickListener(v -> {
                selectPhotoHelper.takeGallery(null, false);
            });
        }

        mScannerView = findViewById(R.id.scannerView);

        ScannerOptions options = new ScannerOptions.Builder()
                .setLaserStyle(ScannerOptions.LaserStyle.RES_GRID, R.mipmap.scan_line)
                .setFrameCornerColor(Color.parseColor("#ff0000"))
                .setFrameOutsideColor(Color.parseColor("#88000000"))
//                .setScanMode(Scanner.ScanMode.QR_CODE_MODE)
//                .setScanMode(BarcodeFormat.QR_CODE)
                .build();
        mScannerView.setScannerOptions(options);
        mScannerView.setOnScannerCompletionListener(new OnScannerCompletionListener() {
            /**
             * 扫描成功后将调用
             *
             * @param rawResult    扫描结果
             * @param parsedResult 结果类型
             * @param barcode      扫描后的图像
             */
            @Override
            public void onScannerCompletion(Result rawResult, ParsedResult parsedResult, Bitmap barcode) {
                mScannerView.restartPreviewAfterDelay(500);
                onScanResult(rawResult, parsedResult, barcode);

                ParsedResultType type = parsedResult.getType();
//                ToastUtil.showToast(rawResult.getText());

//                switch (type) {
//                    case ADDRESSBOOK:
//                        AddressBookParsedResult addressBook = (AddressBookParsedResult) parsedResult;
//                        bundle.putSerializable(Intents.Scan.RESULT, new AddressBookResult(addressBook));
//                        break;
//                    case URI:
//                        URIParsedResult uriParsedResult = (URIParsedResult) parsedResult;
//                        bundle.putString(Intents.Scan.RESULT, uriParsedResult.getURI());
//                        break;
//                    case TEXT:
//                        bundle.putString(Intents.Scan.RESULT, rawResult.getText());
//                        break;
//                }
            }
        });
    }

    @Override
    protected void onResume() {
        mScannerView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mScannerView.onPause();
        super.onPause();
    }

    protected abstract void onScanResult(Result rawResult, ParsedResult parsedResult, Bitmap barcode);

    protected abstract void onScanPicResult(String result);

    private void createQRCode() {
//        //联系人类型
//        Bitmap bitmap1 = new QREncode.Builder(this)
//                .setParsedResultType(ParsedResultType.ADDRESSBOOK)
//                .setAddressBookUri(contactUri).build().encodeAsBitmap();
//
//        //文本类型
//        Bitmap bitmap2 = new QREncode.Builder(this)
//                .setColor(getResources().getColor(R.color.colorPrimary))//二维码颜色
//                //.setParsedResultType(ParsedResultType.TEXT)//默认是TEXT类型
//                .setContents("我是石头")//二维码内容
//                .setLogoBitmap(logoBitmap)//二维码中间logo
//                .build().encodeAsBitmap();

    }
}
