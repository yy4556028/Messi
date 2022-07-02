package com.yamap.lib_chat.ui.viewHolder;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yamap.lib_chat.Constants;
import com.yamap.lib_chat.R;
import com.yamap.lib_chat.cache.LocalCacheUtils;
import com.yamap.lib_chat.data.MessageBean;
import com.yamap.lib_chat.utils.ChatPathUtil;
import com.yamap.lib_chat.widget.AudioPlayButton;

import java.util.Locale;

/**
 * Created by wli on 15/9/17.
 * 聊天页面中的语音 item 对应的 holder
 */
public class ChatAudioHolder extends ChatBaseHolder {

    protected AudioPlayButton playButton;
    protected TextView durationView;

    private static int itemMaxWidth;
    private static int itemMinWidth;

    public ChatAudioHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft);
    }

    @Override
    public void initView() {
        super.initView();
        if (isLeft) {
            conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_left_audio_layout, null));
        } else {
            conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_right_audio_layout, null));
        }
        playButton = (AudioPlayButton) itemView.findViewById(R.id.chat_item_audio_play_btn);
        durationView = (TextView) itemView.findViewById(R.id.chat_item_audio_duration_view);

        itemMaxWidth = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.5);
        itemMinWidth = (int) (getContext().getResources().getDisplayMetrics().density * 40);//40dp
    }

    @Override
    public void bindData(Object o) {
        super.bindData(o);
        MessageBean message = (MessageBean) o;
        long duration = message.getDuration();
        duration = Math.max(duration, 2000);
        duration = Math.min(duration, Constants.CHAT_VOICE_MAX_LENGTH);
        int sec = (int) Math.ceil(duration / 1000);
        durationView.setText(String.format(Locale.getDefault(), "%d\"", sec));//向上取整
        int width = getWidthInPixels(sec);
        if (width > 0) {
            playButton.setWidth(width);
        }

        String localFilePath = message.getLocalFilePath();
        if (!TextUtils.isEmpty(localFilePath)) {
            playButton.setPath(localFilePath);
        } else {
            String path = ChatPathUtil.getAudioCachePath(getContext(), message.getMsdId());
            playButton.setPath(path);
            LocalCacheUtils.downloadFileAsync(message.getFileUrl(), path);
        }
    }

    @Override
    protected void setContentClickEvent() {
    }

    private int getWidthInPixels(int sec) {

        double unitWidth = (itemMaxWidth - itemMinWidth) / ((Constants.CHAT_VOICE_MAX_LENGTH - 2000) / 1000);
        return (int) (itemMinWidth + sec * unitWidth);
    }
}