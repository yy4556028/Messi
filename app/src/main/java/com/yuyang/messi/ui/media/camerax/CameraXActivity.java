package com.yuyang.messi.ui.media.camerax;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Size;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.ZoomState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.PixelUtils;
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
import java.util.concurrent.TimeUnit;

//https://juejin.cn/post/6951017751457005576
//https://github.com/android/camera-samples/tree/main/CameraXBasic
@SuppressLint("RestrictedApi")
public class CameraXActivity extends AppBaseActivity {

    private PreviewView mPreviewView;
    private Preview mPreview;
    private Camera mCamera;
    private ProcessCameraProvider mCameraProvider;
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private int flashMode = ImageCapture.FLASH_MODE_OFF;
    private boolean isVideoMode;
    private boolean isVideoRecording;
    private final boolean privateDir = false;// 保存到内部还是外部

    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    private ImageCapture mImageCapture;
    private VideoCapture mVideoCapture;
    private ImageAnalysis mImageAnalyzer;

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
                    createUseCase();
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
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        permissionsLauncher.launch(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO});
    }

    @Override
    public void setStatusBar() {
        SystemBarUtil.fullScreen_immersive(getActivity(), false, false, true, true, false);
    }

    private void initView() {
        mPreviewView = findViewById(R.id.previewView);
        mPreviewView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Listen to zoom gesture.
                scalePreview(event);
                onTouchPreview(event);
                return true;
            }
        });

        findViewById(R.id.ivFlash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashMode == ImageCapture.FLASH_MODE_OFF) {
                    flashMode = ImageCapture.FLASH_MODE_AUTO;
                } else {
                    flashMode++;
                }
                switch (flashMode) {
                    case ImageCapture.FLASH_MODE_AUTO:
                        ((ImageView) findViewById(R.id.ivFlash)).setImageResource(R.drawable.activity_camera_flash_auto);
                        break;
                    case ImageCapture.FLASH_MODE_ON:
                        ((ImageView) findViewById(R.id.ivFlash)).setImageResource(R.drawable.activity_camera_flash_on);
                        break;
                    case ImageCapture.FLASH_MODE_OFF:
                        ((ImageView) findViewById(R.id.ivFlash)).setImageResource(R.drawable.activity_camera_flash_off);
                        break;
                }
                createUseCase();
                bindPreview();
            }
        });

        findViewById(R.id.ivSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lensFacing = CameraSelector.LENS_FACING_FRONT == lensFacing ?
                        CameraSelector.LENS_FACING_BACK :
                        CameraSelector.LENS_FACING_FRONT;

                bindPreview();
            }
        });
        findViewById(R.id.tvCapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVideoMode) {
                    if (isVideoRecording) {
                        mVideoCapture.stopRecording();
                        isVideoRecording = false;
                        ((TextView) findViewById(R.id.tvCapture)).setText("开始");
                    } else {
                        takeVideo();
                        ((TextView) findViewById(R.id.tvCapture)).setText("停止");
                    }
                } else {
                    takePhoto();
                }
            }
        });
        findViewById(R.id.tvSwitchCaptureMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideoMode = !isVideoMode;
                if (isVideoMode) {
                    ((TextView) findViewById(R.id.tvCapture)).setText("开始");
                    ((ImageView) findViewById(R.id.tvSwitchCaptureMode)).setImageResource(R.drawable.camera);
                } else {
                    ((TextView) findViewById(R.id.tvCapture)).setText("拍照");
                    ((ImageView) findViewById(R.id.tvSwitchCaptureMode)).setImageResource(R.drawable.video);
                }
                bindPreview();
            }
        });
    }

    private void createUseCase() {
        @AspectRatio.Ratio int screenAspectRatio = aspectRatio();
        //创建图像捕捉
        mImageCapture = new ImageCapture.Builder()
//                .setTargetRotation(Surface.ROTATION_0)//设置旋转角度
                .setTargetRotation(mPreviewView.getDisplay().getRotation())
                .setIoExecutor(Executors.newSingleThreadExecutor())//设置执行IO线程
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)// 高画质
                .setFlashMode(flashMode)//闪光灯
                .setTargetAspectRatio(screenAspectRatio)
                /**
                 * 目标分辨率尝试建立图像分辨率的最小界限。实际图像分辨率将是尺寸上最接近的可用分辨率，该分辨率不小于由相机实现确定的目标分辨率。
                 * 但是，如果不存在等于或大于目标分辨率的分辨率，则将选择最接近的小于目标分辨率的可用分辨率。
                 * 与提供的 {@link Size} 具有相同纵横比的分辨率将在不同纵横比的分辨率之前优先考虑。
                 */
//                            .setTargetResolution(new Size(1280, 720))
                .build();

        mVideoCapture = new VideoCapture.Builder()
                .setTargetRotation(mPreviewView.getDisplay().getRotation())
                .setVideoFrameRate(25)
                .setBitRate(3 * 1024 * 1024)
//                .setTargetAspectRatio(screenAspectRatio)
                .build();

        // 创建图像分析
        mImageAnalyzer = new ImageAnalysis.Builder()
                .setTargetRotation(mPreviewView.getDisplay().getRotation())
                .setTargetResolution(new Size(720, 1280))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
//        mImageAnalyzer.clearAnalyzer();
        mImageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor(), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                //CameraX 会生成 YUV_420_888 格式的图片 在ImageFormat类里可以看到此格式
                int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();//旋转角度
                int format = imageProxy.getFormat();//格式
                int width = imageProxy.getWidth();//宽
                int height = imageProxy.getHeight();//高
                imageAnalyzer(imageProxy);
                imageProxy.close();
            }
        });
    }

    private void startCameraPreview() {//无需考虑摄像头的释放
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    //用于将相机的生命周期绑定到生命周期所有者
                    mCameraProvider = cameraProviderFuture.get();
                    bindPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview() {

        // CameraSelector cameraSelector = isBack ? CameraSelector.DEFAULT_BACK_CAMERA : CameraSelector.DEFAULT_FRONT_CAMERA;
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)//选择前后置摄像头
                .build();

        // 创建预览
        Preview.Builder previewBuilder = new Preview.Builder();

        // Add extended effect for capture and preview.
        MyExtenderHelper extenderHelper = new MyExtenderHelper();
        extenderHelper.setPreviewExtender(previewBuilder, cameraSelector);
//        extenderHelper.setCaptureExtender(mImageCaptureBuilder, cameraSelector);

        mPreview = previewBuilder.build();
//                    BeautyPreviewExtender beautyPreviewExtender = BeautyPreviewExtender.create(previewBuilder);
//                    if (beautyPreviewExtender.isExtensionAvailable(cameraSelector)) {
//                        // Enable the extension if available.
//                        beautyPreviewExtender.enableExtension(cameraSelector);
//                    }

        // Unbind use cases before rebinding
        mCameraProvider.unbindAll();
        //将生命周期,选择摄像头,预览,绑定到相机
        if (isVideoMode) {
            mCamera = mCameraProvider.bindToLifecycle(getActivity(), cameraSelector,
                    mPreview, mVideoCapture, mImageAnalyzer);
        } else {
            mCamera = mCameraProvider.bindToLifecycle(getActivity(), cameraSelector,
                    mPreview, mImageCapture);
        }

        //  camera.getCameraInfo()
        //设置预览的View
        mPreview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
    }

    private void showTapView(int x, int y) {
        PopupWindow popupWindow = new PopupWindow(PixelUtils.dp2px(50), PixelUtils.dp2px(50));
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.rectangle_stroke_white);
        popupWindow.setContentView(imageView);
        popupWindow.showAsDropDown(mPreviewView, x, y);
        mPreviewView.postDelayed(popupWindow::dismiss, 1000);
        mPreviewView.playSoundEffect(SoundEffectConstants.CLICK);
    }

    private void takePhoto() {
        final String filename = "CameraX_" + new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis());

        ImageCapture.OutputFileOptions outputOptions;

        if (privateDir) {
            File photoFile = new File(StorageUtil.getPrivateCache(), filename + ".jpg");
            //创建图像文件输出选项
            outputOptions =
                    new ImageCapture.OutputFileOptions.Builder(photoFile)
                            .build();
        } else {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

            outputOptions =
                    new ImageCapture.OutputFileOptions.Builder(getContentResolver(),
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues)
                            .build();
        }

        //拍照,并且注册拍照后的结果监听
        mImageCapture.takePicture(outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = outputFileResults.getSavedUri();
                        String msg = "Photo capture succeeded: " + savedUri.getPath();
                        ToastUtil.showToast(msg);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        ToastUtil.showToast("拍照失败：" + exception.getMessage());
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void takeVideo() {
        if (isVideoRecording) {
            return;
        }
        isVideoRecording = true;

        final String filename = "CameraX_Video_" + new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis());

        VideoCapture.OutputFileOptions outputOptions;

        if (privateDir) {
            File videoFile = new File(StorageUtil.getPrivateCache(), filename + ".mp4");
            //创建图像文件输出选项
            outputOptions =
                    new VideoCapture.OutputFileOptions.Builder(videoFile)
                            .build();
        } else {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");

            outputOptions =
                    new VideoCapture.OutputFileOptions.Builder(getContentResolver(),
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            contentValues)
                            .build();
        }

        mVideoCapture.startRecording(outputOptions,
                ContextCompat.getMainExecutor(this),
                new VideoCapture.OnVideoSavedCallback() {

                    @Override
                    public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = outputFileResults.getSavedUri();
                        String msg = "Video record succeeded: " + savedUri.getPath();
                        ToastUtil.showToast(msg);
                    }

                    @Override
                    public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                        ToastUtil.showToast("录像失败：" + message);
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
        //人像预览
//        BokehPreviewExtender
    }

    private @AspectRatio.Ratio
    int aspectRatio() {
        int screenWidth = CommonUtil.getScreenMetrics().x;
        int screenHeight = CommonUtil.getScreenMetrics().y;
        double previewRatio = ((double) Math.max(screenWidth, screenHeight)) / Math.min(screenWidth, screenHeight);
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    private void imageAnalyzer(ImageProxy imageProxy) {
        ImageProxy.PlaneProxy plane = imageProxy.getPlanes()[0];//PlaneProxy数据
        ByteBuffer byteBuffer = plane.getBuffer();
        byte[] data = new byte[byteBuffer.remaining()];
        byteBuffer.get(data);

        int width = imageProxy.getWidth();
        int height = imageProxy.getHeight();

        // TODO: 2022/9/20 识别二维码
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data,
                width,
                height,
                0,
                0,
                width,
                height,
                false);

        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Result result = null;
        try {

            result = new MultiFormatReader().decode(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result != null) {
            ToastUtil.showToast(result.getText());
        }

        imageProxy.close();
    }

    // Zoom preview screen when scale gesture.
    private void scalePreview(MotionEvent event) {
        if (mScaleGestureDetector == null) {
            mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    ZoomState zoomState = mCamera.getCameraInfo().getZoomState().getValue();
                    if (zoomState != null) {
                        mCamera.getCameraControl().setZoomRatio(zoomState.getZoomRatio() * detector.getScaleFactor());
                    }
                    return true;
                }
            });
        }
        mScaleGestureDetector.onTouchEvent(event);
    }

    private void onTouchPreview(MotionEvent motionEvent) {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent event) {
                    ZoomState zoomState = mCamera.getCameraInfo().getZoomState().getValue();

                    if (zoomState != null) {
                        float zoomRatio = zoomState.getZoomRatio();
                        float minRatio = zoomState.getMinZoomRatio();

                        // Ratio parameter from 0f to 1f.
                        if (zoomRatio > minRatio) {
                            // Reset to original ratio
                            mCamera.getCameraControl().setLinearZoom(0);
                        } else {
                            // Or zoom to 0.5 ratio
                            mCamera.getCameraControl().setLinearZoom(0.5f);
                            // Zoom to max
                            // mCamera.cameraControl.setLinearZoom(Constants.MAX_ZOOM_SCALE.toFloat())
                        }
                    }
                    return true;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent event) {
//                    SurfaceOrientedMeteringPointFactory factory = new SurfaceOrientedMeteringPointFactory(width, height);
                    FocusMeteringAction action = new FocusMeteringAction.Builder(
                            mPreviewView.getMeteringPointFactory().createPoint(event.getX(), event.getY())
//                            , FocusMeteringAction.FLAG_AF
                    )
//                            .addPoint(point2, FocusMeteringAction.FLAG_AE) // could have many
                            // auto calling cancelFocusAndMetering in 5 seconds
                            .setAutoCancelDuration(5, TimeUnit.SECONDS)
                            .build();

                    try {
                        showTapView((int) event.getX(), (int) event.getY());
                        ListenableFuture<FocusMeteringResult> future = mCamera.getCameraControl().startFocusAndMetering(action);
//                        future.addListener(new Runnable() {
//                            @Override
//                            public void run() {
//                                FocusMeteringResult result = future.get();
//                                if (result.isFocusSuccessful()) {
//                                    // deal
//                                }
//                            }
//                        }, ContextCompat.getMainExecutor(getActivity()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return super.onSingleTapConfirmed(event);
                }
            });
        }
        mGestureDetector.onTouchEvent(motionEvent);
    }
}
