package com.yuyang.messi.ui.category.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.bean.ImageBean;
import com.yuyang.messi.ui.common.photo.PhotoShowActivity;

import java.util.List;

public class ExamRecyclerAdapter extends RecyclerView.Adapter<ExamRecyclerAdapter.ViewHolder> {

    private List<ImageBean> imgList;

    public ExamRecyclerAdapter(List<ImageBean> imgList) {
        this.imgList = imgList;
    }

    public void updateData(List<ImageBean> imgList) {
        this.imgList = imgList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(new ImageView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GlideApp.with(holder.imageView.getContext())
                .load(imgList.get(position).getImageUri())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imgList == null ? 0 : imgList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(PixelUtils.dp2px(80), PixelUtils.dp2px(150));
            layoutParams.setMarginEnd(PixelUtils.dp2px(8));
            itemView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PhotoShowActivity.launchActivity((Activity) view.getContext(), imgList.get(getAdapterPosition()), view);
                }
            });
        }
    }
}
