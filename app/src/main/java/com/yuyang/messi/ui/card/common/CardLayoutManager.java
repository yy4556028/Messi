package com.yuyang.messi.ui.card.common;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import static com.yuyang.messi.ui.card.common.CardConfig.MAX_SHOW_COUNT;
import static com.yuyang.messi.ui.card.common.CardConfig.SCALE_GAP;
import static com.yuyang.messi.ui.card.common.CardConfig.TRANS_Y_GAP;

public class CardLayoutManager extends RecyclerView.LayoutManager {

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        int itemCount = getItemCount();
        if (itemCount < 1) {
            return;
        }
        int bottomPosition;//计算最下面一个的 pos
        //边界处理
        if (itemCount <= MAX_SHOW_COUNT) {
            bottomPosition = itemCount - 1;
        } else {
            bottomPosition = MAX_SHOW_COUNT - 1;
        }

        //从可见的最底层View开始layout，依次层叠上去
        for (int position = bottomPosition; position >=0; position--) {
            View view = recycler.getViewForPosition(position);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
            int heightSpace = getHeight() - getDecoratedMeasuredHeight(view);
            //我们在布局时，将childView居中处理，这里也可以改为只水平居中
            layoutDecoratedWithMargins(view, widthSpace / 2, heightSpace / 2,
                    widthSpace / 2 + getDecoratedMeasuredWidth(view),
                    heightSpace / 2 + getDecoratedMeasuredHeight(view));

            //除了顶层不需要缩小和位移
            if (position > 0) {
                //每一层都需要X方向的缩小
                view.setScaleX(1 - SCALE_GAP * position);
                //前N层，依次向下位移和Y方向的缩小
                if (position < MAX_SHOW_COUNT - 1) {
                    view.setTranslationY(TRANS_Y_GAP * position);
                    view.setScaleY(1 - SCALE_GAP * position);
                } else {//第N层在 向下位移和Y方向的缩小的程度与 N-1层保持一致
                    view.setTranslationY(TRANS_Y_GAP * (position - 1));
                    view.setScaleY(1 - SCALE_GAP * (position - 1));
                }
            }
        }
    }

}
