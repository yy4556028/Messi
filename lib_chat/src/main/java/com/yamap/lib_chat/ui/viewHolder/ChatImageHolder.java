package com.yamap.lib_chat.ui.viewHolder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yamap.lib_chat.R;
import com.yamap.lib_chat.data.MessageBean;

import java.io.File;

public class ChatImageHolder extends ChatBaseHolder {

    protected ImageView imageView;//
    protected ImageView maskView;

    public ChatImageHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft);
    }

    @Override
    public void initView() {
        super.initView();
        if (isLeft) {
            conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_left_image_layout, null));
            imageView = (ImageView) itemView.findViewById(R.id.chat_item_image_view);
            maskView = (ImageView) itemView.findViewById(R.id.chat_item_mask_view);
        } else {
            conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_right_image_layout, null));
            imageView = (ImageView) itemView.findViewById(R.id.chat_item_image_view);
            maskView = (ImageView) itemView.findViewById(R.id.chat_item_mask_view);
        }
    }

    @Override
    public void bindData(Object o) {
        super.bindData(o);
        imageView.setImageResource(0);
        MessageBean message = (MessageBean) o;
        if (message.getMsgType() == MessageBean.MessageType.IMAGE) {
            String localFilePath = message.getLocalFilePath();

            if (!TextUtils.isEmpty(localFilePath)) {
                Glide.with(getContext())
                        .load(new File(localFilePath))
                        .thumbnail(0.5f)
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                imageView.setImageDrawable(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            } else if (!TextUtils.isEmpty(message.getFileUrl())) {
                Glide.with(getContext())
                        .load(message.getFileUrl())
                        .thumbnail(0.5f)
                        .into(imageView);
            } else {
                imageView.setImageResource(0);
            }
        }
    }
}
