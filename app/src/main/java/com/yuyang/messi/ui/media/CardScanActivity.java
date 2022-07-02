package com.yuyang.messi.ui.media;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

import com.yuyang.lib_base.utils.SystemBarUtil;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.net.okhttp.OkHttpUtil;
import com.yuyang.messi.net.okhttp.callback.Callback;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.Camera1Util;
import com.yuyang.messi.view.Progress.CustomProgressDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class CardScanActivity extends AppBaseActivity {

    private SurfaceView surfaceView;

    private View takePhotoBtn;
    private View flashBtn;

    private Camera camera;

    private boolean isFlashOpen;

    private CustomProgressDialog progressDialog;

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
                    setContentView(getLayoutId());
                    initView();
                    initEvent();
                    initSurfaceView();
                } else if (deniedNoAskList.size() > 0) {
                    finish();
                } else {
                    finish();
                }
            });

    @Override
    protected int getLayoutId() {
        return R.layout.activity_card_scan;
    }

    @Override
    public void setStatusBar() {
        SystemBarUtil.fullScreen_immersive(getActivity(), false, false, true, true, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new CustomProgressDialog(getActivity());
        permissionsLauncher.launch(new String[]{Manifest.permission.CAMERA});
    }

    private void initView() {
        surfaceView = findViewById(R.id.surfaceView);
        takePhotoBtn = findViewById(R.id.activity_card_scan_takePhotoBtn);
        flashBtn = findViewById(R.id.activity_card_scan_flashBtn);
    }

    private void initEvent() {
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes, Camera camera) {
                        progressDialog.show("正在上传图片");

                        File file = new File(StorageUtil.getPrivateCache(), "cardScan.jpg");
                        FileOutputStream fos = null;

                        try {
                            fos = new FileOutputStream(file);
                            byte[] data;

                            Bitmap oldBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            Matrix matrix = new Matrix();
                            Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, true);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            newBitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
                            data = baos.toByteArray();
                            fos.write(data);

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

                        uploadImage(file);
                    }
                });
            }
        });
        flashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFlashOpen) {
                    Camera1Util.switchFlashMode(camera, Camera.Parameters.FLASH_MODE_TORCH);
                } else {
                    Camera1Util.switchFlashMode(camera, Camera.Parameters.FLASH_MODE_OFF);
                }
                isFlashOpen = !isFlashOpen;
            }
        });
    }

    private void initSurfaceView() {
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                camera = Camera1Util.getCameraInstance(Camera.CameraInfo.CAMERA_FACING_BACK, Camera.Parameters.FLASH_MODE_OFF);
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Camera1Util.releaseCamera(camera);
            }
        });
    }

    private void uploadImage(final File file) {
        String url = "http://bcr2.intsig.net/BCRService/BCR_Crop?user=1666487339@qq.com&pass=GHCXGHJWPF4FAW6H&lang=15&json=1";

        OkHttpUtil.post()
                .url(url)
                .addFile("upfile", file.getAbsolutePath(), file)
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response, int id) throws Exception {
                        return response.body().bytes();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        if (file.exists()) {
                            file.delete();
                        }
                        ToastUtil.showToast("失败");
                    }

                    @Override
                    public void onResponse(Object response, int id) {
                        progressDialog.dismiss();
                        if (file.exists()) {
                            file.delete();
                        }
                        parseCard((byte[]) response);
                    }
                });
    }

    private void parseCard(byte[] bytes) {
        progressDialog.show("正在解析图片");
        int index = 0;
        for (; index < bytes.length; index++) {
            if (bytes[index] == (byte) 0XFF && bytes[index + 1] == (byte) 0XD8) {//JPEG文件格式是以 {0XFF, 0XD8}开始，以{0XFF, 0XD9}结束
                break;
            }
        }

        String cardInfo = new String(bytes, 0, index);
        ToastUtil.showToast(cardInfo);

        try {
            File cropFile = new File(StorageUtil.getPrivateCache(), "cardScan.jpg");
            FileOutputStream fos = new FileOutputStream(cropFile.getAbsoluteFile());
            fos.write(bytes, index, bytes.length - index);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();
    }
}
