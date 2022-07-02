package com.yuyang.messi.utils;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.DeviceUtil;
import com.yuyang.messi.MessiApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Camera1Util {

    private static final String TAG = Camera1Util.class.getSimpleName();

    /**
     * 打开摄像头并返回camera，默认打开后置摄像头
     *
     * @return
     */
    public static Camera getCameraInstance() {
        return getCameraInstance(Camera.CameraInfo.CAMERA_FACING_BACK, Camera.Parameters.FLASH_MODE_OFF);
    }

    /**
     * 打开指定cameraId摄像头并返回 camera
     * 如果返回null，可能是没权限，也可能是该手机没有要打开的cameraId
     *
     * @param cameraId Camera.CameraInfo.CAMERA_FACING_FRONT, Camera.CameraInfo.CAMERA_FACING_BACK
     * @return
     */
    public static Camera getCameraInstance(int cameraId, String flashMode) {
        Camera mCamera = null;
        try {
            mCamera = Camera.open(cameraId);

            final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            int cameraRotationOffset = cameraInfo.orientation;

            final WindowManager windowManager = (WindowManager) MessiApp.getInstance().getSystemService(Context.WINDOW_SERVICE);
            final int rotation = windowManager.getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break; // Natural orientation
                case Surface.ROTATION_90:
                    degrees = 90;
                    break; // Landscape left
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;// Upside down
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;// Landscape right
            }
            int displayRotation;
            //根据前置与后置摄像头的不同，设置预览方向，否则会发生预览图像倒过来的情况。
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                displayRotation = (cameraRotationOffset + degrees) % 360;
                displayRotation = (360 - displayRotation) % 360; // compensate
            } else {
                displayRotation = (cameraRotationOffset - degrees + 360) % 360;
            }
            mCamera.setDisplayOrientation(displayRotation);

//            if (App.getAppContext().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
//                if (DeviceUtil.getDeviceModel().toUpperCase().contains("NEXUS 6P") || DeviceUtil.getDeviceModel().toUpperCase().contains("NEXUS 5X")) {
//                    if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                        mCamera.setDisplayOrientation(270);
//                    } else {
//                        mCamera.setDisplayOrientation(90);
//                    }
//                } else {
//                    mCamera.setDisplayOrientation(90);
//                }
//            } else {
//                mCamera.setDisplayOrientation(0);
//            }

            setCameraParams(mCamera, flashMode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCamera;
    }

    /**
     * 预览前，设置参数
     */
    public static void setCameraParams(Camera camera, String flashMode) {
        Camera.Parameters parameters = camera.getParameters();
        Camera1Util.printSupportPictureSize(parameters);
        Camera1Util.printSupportPreviewSize(parameters);
        Camera1Util.printSupportPreviewFpsRange(parameters);

        //设置相机预览照片帧数
//        parameters.setPreviewFpsRange(30000, 30000);

        //设置拍照后存储的图片格式
        parameters.setPictureFormat(ImageFormat.JPEG);

        //设置图片的质量   0 和 100 看不出来什么区别
        parameters.setJpegQuality(100);

        //默认全屏的比例预览
        float previewRate = CommonUtil.getScreenRate();

        //设置PreviewSize和PictureSize
        Size previewSize = Camera1Util.getPropPreviewSize(parameters.getSupportedPreviewSizes(), previewRate);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        previewRate = (float) previewSize.width / previewSize.height;

        Size pictureSize = Camera1Util.getPropPictureSize(parameters.getSupportedPictureSizes(), previewRate, 1080);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);

        // 设置对焦模式
        //系统版本为8一下的不支持这种对焦
        if (DeviceUtil.getSdkVersion() > Build.VERSION_CODES.FROYO) {
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes != null) {
                if (((Build.MODEL.startsWith("GT-I950"))
                        || (Build.MODEL.endsWith("SCH-I959"))
                        || (Build.MODEL.endsWith("MEIZU MX3"))) && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {

                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
            }
        }

        // 设置闪光灯模式
        if (isCameraSupportFlashMode(parameters)) {
            parameters.setFlashMode(flashMode);
        }
        camera.setParameters(parameters);
    }

    /**
     * 是否支持缩放
     *
     * @param camera
     * @return
     */
    public static boolean isCameraSupportZoom(Camera camera) {
        if (camera == null) return false;
        return camera.getParameters().isZoomSupported();
    }

    public static void zoomCamera(Camera camera, int value) {
        if (camera == null || !camera.getParameters().isZoomSupported()) return;
        Camera.Parameters parameters = camera.getParameters();
        parameters.setZoom(value);
        camera.setParameters(parameters);
    }

    /**
     * 是否支持闪光灯模式切换
     *
     * @param camera
     * @return
     */
    public static boolean isCameraSupportFlashMode(Camera camera) {
        if (camera == null) return false;
        return isCameraSupportFlashMode(camera.getParameters());
    }

    /**
     * 是否支持闪光灯模式切换
     *
     * @param parameters
     * @return
     */
    public static boolean isCameraSupportFlashMode(Camera.Parameters parameters) {
        List<String> modes = parameters.getSupportedFlashModes();
        if (modes != null && modes.size() != 0) {
            boolean autoSupported = modes.contains(Camera.Parameters.FLASH_MODE_AUTO);
            boolean onSupported = modes.contains(Camera.Parameters.FLASH_MODE_ON);
            boolean offSupported = modes.contains(Camera.Parameters.FLASH_MODE_OFF);
            return autoSupported && onSupported && offSupported;
        }
        return false;
    }

    /**
     * 切换指定模式闪光灯
     *
     * @param camera
     * @param flashMode
     */
    public static void switchFlashMode(Camera camera, String flashMode) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(flashMode);
        camera.setParameters(parameters);
    }

    public static void releaseCamera(Camera camera) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public static Size getPropPictureSize(List<Size> list, float ratio, int minWidth) {
        Collections.sort(list, new CameraSizeComparator());// 分辨率从大到小

        // 符合 ratio 的
        List<Size> tempList = new ArrayList<>();
        // 分辨率刚好达标的
        int tempIndex = -1;

        for (int i = 0; i < list.size(); i++) {

            if (equalRate(list.get(i), ratio)) {
                if (list.get(i).width > minWidth && list.get(i).height > minWidth) {
                    tempList.add(list.get(i));
                }
            }

            if (tempIndex == -1) {
                if (list.get(i).width < minWidth || list.get(i).height < minWidth) {
                    tempIndex = i == 0 ? 0 : i - 1;
                }
            }
        }

        if (tempList.size() > 0) {
            return tempList.get(tempList.size() - 1);
        } else {
            if (tempIndex != -1)
                return list.get(tempIndex);
            else {
                return list.get(0);
            }
        }
    }

    /**
     * 找出与指定分辨率最接近的size
     *
     * @return
     */
    public static Size findBestPreviewSizeValue(List<Size> sizeList, int w, int h) {
        Collections.sort(sizeList, new CameraSizeComparator());// 分辨率从大到小
        int diff = Integer.MAX_VALUE;
        Size bestSize = null;

        for (Size size : sizeList) {
            int newDiff = Math.abs(size.width - w) + Math.abs(size.height - h);
            if (newDiff == 0) {
                return size;
            } else if (newDiff < diff) {
                bestSize = size;
                diff = newDiff;
            }
        }
        if (bestSize == null) {
            bestSize = sizeList.get(0);
        }
        return bestSize;
    }

    /**
     * 找出与指定比例最接近的size
     *
     * @return
     */
    public static Size getPropPreviewSize(List<Size> list, float ratio) {
        Collections.sort(list, new CameraSizeComparator());// 分辨率从大到小

        // ratio <= 0 选最大的
        if (ratio <= 0)
            return list.get(0);

        int i = 0;

        for (Size s : list) {
            if (equalRate(s, ratio)) {
                Log.i(TAG, "PreviewSize:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;//如果没找到，就选最大的size
        }
        return list.get(i);
    }

    private static boolean equalRate(Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

    private static class CameraSizeComparator implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {

            if (lhs.width * lhs.height > rhs.width * rhs.height) {
                return -1;
            } else if (lhs.width * lhs.height == rhs.width * rhs.height) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    /**
     * 打印支持的previewSizes
     *
     * @param params
     */
    public static void printSupportPreviewSize(Camera.Parameters params) {
        List<Size> previewSizes = params.getSupportedPreviewSizes();
        Log.i(TAG, "previewSizes =====================Start========================");
        for (int i = 0; i < previewSizes.size(); i++) {
            Size size = previewSizes.get(i);
            Log.i(TAG, "previewSizes:width = " + size.width + " height = " + size.height);
        }
        Log.i(TAG, "previewSizes =====================Stop========================");
    }

    /**
     * 打印支持的pictureSizes
     *
     * @param params
     */
    public static void printSupportPictureSize(Camera.Parameters params) {
        List<Size> pictureSizes = params.getSupportedPictureSizes();
        Log.i(TAG, "pictureSizes =====================Start========================");
        for (int i = 0; i < pictureSizes.size(); i++) {
            Size size = pictureSizes.get(i);
            Log.i(TAG, "pictureSizes:width = " + size.width
                    + " height = " + size.height);
        }
        Log.i(TAG, "pictureSizes ======================Stop=======================");
    }

    public static void printSupportPreviewFpsRange(Camera.Parameters params) {
        Log.i(TAG, "previewFpsRange ======================Stop=======================");
        List<int[]> previewFpsRange = params.getSupportedPreviewFpsRange();
        for (int i = 0; i < previewFpsRange.size(); i++) {
            int[] range = previewFpsRange.get(i);
            Log.i(TAG, "previewFpsRange: = " + range[0] + " ~ " + range[1]);
        }
        Log.i(TAG, "previewFpsRange ======================Stop=======================");
    }

    /**
     * 打印支持的聚焦模式
     * Camera.Parameters.FOCUS_MODE_AUTO 自动对焦模式，摄影小白专用模式；
     * Camera.Parameters.FOCUS_MODE_FIXED 固定焦距模式，拍摄老司机模式；
     * Camera.Parameters.FOCUS_MODE_EDOF 景深模式，文艺女青年最喜欢的模式；
     * Camera.Parameters.FOCUS_MODE_INFINITY 远景模式，拍风景大场面的模式；
     * Camera.Parameters.FOCUS_MODE_MACRO 微焦模式，拍摄小花小草小蚂蚁专用模式；
     *
     * @param params
     */
    public static void printSupportFocusMode(Camera.Parameters params) {
        List<String> focusModes = params.getSupportedFocusModes();
        for (String mode : focusModes) {
            Log.i(TAG, "focusModes--" + mode);
        }
    }

    /**
     * 打印支持的聚焦模式
     * Camera.Parameters.SCENE_MODE_BARCODE 扫描条码场景，NextQRCode项目会判断并设置为这个场景；
     * Camera.Parameters.SCENE_MODE_ACTION 动作场景，就是抓拍跑得飞快的运动员、汽车等场景用的；
     * Camera.Parameters.SCENE_MODE_AUTO 自动选择场景；
     * Camera.Parameters.SCENE_MODE_HDR 高动态对比度场景，通常用于拍摄晚霞等明暗分明的照片；
     * Camera.Parameters.SCENE_MODE_NIGHT 夜间场景；
     *
     * @param params
     */
    public static void printSupportSceneMode(Camera.Parameters params) {
        List<String> sceneModes = params.getSupportedSceneModes();
        for (String mode : sceneModes) {
            Log.i(TAG, "sceneModes--" + mode);
        }
    }
}
