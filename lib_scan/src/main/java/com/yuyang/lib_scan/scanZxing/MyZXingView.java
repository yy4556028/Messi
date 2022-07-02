package com.yuyang.lib_scan.scanZxing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.yuyang.lib_scan.R;

import java.util.Map;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.ScanResult;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class MyZXingView extends ZXingView {

    private MultiFormatReader mMultiFormatReader;
    private Map<DecodeHintType, Object> mHintMap;

    public MyZXingView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MyZXingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setupReader() {
        mMultiFormatReader = new MultiFormatReader();
        super.setupReader();
    }

    /**
     * 设置识别的格式
     *
     * @param barcodeType 识别的格式
     * @param hintMap barcodeType 为 BarcodeType.CUSTOM 时，必须指定该值
     */
    public void setType(BarcodeType barcodeType, Map<DecodeHintType, Object> hintMap) {
        mBarcodeType = barcodeType;
        mHintMap = hintMap;

        if (mBarcodeType == BarcodeType.CUSTOM && (mHintMap == null || mHintMap.isEmpty())) {
            throw new RuntimeException("barcodeType 为 BarcodeType.CUSTOM 时 hintMap 不能为空");
        }
        setupReader();
    }

    @Override
    protected ScanResult processBitmapData(Bitmap bitmap) {
        return new ScanResult(QRCodeDecoder.syncDecodeQRCode(bitmap));
    }

    @Override
    protected ScanResult processData(byte[] data, int width, int height, boolean isRetry) {
        Result rawResult = null;
        Rect scanBoxAreaRect = null;

        try {
            PlanarYUVLuminanceSource source;
            scanBoxAreaRect = mScanBoxView.getScanBoxAreaRect(height);
            if (scanBoxAreaRect != null) {
                source = new PlanarYUVLuminanceSource(data, width, height, scanBoxAreaRect.left, scanBoxAreaRect.top, scanBoxAreaRect.width(),
                    scanBoxAreaRect.height(), false);
            } else {
                source = new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
            }

            rawResult = mMultiFormatReader.decodeWithState(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
            if (rawResult == null) {
                rawResult = mMultiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
                if (rawResult != null) {
                    BGAQRCodeUtil.d("GlobalHistogramBinarizer 没识别到，HybridBinarizer 能识别到");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mMultiFormatReader.reset();
        }

        if (rawResult == null) {
            return null;
        }

        String result = rawResult.getText();
        if (TextUtils.isEmpty(result)) {
            return null;
        }

        BarcodeFormat barcodeFormat = rawResult.getBarcodeFormat();
        BGAQRCodeUtil.d("格式为：" + barcodeFormat.name());

        // 处理自动缩放和定位点
        boolean isNeedAutoZoom = isNeedAutoZoom(barcodeFormat);
        if (isShowLocationPoint() || isNeedAutoZoom) {

//            float ratio = 1.0f * height / mScanBoxView.getMeasuredHeight();

            ResultPoint[] resultPoints = rawResult.getResultPoints();
            final PointF[] pointArr = new PointF[resultPoints.length];
            int pointIndex = 0;
            for (ResultPoint resultPoint : resultPoints) {
                pointArr[pointIndex] = new PointF(resultPoint.getX(), resultPoint.getY());
                pointIndex++;
            }
//
//            Rect ratioRect = null;
//            if (scanBoxAreaRect != null) {
//                ratioRect = new Rect((int) (scanBoxAreaRect.left / ratio),
//                    (int) (scanBoxAreaRect.right / ratio),
//                    (int) (scanBoxAreaRect.top / ratio),
//                    (int) (scanBoxAreaRect.bottom / ratio));
//            }

            if (transformToViewCoordinates(pointArr, scanBoxAreaRect, isNeedAutoZoom, result)) {
                return null;
            }
            _transformToViewCoordinates(pointArr, scanBoxAreaRect, isNeedAutoZoom, result);

            if (scanBoxAreaRect != null) {
                addMarker(scanBoxAreaRect);
            }
        }
        return new ScanResult(result);
    }

    private PointF[] _mLocationPoints;

    protected boolean _transformToViewCoordinates(final PointF[] pointArr, final Rect scanBoxAreaRect, final boolean isNeedAutoZoom, final String result) {
        if (pointArr == null || pointArr.length == 0) {
            return false;
        }

        try {
            // 不管横屏还是竖屏，size.width 大于 size.height
            Camera.Size size = mCamera.getParameters().getPreviewSize();
            boolean isMirrorPreview = mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
            int statusBarHeight = BGAQRCodeUtil.getStatusBarHeight(getContext());

            PointF[] transformedPoints = new PointF[pointArr.length];
            int index = 0;
            for (PointF qrPoint : pointArr) {
                transformedPoints[index] = transform(qrPoint.x, qrPoint.y, size.width, size.height, isMirrorPreview, statusBarHeight, scanBoxAreaRect);
                index++;
            }
            _mLocationPoints = transformedPoints;
            postInvalidate();

//            if (isNeedAutoZoom) {
//                return handleAutoZoom(transformedPoints, result);
//            }
            return false;
        } catch (Exception e) {
            _mLocationPoints = null;
            e.printStackTrace();
            return false;
        }
    }

    private PointF transform(float originX, float originY, float cameraPreviewWidth, float cameraPreviewHeight, boolean isMirrorPreview, int statusBarHeight,
        final Rect scanBoxAreaRect) {
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        PointF result;
        float scaleX;
        float scaleY;

        if (BGAQRCodeUtil.isPortrait(getContext())) {
            scaleX = viewWidth / cameraPreviewHeight;
            scaleY = viewHeight / cameraPreviewWidth;
            result = new PointF((cameraPreviewHeight - originX) * scaleX, (cameraPreviewWidth - originY) * scaleY);
            result.y = viewHeight - result.y;
            result.x = viewWidth - result.x;

            if (scanBoxAreaRect == null) {
                result.y += statusBarHeight;
            }
        } else {
            scaleX = viewWidth / cameraPreviewWidth;
            scaleY = viewHeight / cameraPreviewHeight;
            result = new PointF(originX * scaleX, originY * scaleY);
            if (isMirrorPreview) {
                result.x = viewWidth - result.x;
            }
        }

        if (scanBoxAreaRect != null) {
            result.y += scanBoxAreaRect.top;
            result.x += scanBoxAreaRect.left;
        }

        return result;
    }

    private boolean isNeedAutoZoom(BarcodeFormat barcodeFormat) {
        return isAutoZoom() && barcodeFormat == BarcodeFormat.QR_CODE;
    }

    private void addMarker(Rect rect) {
        ViewGroup contentView = ((Activity)getContext()).findViewById(android.R.id.content);

        View bgView = new View(getContext());
        bgView.setBackgroundResource(R.color.red);
        FrameLayout.LayoutParams bgParams = new FrameLayout.LayoutParams(rect.width(), rect.height());
        bgParams.setMargins(rect.left, rect.top, 0, 0);
        bgView.setLayoutParams(bgParams);
        contentView.addView(bgView);
        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                contentView.removeView(bgView);
            }
        }, 1000);
    }
}
