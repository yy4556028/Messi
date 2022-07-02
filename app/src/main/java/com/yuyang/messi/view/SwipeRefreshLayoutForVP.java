package com.yuyang.messi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class SwipeRefreshLayoutForVP extends SwipeRefreshLayout {

    private float startX;
    private float startY;
    private boolean isVpDragger;
    private final int mTouchSlop;

    public SwipeRefreshLayoutForVP(Context context) {
        this(context, null);
    }

    public SwipeRefreshLayoutForVP(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startX = ev.getX();
                startY = ev.getY();
                isVpDragger = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (isVpDragger) return false;

                float endX = ev.getX();
                float endY = ev.getY();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    isVpDragger = true;
                    return false;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                isVpDragger = false;
                break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
