package com.yuyang.messi.ui.douban.adapter;

import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.douban.DoubanBookBean;
import java.util.List;

public class DoubanBookAdapter extends BaseQuickAdapter<DoubanBookBean, BaseViewHolder> {


    public DoubanBookAdapter(@Nullable List<DoubanBookBean> entityList) {
        super(R.layout.fragment_douban_book_recycler_item, entityList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, DoubanBookBean book) {
        ImageView imageView = helper.getView(R.id.fragment_douban_book_recycler_item_image);
        Glide.with(mContext)
            .load(book.getImage())
            .into(imageView);
        Glide.with(mContext)
                .load(new GlideUrl(""))
                .into(imageView);

        helper.setText(R.id.fragment_douban_book_recycler_item_title, book.getTitle())
            .setText(R.id.fragment_douban_book_recycler_item_price, String.format("¥%s", book.getPrice()))
            .setText(R.id.fragment_douban_book_recycler_item_author, String.format("作者:%s", book.getAuthor()))
            .setText(R.id.fragment_douban_book_recycler_item_date, String.format("出版日期:%s", book.getPubdate()))
            .setText(R.id.fragment_douban_book_recycler_item_publisher, String.format("出版社:%s", book.getPublisher()))
            .setText(R.id.fragment_douban_book_recycler_item_title, book.getTitle())
            .setText(R.id.fragment_douban_book_recycler_item_rating,
                String.format("%1$s 人评分: %2$s",
                    String.valueOf(book.getRating().getNumRaters()),
                    String.valueOf(book.getRating().getAverage())));
    }
}



