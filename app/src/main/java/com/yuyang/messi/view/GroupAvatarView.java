package com.yuyang.messi.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;

import com.yuyang.lib_base.utils.PixelUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupAvatarView extends View {

    private int gap = PixelUtils.dp2px(4);
    private int maxLineNum = 7;// >= 2
    private int arrangeGravity = Gravity.CENTER;

    private int itemWidth;
    private int itemHeight;

    private Rect srcRect = new Rect();
    private Rect dstRect = new Rect();

    private List<Bitmap> bitmapList = new ArrayList<>();

    public GroupAvatarView(Context context) {
        super(context);
    }

    public GroupAvatarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GroupAvatarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getGap() {
        return gap;
    }

    public int getMaxLineNum() {
        return maxLineNum;
    }

    public void setMaxLineNum(int maxLineNum) {
        if (maxLineNum < 2) return;
        this.maxLineNum = maxLineNum;
    }

    public int getArrangeGravity() {
        return arrangeGravity;
    }

    public void setArrangeGravity(int arrangeGravity) {
        this.arrangeGravity = arrangeGravity;
        calcItemSize();
        invalidate();
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public List<Bitmap> getBitmapList() {
        return bitmapList;
    }

    public void setBitmapList(List<Bitmap> bitmapList) {
        this.bitmapList.clear();
        if (bitmapList != null) {
            this.bitmapList.addAll(bitmapList);
        }
        this.bitmapList = this.bitmapList.subList(0, Math.min(this.bitmapList.size(), maxLineNum * maxLineNum));
        calcItemSize();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calcItemSize();
    }

    private void calcItemSize() {
        int contentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int contentHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        if (arrangeGravity == Gravity.START) {
            itemWidth = (contentWidth - gap * (maxLineNum - 1)) / maxLineNum;
            itemHeight = (contentHeight - gap * (maxLineNum - 1)) / maxLineNum;
        } else if (arrangeGravity == Gravity.CENTER) {
            //
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (arrangeGravity == Gravity.START) {
            onDrawStart(canvas);
        } else if (arrangeGravity == Gravity.CENTER) {
            onDrawCenter(canvas);
        }
    }

    private void onDrawStart(Canvas canvas) {
        for (int i = 0; i < bitmapList.size(); i++) {
            int left = getPaddingLeft() + (itemWidth + gap) * (i % maxLineNum);
            int top = getPaddingTop() + (itemHeight + gap) * (i / maxLineNum);
            int right = left + itemWidth;
            int bottom = top + itemHeight;
            Bitmap bitmap = bitmapList.get(i);
            if (bitmap == null) continue;
            srcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            dstRect.set(left, top, right, bottom);
            canvas.drawBitmap(
                    bitmap,
                    srcRect,
                    dstRect,
                    null
            );
        }
    }

    private void onDrawCenter(Canvas canvas) {

        int hrzNum = 0;//一行最多个数
        int verNum = 0;//竖直方向的行个数
        for (int i = 2; i <= maxLineNum; i++) {
            if (bitmapList.size() <= i * i) {
                hrzNum = i;
                verNum = (bitmapList.size() + (i - 1)) / i;
                itemWidth = itemHeight = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - gap * (i - 1)) / i;
                break;
            }
        }

        for (int i = 0; i < bitmapList.size(); i++) {

            int hrzIndex = i < (bitmapList.size() % hrzNum) ? i : (hrzNum - (bitmapList.size() - i - 1) % hrzNum - 1);
            int verIndex = verNum - ((bitmapList.size() - i - 1) / hrzNum) - 1;//0 1 2
            int hrzCount = verIndex == 0 ? (bitmapList.size() % hrzNum) : hrzNum;
            if (hrzCount == 0) hrzCount = hrzNum;

            int left = ((getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) - (itemWidth * hrzCount + gap * (hrzCount - 1))) / 2 + (itemHeight + gap) * (hrzIndex) + getPaddingLeft();
            int top = ((getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) - (itemHeight * verNum + gap * (verNum - 1))) / 2 + (itemHeight + gap) * (verIndex) + getPaddingTop();
            int right = left + itemWidth;
            int bottom = top + itemHeight;
            Bitmap bitmap = bitmapList.get(i);
            if (bitmap == null) continue;
            srcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            dstRect.set(left, top, right, bottom);
            canvas.drawBitmap(
                    bitmap,
                    srcRect,
                    dstRect,
                    null
            );
        }
    }
}
