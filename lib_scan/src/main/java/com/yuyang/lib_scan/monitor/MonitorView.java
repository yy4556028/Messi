package com.yuyang.lib_scan.monitor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Build;
import android.util.AttributeSet;

import com.yuyang.lib_base.utils.BitmapUtil;
import com.yuyang.lib_base.utils.StorageUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.core.ScanResult;


/**
 * TessTwoView
 * <p>
 * create by 18010426 at 2018/12/27
 */
public class MonitorView extends QRCodeView {

    public static final String TAG = MonitorView.class.getSimpleName();

    private boolean isProcessing = false;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.getDefault());

    public MonitorView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MonitorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setupReader() {
        //初始化
    }

    @Override
    protected ScanResult processBitmapData(Bitmap bitmap) {
        return null;
    }

    @Override
    protected ScanResult processData(byte[] data, int width, int height, boolean isRetry) {

        if (!mSpotAble || data == null || data.length == 0 || width == 0 || height == 0) {
            return null;
        }

        Rect scanBoxAreaRect = mScanBoxView.getScanBoxAreaRect(height);

        try {

            Bitmap bitmap = data2Bitmap_yuv(data, width, height, scanBoxAreaRect);

            if (bitmap != null && !isProcessing) {
                isProcessing = true;
                String fileName = sdf.format(new Date()) + ".png";
                BitmapUtil.saveBitmap(bitmap, new File(StorageUtil.getPrivateCache(), fileName));
                isProcessing = false;
                return new ScanResult(fileName);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap data2Bitmap_yuv(byte[] data, int width, int height, Rect scanBoxAreaRect) {
        int imageWidth;
        int imageHeight;
        int top;
        int left;
        if (scanBoxAreaRect != null) {
            imageWidth = scanBoxAreaRect.width();
            imageHeight = scanBoxAreaRect.height();
            left = scanBoxAreaRect.left;
            top = scanBoxAreaRect.top;
        } else {
            imageWidth = width;
            imageHeight = height;
            top = 0;
            left = 0;
        }

        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                FastYUVUtil.initRenderScript(getContext());
                bitmap = FastYUVUtil.convertYUVtoRGB(data, width, height);
            } else {
                YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 50, stream);
                bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                stream.flush();
                stream.close();
            }
            bitmap = Bitmap.createBitmap(bitmap, left, top, imageWidth, imageHeight, null, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void startSpotAndShowRect() {
        startSpot();
        hiddenScanRect();
    }

    @Override
    public void stopSpot() {
        super.stopSpot();

    }

    @Override
    public void startSpot() {
        super.startSpot();
    }
}
