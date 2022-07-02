package com.yuyang.messi.ui.main;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.LogUtil;
import com.yuyang.lib_base.utils.statusbar.MyStatusBarUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class AboutActivity extends AppBaseActivity {

    // 控制ToolBar的变量
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;

    private boolean mIsTheTitleVisible = false;

    private ImageView avatarImageView; // 大图片

    private AppBarLayout mAppBar; // 整个可以滑动的AppBar

    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private TextView mTvMsg;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.setBackgroundResource(R.color.transparent);
        headerLayout.showTitle("关于");
        headerLayout.showLeftBackButton();
        headerLayout.setImageAndTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        ((CollapsingToolbarLayout.LayoutParams) headerLayout.getLayoutParams()).topMargin = MyStatusBarUtil.getStatusBarHeight();

        mAppBar = findViewById(R.id.activity_about_appBar);
        avatarImageView = findViewById(R.id.activity_about_avatar);
        mTvMsg = findViewById(R.id.activity_about_text);
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);

        mCollapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(getActivity(), R.color.textPrimary));
        mCollapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getActivity(), R.color.textPrimary));

        mTvMsg.setAutoLinkMask(Linkify.ALL);
        mTvMsg.setMovementMethod(LinkMovementMethod.getInstance());

        // AppBar的监听
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                String TAG = "AppBar onOffset";

                float BOUNDARY_PERCENTAGE = 0.3f;
                int MAX_SCALE = 2;

                int maxScroll = appBarLayout.getTotalScrollRange();

                LogUtil.d(TAG, "verticalOffset = " + verticalOffset);
                LogUtil.d(TAG, "maxScroll = " + maxScroll);

                float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
                if (percentage > BOUNDARY_PERCENTAGE) {
                    avatarImageView.setScaleX(1);
                    avatarImageView.setScaleY(1);
                } else {
                    float scale = MAX_SCALE - percentage * (MAX_SCALE - 1) / BOUNDARY_PERCENTAGE;
                    avatarImageView.setScaleX(scale);
                    avatarImageView.setScaleY(scale);
                }
                handleToolbarTitleVisibility(percentage);
            }
        });

        initParallaxValues(); // 自动滑动效果

    }

    // 设置自动滑动的动画效果
    private void initParallaxValues() {
        CollapsingToolbarLayout.LayoutParams petDetailsLp =
                (CollapsingToolbarLayout.LayoutParams) avatarImageView.getLayoutParams();

        petDetailsLp.setParallaxMultiplier(0.9f);

        avatarImageView.setLayoutParams(petDetailsLp);
    }

    // 处理ToolBar的显示
    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
//                startAlphaAnimation(mTvToolbarTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }
        } else {
            if (mIsTheTitleVisible) {
//                startAlphaAnimation(mTvToolbarTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    // 设置渐变的动画
    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
