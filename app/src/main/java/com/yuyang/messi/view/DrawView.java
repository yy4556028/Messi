package com.yuyang.messi.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.yuyang.messi.net.socket.SocketThreadManager;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-02
 * 创建时间: 16:14
 * DrawView: 涂鸦view
 *
 * @author yuyang
 * @version 1.0
 */
public class DrawView extends View {

    private float mX, mY;
    private Path mPath;             // 涂鸦的路径
    private Paint mPaint;           // 涂鸦的画笔
    private static final float TOUCH_TOLERANCE = 0; // 两点间距大于该距离才绘制
    private Bitmap originalBitmap;  // 原始 bitmap 用于清屏时重置
    private Bitmap mBitmap;         // 用于绘制的bitmap
    private Canvas mCanvas;         // 绘制bitmap的canvas
    private Paint mBitmapPaint;   // 绘制bitmap的画笔

    // 每条涂鸦的坐标点队列
    private CopyOnWriteArrayList<PointF> pointsList;


    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();//创建画笔渲染对象
        mPaint.setAntiAlias(true);//设置抗锯齿，让绘画比较平滑
        mPaint.setDither(true);//设置递色
        mPaint.setStyle(Paint.Style.STROKE);//画笔的类型有三种（1.FILL 2.FILL_AND_STROKE 3.STROKE ）
        mPaint.setStrokeJoin(Paint.Join.ROUND);//默认类型是MITER（1.BEVEL 2.MITER 3.ROUND ）
        mPaint.setStrokeCap(Paint.Cap.ROUND);//默认类型是BUTT（1.BUTT 2.ROUND 3.SQUARE ）

        pointsList = new CopyOnWriteArrayList<>();
    }

    /**
     * 设置画笔的颜色
     *
     * @param colorId
     */
    public void setPaintColor(int colorId) {
        mPaint.setColor(colorId);
    }

    /**
     * 设置描边的宽度，如果设置的值为0那么边是一条极细的线
     *
     * @param width
     */
    public void setPaintLineWidth(int width) {
        mPaint.setStrokeWidth(width);
    }

    /**
     * 设置要涂鸦的原图
     *
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        originalBitmap = bitmap;
        ViewGroup.LayoutParams params =  getLayoutParams();
        params.width = bitmap.getWidth();
        params.height = bitmap.getHeight();
        setLayoutParams(params);

        mBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        mCanvas = new Canvas();
        mPath = new Path();
        //设置cacheCanvas将会绘制到内存中的cacheBitmap上
        mCanvas.setBitmap(mBitmap);
        //设置画笔的颜色
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    public Bitmap getDrawBitmap() {
        return mBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }

    private void onTouchDown(float x, float y) {
        mPath.reset();//将上次的路径保存起来，并重置新的路径。
        mPath.moveTo(x, y);//设置新的路径“轮廓”的开始
        mX = x;
        mY = y;

        pointsList.clear();
        startDraw(hostId, mX + "", mY + "");
    }

    private void onTouchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            pointsList.add(new PointF(mX, mY));
            doingList();
        }
    }

    private void onTouchUp(float x, float y) {
        mPath.lineTo(mX, mY);//从最后一个指定的xy点绘制一条线，如果没有用moveTo方法，那么起始点表示（0，0）点。
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);//手指离开屏幕后，绘制创建的“所有”路径。
        // kill this so we don't double draw
        mPath.reset();

        endList(mX, mY);

//        mPaint.setColor(getResources().getColor(R.color.blue));
//        mPaint.setStrokeWidth(4);
//        PointF point;
//
//        while(queue.size() > 0) {
//            point = queue.poll();
//            mCanvas.drawPoint(point.x, point.y, mPaint);
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://手指开始按压屏幕，这个动作包含了初始化位置
                onTouchDown(x, y);
                invalidate();//刷新画布，重新运行onDraw（）方法
                break;
            case MotionEvent.ACTION_MOVE://手指按压屏幕时，位置的改变触发，这个方法在ACTION_DOWN和ACTION_UP之间。
                onTouchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP://手指离开屏幕，不再按压屏幕
                onTouchUp(x, y);
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    // 清空涂鸦
    public void clear() {
        mBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        mCanvas.setBitmap(mBitmap);
        mPath.reset();
        invalidate();
    }

    // 南瑞大屏涂鸦命令测试分隔线
    /**
     * **************************************************************
     */

    // 投放画面的 host ID
    String hostId = "-1493554410";

    // 4个点一组 发送给后台
    PointF p1, p2, p3, p4;

    // 依List的数据 判断 是否发送
    private void doingList() {
        // 如果 list 中已有4个点坐标 则发送 4个点 并清空 list
        if (pointsList.size() == 4) {
            p1 = pointsList.get(0);
            p2 = pointsList.get(1);
            p3 = pointsList.get(2);
            p4 = pointsList.get(3);

            doingDraw(hostId, p1.x + "", p1.y + "", p2.x + "", p2.y + "", p3.x + "", p3.y + "", p4.x + "", p4.y + "");
            pointsList.clear();
        }
    }

    // 结束绘制
    private void endList(float x, float y) {

        // 如果 list 中还有未发送的点坐标
        if (pointsList != null && pointsList.size() > 0) {
            // p1 点赋值
            p1 = pointsList.get(0);

            // 如果有多于 1 个点坐标 p2 赋值
            if (pointsList.size() > 1) {
                p2 = pointsList.get(1);
                // 否则 p2 赋值为 p1
            } else {
                p2 = p1;
            }

            // 如果有多于 2 个点坐标 p3 赋值
            if (pointsList.size() > 2) {
                p3 = pointsList.get(2);
                // 否则 p3 赋值为 p2
            } else {
                p3 = p2;
            }

            // p4 赋值为 p3
            p4 = p3;

            // 发送绘制命令
            doingDraw(hostId, p1.x + "", p1.y + "", p2.x + "", p2.y + "", p3.x + "", p3.y + "", p4.x + "", p4.y + "");
            // 清空
            pointsList.clear();
        }

        // 发送结束绘制命令
        endDraw(hostId, x + "", y + "");
    }

    // 通知服务器开始绘制的点坐标
    private void startDraw(String hostId, String X, String Y) {
        String tcpStr = "{\"messageData\":\"{\\\"attachments\\\":[],\\\"hostId\\\":" + hostId + ",\\\"hosts\\\":[],\\\"message\\\":\\\"<Message><Command Name='DeviceStartDraw' Description='doingpaint'><Params><Param Name='HostId' Value='" + hostId + "' /><Param Name='Points' Value='" + X + "," + Y + " " + X + "," + Y + " " + X + "," + Y + " " + X + "," + Y + "' /></Params></Command></Message>\\\"}\",\"messageType\":8}\n\r";
        SocketThreadManager.sharedInstance().sendMsg(tcpStr, null);
    }

    // 通知服务器绘制中的点坐标 4个一组
    private void doingDraw(String hostId, String X1, String Y1, String X2, String Y2, String X3, String Y3, String X4, String Y4) {
        String tcpStr = "{\"messageData\":\"{\\\"attachments\\\":[],\\\"hostId\\\":" + hostId + ",\\\"hosts\\\":[],\\\"message\\\":\\\"<Message><Command Name='DeviceDoingDraw' Description='doingpaint'><Params><Param Name='HostId' Value='" + hostId + "' /><Param Name='Points' Value='" + X1 + "," + Y1 + " " + X2 + "," + Y2 + " " + X3 + "," + Y3 + " " + X4 + "," + Y4 + "' /></Params></Command></Message>\\\"}\",\"messageType\":8}\n\r";
        SocketThreadManager.sharedInstance().sendMsg(tcpStr, null);
    }

    // 发送结束绘制命令
    private void endDraw(String hostId, String X, String Y) {
        String tcpStr = "{\"messageData\":\"{\\\"attachments\\\":[],\\\"hostId\\\":" + hostId + ",\\\"hosts\\\":[],\\\"message\\\":\\\"<Message><Command Name='DeviceEndDraw' Description='doingpaint'><Params><Param Name='HostId' Value='" + hostId + "' /><Param Name='Points' Value='" + X + "," + Y + "' /></Params></Command></Message>\\\"}\",\"messageType\":8}\n\r";
        SocketThreadManager.sharedInstance().sendMsg(tcpStr, null);
    }
}