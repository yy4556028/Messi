package com.yuyang.messi.ui.home.taobao;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.myglide.ProgressInterceptor;
import com.yuyang.lib_base.myglide.ProgressListener;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.common.photo.PhotoShowActivity;

import java.util.List;

public class RecyclerPagerAdapter extends RecyclerView.Adapter<RecyclerPagerAdapter.ViewHolder> {

    private final List<String> urlList;

    public RecyclerPagerAdapter(List<String> urlList) {
        this.urlList = urlList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = new CardView(parent.getContext());
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String imgUrl = urlList.get(position % urlList.size());

        ProgressInterceptor.addListener(imgUrl, new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                Log.d("ProgressInterceptor", "onProgress: " + progress);
            }
        });

        GlideApp.with(holder.imageView.getContext())
                .load(imgUrl)
                .placeholder(R.drawable.photo_loading)
                .error(R.drawable.photo_error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        holder.imageView.setImageDrawable(resource);
                        ProgressInterceptor.removeListener(imgUrl);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            CardView cardView = (CardView) itemView;
            imageView = new ImageView(itemView.getContext());
            cardView.addView(imageView);

            itemView.setLayoutParams(new RecyclerView.LayoutParams(
                    (CommonUtil.getScreenWidth() - PixelUtils.dp2px(40)), RecyclerView.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PhotoShowActivity.launchActivity((Activity) view.getContext(), urlList.get(getAdapterPosition() % urlList.size()), view);
                }
            });
        }
    }
}
