package com.yuyang.messi.view.flow;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.yuyang.messi.R;

/**
 * @see AutoLineFeedLayout
 * @see FlowLayout
 */
public class AutoLineFeedLayout extends ViewGroup {

    private int verticalSpacing = 0;
    private int horizontalSpacing = 0;

    public AutoLineFeedLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public AutoLineFeedLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLineFeedLayout(Context context) {
        this(context, null);
    }

    /**
     * 获取布局文件中的一些值
     *
     * @param attrs attrs
     */
    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AutoLineFeedLayout);
        horizontalSpacing = (int) typedArray.getDimension(R.styleable.AutoLineFeedLayout_horizontalSpacing, 0);
        verticalSpacing = (int) typedArray.getDimension(R.styleable.AutoLineFeedLayout_verticalSpacing, 0);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), 0, heightMeasureSpec);
        }
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            final int width = MeasureSpec.getSize(widthMeasureSpec);
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(getDesiredHeight(width), MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        final int lineWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int maxLineHeight = 0;
        int paddingTop = getPaddingTop();
        int childLeft = getPaddingLeft();

        for (int i = 0; i < getChildCount(); i++) {

            View childView = getChildAt(i);
            childView.measure(0, 0);

            if (childView.getVisibility() != GONE) {
                //子View的宽度
                int childWidth = childView.getMeasuredWidth();
                //子View的高度
                int childHeight = childView.getMeasuredHeight();

                //本行可用宽度 放不下 下一个子View
                if (lineWidth - childLeft < childWidth + horizontalSpacing) {
                    //初始化下一行可用宽度
                    //初始化下一行 top
                    paddingTop = paddingTop + maxLineHeight + verticalSpacing;
                    //初始化下一行 left
                    childLeft = getPaddingLeft();
                    //初始化下一行 子View最大高度
                    maxLineHeight = 0;
                }

                childView.layout(childLeft, paddingTop, childLeft + childWidth, paddingTop + childHeight);
                childLeft += childWidth + horizontalSpacing;
                maxLineHeight = Math.max(maxLineHeight, childHeight);
            }
        }
        bindListener();
    }

    private int getDesiredHeight(int width) {
        final int lineWidth = width - getPaddingLeft() - getPaddingRight();
        int totalHeight = getPaddingTop() + getPaddingBottom();
        int childLeft = getPaddingLeft();
        int lineHeight = 0;
        int lineCount = getChildCount() > 0 ? 1 : 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                if (lineWidth - childLeft < childWidth + horizontalSpacing) {
                    lineCount++;
                    totalHeight += lineHeight;
                    lineHeight = 0;
                    childLeft = getPaddingLeft();
                }
                childLeft += childWidth + horizontalSpacing;
                lineHeight = Math.max(childHeight, lineHeight);
            }
        }
        totalHeight = totalHeight + lineHeight;
        totalHeight += verticalSpacing * (lineCount - 1);
        return totalHeight;
    }

    private void bindListener() {
        if (onItemClickListener != null) {
            for (int i = 0; i < getChildCount(); i++) {
                final View view = getChildAt(i);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.setOnItemClickListener(v, indexOfChild(v));
                    }
                });
            }
        }
    }

    public void setOnItemClickListener(final onItemClickListener listener) {
        onItemClickListener = listener;
        bindListener();
    }

    public interface onItemClickListener {
        void setOnItemClickListener(View v, int index);
    }

    private onItemClickListener onItemClickListener;
}