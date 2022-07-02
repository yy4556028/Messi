package com.yuyang.lib_base.recyclerview.item_decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable mDivider;

    private int mOrientation;
    private boolean isReverse;
    private int spanCount;

    private int mHorizonSpan;
    private int mVerticalSpan;
    private boolean includeEdge;

    private Rect parentPaddingRect = new Rect();
    private Rect rect = new Rect();

    public GridItemDecoration(Context context, boolean includeEdge) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        this.includeEdge = includeEdge;
    }

    public GridItemDecoration(int span, boolean includeEdge) {
        mHorizonSpan = mVerticalSpan = span;
        this.includeEdge = includeEdge;
    }

    public GridItemDecoration(Drawable drawable, int horizonSpan, int verticalSpan, boolean includeEdge) {
        mDivider = drawable;
        mHorizonSpan = horizonSpan;
        mVerticalSpan = verticalSpan;
        this.includeEdge = includeEdge;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (parent.getLayoutManager() instanceof GridLayoutManager || parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            if (mDivider != null) {
                draw(c, parent);
            }
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        parentPaddingRect.left = parent.getPaddingLeft();
        parentPaddingRect.top = parent.getPaddingTop();
        parentPaddingRect.right = parent.getWidth() - parent.getPaddingRight();
        parentPaddingRect.bottom = parent.getHeight() - parent.getPaddingBottom();

        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();
            mOrientation = manager.getOrientation();
            isReverse = manager.getReverseLayout();
            spanCount = manager.getSpanCount();
            int position = parent.getChildAdapterPosition(view);
            int childCount = parent.getAdapter().getItemCount();

            int left = 0;
            int top = 0;
            int right = mHorizonSpan;
            int bottom = mVerticalSpan;

            if (includeEdge) {
                if (isFirstRow(position, childCount)) {
                    top = mVerticalSpan;
                }
                if (isFirstColumn(position, childCount)) {
                    left = mHorizonSpan;
                }
            } else {
                if (isLastRow(position, childCount)) {
                    bottom = 0;
                }
                if (isLastColumn(position, childCount)) {
                    right = 0;
                }
            }
            outRect.set(left, top, right, bottom);
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) parent.getLayoutManager();
            mOrientation = manager.getOrientation();
            isReverse = manager.getReverseLayout();
            spanCount = manager.getSpanCount();
            int position = parent.getChildAdapterPosition(view);
            int childCount = parent.getAdapter().getItemCount();

            int left = 0;
            int top = 0;
            int right = mHorizonSpan;
            int bottom = mVerticalSpan;

            if (includeEdge) {
                if (isFirstRow(position, childCount)) {
                    top = mVerticalSpan;
                }
                if (isFirstColumn(position, childCount)) {
                    left = mHorizonSpan;
                }
            } else {
                if (isLastRow(position, childCount)) {
                    bottom = 0;
                }
                if (isLastColumn(position, childCount)) {
                    right = 0;
                }
            }
            outRect.set(left, top, right, bottom);
        }
    }

    private void draw(Canvas c, RecyclerView parent) {
        int totalCount = parent.getAdapter().getItemCount();
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int itemPosition = parent.getChildAdapterPosition(child);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int left;
            int right;
            int top;
            int bottom;

            if (includeEdge) {
                //draw bottom
                left = child.getLeft() - params.leftMargin;
                if (isFirstColumn(itemPosition, totalCount)) {
                    left -= mHorizonSpan;//补缺角
                }
                right = child.getRight() + params.rightMargin + mHorizonSpan;//补缺角
                top = child.getBottom() + params.bottomMargin;
                bottom = top + mVerticalSpan;
                mDivider.setBounds(limitWithinBounds(left, top, right, bottom));
                mDivider.draw(c);

                if (isFirstRow(itemPosition, totalCount)) {//如果第一行，draw top
                    bottom = child.getTop() - params.topMargin;
                    top = bottom - mVerticalSpan;
                    mDivider.setBounds(limitWithinBounds(left, top, right, bottom));
                    mDivider.draw(c);
                }

                //draw right
                top = child.getTop() - params.topMargin;
                bottom = child.getBottom() + params.bottomMargin;
                left = child.getRight() + params.rightMargin;
                right = left + mHorizonSpan;
                mDivider.setBounds(limitWithinBounds(left, top, right, bottom));
                mDivider.draw(c);

                if (isFirstColumn(itemPosition, totalCount)) {//如果第一列，draw left
                    right = child.getLeft() - params.leftMargin;
                    left = right - mHorizonSpan;
                    mDivider.setBounds(limitWithinBounds(left, top, right, bottom));
                    mDivider.draw(c);
                }
            } else {
                if (!isLastRow(itemPosition, totalCount)) {
                    //draw bottom
                    left = child.getLeft() - params.leftMargin;
                    right = child.getRight() + params.rightMargin;
                    if (!isLastColumn(itemPosition, totalCount)) {
                        right += mHorizonSpan;//补缺角
                    }
                    top = child.getBottom() + params.bottomMargin;
                    bottom = top + mVerticalSpan;
                    mDivider.setBounds(limitWithinBounds(left, top, right, bottom));
                    mDivider.draw(c);
                }

                if (!isLastColumn(itemPosition, totalCount)) {
                    //draw right
                    top = child.getTop() - params.topMargin;
                    bottom = child.getBottom() + params.bottomMargin;
                    left = child.getRight() + params.rightMargin;
                    right = left + mHorizonSpan;
                    mDivider.setBounds(limitWithinBounds(left, top, right, bottom));
                    mDivider.draw(c);
                }
            }
        }

        //如果是 reverse，第一排或第一列如果不满 spanCount，不满的几个item需要draw 左侧或上侧
        if (isReverse) {
            if (childCount % spanCount == 0 || childCount < spanCount) return;
            int pos = childCount - childCount % spanCount;
            int needDealNum = spanCount - childCount % spanCount;
            for (int i = 0; i < needDealNum; i++) {
                pos -= 1;
                View child = parent.getChildAt(pos);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                if (mOrientation == RecyclerView.VERTICAL) {//draw top
                    int left = child.getLeft() - params.leftMargin;
                    int right = child.getRight() + params.rightMargin + mHorizonSpan;//补缺角
                    int bottom = child.getTop() - params.topMargin;
                    int top = bottom - mVerticalSpan;
                    mDivider.setBounds(limitWithinBounds(left, top, right, bottom));
                    mDivider.draw(c);
                } else {//draw left
                    int top = child.getTop() - params.topMargin;
                    int bottom = child.getBottom() + params.bottomMargin + mVerticalSpan;//补缺角
                    int right = child.getLeft() - params.leftMargin;
                    int left = right - mHorizonSpan;
                    mDivider.setBounds(limitWithinBounds(left, top, right, bottom));
                    mDivider.draw(c);
                }
            }
        }
    }

    private boolean isFirstRow(int pos, int childCount) {
        if (mOrientation == RecyclerView.VERTICAL) {
            if (!isReverse) {
                return pos < spanCount;
            } else {
                int topRowItemNum = childCount % spanCount == 0 ? spanCount : childCount % spanCount;//第一排item个数
                return pos >= childCount - topRowItemNum;
            }
        } else {
            return pos % spanCount == 0;
        }
    }

    private boolean isLastRow(int pos, int childCount) {
        if (mOrientation == RecyclerView.VERTICAL) {
            if (isReverse) {
                return pos < spanCount;
            } else {
                int lastRowItemNum = childCount % spanCount == 0 ? spanCount : childCount % spanCount;//最后一排item个数
                return pos >= childCount - lastRowItemNum;
            }
        } else {
            return (pos + 1) % spanCount == 0;
        }
    }

    private boolean isFirstColumn(int pos, int childCount) {
        if (mOrientation == RecyclerView.VERTICAL) {
            return pos % spanCount == 0;
        } else {
            if (!isReverse) {
                return pos < spanCount;
            } else {
                int leftColumnItemNum = childCount % spanCount == 0 ? spanCount : childCount % spanCount;//左侧第一列item个数
                return pos >= childCount - leftColumnItemNum;
            }
        }
    }

    private boolean isLastColumn(int pos, int childCount) {
        if (mOrientation == RecyclerView.VERTICAL) {
            return (pos + 1) % spanCount == 0;
        } else {
            if (isReverse) {
                return pos < spanCount;
            } else {
                int lastColumnItemNum = childCount % spanCount == 0 ? spanCount : childCount % spanCount;//右侧最后一列item个数
                return pos >= childCount - lastColumnItemNum;
            }
        }
    }

    private Rect limitWithinBounds(int left, int top, int right, int bottom) {
        rect.top = Math.max(top, parentPaddingRect.top);
        rect.bottom = Math.min(bottom, parentPaddingRect.bottom);
        rect.top = Math.min(rect.top, rect.bottom);
        rect.left = Math.max(left, parentPaddingRect.left);
        rect.right = Math.min(right, parentPaddingRect.right);
        rect.left = Math.min(rect.left, rect.right);
        return rect;
    }
}