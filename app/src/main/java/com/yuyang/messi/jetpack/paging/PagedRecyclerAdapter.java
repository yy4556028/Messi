package com.yuyang.messi.jetpack.paging;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.kotlinui.beauty.BeautyBean;

public class PagedRecyclerAdapter extends PagedListAdapter<BeautyBean, PagedRecyclerAdapter.MyHolder> {

    private int itemWidth;

    protected PagedRecyclerAdapter(int itemWidth) {
        super(showDiffCallBack);
        this.itemWidth = itemWidth;
    }

    static DiffUtil.ItemCallback<BeautyBean> showDiffCallBack = new DiffUtil.ItemCallback<BeautyBean>() {

        @Override
        public boolean areItemsTheSame(@NonNull BeautyBean oldItem, @NonNull BeautyBean newItem) {
            return (oldItem).getThumbURL().equals((newItem).getThumbURL());
        }

        @Override
        public boolean areContentsTheSame(@NonNull BeautyBean oldItem, @NonNull BeautyBean newItem) {
            return (oldItem).getThumbURL().equals((newItem).getThumbURL());

        }
    };

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_beauty_recyclerview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        BeautyBean beautyBean = getItem(position);

        if (TextUtils.isEmpty(beautyBean.getThumbURL())) {
            holder.itemView.setVisibility(View.GONE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }

        if (beautyBean.getWidth() != 0) {
            holder.imageView.getLayoutParams().height = (itemWidth * beautyBean.getHeight() / beautyBean.getWidth());
        } else {
            holder.imageView.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
        }

        GlideApp.with(holder.itemView.getContext())
                .load(beautyBean.getThumbURL())
                .diskCacheStrategy(DiskCacheStrategy.NONE)//NONE 跳过硬盘缓存
                .skipMemoryCache(true)
//                .crossFade()//淡入显示 默认300
                .into(holder.imageView);

        if (!TextUtils.isEmpty(beautyBean.getFromPageTitleEnc())) {
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(beautyBean.getFromPageTitleEnc());
        } else {
            holder.textView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onViewRecycled(@NonNull PagedRecyclerAdapter.MyHolder holder) {
        super.onViewRecycled(holder);
        GlideApp.with(holder.itemView.getContext()).clear(holder.imageView);
        holder.imageView.setImageDrawable(null);
        holder.textView.setVisibility(View.INVISIBLE);
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public MyHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.activity_beauty_recyclerView_item_image);
            textView = itemView.findViewById(R.id.activity_beauty_recyclerView_item_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final int position = getAdapterPosition();

                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, position, getItem(position));
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    ToastUtil.showToast(String.format("图片来源：%s", getItem(position).getFromURLHost()));
                    return true;
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, BeautyBean beautyBean);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
