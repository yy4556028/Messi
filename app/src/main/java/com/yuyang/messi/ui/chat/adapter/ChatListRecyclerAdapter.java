package com.yuyang.messi.ui.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.messi.R;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.im.v2.LCIMConversation;

public class ChatListRecyclerAdapter extends RecyclerView.Adapter<ChatListRecyclerAdapter.MyHolder> {

    private LayoutInflater mInflater;
    private Context context;

    private List<LCIMConversation> beanList;

    public ChatListRecyclerAdapter(Context context) {
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(List<LCIMConversation> data) {
        if (beanList == null)
            beanList = new ArrayList<>();
        beanList.clear();
        beanList.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<LCIMConversation> data) {
        if (beanList == null)
            beanList = new ArrayList<>();
        beanList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new MyHolder(mInflater.inflate(R.layout.fragment_douban_movie_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {

//        DouabnMovieBean movie = beanList.get(position);
//
//        Glide.with(context)
//                .load(movie.getImages().getMedium())
//                .into(holder.imageView);
//        holder.title.setText(movie.getTitle());
//        holder.directors.setText(String.format(context.getString(R.string.fragment_douban_movie_directors), movie.getDirectors()));
//        holder.casts.setText(String.format(context.getString(R.string.fragment_douban_movie_casts), movie.getCasts()));
//        holder.year.setText(String.format(context.getString(R.string.fragment_douban_movie_year), movie.getYear()));
//        holder.genres.setText(String.format(context.getString(R.string.fragment_douban_movie_genres), movie.getGenres()));
//        holder.rating.setText(String.format(context.getString(R.string.fragment_douban_movie_rating), String.valueOf(movie.getRating().getAverage())));
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                ToastUtil.showToast(beanList.get(position).getTitle());
////                Intent intent = new Intent(mContext, BookDetailActivity.class);
////                intent.putExtra("url", mBeans.get(position - 1).getUrl());
////                mContext.startActivity(intent);
//            }
//        });
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return beanList == null ? 0 : beanList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        private TextView title;
        private TextView directors;
        private TextView casts;
        private TextView year;
        private TextView genres;
        private TextView rating;

        public MyHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.fragment_douban_movie_recycler_item_image);

            title = (TextView) itemView.findViewById(R.id.fragment_douban_movie_recycler_item_title);
            directors = (TextView) itemView.findViewById(R.id.fragment_douban_movie_recycler_item_directors);
            casts = (TextView) itemView.findViewById(R.id.fragment_douban_movie_recycler_item_casts);
            year = (TextView) itemView.findViewById(R.id.fragment_douban_movie_recycler_item_year);
            genres = (TextView) itemView.findViewById(R.id.fragment_douban_movie_recycler_item_genres);
            rating = (TextView) itemView.findViewById(R.id.fragment_douban_movie_recycler_item_rating);
        }
    }

}



