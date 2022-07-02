package com.yuyang.messi.ui.suning;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.yuyang.lib_base.ui.view.RoundRectConstraintLayout;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.lib_base.utils.statusbar.MyStatusBarUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FangyizhanshiActivity extends AppBaseActivity {

    private RoundRectConstraintLayout roundRectLayout;
    private ImageView ivReveal;

    private Disposable disposable;

    private final int[] colorArr = new int[]{
            Color.parseColor("#2A921F"),
            Color.parseColor("#34B525")
    };

    private int curIndex;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fangyizhanshi;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != disposable) {
            disposable.dispose();
            disposable = null;
        }
    }

    private void initView() {
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = ivBack.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
            }
        });
        ImageView ivClose = findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = ivClose.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
            }
        });
        findViewById(R.id.topLyt).setPadding(0, MyStatusBarUtil.getStatusBarHeight(), 0, 0);
        roundRectLayout = findViewById(R.id.roundRectLyt);
        roundRectLayout.setRound(0, 0, PixelUtils.dp2px(20), PixelUtils.dp2px(20));
        roundRectLayout.setBackgroundColor(colorArr[colorArr.length - 1]);
        ivReveal = findViewById(R.id.ivReveal);
        roundRectLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                roundRectLayout.getLayoutParams().height = roundRectLayout.getMeasuredHeight();
                roundRectLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initEvent() {
        disposable = Observable.interval(1000, 1500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        ValueAnimator animator = ValueAnimator.ofFloat(0.0F, (float) (2f * Math.hypot(roundRectLayout.getMeasuredWidth(), roundRectLayout.getMeasuredHeight())));
                        animator.setDuration(1000);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                ivReveal.setColorFilter(colorArr[curIndex % colorArr.length]);
                                ivReveal.getLayoutParams().width = Math.round((Float) animation.getAnimatedValue());
                                ivReveal.getLayoutParams().height = Math.round((Float) animation.getAnimatedValue());
                                ivReveal.requestLayout();
                            }
                        });
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                roundRectLayout.setBackgroundColor(colorArr[curIndex % colorArr.length]);
                                curIndex++;
                                animator.removeListener(this);
                                animator.removeAllUpdateListeners();
                            }
                        });
                        animator.start();
                    }
                });
    }
}
