package com.yuyang.lib_base.recyclerview.item_decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LinearItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private int mOrientation;
    private boolean isReverse;

    private Drawable mDivider;
    private int mSpanSpace;

    public LinearItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        mSpanSpace = mDivider.getIntrinsicHeight();
    }

    public LinearItemDecoration(Drawable drawable, int spanSpace) {
        mDivider = drawable;
        mSpanSpace = spanSpace;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            if (mOrientation == RecyclerView.HORIZONTAL) {
                drawHorizontal(c, parent);
            } else if (mOrientation == RecyclerView.VERTICAL) {
                drawVertical(c, parent);
            }
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) parent.getLayoutManager();
            mOrientation = manager.getOrientation();
            isReverse = manager.getReverseLayout();

            if (mOrientation == RecyclerView.HORIZONTAL) {
                if (isReverse && parent.getChildAdapterPosition(view) == 0) {
                    outRect.set(0, 0, 0, 0);
                } else if (!isReverse && parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                    outRect.set(0, 0, 0, 0);
                } else {
                    outRect.set(0, 0, mSpanSpace, 0);
                }
            } else if (mOrientation == RecyclerView.VERTICAL) {
                if (isReverse && parent.getChildAdapterPosition(view) == 0) {
                    outRect.set(0, 0, 0, 0);
                } else if (!isReverse && parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                    outRect.set(0, 0, 0, 0);
                } else {
                    outRect.set(0, 0, 0, mSpanSpace);
                }
            }
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int recyclerViewTop = parent.getPaddingTop();
        final int recyclerViewBottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int itemPosition = parent.getChildAdapterPosition(child);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mSpanSpace;

            top = Math.max(top, recyclerViewTop);
            bottom = Math.min(bottom, recyclerViewBottom);
            top = Math.min(top, bottom);

            if (isReverse && itemPosition == 0) {
                mDivider.setBounds(left, top, right, top);
            } else if (!isReverse && itemPosition == parent.getAdapter().getItemCount() - 1) {
                mDivider.setBounds(left, top, right, top);
            } else {
                mDivider.setBounds(left, top, right, bottom);
            }
            mDivider.draw(c);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int recyclerViewLeft = parent.getPaddingLeft();
        final int recyclerViewRight = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int itemPosition = parent.getChildAdapterPosition(child);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int left = child.getRight() + params.rightMargin;
            int right = left + mSpanSpace;

            left = Math.max(left, recyclerViewLeft);
            right = Math.min(right, recyclerViewRight);
            left = Math.min(left, right);

            if (isReverse && itemPosition == 0) {
                mDivider.setBounds(left, top, left, bottom);
            } else if (!isReverse && itemPosition == parent.getAdapter().getItemCount() - 1) {
                mDivider.setBounds(left, top, left, top);
            } else {
                mDivider.setBounds(left, top, right, bottom);
            }
            mDivider.draw(c);
        }
    }
}
