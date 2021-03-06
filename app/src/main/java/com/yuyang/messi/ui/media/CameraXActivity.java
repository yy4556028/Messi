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

                if (deniedAskList.size() == 0 && deniedNoAskList.size() == 0) {//?????????
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

    private void startCameraPreview() {//??????????????????????????????
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {

                try {
                    //????????????????????????????????????????????????????????????
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    //????????????
                    preview = new Preview.Builder().build();
                    //??????????????????
                    mImageCapture = new ImageCapture.Builder()
                            .setTargetRotation(Surface.ROTATION_0)//?????????????????? ROTATION_0/ROTATION_90/ROTATION_180/ROTATION_270
                            .setIoExecutor(Executors.newSingleThreadExecutor())//????????????IO??????
                            //CAPTURE_MODE_MAXIMIZE_QUALITY ?????????
                            //CAPTURE_MODE_MINIMIZE_LATENCY ?????????
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                            .setFlashMode(ImageCapture.FLASH_MODE_ON)//?????????
                            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                            /**
                             * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                             * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                             * ???????????? {@link Size} ???????????????????????????????????????????????????????????????????????????????????????
                             */
                            .setTargetResolution(new Size(1280, 720))
                            .build();

                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)//?????????????????????
                            .build();

                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll();
                    //???????????????,???????????????,??????,???????????????
                    camera = cameraProvider.bindToLifecycle(getActivity(), cameraSelector, preview);
                    //  camera.getCameraInfo()
                    //???????????????View
                    preview.setSurfaceProvider(previewView.getSurfaceProvider());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void controlFocus(float width, float height, float x, float y) {//????????????
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

        //??????????????????????????????
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        //??????,????????????????????????????????????
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
                        ToastUtil.showToast("???????????????" + exception.getMessage());
                    }
                });
    }

    private void takeVideo() {
//        VideoCapture videoCapture = new VideoCaptureConfig.Builder()
//                //????????????
//                .setTargetAspectRatio(aspectRatio(width, height))
//                //??????????????????
//                .setTargetRotation(previewView.getDisplay().getRotation())
//                .build();
//        //?????????????????????
//        cameraProvider.unbindAll();
//        //??????????????????
//        preview.setSurfaceProvider(previewView.getSurfaceProvider());
//        //?????????????????????????????????????????????preview?????????????????????????????????
//        cameraProvider.bindToLifecycle(this, cameraSelector,preview, videoCapture);
//        //????????????
//        File file = getFile(".mp4");
//        //????????????
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
//        //???????????????????????????OnVideoSavedCallback
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
                int rotationDegrees = image.getImageInfo().getRotationDegrees();//????????????
                int format = image.getFormat();//??????
                int width = image.getWidth();//???
                int height = image.getHeight();//???
                ImageProxy.PlaneProxy plane = image.getPlanes()[0];//PlaneProxy??????
                ByteBuffer buffer = plane.getBuffer();
                //CameraX ????????? YUV_420_888 ??????????????? ???ImageFormat???????????????????????????
            }
        });
    }

    private void setPreviewExtender(Preview.Builder builder, CameraSelector cameraSelector) {
//        val extender = AutoPreviewExtender.create(builder)
//        //????????????
//        if (extender.isExtensionAvailable(cameraSelector)) {
//            extender.enableExtension(cameraSelector)
//        }
//        //????????????
//        val bokehPreviewExtender = BokehPreviewExtender.create(builder)
//        if (bokehPreviewExtender.isExtensionAvailable(cameraSelector)) {
//            bokehPreviewExtender.enableExtension(cameraSelector)
//        }
//        //hdr??????
//        val hdrPreviewExtender = HdrPreviewExtender.create(builder)
//        if (hdrPreviewExtender.isExtensionAvailable(cameraSelector)) {
//            hdrPreviewExtender.enableExtension(cameraSelector)
//        }
//        //????????????
//        val beautyPreviewExtender = BeautyPreviewExtender.create(builder)
//        if (beautyPreviewExtender.isExtensionAvailable(cameraSelector)) {
//            beautyPreviewExtender.enableExtension(cameraSelector)
//        }
//        //????????????
//        val nightPreviewExtender = NightPreviewExtender.create(builder)
//        if (nightPreviewExtender.isExtensionAvailable(cameraSelector)) {
//            nightPreviewExtender.enableExtension(cameraSelector)
//        }
    }
}
