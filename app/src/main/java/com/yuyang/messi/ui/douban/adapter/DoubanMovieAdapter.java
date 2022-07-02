package com.yuyang.messi.ui.douban.adapter;

import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.douban.DoubanMovieBean;
import java.util.List;

public class DoubanMovieAdapter extends BaseQuickAdapter<DoubanMovieBean, BaseViewHolder> {

    public DoubanMovieAdapter(@Nullable List<DoubanMovieBean> entityList) {
        super(R.layout.fragment_douban_movie_recycler_item, entityList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, DoubanMovieBean movie) {
        ImageView imageView = helper.getView(R.id.fragment_douban_movie_recycler_item_image);
        Glide.with(mContext)
            .load(movie.getImages().getMedium())
            .into(imageView);

        helper.setText(R.id.fragment_douban_movie_recycler_item_title, movie.getTitle())
            .setText(R.id.fragment_douban_movie_recycler_item_directors, String.format("导演:%s", movie.getDirectors()))
            .setText(R.id.fragment_douban_movie_recycler_item_casts, String.format("主演:%s", movie.getCasts()))
            .setText(R.id.fragment_douban_movie_recycler_item_year, String.format("年份:%s", movie.getYear()))
            .setText(R.id.fragment_douban_movie_recycler_item_genres, String.format("类型:%s", movie.getGenres()))
            .setText(R.id.fragment_douban_movie_recycler_item_rating, String.format("评分:%s", String.valueOf(movie.getRating().getAverage())));
    }

}



