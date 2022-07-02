package com.yuyang.messi.ui.media;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette.Swatch;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.header.HeaderRightBean;
import com.yuyang.lib_base.utils.BitmapUtil;
import com.yuyang.lib_base.utils.statusbar.MyStatusBarUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.ImageBean;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.ColorUtil;
import com.yuyang.messi.utils.ColorUtil.PaletteListener;
import com.yuyang.messi.view.Crop.CropImageLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者: yuyang
 * 创建日期: 2015-08-13
 * 创建时间: 15:00
 * ClipActivity: 裁剪图片
 *
 * @author yuyang
 * @version 1.0
 */
public class AlbumCropActivity extends AppBaseActivity {

    private static final String KEY_BEAN = "key_bean";

    private HeaderLayout headerLayout;
    private CropImageLayout cropImageLyt;

    private ImageBean imageBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_album_crop;
    }

    public static void launchActivity(Activity activity, ImageBean imageBean) {
        Intent intent = new Intent(activity, AlbumCropActivity.class);
        intent.putExtra(KEY_BEAN, imageBean);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        imageBean = intent.getParcelableExtra(KEY_BEAN);
        initView();
    }

    private void initView() {
        headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        List<HeaderRightBean> rightBeanList = new ArrayList<>();
        rightBeanList.add(new HeaderRightBean("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = cropImageLyt.crop();
                cropImageLyt.setImageViewBitmap(bitmap);
            }
        }));
       headerLayout.setRight(rightBeanList);

        cropImageLyt = findViewById(R.id.activity_clip_clipLyt);
//		cropImageLyt.setImageViewPath(photoPath);

        GlideApp.with(this)
                .load(imageBean.getImageUri())
                .thumbnail(0.1f)
                .dontAnimate()
                .dontTransform()
                .override(800, 800)
                .diskCacheStrategy(DiskCacheStrategy.NONE) //不缓存到SD卡
                .skipMemoryCache(true)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap bitmap = BitmapUtil.drawableToBitmap0(resource);
                        cropImageLyt.setImageViewBitmap(bitmap);

                        ColorUtil.getPalette(bitmap, new PaletteListener() {
                            @Override
                            public void getVibrant(Swatch vibrant, Swatch vibrantDark, Swatch vibrantLight) {
                                super.getVibrant(vibrant, vibrantDark, vibrantLight);
                                headerLayout.setBackgroundColor(vibrant.getRgb());
                                headerLayout.setImageAndTextColor(ColorUtil.getReverseColor(vibrant.getRgb()));
                                MyStatusBarUtil.setStatusBarColor(getActivity(), vibrant.getRgb());
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
}