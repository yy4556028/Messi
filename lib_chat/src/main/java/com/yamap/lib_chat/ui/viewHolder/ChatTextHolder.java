package com.yamap.lib_chat.ui.viewHolder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yamap.lib_chat.R;
import com.yamap.lib_chat.data.MessageBean;

/**
 * Created by wli on 15/9/17.
 * 聊天页面中的文本 item 对应的 holder
 */
public class ChatTextHolder extends ChatBaseHolder {

    protected TextView contentView;

    public ChatTextHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft);
    }

    @Override
    public void initView() {
        super.initView();
        if (isLeft) {
            conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_left_text_layout, null));
            contentView = (TextView) itemView.findViewById(R.id.chat_left_text_tv_content);
        } else {
            conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_right_text_layout, null));
            contentView = (TextView) itemView.findViewById(R.id.chat_right_text_tv_content);
        }
    }

    @Override
    public void bindData(Object o) {
        super.bindData(o);
        MessageBean message = (MessageBean) o;
        if (message instanceof MessageBean) {
            MessageBean textMessage = (MessageBean) message;
            contentView.setText(textMessage.getContent());
        }
    }
}
