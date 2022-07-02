package com.yuyang.messi.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;


/**
 * 具有上下弹性滚动的ScrollView<br><br>
 */
public class OverScrollView extends ScrollView {

    /**
     * 滚动系数, 视图滚动距离与手指滑动距离的比值
     */
    private static final float MOVE_FACTOR = 0.5f;

    /**
     * 松开手指后 界面回到正常位置需要的时间
     */
    private static final int BACK_ANIM_TIME = 300;

    /**
     * scrollView 的唯一子 View
     */
    private View contentView;

    // 手指按下时的Y值
    private float startY;

    // 记录正常的布局位置
    private Rect originalRect = new Rect();

    // 是否可继续上拉
    private boolean canPullUp;

    // 是否可继续下拉
    private boolean canPullDown;

    // 手指移动中是否移动了布局
    private boolean isMoved;

    public OverScrollView(Context context) {
        this(context, null);
    }

    public OverScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            contentView = getChildAt(0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (contentView == null)
            return;

        /**
         * scrollView 中唯一子控件的位置信息不变
         */
        originalRect.set(contentView.getLeft(), contentView.getTop(), contentView.getRight(), contentView.getBottom());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (contentView == null)
            return super.dispatchTouchEvent(ev);

        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                canPullDown = isCanPullDown();
                canPullUp = isCanPullUp();

                startY = ev.getY();
                break;

            case MotionEvent.ACTION_UP:
                // 如果没移动布局，跳过
                if (!isMoved) {
                    break;
                }

                TranslateAnimation anim = new TranslateAnimation(0, 0
                        , contentView.getTop() - getPaddingTop() - ((MarginLayoutParams) contentView.getLayoutParams()).topMargin
                        , originalRect.top - getPaddingTop() - ((MarginLayoutParams) contentView.getLayoutParams()).topMargin);
                anim.setDuration(BACK_ANIM_TIME);
                contentView.startAnimation(anim);

                contentView.layout(originalRect.left, originalRect.top, originalRect.right, originalRect.bottom);
                // 标记位设置回 false
                canPullDown = false;
                canPullUp = false;
                isMoved = false;
                break;

            case MotionEvent.ACTION_MOVE:
                // 在移动过程中，既没有滚动到上拉  也没下拉的位置
                if (!canPullDown && !canPullUp) {
                    startY = ev.getY();
                    canPullDown = isCanPullDown();
                    canPullUp = isCanPullUp();
                    break;
                }

                float currY = ev.getY();
                int deltaY = (int) (currY - startY);

                boolean shouldMove = (canPullDown && deltaY > 0) // 可上拉，且手指向下移动
                        || (canPullUp && deltaY < 0) // 可下拉，且手指向上移动
                        || (canPullDown && canPullUp); // 既可上拉也可下拉 convertView 比 scrollView 小

                if (shouldMove) {
                    // 计算偏移量
                    int offset = (int) (deltaY * MOVE_FACTOR);
                    contentView.layout(originalRect.left, originalRect.top + offset, originalRect.right, originalRect.bottom + offset);
                    isMoved = true;
                }
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判断是否滚动到顶部
     */
    private boolean isCanPullDown() {
        return getScrollY() == 0 || contentView.getHeight() < getHeight() + getScrollY();
    }

    /**
     * 判断是否滚动到底部
     */
    private boolean isCanPullUp() {
        return contentView.getHeight() <= getHeight() + getScrollY();
    }
}
