package com.yuyang.messi.view;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yuyang.messi.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * https://github.com/saiff35/LivingTabs
 */
public class LivingTabLayout extends TabLayout {
    protected static final int SCALE_HIDDEN = 0;
    private ArrayList<LivingTab> tabs;
    private boolean animationRunning = false;
    private final Animator.AnimatorListener startListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            animationRunning = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
    private final Animator.AnimatorListener stopListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animationRunning = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    public LivingTabLayout(Context context) {
        this(context, null);
    }

    public LivingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);


    }

    public LivingTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }


//TODO // FIXME: 8/27/2015 going swipping the leftest page then clicking the rightest doesn't work


    @Override
    public void setupWithViewPager(@NonNull ViewPager viewPager) {

        final PagerAdapter adapter = viewPager.getAdapter();
        DrawableResIconAdapter drawableResIconAdapter = null;
        DrawableIconAdapter drawableIconAdapter = null;
        boolean isDrawableResAdapter;
        if ((isDrawableResAdapter = adapter instanceof DrawableResIconAdapter)) {
            drawableResIconAdapter = (DrawableResIconAdapter) adapter;
        } else if (adapter instanceof DrawableIconAdapter) {
            drawableIconAdapter = (DrawableIconAdapter) adapter;
        } else {
            throw new IllegalArgumentException(
                    "ViewPager's adapter must implement either DrawableIconAdapter or DrawableResIconAdapter"
            );
        }
        super.setupWithViewPager(viewPager);

        final int selectedTabPosition = getSelectedTabPosition();
        final int count = adapter.getCount();
        if (tabs == null)
            tabs = new ArrayList<>(count);
        else {
            tabs.clear();
        }
        for (int i = 0; i < count; ++i) {
            Tab t = super.getTabAt(i);
            if (isDrawableResAdapter)
                t.setIcon(drawableResIconAdapter.getIcon(i));
            else t.setIcon(drawableIconAdapter.getIcon(i));
            final LivingTab tab = newLivingTab(t);
            //  .setText(adapter.getPageTitle(i))
            //.setIcon(icons.get(i));
            addTab(tab);

            if (i != selectedTabPosition) {
                final TextView textView = tab.textView;
                textView.setScaleY(SCALE_HIDDEN);
                textView.setScaleX(SCALE_HIDDEN);
            } else {
                final ImageView iconView = tab.iconView;
                iconView.setScaleY(SCALE_HIDDEN);
                iconView.setScaleX(SCALE_HIDDEN);
            }
        }


        viewPager.addOnPageChangeListener(
                new LivingTabsOnPageChangeListener(this)
        );
    }

    private void addTab(LivingTab tab) {
        tabs.add(tab);
    }

    private LivingTab newLivingTab(Tab tab) {
        return new LivingTab(tab);

    }

    public static class LivingTabsOnPageChangeListener implements ViewPager.OnPageChangeListener {

        private final WeakReference<LivingTabLayout> tabLayoutRef;
        private int oldPosition = -1;
        private int state = ViewPager.SCROLL_STATE_IDLE;

        private int pendingPosition = -1;

        public LivingTabsOnPageChangeListener(LivingTabLayout tabLayout) {
            tabLayoutRef = new WeakReference(tabLayout);
            oldPosition = tabLayout.getSelectedTabPosition();
        }


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            LivingTabLayout tabLayout = this.tabLayoutRef.get();
            if (tabLayout != null) {
                if (positionOffset == 0.0f || positionOffsetPixels == 0) return;

                if (state == ViewPager.SCROLL_STATE_DRAGGING ||
                        state == ViewPager.SCROLL_STATE_SETTLING) {

                    tabLayout.setScalePosition(position, positionOffset, oldPosition, pendingPosition);

                }
                if (state == ViewPager.SCROLL_STATE_SETTLING && pendingPosition != -1) {
                    tabLayout.animateToPendingTab(pendingPosition, oldPosition);
                }
                if (pendingPosition != -1) {
                    oldPosition = pendingPosition;
                    pendingPosition = -1;
                }
            }
        }


        @Override
        public void onPageSelected(int position) {


            pendingPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

            this.state = state;
        }


    }

    private void animateToPendingTab(final int position, final int oldPosition) {
        {
            final LivingTab newTab = tabs.get(position);
            final View newTv = newTab.textView;


            newTv.animate().setDuration(300).scaleY(1).scaleX(1)
                    .setListener(startListener)
                    .start();

            final View newIv = newTab.iconView;

            newIv.animate().setDuration(300).scaleY(0).scaleX(0).start();

            final LivingTab oldTab = tabs.get(oldPosition);
            final View oldTv = oldTab.textView;

            oldTv.animate().setDuration(300).scaleY(0).scaleX(0).start();

            final View oldIv = oldTab.iconView;

            oldIv.animate().setDuration(300).scaleY(1).scaleX(1)
                    .setListener(stopListener)
                    .start();
        }
    }

    private void setScalePosition(final int position, final float positionOffset, final int oldPosition, final int pendingPosition) {
        if (!animationRunning) {
            int newPosition = position;

            float scale = positionOffset;
            if (position == oldPosition) {
                //TODO solve out of bound
                newPosition = position + 1;


            } else {
                scale = 1 - scale;
            }
            if (pendingPosition != -1) {
                newPosition = pendingPosition;

            }
            final LivingTab newTab = tabs.get(newPosition);
            TextView newTv = newTab.textView;

            newTv.setScaleY(scale);
            newTv.setScaleX(scale);
            ImageView newIv = newTab.iconView;
            float inversedScale = 1 - scale;
            newIv.setScaleY(inversedScale);
            newIv.setScaleX(inversedScale);

            final LivingTab oldTab = tabs.get(oldPosition);
            TextView oldTv = oldTab.textView;
            oldTv.setScaleY(inversedScale);
            oldTv.setScaleX(inversedScale);
            ImageView oldIv = oldTab.iconView;
            oldIv.setScaleY(scale);
            oldIv.setScaleX(scale);
        }
    }

    private static final class LivingTab {
        private TextView textView;
        private ImageView iconView;


        LivingTab(final Tab tab) {
            tab.setCustomView(R.layout.living_tab);
            View v = tab.getCustomView();
            textView = v.findViewById(android.R.id.text1);
            iconView = v.findViewById(android.R.id.icon);
        }
    }

    public interface DrawableIconAdapter {
        @NonNull
        Drawable getIcon(int position);
    }


    public interface DrawableResIconAdapter {
        @DrawableRes
        int getIcon(int position);
    }
}
