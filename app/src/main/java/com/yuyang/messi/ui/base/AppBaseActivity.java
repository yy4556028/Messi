package com.yuyang.messi.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.yuyang.lib_base.ui.base.BaseActivity;
import com.yuyang.messi.widget.watermark.WaterMarkManager;

public abstract class AppBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        initTransition();
        setContentView(getLayoutId());
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (layoutResID != 0) {
            super.setContentView(layoutResID);
        }
//        addWatermark();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }

    public void addWatermark() {

        final ViewGroup content = findViewById(android.R.id.content);
        if (content != null) {
            content.addView(WaterMarkManager.getView(this));
        }
    }

    @Deprecated
    protected <T extends View> T $(int id) {
        return (T) super.findViewById(id);
    }

    @LayoutRes
    protected int getLayoutId() {
        return 0;
    }

    protected void initTransition() {
    }

    public void startActivity_anim(Intent intent) {
        ActivityCompat.startActivity(this, intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    }

    public void startActivityForResult_anim(Intent intent, int requestCode) {
        ActivityCompat.startActivityForResult(this, intent, requestCode, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    }

    /**
     * 相当于 overridePendingTransition
     */
    public void startActivity_CustomAnim(Intent intent, int enterResId, int exitResId) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            enterResId,
            exitResId);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void startActivity_ScaleUp(Intent intent, View view) {
        //让新的Activity从一个小的范围扩大到全屏
        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(
            view, //The View that the new activity is animating from
            view.getWidth() / 2, view.getHeight() / 2, //拉伸开始的坐标
            0, 0);//拉伸开始的区域大小，这里用（0，0）表示从无到全屏
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }
}
