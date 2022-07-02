package com.yuyang.messi.ui.card.common;

import android.graphics.Canvas;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;

import com.yuyang.messi.ui.card.CardRecyclerAdapter.*;

import java.util.List;

import static com.yuyang.messi.ui.card.common.CardConfig.*;

/**
 * 介绍：探探效果定制的Callback
 * 作者：zhangxutong
 * 邮箱：mcxtzhang@163.com
 * 主页：http://blog.csdn.net/zxt0601
 * 时间： 16/12/18.
 */

public class TanTanCallback extends ItemTouchHelper.SimpleCallback {

    private RecyclerView.Adapter recyclerAdapter;
    private List beanList;
    private int swipeDir;

    private float threshold = 0.4f;

    private static final int MAX_ROTATION = 15;

    public interface CardListener {
        void onPraise(Object object);

        void onPass(Object object);

        void onReport(Object object);
    }

    private CardListener cardListener;

    private void cardCallBack(Object object) {
        switch (swipeDir) {
            case ItemTouchHelper.LEFT: {
                cardListener.onPass(object);
                break;
            }
            case ItemTouchHelper.RIGHT: {
                cardListener.onPraise(object);
                break;
            }
            case ItemTouchHelper.DOWN: {
                cardListener.onReport(object);
                break;
            }
        }
    }

    public TanTanCallback(RecyclerView.Adapter recyclerAdapter, List beanList, CardListener cardListener) {
        super(0, ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.recyclerAdapter = recyclerAdapter;
        this.beanList = beanList;
        this.cardListener = cardListener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return super.getMovementFlags(recyclerView, viewHolder);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder.getAdapterPosition() == 0) {
            return super.getSwipeDirs(recyclerView, viewHolder);
        } else {
            return 0;
        }
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        if (swipeDir != ItemTouchHelper.LEFT && swipeDir != ItemTouchHelper.RIGHT && swipeDir != ItemTouchHelper.DOWN) {
            return Float.MAX_VALUE;
        }
//        return super.getSwipeThreshold(viewHolder);
        return threshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        if (swipeDir != ItemTouchHelper.LEFT && swipeDir != ItemTouchHelper.RIGHT && swipeDir != ItemTouchHelper.DOWN) {
            return Float.MAX_VALUE;
        }
        return super.getSwipeEscapeVelocity(defaultValue);
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return super.getSwipeVelocityThreshold(defaultValue);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Object remove = beanList.remove(viewHolder.getLayoutPosition());
        recyclerAdapter.notifyDataSetChanged();

        if (cardListener != null) {
            cardCallBack(remove);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        //先根据滑动的dx dy 算出现在动画的比例系数fraction
        double swipeValue = Math.sqrt(dX * dX + dY * dY);
        double fraction = swipeValue / (recyclerView.getWidth() / 2);
        //边界修正 最大为1
        if (fraction > 1) {
            fraction = 1;
        }
        //对每个ChildView进行缩放 位移
        int childCount = recyclerView.getChildCount();
        for (int position = 0; position < childCount; position++) {
            View child = recyclerView.getChildAt(position);

            int level = childCount - position - 1;//第几层

            if (level == 0) {
                float xFraction = dX / (recyclerView.getWidth() / 2);
                float yFraction = dY / (recyclerView.getHeight() / 2);

                //边界修正 最大为1
                if (xFraction > 1) {
                    xFraction = 1;
                } else if (xFraction < -1) {
                    xFraction = -1;
                }

                if (yFraction > 1) {
                    yFraction = 1;
                } else if (yFraction < -1) {
                    yFraction = -1;
                }

                child.setRotation(xFraction * MAX_ROTATION);

                if (viewHolder instanceof CardHolder) {
                    CardHolder holder = (CardHolder) viewHolder;

                    if (Math.abs(xFraction) > Math.abs(yFraction)) {
                        if (xFraction >= 0) {
                            //露出左边
                            holder.praiseImage.setAlpha(xFraction);
                        } else if (xFraction < 0) {
                            //露出右边
                            holder.passImage.setAlpha(-xFraction);
                        } else {
                            holder.praiseImage.setAlpha(0f);
                            holder.passImage.setAlpha(0f);
                        }
                        holder.reportImage.setAlpha(0f);
                    } else {
                        if (yFraction > 0) {
                            holder.reportImage.setAlpha(yFraction);
                        } else {
                            holder.reportImage.setAlpha(0f);
                        }
                        holder.praiseImage.setAlpha(0f);
                        holder.passImage.setAlpha(0f);
                    }
                }
            } else {
                child.setScaleX((float) (1 - SCALE_GAP * level + fraction * SCALE_GAP));
                if (level < MAX_SHOW_COUNT - 1) {//最下面一个不缩放
                    child.setScaleY((float) (1 - SCALE_GAP * level + fraction * SCALE_GAP));
                    child.setTranslationY((float) (TRANS_Y_GAP * level - fraction * TRANS_Y_GAP));
                }
            }
        }

        //判断滑动方向
        float swipeX = viewHolder.itemView.getX() + (viewHolder.itemView.getWidth() / 2) - recyclerView.getWidth() / 2;
        float swipeY = viewHolder.itemView.getY() + (viewHolder.itemView.getHeight() / 2) - recyclerView.getHeight() / 2;

        if (Math.abs(swipeX) > recyclerView.getWidth() * threshold && Math.abs(swipeY) < recyclerView.getWidth() * threshold) {
            swipeDir = swipeX > 0 ? ItemTouchHelper.RIGHT : ItemTouchHelper.LEFT;
        } else if (Math.abs(swipeX) < recyclerView.getWidth() * threshold && Math.abs(swipeY) > recyclerView.getWidth() * threshold) {
            swipeDir = swipeY > 0 ? ItemTouchHelper.DOWN : ItemTouchHelper.UP;
        } else {
            swipeDir = 0;
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setRotation(0);

        if (viewHolder instanceof CardHolder) {
            CardHolder holder = (CardHolder) viewHolder;
            holder.praiseImage.setAlpha(0f);
            holder.passImage.setAlpha(0f);
            holder.reportImage.setAlpha(0f);
        }
    }

}
