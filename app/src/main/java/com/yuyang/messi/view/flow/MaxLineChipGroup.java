package com.yuyang.messi.view.flow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.MarginLayoutParamsCompat;
import androidx.core.view.ViewCompat;
import com.google.android.material.chip.ChipGroup;

@SuppressLint("RestrictedApi")
public class MaxLineChipGroup extends ChipGroup {

    private int maxLine = -1;

    public MaxLineChipGroup(Context context) {
        super(context);
    }

    public MaxLineChipGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxLineChipGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getMaxLine() {
        return maxLine;
    }

    public void setMaxLine(int maxLine) {
        this.maxLine = maxLine;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        final int height = MeasureSpec.getSize(heightMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        final int maxWidth =
            widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY
                ? width
                : Integer.MAX_VALUE;

        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int childBottom = childTop;
        int childRight = childLeft;
        int maxChildRight = 0;
        final int maxRight = maxWidth - getPaddingRight();
        int lineCount = 1;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == View.GONE) {
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            ViewGroup.LayoutParams lp = child.getLayoutParams();
            int leftMargin = 0;
            int rightMargin = 0;
            if (lp instanceof MarginLayoutParams) {
                MarginLayoutParams marginLp = (MarginLayoutParams) lp;
                leftMargin += marginLp.leftMargin;
                rightMargin += marginLp.rightMargin;
            }

            childRight = childLeft + leftMargin + child.getMeasuredWidth();

            // If the current child's right bound exceeds Flowlayout's max right bound and flowlayout is
            // not confined to a single line, move this child to the next line and reset its left bound to
            // flowlayout's left bound.
            if (childRight > maxRight && !isSingleLine()) {
                lineCount++;
                if (maxLine > 0 && lineCount > maxLine) {
                    break;
                }
                childLeft = getPaddingLeft();
                childTop = childBottom + getLineSpacing();
            }

            childRight = childLeft + leftMargin + child.getMeasuredWidth();
            childBottom = childTop + child.getMeasuredHeight();

            // Updates Flowlayout's max right bound if current child's right bound exceeds it.
            if (childRight > maxChildRight) {
                maxChildRight = childRight;
            }

            childLeft += (leftMargin + rightMargin + child.getMeasuredWidth()) + getItemSpacing();

            // For all preceding children, the child's right margin is taken into account in the next
            // child's left bound (childLeft). However, childLeft is ignored after the last child so the
            // last child's right margin needs to be explicitly added to Flowlayout's max right bound.
            if (i == (getChildCount() - 1)) {
                maxChildRight += rightMargin;
            }
        }

        maxChildRight += getPaddingRight();
        childBottom += getPaddingBottom();

        int finalWidth = getMeasuredDimension(width, widthMode, maxChildRight);
        int finalHeight = getMeasuredDimension(height, heightMode, childBottom);
        setMeasuredDimension(finalWidth, finalHeight);
    }

    private static int getMeasuredDimension(int size, int mode, int childrenEdge) {
        switch (mode) {
            case MeasureSpec.EXACTLY:
                return size;
            case MeasureSpec.AT_MOST:
                return Math.min(childrenEdge, size);
            default: // UNSPECIFIED:
                return childrenEdge;
        }
    }

    @Override
    protected void onLayout(boolean sizeChanged, int left, int top, int right, int bottom) {
        if (getChildCount() == 0) {
            // Do not re-layout when there are no children.
            return;
        }

        boolean isRtl = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;
        int paddingStart = isRtl ? getPaddingRight() : getPaddingLeft();
        int paddingEnd = isRtl ? getPaddingLeft() : getPaddingRight();
        int childStart = paddingStart;
        int childTop = getPaddingTop();
        int childBottom = childTop;
        int childEnd;

        final int maxChildEnd = right - left - paddingEnd;

        int lineCount = 1;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == View.GONE) {
                continue;
            }

            ViewGroup.LayoutParams lp = child.getLayoutParams();
            int startMargin = 0;
            int endMargin = 0;
            if (lp instanceof MarginLayoutParams) {
                MarginLayoutParams marginLp = (MarginLayoutParams) lp;
                startMargin = MarginLayoutParamsCompat.getMarginStart(marginLp);
                endMargin = MarginLayoutParamsCompat.getMarginEnd(marginLp);
            }

            childEnd = childStart + startMargin + child.getMeasuredWidth();

            if (!isSingleLine() && (childEnd > maxChildEnd)) {
                lineCount++;
                if (maxLine > 0 && lineCount > maxLine) {
                    break;
                }
                childStart = paddingStart;
                childTop = childBottom + getLineSpacing();
            }

            childEnd = childStart + startMargin + child.getMeasuredWidth();
            childBottom = childTop + child.getMeasuredHeight();

            if (isRtl) {
                child.layout(
                    maxChildEnd - childEnd, childTop, maxChildEnd - childStart - startMargin, childBottom);
            } else {
                child.layout(childStart + startMargin, childTop, childEnd, childBottom);
            }

            childStart += (startMargin + endMargin + child.getMeasuredWidth()) + getItemSpacing();
        }
    }
}
