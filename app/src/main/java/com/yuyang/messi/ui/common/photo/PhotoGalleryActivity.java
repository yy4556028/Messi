package com.yuyang.messi.ui.common.photo;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.palette.graphics.Palette.Swatch;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.appbar.AppBarLayout;
import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.header.HeaderRightBean;
import com.yuyang.lib_base.utils.BitmapUtil;
import com.yuyang.lib_base.utils.SystemBarUtil;
import com.yuyang.lib_base.utils.statusbar.MyStatusBarUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.ImageBean;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.ui.media.AlbumCropActivity;
import com.yuyang.messi.utils.ColorUtil;
import com.yuyang.messi.utils.ColorUtil.PaletteListener;
import com.yuyang.messi.widget.ViewPagerForPhotoView;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryActivity extends AppBaseActivity {

    private final static String KEY_LIST = "key_list";
    private final static String KEY_INDEX = "key_index";

    private AppBarLayout appBarLayout;
    private HeaderLayout headerLayout;

    private ViewPagerForPhotoView viewPager;

    private List<ImageBean> imageBeanList;
    private List<Integer> colorList;

    private boolean isBarShow = true;

    public static void launchActivity(Activity activity, List<ImageBean> photoPathList, int index) {
        Intent intent = new Intent(activity, PhotoGalleryActivity.class);
        intent.putParcelableArrayListExtra(KEY_LIST, (ArrayList<? extends Parcelable>) photoPathList);
        intent.putExtra(KEY_INDEX, index);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_album_gallery;
    }

    @Override
    public void setStatusBar() {
        SystemBarUtil.fullScreen_immersive(getActivity(), true, false, true, false, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageBeanList = getIntent().getParcelableArrayListExtra(KEY_LIST);
        colorList = new ArrayList<>();

        for (int i = 0; i < imageBeanList.size(); i++) {
            colorList.add(ContextCompat.getColor(getActivity(), R.color.white));
        }

        initViews();
        initEvents();
    }

    private void initViews() {
        appBarLayout = findViewById(R.id.common_header_layout_appBarLayout);
        headerLayout = findViewById(R.id.headerLayout);
        headerLayout.setPadding(0, MyStatusBarUtil.getStatusBarHeight(), 0, 0);
        headerLayout.showLeftBackButton();
        int index = getIntent().getIntExtra(KEY_INDEX, 0);
        headerLayout.showTitle(index + 1 + "/" + imageBeanList.size());

        List<HeaderRightBean> rightBeanList = new ArrayList<>();
        rightBeanList.add(new HeaderRightBean(R.drawable.crop, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumCropActivity.launchActivity(getActivity(), imageBeanList.get(viewPager.getCurrentItem()));
            }
        }));
        headerLayout.setRight(rightBeanList);

        viewPager = findViewById(R.id.activity_album_gallery_viewPager);

        viewPager.setAdapter(new GalleryPagerAdapter());
        viewPager.setCurrentItem(index);
    }

    private void initEvents() {
        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                if (insets.isVisible(WindowInsetsCompat.Type.navigationBars())) {
                    ObjectAnimator.ofFloat(appBarLayout, "translationY", -headerLayout.getMeasuredHeight(), 0).start();
                } else {
                    ObjectAnimator.ofFloat(appBarLayout, "translationY", 0, -headerLayout.getMeasuredHeight()).start();
                }
                return insets;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                headerLayout.showTitle(position + 1 + "/" + imageBeanList.size());
                if (colorList.get(position) != 0) {
                    headerLayout.setBackgroundColor(colorList.get(position));
                    headerLayout.setImageAndTextColor(ColorUtil.getReverseColor(colorList.get(position)));
                } else {
                    headerLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                    headerLayout.setImageAndTextColor(ContextCompat.getColor(getActivity(), R.color.theme));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 相册适配器
     */
    private class GalleryPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageBeanList == null ? 0 : imageBeanList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            PhotoView photoView = view.findViewById(R.id.activity_album_gallery_pager_item_photoView);
            photoView.setScale(1.0f);//让图片在滑动过程中恢复回缩放操作前原图大小
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View itemView = View.inflate(getActivity(), R.layout.activity_album_gallery_pager_item, null);

            final PhotoView photoView = itemView.findViewById(R.id.activity_album_gallery_pager_item_photoView);

            final ImageBean imageBean = imageBeanList.get(position);

            photoView.setOnViewTapListener(new OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {

                    WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(view);
                    if (controller == null) return;
                    if (isBarShow) {
                        SystemBarUtil.hideSystemBar(getActivity(), WindowInsetsCompat.Type.systemBars());
                    } else {
                        SystemBarUtil.showSystemBar(getActivity(), WindowInsetsCompat.Type.systemBars());
                    }
                    isBarShow = !isBarShow;
                }
            });

            if (imageBean.getImageName().toLowerCase().endsWith("gif")) {
                GlideApp.with(getActivity())
                        .load(imageBean.getImageUri())
                        .placeholder(R.drawable.photo_loading)
                        .error(R.drawable.photo_error)
                        .dontTransform()
                        .override(800, 800)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) //不缓存到SD卡
                        .skipMemoryCache(true)
                        .into(photoView);
            } else {
                GlideApp.with(getActivity())
                        .load(imageBean.getImageUri())
                        .placeholder(R.drawable.photo_loading)
                        .error(R.drawable.photo_error)
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
                                photoView.setImageDrawable(BitmapUtil.bitmapToDrawable(bitmap));

                                ColorUtil.getPalette(bitmap, new PaletteListener() {
                                    @Override
                                    public void getVibrant(Swatch vibrant, Swatch vibrantDark, Swatch vibrantLight) {
                                        super.getVibrant(vibrant, vibrantDark, vibrantLight);
                                        if (vibrant != null) {
                                            colorList.set(position, vibrant.getRgb());
                                        }

                                        if (position == viewPager.getCurrentItem()) {
                                            headerLayout.setBackgroundColor(colorList.get(position));
                                            headerLayout.setImageAndTextColor(ColorUtil.getReverseColor(colorList.get(position)));
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
            GlideApp.with(getActivity()).clear((View) object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }
}