package com.yuyang.lib_base.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public class BannerViewPager extends ViewPager {

    private final MyHandler myHandler;

    public BannerViewPager(Context context) {
        this(context, null);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        myHandler = new MyHandler(this);
        myHandler.sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                myHandler.removeCallbacksAndMessages(null);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                myHandler.sendEmptyMessageDelayed(0, 3000);
                break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private static class MyHandler extends Handler {
        private WeakReference<BannerViewPager> mWeakReference;

        private MyHandler(BannerViewPager bannerViewPager) {
            mWeakReference = new WeakReference<>(bannerViewPager);
        }

        @Override
        public void handleMessage(@NotNull Message msg) {
            super.handleMessage(msg);

            BannerViewPager bannerViewPager = mWeakReference.get();
            if (bannerViewPager != null) {
                int currentItem = bannerViewPager.getCurrentItem();
                currentItem++;
                if (bannerViewPager.getAdapter() == null || currentItem >= bannerViewPager.getAdapter().getCount()) {
                    currentItem = 0;
                }
                bannerViewPager.setCurrentItem(currentItem);
                sendEmptyMessageDelayed(0, 3000);
            }
        }
    }
}
