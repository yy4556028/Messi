package com.yuyang.messi.ui.douban.adapter;

import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.douban.DoubanMusicBean;
import java.util.List;

public class DoubanMusicAdapter extends BaseQuickAdapter<DoubanMusicBean, BaseViewHolder> {

    public DoubanMusicAdapter(@Nullable List<DoubanMusicBean> entityList) {
        super(R.layout.fragment_douban_music_recycler_item, entityList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, DoubanMusicBean music) {
        ImageView imageView = helper.getView(R.id.fragment_douban_music_recycler_item_image);
        Glide.with(mContext)
            .load(music.getImage())
            .into(imageView);

        helper.setText(R.id.fragment_douban_music_recycler_item_title, music.getTitle())
            .setText(R.id.fragment_douban_music_recycler_item_singer, "歌手:" + (music.getAuthor().size() > 0 ? music.getAuthor().get(0).getName() : ""))
            .setText(R.id.fragment_douban_music_recycler_item_date, String.format("发行时间:%s", music.getAttrs().getPubdate().get(0)))
            .setText(R.id.fragment_douban_music_recycler_item_publisher, String.format("发行商:%s", music.getAttrs().getPublisher().get(0)))
            .setText(R.id.fragment_douban_music_recycler_item_rating, String.format("%sd 人评分: %sf", music.getRating().getNumRaters(), music.getRating().getAverage()));
    }
}



