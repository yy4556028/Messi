package com.yuyang.messi.ui.login;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StartActivity extends AppBaseActivity {

    public static final String KEY_LAUNCH_INTENT = "key_launch_intent";

    private RelativeLayout advertLyt;
    private ImageView advertImageView;
    private TextView skipText;
    private CircleProgress circleProgress;

    private LinearLayout guideLyt;
    private ViewPager viewPager;

    private TextView startButton;

    private final int[] guideDrawables = {R.mipmap.guide_1_img, R.mipmap.guide_2_img, R.mipmap.guide_3_img};
    private final int[] colorIds = {R.color.red, R.color.yellow, R.color.blue};

    private final ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    private final ActivityResultLauncher<Intent> externalStorageManagerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                checkPermissionAndJump();
            });

    private final ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                List<String> deniedAskList = new ArrayList<>();
                List<String> deniedNoAskList = new ArrayList<>();
                for (Map.Entry<String, Boolean> stringBooleanEntry : result.entrySet()) {
                    if (!stringBooleanEntry.getValue()) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), stringBooleanEntry.getKey())) {
                            deniedAskList.add(stringBooleanEntry.getKey());
                        } else {
                            deniedNoAskList.add(stringBooleanEntry.getKey());
                        }
                    }
                }

                if (deniedAskList.isEmpty() && deniedNoAskList.isEmpty()) {//全通过
                    if (getIntent().getParcelableExtra(KEY_LAUNCH_INTENT) != null) {
                        startActivities(new Intent[]{
                                new Intent(getActivity(), MainActivity.class),
                                getIntent().getParcelableExtra(KEY_LAUNCH_INTENT)});
                    } else {
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else if (!deniedNoAskList.isEmpty()) {
                    showMissingPermissionDialog();
                } else {
                    checkPermissionAndJump();
                }
            });

    @Override
    protected int getLayoutId() {
        return R.layout.activity_start;
    }

    @Override
    public void setStatusBar() {
        SystemBarUtil.fullScreen_immersive(getActivity(), false, false, false, false, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        try {
//            adb shell am start -W -n com.yuyang.messi/com.yuyang.messi.ui.login.StartActivity
            reportFullyDrawn();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    checkPermissionAndJump();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

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
        if (hasFocus) {
//            TraceCompat.endSection();
            //Debug.stopMethodTracing();
        }
    }

    private void initView() {
        advertLyt = findViewById(R.id.activity_start_advert_lyt);
        advertImageView = findViewById(R.id.activity_start_advert);
        skipText = findViewById(R.id.activity_start_skip_text);
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
                checkPermissionAndJump();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setFirstStart();
                checkPermissionAndJump();
            }
        });
    }

    @SuppressLint("CheckResult")
    private void checkPermissionAndJump() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                externalStorageManagerLauncher.launch(intent);
                return;
            }
        } else {
            permissionsLauncher.launch(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE});
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsLauncher.launch(new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO});
        }
    }

    private void showMissingPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限。")
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        System.exit(0);
                    }
                })
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                })
                .setCancelable(false)
                .show();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            advertImageView.setClipToOutline(true);
            ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    Float aFloat = (Float) valueAnimator.getAnimatedValue();

                    advertImageView.setOutlineProvider(new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, Outline outline) {
                            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), PixelUtils.dp2px((1 - aFloat) * 200));
                        }
                    });
                }
            });
        }
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
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}
