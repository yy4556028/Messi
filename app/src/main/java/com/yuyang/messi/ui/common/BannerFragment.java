package com.yuyang.messi.ui.common;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.ui.base.BaseFragment;
import com.yuyang.lib_base.ui.view.BannerViewPager;
import com.yuyang.messi.R;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.messi.view.Indicator.flyco.indicator.RoundCornerIndicator;
import com.yuyang.messi.view.PageTransFormer.ScalePageTransformer;

import java.util.List;

public class BannerFragment extends BaseFragment {

    private int width = 750;
    private int height = 310;

    private BannerViewPager viewPager;
    private ViewPagerAdapter bannerAdapter;
    private RoundCornerIndicator indicator;

    private List<Drawable> drawableList;
    private List<String> urlList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_banner;
    }

    @Override
    protected void doOnViewCreated() {
        initViews();
    }

    private void initViews() {
        viewPager = $(R.id.fragment_banner_viewpager);
        indicator = $(R.id.fragment_banner_indicator);

        viewPager.setPageMargin(0);
        viewPager.setPageTransformer(false, new ScalePageTransformer(0.7f));

        viewPager.setAdapter(bannerAdapter = new ViewPagerAdapter());
        indicator.setViewPager(viewPager);

        if (bannerAdapter.getCount() <= 1) {
            indicator.setVisibility(View.INVISIBLE);
        } else {
            indicator.setVisibility(View.VISIBLE);
            viewPager.getLayoutParams().height = CommonUtil.getScreenWidth() * height / width;
            viewPager.requestLayout();
        }
    }

    public void setRatio(int width, int height) {
        this.width = width;
        this.height = height;
        if (viewPager != null) {
            viewPager.getLayoutParams().height = CommonUtil.getScreenWidth() * height / width;
            viewPager.requestLayout();
        }
    }

    public void updateDrawable(List<Drawable> drawableList) {
        this.urlList = null;
        this.drawableList = drawableList;
        if (viewPager == null) return;
        viewPager.setAdapter(bannerAdapter = new ViewPagerAdapter());
        indicator.setViewPager(viewPager);

        if (bannerAdapter.getCount() <= 1) {
            indicator.setVisibility(View.INVISIBLE);
        } else {
            indicator.setVisibility(View.VISIBLE);
            viewPager.getLayoutParams().height = CommonUtil.getScreenWidth() * height / width;
            viewPager.requestLayout();
        }
    }

    public void updateUrl(List<String> urlList) {
        this.drawableList = null;
        this.urlList = urlList;
        if (viewPager == null) return;
        viewPager.setAdapter(bannerAdapter = new ViewPagerAdapter());
        indicator.setViewPager(viewPager);

        if (bannerAdapter.getCount() <= 1) {
            indicator.setVisibility(View.INVISIBLE);
        } else {
            indicator.setVisibility(View.VISIBLE);
            viewPager.getLayoutParams().height = CommonUtil.getScreenWidth() * height / width;
            viewPager.requestLayout();
        }
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(imageView);

            if (drawableList != null) {
                GlideApp.with(container.getContext())
                        .load(drawableList.get(position))
                        .placeholder(R.drawable.photo_loading)
                        .error(R.drawable.photo_error)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) //不缓存到SD卡
                        .skipMemoryCache(true)
                        .into(imageView);
            } else if (urlList != null) {
                GlideApp.with(container.getContext())
                        .load(urlList.get(position))
                        .placeholder(R.drawable.photo_loading)
                        .error(R.drawable.photo_error)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .into(imageView);
            }
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public int getCount() {
            if (drawableList != null) return drawableList.size();
            else if (urlList != null) return urlList.size();
            return 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}
