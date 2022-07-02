package com.yuyang.lib_scan.scanDm;

import android.Manifest;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.yuyang.lib_base.helper.SelectPhotoHelper;
import com.yuyang.lib_base.ui.base.BaseActivity;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.ClipboardUtil;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.lib_base.utils.ReflectionUtil;
import com.yuyang.lib_base.utils.StringDealUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.lib_scan.R;
import com.yuyang.lib_scan.utils.QrCodeCallback;
import com.yuyang.lib_scan.utils.QrCodeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * https://github.com/dm77/barcodescanner
 */
public class ScanZXingActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;

    private final SelectPhotoHelper selectPhotoHelper = new SelectPhotoHelper(getActivity());

    private ViewGroup contentFrame;

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
                QrCodeUtils.parseFromBitmapAsync(null, bitmap, new QrCodeCallback<Result>() {
                    @Override
                    public void onComplete(boolean success, Result result) {
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
        headerLayout.showTitle("ZXing");

        contentFrame = findViewById(R.id.activity_scan_frame);
        mScannerView = new ZXingScannerView(this);
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
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        if (mAutoFocus) {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, "Auto Focus On");
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, "Auto Focus Off");
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, "Formats");
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, "Select Camera");
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

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
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            r.play();
//            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//            vibrator.vibrate(200);
            addMark(rawResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

        showResult(rawResult.getBarcodeFormat().toString(), rawResult.getText());
        // Resume the camera
//        mScannerView.resumeCameraPreview(this);
    }

    private void addMark(Result rawResult) {

        IViewFinder viewFinderView = (ViewFinderView) ReflectionUtil.getFieldValue(mScannerView, "mViewFinderView");
        final Rect rectPreview = mScannerView.getFramingRectInPreview(0, 0);
        final Rect rectFinder = viewFinderView.getFramingRect();

        float radioW = rectPreview.width() * 1f / rectFinder.width();
        float radioH = rectPreview.height() * 1f / rectFinder.height();
        radioW = radioH;//??横竖都是按照 radioH 比率

        if (rawResult.getResultPoints().length < 3) {
            return;
        }

        PointF centerPoint = calcCenter(rawResult.getResultPoints());

        View markView = new View(this);
        markView.setBackgroundResource(R.drawable.oval_theme);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(PixelUtils.dp2px(4), PixelUtils.dp2px(4));
        params.setMargins((int) (rectFinder.left + centerPoint.x / radioW - PixelUtils.dp2px(2)), (int) (rectFinder.top + centerPoint.y / radioH - PixelUtils.dp2px(2)), 0, 0);
        markView.setLayoutParams(params);
        contentFrame.addView(markView);

//        View bgView = new View(this);
//        bgView.setBackgroundResource(R.color.red);
//        FrameLayout.LayoutParams bgParams = new FrameLayout.LayoutParams(rectFinder.width(), rectFinder.height());
//        bgParams.setMargins(rectFinder.left, rectFinder.top, 0, 0);
//        bgView.setLayoutParams(bgParams);
//        contentFrame.addView(bgView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                contentFrame.removeView(markView);
//                contentFrame.removeView(bgView);
                mScannerView.resumeCameraPreview(ScanZXingActivity.this);
            }
        }, 2000);
    }

    /**
     * rawResult.getResultPoints()
     * <p>
     * 二维码的话，当信息量较少 圆框占7格，间距占7个也就是横竖都是21格的情况，返回3个点，否则返回4个点
     * <p>
     * 前三个点在二维码四角黑色方形的左边中部 所以X坐标是偏左的，Y坐标是准确的 是 FinderPattern 类型
     * <p>
     * 如果有第四个点，第四个点是靠内的点，用来定位   是 AlignmentPattern 类型
     * <p>
     * new ResultPoint[]{bottomLeft, topLeft, topRight, alignmentPattern}
     */
    private PointF calcCenter(ResultPoint[] points) {
        float slope = (points[1].getY() - points[0].getY()) / (points[1].getX() - points[0].getX());
        if (points.length == 4) {
            ResultPoint bigPoint_rightBottom = new ResultPoint(
                    points[0].getX() + points[2].getX() - points[1].getX(),
                    points[0].getY() + points[2].getY() - points[1].getY());

            float offsetX_double = bigPoint_rightBottom.getX() - points[3].getX();
            float offsetY_double = bigPoint_rightBottom.getY() - points[3].getY();

            return new PointF(
                    (points[0].getX() + points[2].getX() + offsetX_double) / 2,
                    (points[0].getY() + points[2].getY()) / 2);
        } else {
            ResultPoint bottomLeft = points[0];
            ResultPoint topLeft = points[1];
            ResultPoint topRight = points[2];

            float offsetX_double = (topRight.getX() - topLeft.getX()) * 3f / 14;

            return new PointF((bottomLeft.getX() + topRight.getX() + offsetX_double) / 2, (bottomLeft.getY() + topRight.getY()) / 2);
        }
    }

    private void drawResultPoints(Bitmap barcode, Result rawResult) {
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.theme));
            paint.setStrokeWidth(3.0f);
            paint.setStyle(Paint.Style.STROKE);
            Rect border = new Rect(2, 2, barcode.getWidth() - 2, barcode.getHeight() - 2);
            canvas.drawRect(border, paint);

            paint.setColor(getResources().getColor(R.color.theme));
            if (points.length == 2) {
                Toast.makeText(this, "条形码", Toast.LENGTH_SHORT).show();

                canvas.drawLine(points[0].getX(), points[0].getY(), points[1].getX(), points[1].getY(), paint);
            } else if (points.length == 4 && (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
                // Hacky special case -- draw two lines, for the barcode and metadata
                Toast.makeText(this, "画两条线", Toast.LENGTH_SHORT).show();
                canvas.drawLine(points[0].getX(), points[0].getY(), points[1].getX(), points[1].getY(), paint);
                canvas.drawLine(points[2].getX(), points[2].getY(), points[3].getX(), points[3].getY(), paint);
            } else {
                Toast.makeText(this, "二维码", Toast.LENGTH_SHORT).show();
//                viewCircle.setVisibility(View.VISIBLE);
//                viewCircle.setCoordinate(rawResult, "circle", viewfinderView.getX(), viewfinderView.getY());
                for (ResultPoint point : points) {
                    canvas.drawPoint(point.getX(), point.getY(), paint);
                }
            }
        }
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<Integer>();
            for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for (int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    ArrayList<Integer> tempSelectedIndices;

    private void showFormatDialog() {

        if (tempSelectedIndices == null) {
            tempSelectedIndices = new ArrayList<>(mSelectedIndices);
        }

        String[] formats = new String[ZXingScannerView.ALL_FORMATS.size()];
        boolean[] checkedIndices = new boolean[ZXingScannerView.ALL_FORMATS.size()];

        for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
            formats[i] = ZXingScannerView.ALL_FORMATS.get(i).toString();
            if (tempSelectedIndices.contains(i)) {
                checkedIndices[i] = true;
            } else {
                checkedIndices[i] = false;
            }
        }

        new AlertDialog.Builder(this)
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
                .setNegativeButton("取消", null).create()
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
            if (i == tempCameraId) {
                checkedIndex = i;
            }
        }
        new AlertDialog.Builder(this)
                .setTitle("Select Camera")
                .setSingleChoiceItems(cameraNames, checkedIndex,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tempCameraId = which;
                            }
                        })
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
                        mScannerView.resumeCameraPreview(ScanZXingActivity.this);
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
