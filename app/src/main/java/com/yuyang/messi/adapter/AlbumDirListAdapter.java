package com.yuyang.messi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.PhotoDirectory;

import java.util.List;

public class AlbumDirListAdapter extends BaseAdapter {

    private Context context;
    private List<PhotoDirectory> directories;

    public AlbumDirListAdapter(Context context, List<PhotoDirectory> directories) {
        this.context = context;
        this.directories = directories;
    }

    @Override
    public int getCount() {
        return directories.size();
    }


    @Override
    public PhotoDirectory getItem(int position) {
        return directories.get(position);
    }


    @Override
    public long getItemId(int position) {
        return directories.get(position).hashCode();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());
            convertView = mLayoutInflater.inflate(R.layout.activity_album_dir_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bindData(directories.get(position));

        return convertView;
    }

    private class ViewHolder {

        public ImageView imageView;
        public TextView titleText;
        public TextView countText;

        public ViewHolder(View rootView) {
            imageView = rootView.findViewById(R.id.activity_album_dir_list_item_image);
            titleText = rootView.findViewById(R.id.activity_album_dir_list_item_title);
            countText = rootView.findViewById(R.id.activity_album_dir_list_item_count);
        }

        public void bindData(PhotoDirectory directory) {
            if (directory.getCoverImageBean() == null) {
                imageView.setImageDrawable(null);
            } else if (directory.getCoverImageBean().getImageName().toLowerCase().endsWith("gif")) {
                GlideApp.with(context)
                        .load(directory.getCoverImageBean().getImageUri())
                        .placeholder(R.drawable.photo_loading)
                        .error(R.drawable.photo_error)
                        .dontTransform()
                        .override(800, 800)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) //不缓存到SD卡
                        .skipMemoryCache(true)
                        .into(imageView);
            } else {
                GlideApp.with(context)
                        .load(directory.getCoverImageBean().getImageUri())
                        .placeholder(R.drawable.photo_loading)
                        .error(R.drawable.photo_error)
                        .thumbnail(0.1f)
                        .dontAnimate()
                        .dontTransform()
                        .override(800, 800)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) //不缓存到SD卡
                        .skipMemoryCache(true)
                        .into(imageView);
            }
            titleText.setText(directory.getName());
            countText.setText(directory.getImageBeans().size() + "张");
        }
    }

}
