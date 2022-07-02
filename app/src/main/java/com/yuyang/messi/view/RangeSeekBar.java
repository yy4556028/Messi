/*
 * Copyright (C) 2014 Roy Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yuyang.messi.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.yuyang.messi.R;

import java.util.List;

/**
 * A seekBar contains two cursor(left and right). Multiple touch supported.
 *
 * @author dolphinWang
 * @author yuyang
 * @version 2016-09-08
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class RangeSeekBar extends View {

    private static final int DEFAULT_DURATION = 100;

    private enum DIRECTION {
        LEFT, RIGHT
    }

    private DIRECTION lastTouch;

    private int mDuration;

    private boolean allowSame = false;//allow left and right same index

    /**
     * Scrollers for left and right cursor
     */
    private Scroller mLeftScroller;
    private Scroller mRightScroller;

    /**
     * Background drawables for left and right cursor. State list supported.
     */
    private Drawable mLeftCursorBG;
    private Drawable mRightCursorBG;

    /**
     * Represent states.
     */
    private int[] mPressedEnableState = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
    private int[] mUnPressedEnabledState = new int[]{-android.R.attr.state_pressed, android.R.attr.state_enabled};

    /**
     * Colors of text and seekbar in different states.
     */
    private int mTextColorNormal;
    private int mTextColorSelected;
    private int mSeekBarColorNormal;
    private int mSeekBarColorSelected;

    /**
     * Height of seekbar
     */
    private int mSeekBarHeight;

    /**
     * Size of text mark.
     */
    private int mTextSize;

    /**
     * Space between the text and the seekbar
     */
    private int mMarginBetween;

    /**
     * Length of every part. As we divide some parts according to marks.
     */
    private int mPartLength;

    /**
     * Contents of text mark.
     */
    private CharSequence[] mTextArray;

    /**
     *
     */
    private float[] mTextWidthArray;

    private Rect mPaddingRect;
    private Rect mLeftCursorRect;
    private Rect mRightCursorRect;

    private RectF mSeekBarRect;
    private RectF mSeekBarRectSelected;

    private float mLeftCursorIndex = 0;
    private float mRightCursorIndex = 1.0f;
    private int mLeftCursorNextIndex = 0;
    private int mRightCursorNextIndex = 1;

    private Paint mPaint;

    private int mLeftPointerLastX;
    private int mRightPointerLastX;

    private int mLeftPointerID = -1;
    private int mRightPointerID = -1;

    private boolean mLeftHited;
    private boolean mRightHited;

    private int mRightBoundary;

    private OnCursorChangeListener mListener;

    private Rect[] mClickRectArray;
    private int mClickIndex = -1;

    public RangeSeekBar(Context context) {
        this(context, null, 0);
    }

    public RangeSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RangeSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyConfig(context, attrs);

        if (mPaddingRect == null) {
            mPaddingRect = new Rect();
        }
        mPaddingRect.left = getPaddingLeft();
        mPaddingRect.top = getPaddingTop();
        mPaddingRect.right = getPaddingRight();
        mPaddingRect.bottom = getPaddingBottom();

        mLeftCursorRect = new Rect();
        mRightCursorRect = new Rect();

        mSeekBarRect = new RectF();
        mSeekBarRectSelected = new RectF();

        if (mTextArray != null) {
            mTextWidthArray = new float[mTextArray.length];
            mClickRectArray = new Rect[mTextArray.length];
        }

        mLeftScroller = new Scroller(context, new DecelerateInterpolator());
        mRightScroller = new Scroller(context, new DecelerateInterpolator());

        initPaint();
        initTextWidthArray();

        setWillNotDraw(false);
        setFocusable(true);
        setClickable(true);
    }

    private void applyConfig(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RangeSeekBar);

        mDuration = a.getInteger(R.styleable.RangeSeekBar_autoMoveDuration, DEFAULT_DURATION);

        mLeftCursorBG = a.getDrawable(R.styleable.RangeSeekBar_leftCursorBackground);
        mRightCursorBG = a.getDrawable(R.styleable.RangeSeekBar_rightCursorBackground);

        mTextColorNormal = a.getColor(R.styleable.RangeSeekBar_textColorNormal, Color.BLACK);
        mTextColorSelected = a.getColor(R.styleable.RangeSeekBar_textColorSelected, Color.rgb(242, 79, 115));

        mSeekBarColorNormal = a.getColor(R.styleable.RangeSeekBar_seekBarColorNormal, Color.rgb(218, 215, 215));
        mSeekBarColorSelected = a.getColor(R.styleable.RangeSeekBar_seekBarColorSelected, Color.rgb(242, 79, 115));

        mSeekBarHeight = (int) a.getDimension(R.styleable.RangeSeekBar_seekBarHeight, 10);
        mTextSize = (int) a.getDimension(R.styleable.RangeSeekBar_textSize, 15);
        mMarginBetween = (int) a.getDimension(R.styleable.RangeSeekBar_spaceBetween, 15);

        mTextArray = a.getTextArray(R.styleable.RangeSeekBar_markTextArray);
        if (mTextArray != null && mTextArray.length > 0) {
            mLeftCursorIndex = 0;
            mRightCursorIndex = mTextArray.length - 1;
            mRightCursorNextIndex = (int) mRightCursorIndex;
        }

        a.recycle();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.FILL);
        mPaint.setTextSize(mTextSize);
    }

    private void initTextWidthArray() {
        if (mTextArray != null && mTextArray.length > 0) {
            final int length = mTextArray.length;
            for (int i = 0; i < length; i++) {
                mTextWidthArray[i] = mPaint.measureText(mTextArray[i].toString());
            }
        }
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);

        if (mPaddingRect == null) {
            mPaddingRect = new Rect();
        }
        mPaddingRect.left = left;
        mPaddingRect.top = top;
        mPaddingRect.right = right;
        mPaddingRect.bottom = bottom;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        final int leftPointerH = mLeftCursorBG.getIntrinsicHeight();
        final int rightPointerH = mRightCursorBG.getIntrinsicHeight();

        // Get max height between left and right cursor.
        final int maxOfCursor = Math.max(leftPointerH, rightPointerH);
        // Then get max height between seekbar and cursor.
        final int maxOfCursorAndSeekbar = Math.max(mSeekBarHeight, maxOfCursor);
        // So we get the needed height.
        int heightNeeded = maxOfCursorAndSeekbar + mMarginBetween + mTextSize + mPaddingRect.top + mPaddingRect.bottom;

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightNeeded, MeasureSpec.EXACTLY);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        mSeekBarRect.left = mPaddingRect.left + mLeftCursorBG.getIntrinsicWidth() / 2;
        mSeekBarRect.right = widthSize - mPaddingRect.right - mRightCursorBG.getIntrinsicWidth() / 2;
        mSeekBarRect.top = mPaddingRect.top + mTextSize + mMarginBetween;
        mSeekBarRect.bottom = mSeekBarRect.top + mSeekBarHeight;

        mSeekBarRectSelected.top = mSeekBarRect.top;
        mSeekBarRectSelected.bottom = mSeekBarRect.bottom;

        mPartLength = ((int) (mSeekBarRect.right - mSeekBarRect.left)) / (mTextArray.length - 1);

        mRightBoundary = (int) (mSeekBarRect.right + mRightCursorBG.getIntrinsicWidth() / 2);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*** Draw text marks ***/
        final int length = mTextArray.length;
        mPaint.setTextSize(mTextSize);
        for (int i = 0; i < length; i++) {
            if ((i > mLeftCursorIndex && i < mRightCursorIndex) || (i == mLeftCursorIndex || i == mRightCursorIndex)) {
                mPaint.setColor(mTextColorSelected);
            } else {
                mPaint.setColor(mTextColorNormal);
            }

            final String text2draw = mTextArray[i].toString();
            final float textWidth = mTextWidthArray[i];

            float textDrawLeft;
            // The last text mark's draw location should be adjust.
            if (i == length - 1) {
                textDrawLeft = mSeekBarRect.right
                        + (mRightCursorBG.getIntrinsicWidth() / 2) - textWidth;
            } else {
                textDrawLeft = mSeekBarRect.left + i * mPartLength - textWidth
                        / 2;
            }

            canvas.drawText(text2draw, textDrawLeft, mPaddingRect.top + mTextSize, mPaint);

            Rect rect = mClickRectArray[i];
            if (rect == null) {
                rect = new Rect();
                rect.top = mPaddingRect.top;
                rect.bottom = rect.top + mTextSize + mMarginBetween
                        + mSeekBarHeight;
                rect.left = (int) textDrawLeft;
                rect.right = (int) (rect.left + textWidth);

                mClickRectArray[i] = rect;
            }
        }

        /*** Draw seekbar ***/
        final float radius = (float) mSeekBarHeight / 2;
        mSeekBarRectSelected.left = mSeekBarRect.left + mPartLength
                * mLeftCursorIndex;
        mSeekBarRectSelected.right = mSeekBarRect.left + mPartLength
                * mRightCursorIndex;
        // If whole of seekbar is selected, just draw seekbar with selected
        // color.
        if (mLeftCursorIndex == 0 && mRightCursorIndex == length - 1) {
            mPaint.setColor(mSeekBarColorSelected);
            canvas.drawRoundRect(mSeekBarRect, radius, radius, mPaint);
        } else {
            // Draw background first.
            mPaint.setColor(mSeekBarColorNormal);
            canvas.drawRoundRect(mSeekBarRect, radius, radius, mPaint);

            // Draw selected part.
            mPaint.setColor(mSeekBarColorSelected);
            // Can draw rounded rectangle, but original rectangle is enough.
            // Because edges of selected part will be covered by cursors.
            canvas.drawRect(mSeekBarRectSelected, mPaint);
        }

        /*** Draw cursors ***/
        if (lastTouch == DIRECTION.LEFT) {
            drawRightCursor(canvas);
            drawLeftCursor(canvas);
        } else {
            drawLeftCursor(canvas);
            drawRightCursor(canvas);
        }
    }

    private void drawLeftCursor(Canvas canvas) {
        final int leftWidth = mLeftCursorBG.getIntrinsicWidth();
        final int leftHieght = mLeftCursorBG.getIntrinsicHeight();
        final int leftLeft = (int) (mSeekBarRectSelected.left - (float) leftWidth / 2);
        final int leftTop = (int) ((mSeekBarRect.top + mSeekBarHeight / 2) - (leftHieght / 2));
        mLeftCursorRect.left = leftLeft;
        mLeftCursorRect.top = leftTop;
        mLeftCursorRect.right = leftLeft + leftWidth;
        mLeftCursorRect.bottom = leftTop + leftHieght;
        mLeftCursorBG.setBounds(mLeftCursorRect);
        mLeftCursorBG.draw(canvas);
    }

    private void drawRightCursor(Canvas canvas) {
        final int rightWidth = mRightCursorBG.getIntrinsicWidth();
        final int rightHeight = mRightCursorBG.getIntrinsicHeight();
        final int rightLeft = (int) (mSeekBarRectSelected.right - (float) rightWidth / 2);
        final int rightTop = (int) ((mSeekBarRectSelected.top + mSeekBarHeight / 2) - (rightHeight / 2));
        mRightCursorRect.left = rightLeft;
        mRightCursorRect.top = rightTop;
        mRightCursorRect.right = rightLeft + rightWidth;
        mRightCursorRect.bottom = rightTop + rightHeight;
        mRightCursorBG.setBounds(mRightCursorRect);
        mRightCursorBG.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        // For multiple touch
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                handleTouchDown(event);

                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                handleTouchDown(event);

                break;
            case MotionEvent.ACTION_MOVE:

                handleTouchMove(event);

                break;
            case MotionEvent.ACTION_POINTER_UP:

                handleTouchUp(event);

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                handleTouchUp(event);
                mClickIndex = -1;

                break;
        }

        return super.onTouchEvent(event);
    }

    private void handleTouchDown(MotionEvent event) {
        final int actionIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int downX = (int) event.getX(actionIndex);
        final int downY = (int) event.getY(actionIndex);

        // 如果重合 则最后移动的先移动
        if (mLeftCursorRect.contains(downX, downY) && (!mRightCursorRect.contains(downX, downY) || lastTouch == DIRECTION.LEFT)) {
            if (mLeftHited) {
                return;
            }

            // If hit, change state of drawable, and record id of touch pointer.
            mLeftPointerLastX = downX;
            mLeftCursorBG.setState(mPressedEnableState);
            mLeftPointerID = event.getPointerId(actionIndex);
            mLeftHited = true;
            lastTouch = DIRECTION.LEFT;

            invalidate();
        } else if (mRightCursorRect.contains(downX, downY)) {
            if (mRightHited) {
                return;
            }

            mRightPointerLastX = downX;
            mRightCursorBG.setState(mPressedEnableState);
            mRightPointerID = event.getPointerId(actionIndex);
            mRightHited = true;
            lastTouch = DIRECTION.RIGHT;

            invalidate();
        } else {
            // If touch x-y not be contained in cursor,
            // then we check if it in click areas
            final int clickBoundaryTop = mClickRectArray[0].top;
            final int clickBoundaryBottom = mClickRectArray[0].bottom;

            // Step one : if in boundary of total Y.
            if (downY < clickBoundaryTop || downY > clickBoundaryBottom) {
                mClickIndex = -1;
                return;
            }

            // Step two: find nearest mark in x-axis
            final int partIndex = (int) ((downX - mSeekBarRect.left) / mPartLength);
            final int partDelta = (int) ((downX - mSeekBarRect.left) % mPartLength);
            if (partDelta < mPartLength / 2) {
                mClickIndex = partIndex;
            } else if (partDelta > mPartLength / 2) {
                mClickIndex = partIndex + 1;
            }

            if (mClickIndex == mLeftCursorIndex
                    || mClickIndex == mRightCursorIndex) {
                mClickIndex = -1;
                return;
            }

            // Step three: check contain
            if (!mClickRectArray[mClickIndex].contains(downX, downY)) {
                mClickIndex = -1;
            }
        }
    }

    private void handleTouchUp(MotionEvent event) {
        final int actionIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int actionID = event.getPointerId(actionIndex);

        if (actionID == mLeftPointerID) {
            if (!mLeftHited) {
                return;
            }

            // If cursor between in tow mark locations, it should be located on
            // the lower or higher one.

            // step 1:Calculate the offset with lower mark.
            final int lower = (int) Math.floor(mLeftCursorIndex);
            final int higher = (int) Math.ceil(mLeftCursorIndex);

            final float offset = mLeftCursorIndex - lower;
            if (offset != 0) {

                // step 2:Decide which mark will go to.
                if (offset < 0.5f) {
                    // If left cursor want to be located on lower mark, go ahead
                    // guys.
                    // Because right cursor will never appear lower than the
                    // left one.
                    mLeftCursorNextIndex = lower;
                } else if (offset > 0.5f) {
                    mLeftCursorNextIndex = higher;
                    // If left cursor want to be located on higher mark,
                    // situation becomes a little complicated.
                    // We should check that whether distance between left and
                    // right cursor is less than 1, and next index of left
                    // cursor is difference with current
                    // of right one.
                    if (allowSame && Math.abs(mLeftCursorIndex - mRightCursorIndex) <= 1 && mLeftCursorNextIndex == mRightCursorNextIndex) {
                        // Left can not go to the higher, just to the lower one.
                        mLeftCursorNextIndex = lower;
                    }
                }

                // step 3: Move to.
                if (!mLeftScroller.computeScrollOffset()) {
                    final int fromX = (int) (mLeftCursorIndex * mPartLength);

                    mLeftScroller.startScroll(fromX, 0, mLeftCursorNextIndex * mPartLength - fromX, 0, mDuration);

                    triggleCallback(true, mLeftCursorNextIndex);
                }
            }

            // Reset values of parameters
            mLeftPointerLastX = 0;
            mLeftCursorBG.setState(mUnPressedEnabledState);
            mLeftPointerID = -1;
            mLeftHited = false;

            invalidate();
        } else if (actionID == mRightPointerID) {
            if (!mRightHited) {
                return;
            }

            final int lower = (int) Math.floor(mRightCursorIndex);
            final int higher = (int) Math.ceil(mRightCursorIndex);

            final float offset = mRightCursorIndex - lower;
            if (offset != 0) {

                if (offset > 0.5f) {
                    mRightCursorNextIndex = higher;
                } else if (offset < 0.5f) {
                    mRightCursorNextIndex = lower;
                    if (allowSame & Math.abs(mLeftCursorIndex - mRightCursorIndex) <= 1
                            && mRightCursorNextIndex == mLeftCursorNextIndex) {
                        mRightCursorNextIndex = higher;
                    }
                }

                if (!mRightScroller.computeScrollOffset()) {
                    final int fromX = (int) (mRightCursorIndex * mPartLength);

                    mRightScroller.startScroll(fromX, 0, mRightCursorNextIndex * mPartLength - fromX, 0, mDuration);

                    triggleCallback(false, mRightCursorNextIndex);
                }
            }

            mRightPointerLastX = 0;
            mLeftCursorBG.setState(mUnPressedEnabledState);
            mRightPointerID = -1;
            mRightHited = false;

            invalidate();
        } else {
            final int pointerIndex = event.findPointerIndex(actionID);
            final int upX = (int) event.getX(pointerIndex);
            final int upY = (int) event.getY(pointerIndex);

            if (mClickIndex != -1 && mClickRectArray[mClickIndex].contains(upX, upY)) {
                // Find nearest cursor
                final float distance2LeftCursor = Math.abs(mLeftCursorIndex - mClickIndex);
                final float distance2Right = Math.abs(mRightCursorIndex - mClickIndex);

                final boolean moveLeft;

                if (distance2LeftCursor == distance2Right) {
                    moveLeft = mLeftCursorIndex != mRightCursorIndex || mClickIndex < mLeftCursorIndex;
                } else {
                    moveLeft = distance2LeftCursor < distance2Right;
                }
                int fromX;

                if (moveLeft) {
                    if (!mLeftScroller.computeScrollOffset() && mClickIndex < mRightCursorIndex) {
                        mLeftCursorNextIndex = mClickIndex;
                        fromX = (int) (mLeftCursorIndex * mPartLength);
                        mLeftScroller.startScroll(fromX, 0, mLeftCursorNextIndex * mPartLength - fromX, 0, mDuration);

                        triggleCallback(true, mLeftCursorNextIndex);

                        invalidate();
                    }
                } else {
                    if (!mRightScroller.computeScrollOffset() && mClickIndex > mLeftCursorIndex) {
                        mRightCursorNextIndex = mClickIndex;
                        fromX = (int) (mRightCursorIndex * mPartLength);
                        mRightScroller.startScroll(fromX, 0, mRightCursorNextIndex * mPartLength - fromX, 0, mDuration);

                        triggleCallback(false, mRightCursorNextIndex);

                        invalidate();
                    }
                }
            }
        }
    }

    private void handleTouchMove(MotionEvent event) {
        if (mClickIndex != -1) {
            final int actionIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
            final int x = (int) event.getX(actionIndex);
            final int y = (int) event.getY(actionIndex);

            if (!mClickRectArray[mClickIndex].contains(x, y)) {
                mClickIndex = -1;
            }
        }

        if (mLeftHited && mLeftPointerID != -1) {

            final int index = event.findPointerIndex(mLeftPointerID);
            final float x = event.getX(index);

            float deltaX = x - mLeftPointerLastX;
            mLeftPointerLastX = (int) x;

            DIRECTION direction = (deltaX < 0 ? DIRECTION.LEFT : DIRECTION.RIGHT);

            if (direction == DIRECTION.LEFT && mLeftCursorIndex == 0) {
                return;
            }

            // Check whether cursor will move out of boundary
            if (mLeftCursorRect.left + deltaX < mPaddingRect.left) {
                mLeftCursorIndex = 0;
                invalidate();
                return;
            }

            if (allowSame) {
                // Check whether left and right cursor will collision.
                if (mLeftCursorRect.right + deltaX >= mRightCursorRect.left) {
                    // Check whether right cursor is in "Touch" mode( if in touch
                    // mode, represent that we can not move it at all), or right
                    // cursor reach the boundary.
                    if (mRightHited || mRightCursorIndex == mTextArray.length - 1 || mRightScroller.computeScrollOffset()) {
                        // Just move left cursor to the left side of right one.
                        deltaX = mRightCursorRect.left - mLeftCursorRect.right;
                    } else {
                        // Move right cursor to higher location.
                        final int maxMarkIndex = mTextArray.length - 1;

                        if (mRightCursorIndex <= maxMarkIndex - 1) {
                            mRightCursorNextIndex = (int) (mRightCursorIndex + 1);

                            if (!mRightScroller.computeScrollOffset()) {
                                final int fromX = (int) (mRightCursorIndex * mPartLength);

                                mRightScroller.startScroll(fromX, 0, mRightCursorNextIndex * mPartLength - fromX, 0, mDuration);
                                triggleCallback(false, mRightCursorNextIndex);
                            }
                        }
                    }
                }
            } else {
                // Check whether left and right cursor will collision.
                if ((mLeftCursorRect.left + mLeftCursorRect.right) / 2 + deltaX >= (mRightCursorRect.left + mRightCursorRect.right) / 2) {
                    // Just move left cursor to the left side of right one.
                    deltaX = 0;
                }
            }

            // After some calculate, if deltaX is still be zero, do quick
            // return.
            if (deltaX == 0) {
                return;
            }

            // Calculate the movement.
            final float moveX = deltaX / mPartLength;
            mLeftCursorIndex += moveX;

            invalidate();
        }

        if (mRightHited && mRightPointerID != -1) {

            final int index = event.findPointerIndex(mRightPointerID);
            final float x = event.getX(index);

            float deltaX = x - mRightPointerLastX;
            mRightPointerLastX = (int) x;

            DIRECTION direction = (deltaX < 0 ? DIRECTION.LEFT
                    : DIRECTION.RIGHT);

            final int maxIndex = mTextArray.length - 1;
            if (direction == DIRECTION.RIGHT && mRightCursorIndex == maxIndex) {
                return;
            }

            if (mRightCursorRect.right + deltaX > mRightBoundary) {
                deltaX = mRightBoundary - mRightCursorRect.right;
            }

            final int maxMarkIndex = mTextArray.length - 1;
            if (direction == DIRECTION.RIGHT && mRightCursorIndex == maxMarkIndex) {
                return;
            }

            if (allowSame) {
                if (mRightCursorRect.left + deltaX < mLeftCursorRect.right) {
                    if (mLeftHited || mLeftCursorIndex == 0 || mLeftScroller.computeScrollOffset()) {
                        deltaX = mLeftCursorRect.right - mRightCursorRect.left;
                    } else {
                        if (mLeftCursorIndex >= 1) {
                            mLeftCursorNextIndex = (int) (mLeftCursorIndex - 1);

                            if (!mLeftScroller.computeScrollOffset()) {
                                final int fromX = (int) (mLeftCursorIndex * mPartLength);
                                mLeftScroller.startScroll(fromX, 0, mLeftCursorNextIndex * mPartLength - fromX, 0, mDuration);
                                triggleCallback(true, mLeftCursorNextIndex);
                            }
                        }
                    }
                }
            } else {
                if ((mRightCursorRect.left + mRightCursorRect.right) / 2 + deltaX < (mLeftCursorRect.left + mLeftCursorRect.right) / 2) {
                    deltaX = 0;
                }
            }

            if (deltaX == 0) {
                return;
            }

            final float moveX = deltaX / mPartLength;
            mRightCursorIndex += moveX;

            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        if (mLeftScroller.computeScrollOffset()) {
            final int deltaX = mLeftScroller.getCurrX();

            mLeftCursorIndex = (float) deltaX / mPartLength;

            invalidate();
        }

        if (mRightScroller.computeScrollOffset()) {
            final int deltaX = mRightScroller.getCurrX();

            mRightCursorIndex = (float) deltaX / mPartLength;

            invalidate();
        }

        if (mLeftCursorIndex == mRightCursorIndex) {
            if (mLeftCursorIndex == 0) {
                lastTouch = DIRECTION.RIGHT;
            } else if (mLeftCursorIndex == mTextArray.length - 1) {
                lastTouch = DIRECTION.LEFT;
            }
        }
    }

    private void triggleCallback(boolean isLeft, int location) {
        if (mListener == null) {
            return;
        }

        if (isLeft) {
            mListener.onLeftCursorChanged(location,
                    mTextArray[location].toString());
        } else {
            mListener.onRightCursorChanged(location,
                    mTextArray[location].toString());
        }
    }

    public void setLeftSelection(int partIndex) {
        if (partIndex >= mTextArray.length - 1 || partIndex <= 0) {
            throw new IllegalArgumentException(
                    "Index should from 0 to size of text array minus 2!");
        }

        if (partIndex != mLeftCursorIndex) {
            if (!mLeftScroller.isFinished()) {
                mLeftScroller.abortAnimation();
            }
            mLeftCursorNextIndex = partIndex;
            final int leftFromX = (int) (mLeftCursorIndex * mPartLength);
            mLeftScroller.startScroll(leftFromX, 0, mLeftCursorNextIndex * mPartLength - leftFromX, 0, mDuration);
            triggleCallback(true, mLeftCursorNextIndex);

            if (mRightCursorIndex <= mLeftCursorNextIndex) {
                if (!mRightScroller.isFinished()) {
                    mRightScroller.abortAnimation();
                }
                mRightCursorNextIndex = mLeftCursorNextIndex + 1;
                final int rightFromX = (int) (mRightCursorIndex * mPartLength);
                mRightScroller.startScroll(rightFromX, 0, mRightCursorNextIndex * mPartLength - rightFromX, 0, mDuration);
                triggleCallback(false, mRightCursorNextIndex);
            }

            invalidate();
        }
    }

    public void setRightSelection(int partIndex) {
        if (partIndex >= mTextArray.length || partIndex <= 0) {
            throw new IllegalArgumentException(
                    "Index should from 1 to size of text array minus 1!");
        }

        if (partIndex != mRightCursorIndex) {
            if (!mRightScroller.isFinished()) {
                mRightScroller.abortAnimation();
            }

            mRightCursorNextIndex = partIndex;
            final int rightFromX = (int) (mPartLength * mRightCursorIndex);
            mRightScroller.startScroll(rightFromX, 0, mRightCursorNextIndex * mPartLength - rightFromX, 0, mDuration);
            triggleCallback(false, mRightCursorNextIndex);

            if (mLeftCursorIndex >= mRightCursorNextIndex) {
                if (!mLeftScroller.isFinished()) {
                    mLeftScroller.abortAnimation();
                }

                mLeftCursorNextIndex = mRightCursorNextIndex - 1;
                final int leftFromX = (int) (mLeftCursorIndex * mPartLength);
                mLeftScroller.startScroll(leftFromX, 0, mLeftCursorNextIndex * mPartLength - leftFromX, 0, mDuration);
                triggleCallback(true, mLeftCursorNextIndex);
            }
            invalidate();
        }
    }

    public void setLeftCursorBackground(Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException(
                    "Do you want to make left cursor invisible?");
        }

        mLeftCursorBG = drawable;

        requestLayout();
        invalidate();
    }

    public void setLeftCursorBackground(int resID) {
        if (resID < 0) {
            throw new IllegalArgumentException(
                    "Do you want to make left cursor invisible?");
        }

        mLeftCursorBG = getResources().getDrawable(resID);

        requestLayout();
        invalidate();
    }

    public void setRightCursorBackground(Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException(
                    "Do you want to make right cursor invisible?");
        }

        mRightCursorBG = drawable;

        requestLayout();
        invalidate();
    }

    public void setRightCursorBackground(int resID) {
        if (resID < 0) {
            throw new IllegalArgumentException(
                    "Do you want to make right cursor invisible?");
        }

        mRightCursorBG = getResources().getDrawable(resID);

        requestLayout();
        invalidate();
    }

    public void setTextMarkColorNormal(int color) {
        if (color == Color.TRANSPARENT) {
            throw new IllegalArgumentException(
                    "Do you want to make text mark invisible?");
        }

        mTextColorNormal = color;

        invalidate();
    }

    public void setTextMarkColorSelected(int color) {
        if (color == Color.TRANSPARENT) {
            throw new IllegalArgumentException(
                    "Do you want to make text mark invisible?");
        }

        mTextColorSelected = color;

        invalidate();
    }

    public void setSeekbarColorNormal(int color) {
        if (color == Color.TRANSPARENT) {
            throw new IllegalArgumentException(
                    "Do you want to make seekbar invisible?");
        }

        mSeekBarColorNormal = color;

        invalidate();
    }

    public void setSeekbarColorSelected(int color) {
        if (color <= 0 || color == Color.TRANSPARENT) {
            throw new IllegalArgumentException(
                    "Do you want to make seekbar invisible?");
        }

        mSeekBarColorSelected = color;

        invalidate();
    }

    /**
     * In pixels. Users should call this method before view is added to parent.
     *
     * @param height
     */
    public void setSeekbarHeight(int height) {
        if (height <= 0) {
            throw new IllegalArgumentException(
                    "Height of seekbar can not less than 0!");
        }

        mSeekBarHeight = height;
    }

    /**
     * To set space between text mark and seekBar.
     *
     * @param space
     */
    public void setSpaceBetween(int space) {
        if (space < 0) {
            throw new IllegalArgumentException(
                    "Space between text mark and seekbar can not less than 0!");
        }

        mMarginBetween = space;

        requestLayout();
        invalidate();
    }

    /**
     * This method should be called after {@link #setTextMarkSize(int)}, because
     * view will measure size of text mark by paint.
     *
     * @param marks
     */
    public void setTextMarks(CharSequence... marks) {
        if (marks == null || marks.length == 0) {
            throw new IllegalArgumentException(
                    "Text array is null, how can i do...");
        }

        mTextArray = marks;
        mLeftCursorIndex = 0;
        mRightCursorIndex = mTextArray.length - 1;
        mRightCursorNextIndex = (int) mRightCursorIndex;
        mTextWidthArray = new float[marks.length];
        mClickRectArray = new Rect[mTextArray.length];
        initTextWidthArray();

        requestLayout();
        invalidate();
    }

    public void setTextMarks(List<CharSequence> marks) {
        if (marks == null || marks.size() == 0) {
            throw new IllegalArgumentException(
                    "Text array is null, how can i do...");
        }

        mTextArray = marks.toArray(new CharSequence[marks.size()]);
        mLeftCursorIndex = 0;
        mRightCursorIndex = mTextArray.length - 1;
        mRightCursorNextIndex = (int) mRightCursorIndex;
        mTextWidthArray = new float[marks.size()];
        mClickRectArray = new Rect[mTextArray.length];
        initTextWidthArray();

        requestLayout();
        invalidate();
    }

    /**
     * Users should call this method before view is added to parent.
     *
     * @param size in pixels
     */
    public void setTextMarkSize(int size) {
        if (size < 0) {
            return;
        }

        mTextSize = size;
        mPaint.setTextSize(size);
    }

    public void setAllowSame(boolean allowSame) {
        this.allowSame = allowSame;
    }

    public int getLeftCursorIndex() {
        return (int) mLeftCursorIndex;
    }

    public int getRightCursorIndex() {
        return (int) mRightCursorIndex;
    }

    public void setOnCursorChangeListener(OnCursorChangeListener l) {
        mListener = l;
    }

    public interface OnCursorChangeListener {
        void onLeftCursorChanged(int location, String textMark);

        void onRightCursorChanged(int location, String textMark);
    }
}
