package com.yuyang.lib_base.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

/**
 * https://blog.csdn.net/m5314/article/details/68943869  代码来自
 * https://juejin.cn/post/6844903761060577294             ns原理
 */
public class NestedScrollWebView extends WebView implements NestedScrollingChild {

  public static final String TAG = NestedScrollWebView.class.getSimpleName();

  private int mLastMotionY;

  private final int[] mScrollOffset = new int[2];
  private final int[] mScrollConsumed = new int[2];

  private int mNestedYOffset;

  private NestedScrollingChildHelper mChildHelper;

  public NestedScrollWebView(Context context) {
    super(context);
    init();
  }

  public NestedScrollWebView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public NestedScrollWebView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    mChildHelper = new NestedScrollingChildHelper(this);
    setNestedScrollingEnabled(true);// 启用嵌套滑动机制
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    boolean result = false;

    MotionEvent trackedEvent = MotionEvent.obtain(event);

    final int action = MotionEventCompat.getActionMasked(event);

    if (action == MotionEvent.ACTION_DOWN) {
      mNestedYOffset = 0;
    }

    int y = (int) event.getY();

    event.offsetLocation(0, mNestedYOffset);

    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mLastMotionY = y;
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);// 找到自己祖上中最近的与自己匹配的ns parent，进行绑定并关闭ns parent的事件拦截机制
        result = super.onTouchEvent(event);
        break;
      case MotionEvent.ACTION_MOVE:
        int deltaY = mLastMotionY - y;

        if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
          deltaY -= mScrollConsumed[1];
          trackedEvent.offsetLocation(0, mScrollOffset[1]);
          mNestedYOffset += mScrollOffset[1];
        }

        int oldY = getScrollY();
        mLastMotionY = y - mScrollOffset[1];
        int newScrollY = Math.max(0, oldY + deltaY);
        deltaY -= newScrollY - oldY;
        if (dispatchNestedScroll(0, newScrollY - deltaY, 0, deltaY, mScrollOffset)) {
          mLastMotionY -= mScrollOffset[1];
          trackedEvent.offsetLocation(0, mScrollOffset[1]);
          mNestedYOffset += mScrollOffset[1];
        }
        if (mScrollConsumed[1] == 0 && mScrollOffset[1] == 0) {
          trackedEvent.recycle();
          result = super.onTouchEvent(trackedEvent);
        }
        break;
      case MotionEvent.ACTION_POINTER_DOWN:
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        stopNestedScroll();
        result = super.onTouchEvent(event);
        break;
    }
    return result;
  }

  // NestedScrollingChild

  @Override
  public void setNestedScrollingEnabled(boolean enabled) {
    mChildHelper.setNestedScrollingEnabled(enabled);
  }

  @Override
  public boolean isNestedScrollingEnabled() {
    return mChildHelper.isNestedScrollingEnabled();
  }

  @Override
  public boolean startNestedScroll(int axes) {
    return mChildHelper.startNestedScroll(axes);
  }

  @Override
  public void stopNestedScroll() {
    mChildHelper.stopNestedScroll();
  }

  @Override
  public boolean hasNestedScrollingParent() {
    return mChildHelper.hasNestedScrollingParent();
  }

  @Override
  public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
      int dyUnconsumed, int[] offsetInWindow) {
    return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
        offsetInWindow);
  }

  @Override
  public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
    return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
  }

  @Override
  public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
    return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
  }

  @Override
  public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
    return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
  }

}
