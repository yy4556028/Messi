package com.yuyang.messi.listener;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * 创建者: yuyang
 * 创建日期: 2015-06-28
 * 创建时间: 18:14
 * MultiPointTouchListener: MultiPointTouchListener
 *
 * @author yuyang
 * @version 1.0
 */

public class MultiPointTouchListener implements OnTouchListener {

    // These matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int SINGLE = 1;
    static final int MULTI = 2;
    private int mode = NONE;

    // Remember some things for translate zoom rotate
    // single translate
    private PointF start = new PointF();
    // multi translate
    private PointF oldMid = new PointF();
    private PointF newMid = new PointF();
    // multi zoom
    private float oldDist = 0f;
    private float newDist = 0f;
    // multi rotate
    private float oldDegree = 0f;
    private float newDegree = 0f;

    public boolean canTranslate = true;
    public boolean canScale = true;
    public boolean canRotate = true;

    private float translateX;
    private float translateY;

    private float scaleRate;

    // 保存手指按下前的旋转角度
    private float savedDegree = 0f;
    private float rotateDegree = 0f;

    //暂时不用 for 边界控制
//    private int viewWidth;
//    private int viewHeight;
//
//    public void setViewSize(int viewWidth, int viewHeight) {
//        this.viewWidth = viewWidth;
//        this.viewHeight = viewHeight;
//    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        ImageView view = (ImageView) v;

        // Log.e("view_width",
        // view.getImageMatrix()..toString()+"*"+v.getWidth());
        // Dump touch event to log
        dumpEvent(event);

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                matrix.set(view.getImageMatrix());
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                // Log.d(TAG, "mode=DRAG");
                mode = SINGLE;

                // Log.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = (float) spacing(event);
                // Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(oldMid, event);
                    oldDegree = gerDegree(new PointF(event.getX(0), event.getY(0)),
                            new PointF(event.getX(1), event.getY(1)));
                    mode = MULTI;
                    // Log.d(TAG, "mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                savedDegree = rotateDegree;
                mode = NONE;
                // Log.e("view.getWidth", view.getWidth() + "");
                // Log.e("view.getHeight", view.getHeight() + "");

                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == SINGLE && canTranslate) {
                    // ...

                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x
                            , event.getY() - start.y);
                } else if (mode == MULTI) {

                    newDist = (float) spacing(event);
                    midPoint(newMid, event);
                    newDegree = gerDegree(new PointF(event.getX(0), event.getY(0)),
                            new PointF(event.getX(1), event.getY(1)));

                    // Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;

                        if (canTranslate)
                            matrix.postTranslate(newMid.x - oldMid.x, newMid.y - oldMid.y);

                        if (canScale)
                            matrix.postScale(scale, scale, oldMid.x, oldMid.y);

                        if (canRotate)
                            matrix.postRotate(newDegree - oldDegree, newMid.x, newMid.y);
                    }
                }
                float[] values = new float[9];
                matrix.getValues(values);
                // 左上定点X坐标
                translateX = values[Matrix.MTRANS_X];
                // 左上定点Y坐标
                translateY = values[Matrix.MTRANS_Y];
                // 宽度缩放倍数
                scaleRate = values[Matrix.MSCALE_X];
                // 同Y轴间角度
                if (mode == MULTI) {
                    // 按下前的图片角度 + （手指松开的角度 - 手指按下的角度）
                    rotateDegree = savedDegree + newDegree - oldDegree;

                    rotateDegree %= 360;

                    if (rotateDegree < 0)
                        rotateDegree += 360;
                }

                if (imageChangeListener != null) {
                    imageChangeListener.onImageChange(translateX, translateY, scaleRate, rotateDegree);
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true; // indicate event was not handled
    }

    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
        sb.append("]");
        // Log.d(TAG, sb.toString());
    }

    // 计算2点间的距离
    private double spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Math.sqrt(x * x + y * y);
    }

    /**
     * 计算2点间的中心点
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * 计算2点之间连线去Y轴的角度
     *
     * @param p1
     * @param p2
     * @return
     */
    private float gerDegree(PointF p1, PointF p2) {
        float tran_x = p2.x - p1.x;
        float tran_y = p2.y - p1.y;
        float degree = 0f;
        float angle = (float) (Math.asin(tran_x
                / Math.sqrt(tran_x * tran_x + tran_y * tran_y)) * 180 / Math.PI);

        if (!Float.isNaN(angle)) {
            // 第一象限
            if (tran_x >= 0 && tran_y <= 0) {
                degree = angle;
            }
            // 第二象限
            else if (tran_x >= 0 && tran_y >= 0) {
                degree = 180 - angle;
            }
            // 第三象限
            else if (tran_x <= 0 && tran_y >= 0) {
                degree = 180 - angle;
            }
            // 第四象限
            else if (tran_x <= 0 && tran_y <= 0) {
                degree = 360 + angle;
            }
        }

        return degree;
    }

    public interface ImageChangeListener {
        public void onImageChange(float transX, float transY, float scale, float rotate);
    }

    ;

    private ImageChangeListener imageChangeListener;

    public void setOnImageChangeListener(ImageChangeListener imageChangeListener) {
        this.imageChangeListener = imageChangeListener;
    }
}
