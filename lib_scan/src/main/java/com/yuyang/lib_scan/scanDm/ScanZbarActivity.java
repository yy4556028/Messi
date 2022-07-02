package com.yuyang.lib_scan.scanDm;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yuyang.lib_base.helper.SelectPhotoHelper;
import com.yuyang.lib_base.ui.base.BaseActivity;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.ClipboardUtil;
import com.yuyang.lib_base.utils.StringDealUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.lib_scan.R;
import com.yuyang.lib_scan.utils.QrCodeCallback;
import com.yuyang.lib_scan.utils.QrCodeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * https://github.com/dm77/barcodescanner
 */
public class ScanZbarActivity extends BaseActivity implements ZBarScannerView.ResultHandler {

    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZBarScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;

    private final SelectPhotoHelper selectPhotoHelper = new SelectPhotoHelper(getActivity());

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

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.scan_activity_scan_dm);
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }

        selectPhotoHelper.setOnResultListener(new SelectPhotoHelper.OnResultListener() {
            @Override
            public void onPhotoResult(Bitmap bitmap, Uri uri, String path) {

                QrCodeUtils.parseFromBitmapAsync(null, bitmap, new QrCodeCallback<com.google.zxing.Result>() {
                    @Override
                    public void onComplete(boolean success, com.google.zxing.Result result) {
                        if (success) {
                            showResult(result.getBarcodeFormat().toString(), result.getText());
                        } else {
                            ToastUtil.showToast("未识别到二维码");
                        }
                    }
                });
            }
        });

        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("ZBar");

        ViewGroup contentFrame = findViewById(R.id.activity_scan_frame);
        mScannerView = new ZBarScannerView(this);
//        mScannerView.setLaserEnabled(false);
        mScannerView.setLaserColor(ContextCompat.getColor(this, R.color.theme));
        mScannerView.setBorderColor(ContextCompat.getColor(this, R.color.theme));
        setupFormats();
        contentFrame.addView(mScannerView);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        permissionsLauncher.launch(new String[]{Manifest.permission.CAMERA});
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;

        menuItem = menu.add("相册");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        if (mFlash) {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, "Flash On");
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, "Flash Off");
        }
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        if (mAutoFocus) {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, "Auto Focus On");
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, "Auto Focus Off");
        }
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, "Formats");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, "Select Camera");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (TextUtils.equals("相册", item.getTitle())) {
            selectPhotoHelper.takeGallery(null, false);
            return true;
        }
        // Handle presses on the action bar items
        int itemId = item.getItemId();
        if (itemId == R.id.menu_flash) {
            mFlash = !mFlash;
            if (mFlash) {
                item.setTitle("Flash On");
            } else {
                item.setTitle("Flash Off");
            }
            mScannerView.setFlash(mFlash);
            return true;
        } else if (itemId == R.id.menu_auto_focus) {
            mAutoFocus = !mAutoFocus;
            if (mAutoFocus) {
                item.setTitle("Auto Focus On");
            } else {
                item.setTitle("Auto Focus Off");
            }
            mScannerView.setAutoFocus(mAutoFocus);
            return true;
        } else if (itemId == R.id.menu_formats) {
            showFormatDialog();
            return true;
        } else if (itemId == R.id.menu_camera_selector) {
            mScannerView.stopCamera();
            showCameraSelectDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        showResult(rawResult.getBarcodeFormat().getName(), rawResult.getContents());
        // Resume the camera
//        mScannerView.resumeCameraPreview(this);
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<>();
            for (int i = 0; i < BarcodeFormat.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for (int index : mSelectedIndices) {
            formats.add(BarcodeFormat.ALL_FORMATS.get(index));
        }
        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    ArrayList<Integer> tempSelectedIndices;

    private void showFormatDialog() {
        if (tempSelectedIndices == null) {
            tempSelectedIndices = new ArrayList<>(mSelectedIndices);
        }

        String[] formats = new String[BarcodeFormat.ALL_FORMATS.size()];
        boolean[] checkedIndices = new boolean[BarcodeFormat.ALL_FORMATS.size()];
        for (int i = 0; i < BarcodeFormat.ALL_FORMATS.size(); i++) {
            formats[i] = BarcodeFormat.ALL_FORMATS.get(i).getName();
            if (mSelectedIndices.contains(i)) {
                checkedIndices[i] = true;
            } else {
                checkedIndices[i] = false;
            }
        }

        new AlertDialog.Builder(getActivity(), R.style.MyCheckBox)
                .setTitle("Choose formats")
                .setMultiChoiceItems(formats, checkedIndices, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            tempSelectedIndices.add(which);
                        } else if (tempSelectedIndices.contains(which)) {
                            tempSelectedIndices.remove(tempSelectedIndices.indexOf(which));
                        }
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mSelectedIndices = tempSelectedIndices;
                        setupFormats();
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    int tempCameraId = mCameraId;

    private void showCameraSelectDialog() {

        int numberOfCameras = Camera.getNumberOfCameras();
        String[] cameraNames = new String[numberOfCameras];
        int checkedIndex = 0;

        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraNames[i] = "Front Facing";
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraNames[i] = "Rear Facing";
            } else {
                cameraNames[i] = "Camera ID: " + i;
            }
            if (i == mCameraId) {
                checkedIndex = i;
            }
        }

        new AlertDialog.Builder(getActivity())
                .setTitle("Select Camera")
                .setSingleChoiceItems(cameraNames, checkedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempCameraId = which;
                    }
                })
                // Set the action buttons
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mCameraId = tempCameraId;
                        mScannerView.startCamera(mCameraId);
                        mScannerView.setFlash(mFlash);
                        mScannerView.setAutoFocus(mAutoFocus);
                    }
                })
                .setNegativeButton("取消", null)

                .create()
                .show();
    }

    private void showResult(String format, String codeText) {
        CharSequence msg = "Format：" + format + "\r\n" + codeText;
        AlertDialog.Builder builder = new Builder(getActivity())
                .setMessage(msg)
                .setPositiveButton("复制", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardUtil.setText(codeText);
                        ToastUtil.showToast("已复制到剪切板");
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mScannerView.resumeCameraPreview(ScanZbarActivity.this);
                    }
                });

        if (codeText.startsWith("http://") || codeText.startsWith("https://")) {
            builder.setMessage(StringDealUtil.highlightKeyword(Color.RED, msg, codeText))
                    .setNegativeButton("跳转", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(codeText)));
                        }
                    });
        }
        builder.show();
    }
}
