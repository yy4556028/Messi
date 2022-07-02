package com.yuyang.messi.ui.common.photo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.utils.SystemBarUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.ImageBean;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class PhotoShowActivity extends AppBaseActivity {

    private static final String KEY_BITMAP = "key_bitmap";
    private static final String KEY_RES_ID = "key_resId";
    private static final String KEY_URL = "key_url";
    private static final String KEY_PHOTO_BEAN = "key_photo_bean";

    private PhotoView photoView;

    private Bitmap bitmap;
    private @DrawableRes
    int resId;
    private String mUrl;
    private ImageBean mImageBean;

    public static void launchActivityNew(Activity activity, String url, View anchor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            activity.setExitSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
            activity.getWindow().setSharedElementsUseOverlay(false);

            MaterialContainerTransform m = new MaterialContainerTransform();
        }
    }

    public static void launchActivity(Activity activity, @DrawableRes int resId, View anchor) {
        Intent intent = new Intent(activity, PhotoShowActivity.class);
        intent.putExtra(KEY_RES_ID, resId);

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, anchor, String.valueOf(resId));
        ActivityCompat.startActivity(activity, intent, activityOptions.toBundle());
    }

    public static void launchActivity(Activity activity, String url, View anchor) {
        Intent intent = new Intent(activity, PhotoShowActivity.class);
        intent.putExtra(KEY_URL, url);

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, anchor, url);
        ActivityCompat.startActivity(activity, intent, activityOptions.toBundle());
    }

    public static void launchActivity(Activity activity, ImageBean imageBean, View anchor) {
        Intent intent = new Intent(activity, PhotoShowActivity.class);
        intent.putExtra(KEY_PHOTO_BEAN, imageBean);

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, anchor, imageBean.getPath());
        ActivityCompat.startActivity(activity, intent, activityOptions.toBundle());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photo_show;
    }

    @Override
    public void setStatusBar() {
        SystemBarUtil.fullScreen_immersive(getActivity(), false, false, true, true, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportPostponeEnterTransition();
        bitmap = getIntent().getParcelableExtra(KEY_BITMAP);
        resId = getIntent().getIntExtra(KEY_RES_ID, -1);
        mUrl = getIntent().getStringExtra(KEY_URL);
        mImageBean = getIntent().getParcelableExtra(KEY_PHOTO_BEAN);
        initView();
    }

    private void initView() {
        photoView = findViewById(R.id.activity_photo_show_photoView);

        if (bitmap != null) {
            ViewCompat.setTransitionName(photoView, String.valueOf(bitmap.hashCode()));
        } else if (resId != -1) {
            ViewCompat.setTransitionName(photoView, String.valueOf(resId));
        } else if (!TextUtils.isEmpty(mUrl)) {
            ViewCompat.setTransitionName(photoView, mUrl);
        } else {
            ViewCompat.setTransitionName(photoView, mImageBean.getPath());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addTransitionListener();
        }

        Object obj = null;
        if (bitmap != null) {
            obj = bitmap;
        } else if (resId != -1) {
            obj = resId;
        } else if (!TextUtils.isEmpty(mUrl)) {
            obj = mUrl;
        } else if (mImageBean != null) {
            obj = mImageBean.getImageUri();
        }

        GlideApp.with(getActivity())
                .load(obj)
                .placeholder(R.drawable.photo_loading)
                .error(R.drawable.photo_error)
                .diskCacheStrategy(DiskCacheStrategy.NONE)//NONE 跳过硬盘缓存
                .skipMemoryCache(true)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        photoView.setImageDrawable(resource);
                        if (resource instanceof GifDrawable) {
                            GifDrawable gifDrawable = (GifDrawable) resource;
                            if (!gifDrawable.isRunning()) {
                                gifDrawable.start();
                            }
                        }

                        supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void addTransitionListener() {
        android.transition.Transition transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            transition.addListener(new android.transition.Transition.TransitionListener() {
                @Override
                public void onTransitionStart(android.transition.Transition transition) {

                }

                @Override
                public void onTransitionEnd(android.transition.Transition transition) {
                    transition.removeListener(this);

                    if (photoView.getDrawable() instanceof GifDrawable) {
                        photoView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GifDrawable drawable = (GifDrawable) photoView.getDrawable();
                                if (drawable.isRunning()) {
                                    drawable.stop();
                                } else {
                                    drawable.start();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onTransitionCancel(android.transition.Transition transition) {

                }

                @Override
                public void onTransitionPause(android.transition.Transition transition) {

                }

                @Override
                public void onTransitionResume(android.transition.Transition transition) {

                }
            });
        }
    }
}
