package com.yuyang.messi.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.lib_base.utils.SystemBarUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.kotlinui.main.MainActivity;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.SharedPreferencesUtil;
import com.yuyang.messi.view.Indicator.CircleIndicator;
import com.yuyang.messi.view.Indicator.flyco.anim.select.RotateEnter;
import com.yuyang.messi.view.Indicator.flyco.anim.select.ZoomInEnter;
import com.yuyang.messi.view.Indicator.flyco.indicator.FlycoPageIndicator;
import com.yuyang.messi.view.Indicator.flyco.indicator.RoundCornerIndicator;
import com.yuyang.messi.view.Indicator.vp_indicator.CirclePageIndicator;
import com.yuyang.messi.view.Progress.CircleProgress;

import org.greenrobot.eventbus.EventBus;


public class StartActivity extends AppBaseActivity {

    public static final String KEY_LAUNCH_INTENT = "key_launch_intent";

    private RelativeLayout advertLyt;
    private ImageView advertImageView;
    private CircleProgress circleProgress;

    private LinearLayout guideLyt;
    private ViewPager viewPager;

    private TextView startButton;

    private final int[] guideDrawables = {R.mipmap.guide_1_img, R.mipmap.guide_2_img, R.mipmap.guide_3_img};
    private final int[] colorIds = {R.color.red, R.color.yellow, R.color.blue};

    private final ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_start;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        SystemBarUtil.configBar(getActivity(), false, false, false, false, true);

//            adb shell am start -W -n com.yuyang.messi/com.yuyang.messi.ui.login.StartActivity
        reportFullyDrawn();

//        MediaScannerConnection.scanFile(App.getAppContext(), new String[]{PathConstant.DIR_ROOT}, new String[]{"application/octet-stream"}, null);
        initView();
        initEvent();

        EventBus.getDefault().removeAllStickyEvents();

        if (SharedPreferencesUtil.getFirstStart()) {
            advertLyt.setVisibility(View.INVISIBLE);
            guideLyt.setVisibility(View.VISIBLE);
        } else {
            advertLyt.setVisibility(View.VISIBLE);
            guideLyt.setVisibility(View.INVISIBLE);
            showAdvertImage();
            circleProgress.setValue(100, new int[]{Color.RED, Color.BLUE}, 4000, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    goMain();
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {

                }
            });
        }

        Uri uri = getIntent().getData();
        if (uri != null) {
            String type = uri.getQueryParameter("type");
            String id = uri.getQueryParameter("id");
            ToastUtil.showToast(type + id);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //第一帧显示出来后，用户能接触到的第一个回调
//        if (hasFocus) {
//            TraceCompat.endSection();
        //Debug.stopMethodTracing();
//        }
    }

    private void initView() {
        advertLyt = findViewById(R.id.activity_start_advert_lyt);
        advertImageView = findViewById(R.id.activity_start_advert);
        circleProgress = findViewById(R.id.activity_start_skip_circle);

        guideLyt = findViewById(R.id.activity_start_guide_lyt);
        viewPager = findViewById(R.id.activity_start_viewpager);
        viewPager.setAdapter(new ViewPagerAdapter(guideDrawables));
        viewPager.setOffscreenPageLimit(guideDrawables.length - 1);

        ((CirclePageIndicator) findViewById(R.id.activity_start_indicator0)).setViewPager(viewPager);
        ((CircleIndicator) findViewById(R.id.activity_start_indicator1)).setViewPager(viewPager);
        ((CircleIndicator) findViewById(R.id.activity_start_indicator2)).setViewPager(viewPager);
        ((RoundCornerIndicator) findViewById(R.id.activity_start_indicator3)).setViewPager(viewPager);
        ((RoundCornerIndicator) findViewById(R.id.activity_start_indicator4)).setViewPager(viewPager);
        ((RoundCornerIndicator) findViewById(R.id.activity_start_indicator5)).setViewPager(viewPager);
        ((FlycoPageIndicator) findViewById(R.id.activity_start_indicator6)).setViewPager(viewPager);
        ((FlycoPageIndicator) findViewById(R.id.activity_start_indicator7)).setViewPager(viewPager);
        ((FlycoPageIndicator) findViewById(R.id.activity_start_indicator8)).setViewPager(viewPager);
        ((FlycoPageIndicator) findViewById(R.id.activity_start_indicator9)).setViewPager(viewPager);
        ((FlycoPageIndicator) findViewById(R.id.activity_start_indicator10)).setViewPager(viewPager);
        ((FlycoPageIndicator) findViewById(R.id.activity_start_indicator11))
                .setIsSnap(true)
                .setSelectAnimClass(ZoomInEnter.class)
                .setUnselectAnimClass(RotateEnter.class)
                .setViewPager(viewPager);
        startButton = findViewById(R.id.activity_start_button);
    }

    private void initEvent() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int nextColorPosition = position + 1;
                int backgroundColor;
                if (nextColorPosition < viewPager.getAdapter().getCount()) {
                    backgroundColor = (Integer) argbEvaluator
                            .evaluate(positionOffset, ContextCompat.getColor(getActivity(), colorIds[position]), ContextCompat.getColor(getActivity(), colorIds[nextColorPosition]));
                } else {
                    backgroundColor = ContextCompat.getColor(getActivity(), colorIds[position]);
                }

                viewPager.setBackgroundColor(backgroundColor);
//                guideLyt.setBackgroundColor(backgroundColor);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                /**
                 * 停止：0
                 * 滑动：1
                 * 惯性：2
                 */
//                if (0 == state && viewPager.getCurrentItem() == guideDrawables.length - 1)
                if (viewPager.getCurrentItem() == guideDrawables.length - 1) {
                    startButton.setVisibility(View.VISIBLE);
                } else {
                    startButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        circleProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleProgress.stop();
                goMain();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setFirstStart();
                goMain();
            }
        });
    }

    @SuppressLint("CheckResult")
    private void goMain() {
        if (getIntent().getParcelableExtra(KEY_LAUNCH_INTENT) != null) {
            startActivities(new Intent[]{
                    new Intent(getActivity(), MainActivity.class),
                    getIntent().getParcelableExtra(KEY_LAUNCH_INTENT)});
        } else {
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void showAdvertImage() {
        GlideApp.with(getActivity())
                .load(R.drawable.start_ppx)
                .into(advertImageView);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(advertImageView, "alpha", 0, 1),
                ObjectAnimator.ofFloat(advertImageView, "scaleX", 0.8f, 1),
                ObjectAnimator.ofFloat(advertImageView, "scaleY", 0.8f, 1));
        animatorSet.setDuration(3000);
        advertImageView.setClipToOutline(true);
        ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                Float aFloat = (Float) valueAnimator.getAnimatedValue();

                advertImageView.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), PixelUtils.dp2px((1 - aFloat) * 200));
                    }
                });
            }
        });
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorSet.removeListener(this);
                ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).removeAllUpdateListeners();
            }
        });
        animatorSet.start();
    }

    private static class ViewPagerAdapter extends PagerAdapter {

        private final int[] guideDrawables;

        public ViewPagerAdapter(int[] guideDrawables) {
            this.guideDrawables = guideDrawables;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            RelativeLayout relativeLayout = new RelativeLayout(container.getContext());
            relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            ImageView guideView = new ImageView(container.getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtils.dp2px(300));
            params.setMargins(0, PixelUtils.dp2px(70), 0, 0);
            guideView.setLayoutParams(params);
            guideView.setImageResource(guideDrawables[position]);
            guideView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            relativeLayout.addView(guideView);
            container.addView(relativeLayout);

            return relativeLayout;
        }

        @Override
        public int getCount() {
            return guideDrawables.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}
