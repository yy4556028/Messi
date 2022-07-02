package com.yuyang.messi.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;

import com.otaliastudios.cameraview.CameraView;

public class CameraViewUtil {

    private static final String TAG = CameraViewUtil.class.getSimpleName();

    public static Bitmap getCropBitmap(final Bitmap bitmap, final int orientation, CameraView mCameraView, View cropView) {
        int rotate = -orientation;

        Bitmap rotateBitmap;
        if (rotate != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            rotateBitmap = Bitmap
                    .createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            rotateBitmap = bitmap;
        }

        Bitmap previewBitmap;
        float rotateBitmapRatio = rotateBitmap.getHeight() / (float) rotateBitmap.getWidth();
        float previewRatio = mCameraView.getMeasuredHeight() / (float) mCameraView.getMeasuredWidth();
        if (rotateBitmapRatio > previewRatio) {
            int delta = (int) (rotateBitmap.getHeight() - rotateBitmap.getWidth() * previewRatio);
            previewBitmap = Bitmap.createBitmap(rotateBitmap,
                    0, delta / 2,
                    rotateBitmap.getWidth(), rotateBitmap.getHeight() - delta);
        } else {
            int delta = (int) (rotateBitmap.getWidth() - rotateBitmap.getHeight() / previewRatio);
            previewBitmap = Bitmap.createBitmap(rotateBitmap,
                    delta / 2, 0,
                    rotateBitmap.getWidth() - delta, rotateBitmap.getHeight());
        }

        Bitmap scaleBitmap = Bitmap.createScaledBitmap(previewBitmap, mCameraView.getMeasuredWidth(), mCameraView.getMeasuredHeight(), true);
        return Bitmap.createBitmap(scaleBitmap,
                cropView.getLeft(), cropView.getTop(),
                cropView.getMeasuredWidth(), cropView.getMeasuredHeight());
    }
}
