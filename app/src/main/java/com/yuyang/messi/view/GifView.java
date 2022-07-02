package com.yuyang.messi.view;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.UNSPECIFIED;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;
import com.yuyang.messi.threadPool.ThreadPool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class GifView extends View {

    private Movie mMovie;

    private int mMovieResourceId;

    private long mMovieStart;

    private int mCurrentAnimationTime = 0;

    private float mLeft;

    private float mTop;

    private float xScale, yScale;

    private int mMeasuredMovieWidth;

    private int mMeasuredMovieHeight;

    /**
     * 用于控制gif 播放的速度
     */
    private float speed = 2f;

    private boolean mVisible = true;

    private volatile boolean mPaused = false;

    public GifView(Context context) {
        this(context, null);
    }

    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs, R.styleable.CustomTheme_gifViewStyle);
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setViewAttributes(context, attrs, defStyleAttr);
    }

    private void setViewAttributes(Context context, AttributeSet attrs, int defStyle) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // 从描述文件中读出gif的值，创建出Movie实例
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GifView, defStyle, android.R.style.Widget);
        mMovieResourceId = array.getResourceId(R.styleable.GifView_gif, -1);
        mPaused = array.getBoolean(R.styleable.GifView_paused, false);
        array.recycle();
        if (mMovieResourceId != -1) {
            mMovie = Movie.decodeStream(getResources().openRawResource(mMovieResourceId));
        }
    }

    public void setMovieFile(String path) {
        setMovieFile(path, null);
    }

    public void setMovieFile(final String path, final OnPrepareListener onPrepareListener) {
        setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeFile(path)));
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                mMovie = Movie.decodeFile(path);
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestLayout();
                        if (onPrepareListener != null) {
                            onPrepareListener.onPrepare();
                        }
                    }
                });
            }
        });
    }

    public void setMovieUri(final Uri uri, final OnPrepareListener onPrepareListener) {
        setBackground(new ColorDrawable(0xff000000));

        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                try {
                    is = MessiApp.getInstance().getContentResolver().openInputStream(uri);
                    mMovie = Movie.decodeStream(is);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException ignored) {
                    }
                }
                if (is == null) return;

                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestLayout();
                        if (onPrepareListener != null) {
                            onPrepareListener.onPrepare();
                        }
                    }
                });
            }
        });
    }

    public interface OnPrepareListener {
        void onPrepare();
    }

    public void setBytes(byte[] bytes) {
        mMovie = Movie.decodeByteArray(bytes, 0, bytes.length);
        requestLayout();
    }

    /**
     * 设置gif图资源
     *
     * @param movieResId
     */
    public void setMovieResource(int movieResId) {
        this.mMovieResourceId = movieResId;
        try {
            mMovie = Movie.decodeStream(getResources().openRawResource(mMovieResourceId));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        requestLayout();
    }

    public void setMovie(Movie movie) {
        this.mMovie = movie;
        requestLayout();
    }

    public Movie getMovie() {
        return mMovie;
    }

    public void setMovieTime(int time) {
        mCurrentAnimationTime = time;
        invalidate();
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * 设置暂停
     *
     * @param paused
     */
    public void setPaused(boolean paused) {
        this.mPaused = paused;
        if (!paused) {
            mMovieStart = (long) (SystemClock.uptimeMillis() - mCurrentAnimationTime / speed);
        }
        invalidate();
    }

    /**
     * 判断gif图是否停止了
     *
     * @return
     */
    public boolean isPaused() {
        return this.mPaused;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMovie != null) {

            int movieWidth = mMovie.width();
            int movieHeight = mMovie.height();
            int maximumWidth = MeasureSpec.getSize(widthMeasureSpec);
            int maximumHeight = MeasureSpec.getSize(heightMeasureSpec);
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            xScale = (float) maximumWidth / movieWidth;
            yScale = (float) maximumHeight / movieHeight;

            switch (widthMode) {
                case EXACTLY: {//确定值
                    switch (heightMode) {

                        case EXACTLY: {//确定值
                            mMeasuredMovieWidth = maximumWidth;
                            mMeasuredMovieHeight = maximumHeight;
                            break;
                        }
                        case AT_MOST: {//最多不超过
                            if (maximumHeight < (int) (movieHeight * xScale)) {
                                mMeasuredMovieWidth = maximumWidth;
                                mMeasuredMovieHeight = maximumHeight;
                            } else {
                                mMeasuredMovieWidth = maximumWidth;
                                mMeasuredMovieHeight = (int) (movieHeight * xScale);
                            }
                            break;
                        }
                        case UNSPECIFIED: {//无限制
                            mMeasuredMovieWidth = maximumWidth;
                            mMeasuredMovieHeight = (int) (movieHeight * xScale);
                            break;
                        }
                    }
                    break;
                }
                case AT_MOST: {//最多不超过
                    switch (heightMode) {

                        case EXACTLY: {//确定值

                            if (maximumWidth < (int) (movieWidth * yScale)) {
                                mMeasuredMovieWidth = maximumWidth;
                                mMeasuredMovieHeight = maximumHeight;
                            } else {
                                mMeasuredMovieWidth = (int) (movieWidth * yScale);
                                mMeasuredMovieHeight = maximumHeight;
                            }
                            break;
                        }
                        case AT_MOST: {//最多不超过
                            mMeasuredMovieWidth = movieWidth;
                            mMeasuredMovieHeight = movieHeight;
                            break;
                        }
                        case UNSPECIFIED: {//无限制
                            mMeasuredMovieWidth = movieWidth;
                            mMeasuredMovieHeight = movieHeight;
                            break;
                        }
                    }
                    break;
                }
                case UNSPECIFIED: {//无限制
                    switch (heightMode) {

                        case EXACTLY: {//确定值
                            mMeasuredMovieWidth = (int) (movieWidth * yScale);
                            mMeasuredMovieHeight = maximumHeight;
                            break;
                        }
                        case AT_MOST: {//最多不超过
                            mMeasuredMovieWidth = movieWidth;
                            mMeasuredMovieHeight = movieHeight;
                            break;
                        }
                        case UNSPECIFIED: {
                            mMeasuredMovieWidth = movieWidth;
                            mMeasuredMovieHeight = movieHeight;
                            break;
                        }
                    }
                    break;
                }
            }
            xScale = (float) mMeasuredMovieWidth / movieWidth;
            yScale = (float) mMeasuredMovieHeight / movieHeight;
            setMeasuredDimension(mMeasuredMovieWidth, mMeasuredMovieHeight);
        } else {
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mLeft = (getWidth() - mMeasuredMovieWidth) / 2f;
        mTop = (getHeight() - mMeasuredMovieHeight) / 2f;
        mVisible = getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie != null) {
            if (!mPaused) {
                updateAnimationTime();
                drawMovieFrame(canvas);
                invalidateView();
            } else {
                drawMovieFrame(canvas);
            }
        }
    }

    private void invalidateView() {
        if (mVisible && mMovie != null && mMovie.duration() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                postInvalidateOnAnimation();
            } else {
                invalidate();
            }
        }
    }

    private void updateAnimationTime() {
        long now = SystemClock.uptimeMillis();
        // 如果第一帧，记录起始时间
        if (mMovieStart == 0) {
            mMovieStart = now;
        }
        // 取出动画的时长
        int dur = mMovie.duration();
        if (dur == 0) {
            mCurrentAnimationTime = 0;
        } else {
            // 算出需要显示第几帧
            mCurrentAnimationTime = (int) ((now - mMovieStart) * speed % dur);
        }

    }

    private synchronized void drawMovieFrame(Canvas canvas) {
        // 设置要显示的帧，绘制即可
        mMovie.setTime(mCurrentAnimationTime);
//        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.save();
        canvas.scale(xScale, yScale);
        mMovie.draw(canvas, mLeft / xScale, mTop / yScale);
        canvas.restore();
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        mVisible = screenState == SCREEN_STATE_ON;
        invalidateView();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }
}
