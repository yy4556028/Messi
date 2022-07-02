package com.yuyang.messi.ui.camera_crop;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraUtils.BitmapCallback;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.SizeSelector;
import com.otaliastudios.cameraview.SizeSelectors;
import com.yuyang.messi.R;
import com.yuyang.messi.threadPool.ThreadPool;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.CameraViewUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CameraCropVActivity extends AppBaseActivity {

    public static final int TYPE_IDCARD_FRONT = 101;//身份证正面
    public static final int TYPE_IDCARD_BACK = 102;//身份证背面

    private static final String KEY_TYPE = "key_type";
    private static final String KEY_PATH = "key_path";

    public static final String RETURN_PATH = "return_path";

    private ImageView flashImage;
    private CameraView mCameraView;
    private View rectView;

    private int mOrientation;

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
    protected int getLayoutId() {
        return R.layout.activity_camera_crop_v;
    }

    public static Intent getLaunchIntent(ComponentActivity activity, int type, String photoPath) {
        Intent intent = new Intent(activity, CameraCropVActivity.class);
        intent.putExtra(KEY_TYPE, type);
        intent.putExtra(KEY_PATH, photoPath);
        return intent;
    }

    @Override
    public void setContentView(int layoutResID) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.setContentView(layoutResID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
        permissionsLauncher.launch(new String[]{Manifest.permission.CAMERA});
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCameraView.destroy();
    }

    private void initView() {
        flashImage = findViewById(R.id.activity_camera_crop_flashImage);
        mCameraView = findViewById(R.id.activity_camera_crop_cameraView);
        SizeSelector width = SizeSelectors.maxWidth(1080);
        SizeSelector height = SizeSelectors.maxHeight(1920);
        SizeSelector dimensions = SizeSelectors.and(width, height, SizeSelectors.biggest());
        mCameraView.setPictureSize(dimensions);

        rectView = findViewById(R.id.activity_camera_crop_rectView);

        TextView titleText = findViewById(R.id.activity_camera_crop_titleText);
        View headView = findViewById(R.id.activity_camera_crop_headView);

        switch (getIntent().getIntExtra(KEY_TYPE, TYPE_IDCARD_FRONT)) {
            case TYPE_IDCARD_FRONT://身份证正面
                titleText.setText("请将身份证人像面置于此区域内拍摄");
                headView.setVisibility(View.VISIBLE);
                break;
            case TYPE_IDCARD_BACK://身份证背面
                titleText.setText("请将身份证国徽面置于此区域内拍摄");
                headView.setVisibility(View.GONE);
                break;
        }
    }

    private void initEvent() {
        findViewById(R.id.activity_camera_crop_backImage).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        flashImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                flashImage.setSelected(!flashImage.isSelected());
                mCameraView.setFlash(flashImage.isSelected() ? Flash.ON : Flash.OFF);
                flashImage.setImageResource(flashImage.isSelected() ? R.drawable.activity_camera_flash_on : R.drawable.activity_camera_flash_off);

            }
        });
        findViewById(R.id.activity_camera_crop_takePhotoBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraView.capturePicture();
            }
        });
        mCameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(final byte[] bytes) {
                CameraUtils.decodeBitmap(bytes, new BitmapCallback() {
                    @Override
                    public void onBitmapReady(final Bitmap bitmap) {
                        ThreadPool.getInstance().execute(new Runnable() {
                            @Override
                            public void run() {
                                FileOutputStream fos = null;
                                try {

                                    Bitmap cropBitmap = CameraViewUtil.getCropBitmap(bitmap, mOrientation, mCameraView, rectView);

                                    Matrix matrix = new Matrix();
                                    matrix.postRotate(-90);
                                    Bitmap finalBitmap = Bitmap
                                            .createBitmap(cropBitmap, 0, 0, cropBitmap.getWidth(), cropBitmap.getHeight(), matrix, true);

                                    final String photoPath = getIntent().getStringExtra(KEY_PATH);
                                    File file = new File(photoPath);
                                    if (!file.getParentFile().exists() || !file.getParentFile().isDirectory()) {
                                        file.getParentFile().mkdirs();
                                    }

                                    fos = new FileOutputStream(photoPath);
                                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent();
                                            intent.putExtra(RETURN_PATH, photoPath);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (fos != null) {
                                            fos.close();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onOrientationChanged(int orientation) {
                super.onOrientationChanged(orientation);
                mOrientation = orientation;
            }
        });
    }
}
