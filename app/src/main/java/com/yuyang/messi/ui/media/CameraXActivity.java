package com.yuyang.messi.ui.media;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.util.Size;
import android.view.Surface;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.lib_base.utils.SystemBarUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

public class CameraXActivity extends AppBaseActivity {

    private PreviewView previewView;
    private Preview preview;
    private ImageCapture mImageCapture;
    private ImageAnalysis imageAnalyzer;
    private Camera camera;

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
                    startCameraPreview();
                } else if (deniedNoAskList.size() > 0) {
                    finish();
                } else {
                    finish();
                }
            });

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camerax;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        permissionsLauncher.launch(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO});
    }

    @Override
    public void setStatusBar() {
        SystemBarUtil.fullScreen_immersive(getActivity(), false, false, true, true, false);
    }

    private void initView() {
        previewView = findViewById(R.id.previewView);
    }

    private void initEvent() {
    }

    private void startCameraPreview() {//无需考虑摄像头的释放
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {

                try {
                    //用于将相机的生命周期绑定到生命周期所有者
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    //创建预览
                    preview = new Preview.Builder().build();
                    //创建图像捕捉
                    mImageCapture = new ImageCapture.Builder()
                            .setTargetRotation(Surface.ROTATION_0)//设置旋转角度 ROTATION_0/ROTATION_90/ROTATION_180/ROTATION_270
                            .setIoExecutor(Executors.newSingleThreadExecutor())//设置执行IO线程
                            //CAPTURE_MODE_MAXIMIZE_QUALITY 高画质
                            //CAPTURE_MODE_MINIMIZE_LATENCY 低延迟
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                            .setFlashMode(ImageCapture.FLASH_MODE_ON)//闪光灯
                            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                            /**
                             * 目标分辨率尝试建立图像分辨率的最小界限。实际图像分辨率将是尺寸上最接近的可用分辨率，该分辨率不小于由相机实现确定的目标分辨率。
                             * 但是，如果不存在等于或大于目标分辨率的分辨率，则将选择最接近的小于目标分辨率的可用分辨率。
                             * 与提供的 {@link Size} 具有相同纵横比的分辨率将在不同纵横比的分辨率之前优先考虑。
                             */
                            .setTargetResolution(new Size(1280, 720))
                            .build();

                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)//选择后置摄像头
                            .build();

                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll();
                    //将生命周期,选择摄像头,预览,绑定到相机
                    camera = cameraProvider.bindToLifecycle(getActivity(), cameraSelector, preview);
                    //  camera.getCameraInfo()
                    //设置预览的View
                    preview.setSurfaceProvider(previewView.getSurfaceProvider());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void controlFocus(float width, float height, float x, float y) {//控制对焦
//        SurfaceOrientedMeteringPointFactory factory = new SurfaceOrientedMeteringPointFactory(width, height);
//        MeteringPoint point = factory.createPoint(x, y);
//        FocusMeteringAction action = new FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
//                .addPoint(point2, FocusMeteringAction.FLAG_AE) // could have many
//                // auto calling cancelFocusAndMetering in 5 seconds
//                .setAutoCancelDuration(5, TimeUnit.SECONDS)
//                .build();
//
//        val future = cameraControl.startFocusAndMetering(action);
//        future.addListener( Runnable {
//            val result = future.get()
//            // process the result
//        } , executor)
    }

    private void takePhoto() {
        // Create timestamped output file to hold the image
        File photoFile = new File(
                StorageUtil.getPrivateCache(),
                "CameraX_" + new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis()) + ".jpg"
        );

        //创建图像文件输出选项
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        //拍照,并且注册拍照后的结果监听
        mImageCapture.takePicture(outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = Uri.fromFile(photoFile);
                        String msg = "Photo capture succeeded: $savedUri";
                        ToastUtil.showToast(msg);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        ToastUtil.showToast("拍照失败：" + exception.getMessage());
                    }
                });
    }

    private void takeVideo() {
//        VideoCapture videoCapture = new VideoCaptureConfig.Builder()
//                //设置宽高
//                .setTargetAspectRatio(aspectRatio(width, height))
//                //设置旋转角度
//                .setTargetRotation(previewView.getDisplay().getRotation())
//                .build();
//        //录像前必须解绑
//        cameraProvider.unbindAll();
//        //开启相机预览
//        preview.setSurfaceProvider(previewView.getSurfaceProvider());
//        //绑定生命周期，这里如果没有参数preview，则只录像，不显示画面
//        cameraProvider.bindToLifecycle(this, cameraSelector,preview, videoCapture);
//        //视频路径
//        File file = getFile(".mp4");
//        //开始录像
//        videoCapture.startRecording(file, ContextCompat.getMainExecutor(MainActivity.this), new VideoCapture.OnVideoSavedCallback() {
//            @Override
//            public void onVideoSaved(@NonNull File file) {
//                Toast.makeText(MainActivity.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
//                Log.d(TAG, "onError: " + message);
//            }
//        });
//        //停止录像，并且回调OnVideoSavedCallback
//        btn4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                videoCapture.stopRecording();
//                preview.clear();
//            }
//        });
    }

    private void imageAnalysis() {
        ImageAnalysis imageAnalyzer = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor(), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                int rotationDegrees = image.getImageInfo().getRotationDegrees();//旋转角度
                int format = image.getFormat();//格式
                int width = image.getWidth();//宽
                int height = image.getHeight();//高
                ImageProxy.PlaneProxy plane = image.getPlanes()[0];//PlaneProxy数据
                ByteBuffer buffer = plane.getBuffer();
                //CameraX 会生成 YUV_420_888 格式的图片 在ImageFormat类里可以看到此格式
            }
        });
    }

    private void setPreviewExtender(Preview.Builder builder, CameraSelector cameraSelector) {
//        val extender = AutoPreviewExtender.create(builder)
//        //自动模式
//        if (extender.isExtensionAvailable(cameraSelector)) {
//            extender.enableExtension(cameraSelector)
//        }
//        //散景扩展
//        val bokehPreviewExtender = BokehPreviewExtender.create(builder)
//        if (bokehPreviewExtender.isExtensionAvailable(cameraSelector)) {
//            bokehPreviewExtender.enableExtension(cameraSelector)
//        }
//        //hdr扩展
//        val hdrPreviewExtender = HdrPreviewExtender.create(builder)
//        if (hdrPreviewExtender.isExtensionAvailable(cameraSelector)) {
//            hdrPreviewExtender.enableExtension(cameraSelector)
//        }
//        //美颜模式
//        val beautyPreviewExtender = BeautyPreviewExtender.create(builder)
//        if (beautyPreviewExtender.isExtensionAvailable(cameraSelector)) {
//            beautyPreviewExtender.enableExtension(cameraSelector)
//        }
//        //夜晚模式
//        val nightPreviewExtender = NightPreviewExtender.create(builder)
//        if (nightPreviewExtender.isExtensionAvailable(cameraSelector)) {
//            nightPreviewExtender.enableExtension(cameraSelector)
//        }
    }
}
