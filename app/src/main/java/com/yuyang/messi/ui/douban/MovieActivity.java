package com.yuyang.messi.ui.douban;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.palette.graphics.Palette.Swatch;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.liulishuo.magicprogresswidget.MagicProgressBar;
import com.yuyang.lib_base.net.common.RxUtils;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.viewmodel.LiveProcess;
import com.yuyang.lib_base.utils.BitmapUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.douban.DoubanMovieBean;
import com.yuyang.messi.net.retrofit.CommonRequest;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.ColorUtil;
import com.yuyang.messi.utils.ColorUtil.PaletteListener;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class MovieActivity extends AppBaseActivity {

    private static final String KEY_MOVIE_BEAN = "movie_bean";
    private DoubanMovieBean movieBean;

    private ImageView iconImage;

    public static void launchActivity(Activity activity, ImageView iconImage, DoubanMovieBean movieBean) {

        Intent intent = new Intent(activity, MovieActivity.class);
        intent.putExtra(KEY_MOVIE_BEAN, movieBean);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, iconImage, movieBean.getImages().getMedium());
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_movie;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieBean = getIntent().getParcelableExtra(KEY_MOVIE_BEAN);
        initView();
        loadData();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(movieBean.getTitle());

        iconImage = findViewById(R.id.activity_movie_iv);
        ViewCompat.setTransitionName(iconImage, movieBean.getImages().getMedium());
        Glide.with(this)
            .load(movieBean.getImages().getLarge())
            .into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    iconImage.setImageDrawable(resource);
                    ColorUtil.getPalette(BitmapUtil.drawableToBitmap0(resource), new PaletteListener() {
                        @Override
                        public void getDominant(Swatch dominantSwatch) {
                            getWindow().getDecorView().setBackgroundColor(dominantSwatch.getRgb());
                        }

                        @Override
                        public void getVibrant(Swatch vibrant, Swatch vibrantDark, Swatch vibrantLight) {
//                            getWindow().getDecorView().setBackgroundColor(vibrantDark.getRgb());
                        }

                        @Override
                        public void getMuted(Swatch muted, Swatch mutedDark, Swatch mutedLight) {
                        }
                    });
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
    }

    private void loadData() {
        showLoading(new LiveProcess(true));
        CommonRequest.getInstance().doubanMovie(movieBean.getId())
            .compose(RxUtils.io2main())
            .doFinally(() -> {
                showLoading(new LiveProcess(false));
            })
            .subscribe(new Observer<DoubanMovieBean>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(DoubanMovieBean responseData) {
                    ((TextView) findViewById(R.id.activity_movie_tvTitle)).setText(responseData.getTitle());
                    if (TextUtils.equals(responseData.getTitle(), responseData.getOrigin_title())) {
                        ((TextView) findViewById(R.id.activity_movie_tvTitleEng)).setText(
                            String.format("%s(%s)", responseData.getOrigin_title(), responseData.getTitle()));
                    } else {
                        ((TextView) findViewById(R.id.activity_movie_tvTitleEng)).setText(
                            String.format("(%s)", responseData.getYear()));
                    }
                    ((TextView) findViewById(R.id.activity_movie_tvTag)).setText(
                        list2Str(responseData.getCountries()) + "/ " +
                            list2Str(responseData.getGenres()) + "/ 上映时间：" +
                            responseData.getMainland_pubdate() + "(中国大陆) / 片长：" +
                            responseData.getDurations().get(0));
                    ((TextView) findViewById(R.id.activity_movie_tvRating)).setText(responseData.getRating().getAverage() + "");

                    MaterialRatingBar ratingBar = findViewById(R.id.activity_movie_ratingBar);
                    ratingBar.setRating(responseData.getRating().getAverage() / 2);

                    MaterialRatingBar ratingBar5 = findViewById(R.id.activity_movie_ratingBar5);
                    MaterialRatingBar ratingBar4 = findViewById(R.id.activity_movie_ratingBar4);
                    MaterialRatingBar ratingBar3 = findViewById(R.id.activity_movie_ratingBar3);
                    MaterialRatingBar ratingBar2 = findViewById(R.id.activity_movie_ratingBar2);
                    MaterialRatingBar ratingBar1 = findViewById(R.id.activity_movie_ratingBar1);
                    MagicProgressBar ratingProgress5 = findViewById(R.id.activity_movie_ratingProgress5);
                    MagicProgressBar ratingProgress4 = findViewById(R.id.activity_movie_ratingProgress4);
                    MagicProgressBar ratingProgress3 = findViewById(R.id.activity_movie_ratingProgress3);
                    MagicProgressBar ratingProgress2 = findViewById(R.id.activity_movie_ratingProgress2);
                    MagicProgressBar ratingProgress1 = findViewById(R.id.activity_movie_ratingProgress1);
                    ratingBar5.setRating(responseData.getRating().getDetails().getRating5());
                    ratingBar4.setRating(responseData.getRating().getDetails().getRating4());
                    ratingBar3.setRating(responseData.getRating().getDetails().getRating3());
                    ratingBar2.setRating(responseData.getRating().getDetails().getRating2());
                    ratingBar1.setRating(responseData.getRating().getDetails().getRating1());
                    float total = responseData.getRating().getDetails().getRating5() +
                        responseData.getRating().getDetails().getRating4() +
                        responseData.getRating().getDetails().getRating3() +
                        responseData.getRating().getDetails().getRating2() +
                        responseData.getRating().getDetails().getRating1();
                    ratingProgress5.setSmoothPercent(responseData.getRating().getDetails().getRating5() / total);
                    ratingProgress4.setSmoothPercent(responseData.getRating().getDetails().getRating4() / total);
                    ratingProgress3.setSmoothPercent(responseData.getRating().getDetails().getRating3() / total);
                    ratingProgress2.setSmoothPercent(responseData.getRating().getDetails().getRating2() / total);
                    ratingProgress1.setSmoothPercent(responseData.getRating().getDetails().getRating1() / total);


                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.showToast(e.getMessage());
                }

                @Override
                public void onComplete() {

                }
            });
    }

    private String list2Str(List<String> list) {
        if (list == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append(" ");
        }
        return sb.toString();
    }
}
