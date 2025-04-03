package com.yuyang.messi.ui.media;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

import com.yuyang.lib_base.utils.BitmapUtil;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.lib_base.utils.SystemBarUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.helper.VideoRecorderHelper;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.Camera1Util;
import com.yuyang.messi.utils.media.VideoCodec;
import com.yuyang.messi.view.Progress.RoundProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Camera1Activity extends AppBaseActivity implements View.OnTouchListener {

    private View surfaceOrTextureView;
    //    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    //    private TextureView textureView;
    private SurfaceTexture surfaceTexture;
    private byte[] buffer;

    private Camera mCamera;
    private int mCurrentCamera = Camera.CameraInfo.CAMERA_FACING_BACK;

    private ImageView flashSwitchButton;    //闪光灯模式切换
    private int mFlashMode = 0;
    private final String[] flashModeArr = new String[]{
            Camera.Parameters.FLASH_MODE_AUTO,
            Camera.Parameters.FLASH_MODE_ON,
            Camera.Parameters.FLASH_MODE_OFF,
//            Camera.Parameters.FLASH_MODE_TORCH,
//            Camera.Parameters.FLASH_MODE_RED_EYE,
    };

    private ImageView cameraSwitchButton;   // 前后镜头切换

    private RelativeLayout seekBarLayout;   //远近镜头 seek bar layout
    private SeekBar seekBar;

    private RoundProgressBar cameraShutter;

    private final VideoRecorderHelper videoRecorderHelper = new VideoRecorderHelper();

    private VideoCodec videoCodec = new VideoCodec();

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
                } else if (deniedNoAskList.size() > 0) {
                    finish();
                } else {
                    finish();
                }
            });

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera1;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        SystemBarUtil.configBar(getActivity(), false, false, true, true, false);

        permissionsLauncher.launch(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO});
    }

    private void initView() {
        surfaceOrTextureView = findViewById(R.id.surfaceOrTextureView);
        if (surfaceOrTextureView instanceof SurfaceView) {
        } else if (surfaceOrTextureView instanceof TextureView) {
        } else {
            finish();
            return;
        }

        surfaceOrTextureView.setKeepScreenOn(true);

        flashSwitchButton = findViewById(R.id.activity_camera_flash);
        cameraSwitchButton = findViewById(R.id.activity_camera_switch);
        seekBarLayout = findViewById(R.id.activity_camera_seekbar_layout);
        seekBar = findViewById(R.id.activity_camera_seekbar);
        cameraShutter = findViewById(R.id.activity_camera_shutter);

        if (surfaceOrTextureView instanceof SurfaceView) {  //SurfaceView
            surfaceHolder = ((SurfaceView) surfaceOrTextureView).getHolder();
            // surfaceView 不需要自己的缓冲区
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    mCamera = Camera1Util.getCameraInstance(mCurrentCamera, flashModeArr[mFlashMode]);
                    if (mCamera == null) {
                        ToastUtil.showToast("没有权限");
                        finish();
                        return;
                    }

                    try {
                        mCamera.setPreviewDisplay(holder);//通过SurfaceView显示预览
                        mCamera.startPreview();//开始预览
                        initZoomSeekBar();
                    } catch (IOException e) {
                        Camera1Util.releaseCamera(mCamera);
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    Camera1Util.releaseCamera(mCamera);
                }
            });
        } else {    //TextureView
            ((TextureView) surfaceOrTextureView).setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    surfaceTexture = surface;
                    mCamera = Camera1Util.getCameraInstance(mCurrentCamera, flashModeArr[mFlashMode]);

                    if (mCamera == null) {
                        ToastUtil.showToast("没有权限");
                        finish();
                        return;
                    }
                    boolean study = true;//学习视频图像编码

                    if (study) {
                        buffer = new byte[mCamera.getParameters().getPreviewSize().width * mCamera.getParameters().getPreviewSize().height * 3 / 2];
                        mCamera.addCallbackBuffer(buffer);
                        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                            @Override
                            public void onPreviewFrame(byte[] data, Camera camera) {//
                                videoCodec.queueEncode(data);
                            }
                        });
                    }

                    try {
                        mCamera.setPreviewTexture(surfaceTexture);
                        mCamera.startPreview();//开启预览
                        initZoomSeekBar();
                    } catch (IOException ioe) {
                        Camera1Util.releaseCamera(mCamera);
                    }
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    Camera1Util.releaseCamera(mCamera);
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });
        }
    }

    private void initEvent() {
        surfaceOrTextureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCamera == null)
                    return;

                surfaceOrTextureView.setEnabled(false);
                if (mCamera.getParameters().getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            surfaceOrTextureView.setEnabled(true);
                            if (success) {
                            } else {
                            }
                        }
                    });
                }
            }
        });
        surfaceOrTextureView.setOnTouchListener(this);

        flashSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Camera1Util.isCameraSupportFlashMode(mCamera)) {

                    mFlashMode = (mFlashMode + 1) % flashModeArr.length;
                    switch (flashModeArr[mFlashMode]) {
                        case Camera.Parameters.FLASH_MODE_AUTO:
                            flashSwitchButton.setImageResource(R.drawable.activity_camera_flash_auto);
                            break;
                        case Camera.Parameters.FLASH_MODE_ON:
                            flashSwitchButton.setImageResource(R.drawable.activity_camera_flash_on);
                            break;
                        case Camera.Parameters.FLASH_MODE_OFF:
                            flashSwitchButton.setImageResource(R.drawable.activity_camera_flash_off);
                            break;
                    }
                    Camera1Util.switchFlashMode(mCamera, flashModeArr[mFlashMode]);
                } else {
                    ToastUtil.showToast("Not_Support_Camera_Flash_Error");
                }
            }
        });

        cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Camera.getNumberOfCameras() > 1) {
                    cameraShutter.setEnabled(false);
                    switchCamera();
                } else {
                    ToastUtil.showToast("No_More_Cameras_Error");
                }
            }
        });

        cameraShutter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleShutterTouch(event);
            }
        });
    }

    private void switchCamera() {
        int number = Camera.CameraInfo.CAMERA_FACING_BACK;
        switch (mCurrentCamera) {
            case Camera.CameraInfo.CAMERA_FACING_BACK:
                number = Camera.CameraInfo.CAMERA_FACING_FRONT;
                break;
            case Camera.CameraInfo.CAMERA_FACING_FRONT:
                number = Camera.CameraInfo.CAMERA_FACING_BACK;
                break;
        }
        mCurrentCamera = number;
        Camera1Util.releaseCamera(mCamera);
        mCamera = Camera1Util.getCameraInstance(mCurrentCamera, flashModeArr[mFlashMode]);

        if (mCamera == null) {
            ToastUtil.showToast("没有权限");
            finish();
            return;
        }

        try {
            if (surfaceOrTextureView instanceof SurfaceView)
                mCamera.setPreviewDisplay(surfaceHolder);
            else
                mCamera.setPreviewTexture(surfaceTexture);
            //开启预览
            mCamera.startPreview();
        } catch (IOException ioe) {
            Camera1Util.releaseCamera(mCamera);
        }
        cameraShutter.setEnabled(true);
    }

    private void initZoomSeekBar() {
        seekBar.setProgress(0);

        if (Camera1Util.isCameraSupportZoom(mCamera)) {
            seekBarLayout.setVisibility(View.VISIBLE);
            seekBar.setMax(mCamera.getParameters().getMaxZoom());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Camera1Util.zoomCamera(mCamera, progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        } else {
            seekBarLayout.setVisibility(View.INVISIBLE);
            seekBar.setOnSeekBarChangeListener(null);
        }
    }

    /**
     * 拍摄处理分割线
     **/

    private final static int CAPTURE_PHOTO = 3;// 拍照
    private final static int CAPTURE_VIDEO = 4;// 录像
    private final static int SAVE_PHOTO = 5;//保存拍照

    private CameraHandler cameraHandler = new CameraHandler();

    class CameraHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {

                case CAPTURE_PHOTO:
                    capturePhoto();
                    break;

                case CAPTURE_VIDEO:
                    videoRecorderHelper.initRecorder(mCamera, mCurrentCamera);
                    videoRecorderHelper.startRecording(mCamera, mCurrentCamera);
                    videoCodec.startRecoding(
                            StorageUtil.getExternalFile("/video/a.mp4").getAbsolutePath(),
                            mCamera.getParameters().getPreviewSize().width,
                            mCamera.getParameters().getPreviewSize().height,
                            90);
                    break;

                case SAVE_PHOTO:
                    if (msg.obj instanceof Bitmap) {
                        BitmapUtil.saveBitmap((Bitmap) msg.obj, new File(StorageUtil.getExternalFilesDir(StorageUtil.PICTURE), "Camera1.png"));
                    }
                    break;

                default:
                    break;
            }
        }
    }

    // 拍照
    private void capturePhoto() {
        Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (data == null) {
                    ToastUtil.showToast("拍照失败");
                    return;
                }

                ToastUtil.showToast("拍照成功");

                // 原图 bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                bitmap = BitmapUtil.rotateBitmapByDegree(bitmap, 90);
                bitmap = BitmapUtil.cropBitmap(bitmap, 1080, 1080, true);

                //拍照完成后如果还要继续拍照则调用camera.startPreview()继续开启预览，否则关闭预览，释放相机资源
                mCamera.startPreview();
                Message message = cameraHandler.obtainMessage(SAVE_PHOTO);
                message.obj = bitmap;
                cameraHandler.sendMessage(message);
            }
        };

        /**
         * shutter ：在拍照的瞬间被回调，这里通常可以播放"咔嚓"这样的拍照音效
         * raw ：返回未经压缩的图像数据
         * postview ：返回postview类型的图像数据
         * jpeg ：返回经过JPEG压缩的图像数据
         */
        mCamera.takePicture(null, null, pictureCallback);
    }

    private boolean handleShutterTouch(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cameraHandler.removeMessages(CAPTURE_PHOTO);
                cameraHandler.removeMessages(CAPTURE_VIDEO);

                cameraHandler.sendEmptyMessageDelayed(CAPTURE_VIDEO, 300);
                break;
            case MotionEvent.ACTION_UP:
                cameraHandler.removeMessages(CAPTURE_VIDEO);
                if (!videoRecorderHelper.isRecording())
                    cameraHandler.sendEmptyMessage(CAPTURE_PHOTO);
                else {
                    videoRecorderHelper.stopRecording();
                    videoCodec.stopRecording();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /********************手势缩放处理***************************/
    private double startDistance;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (event.getPointerCount() > 1) {
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                startDistance = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE && mCamera.getParameters().isZoomSupported()) {
                mCamera.cancelAutoFocus();
                handleZoom(event);
            }
        } else {
            // Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO 模式下不支持自动对焦
            if (action == MotionEvent.ACTION_UP && mCamera.getParameters().getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                        } else {
                        }
                    }
                });
            }
        }
        return false;
    }

    private double getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Math.sqrt(x * x + y * y);
    }

    private void handleZoom(MotionEvent event) {
        double newDist = getFingerSpacing(event);
        int maxZoom = mCamera.getParameters().getMaxZoom();
        int zoom = mCamera.getParameters().getZoom();
        if (newDist > startDistance) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < startDistance) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        startDistance = newDist;
        seekBar.setProgress(zoom);
    }

}
