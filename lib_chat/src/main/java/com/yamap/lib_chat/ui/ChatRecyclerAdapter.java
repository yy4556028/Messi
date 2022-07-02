package com.yamap.lib_chat.ui;


import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.yamap.lib_chat.data.MessageBean;
import com.yamap.lib_chat.ui.viewHolder.ChatAudioHolder;
import com.yamap.lib_chat.ui.viewHolder.ChatBaseHolder;
import com.yamap.lib_chat.ui.viewHolder.ChatCommonHolder;
import com.yamap.lib_chat.ui.viewHolder.ChatImageHolder;
import com.yamap.lib_chat.ui.viewHolder.ChatTextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM_LEFT = 100;
    private final int ITEM_LEFT_TEXT = 101;
    private final int ITEM_LEFT_IMAGE = 102;
    private final int ITEM_LEFT_AUDIO = 103;
    private final int ITEM_LEFT_LOCATION = 104;

    private final int ITEM_RIGHT = 200;
    private final int ITEM_RIGHT_TEXT = 201;
    private final int ITEM_RIGHT_IMAGE = 202;
    private final int ITEM_RIGHT_AUDIO = 203;
    private final int ITEM_RIGHT_LOCATION = 204;

    private final int ITEM_UNKNOWN = 300;

    // 时间间隔最小为十分钟
    private final static long TIME_INTERVAL = 1000 * 60 * 3;

    private boolean isShowAvatar = true;

    protected List<MessageBean> messageList = new ArrayList<MessageBean>();

    public void setMessageList(List<MessageBean> messages) {
        messageList.clear();
        if (null != messages) {
            messageList.addAll(messages);
        }
    }

    /**
     * 添加多条消息记录
     *
     * @param messages
     */
    public void addMessageList(List<MessageBean> messages) {
        messageList.addAll(0, messages);
    }

    /**
     * 添加消息记录
     *
     * @param message
     */
    public void addMessage(MessageBean message) {
        messageList.addAll(Arrays.asList(message));
    }

    /**
     * 获取第一条消息记录，方便下拉时刷新数据
     *
     * @return
     */
    public MessageBean getFirstMessage() {
        if (null != messageList && messageList.size() > 0) {
            return messageList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_LEFT:
            case ITEM_LEFT_TEXT:
                return new ChatTextHolder(parent.getContext(), parent, true);
            case ITEM_LEFT_IMAGE:
                return new ChatImageHolder(parent.getContext(), parent, true);
            case ITEM_LEFT_AUDIO:
                return new ChatAudioHolder(parent.getContext(), parent, true);
//            case ITEM_LEFT_LOCATION:
//                return new LCIMChatItemLocationHolder(parent.getContext(), parent, true);
            case ITEM_RIGHT:
            case ITEM_RIGHT_TEXT:
                return new ChatTextHolder(parent.getContext(), parent, false);
            case ITEM_RIGHT_IMAGE:
                return new ChatImageHolder(parent.getContext(), parent, false);
            case ITEM_RIGHT_AUDIO:
                return new ChatAudioHolder(parent.getContext(), parent, false);
//            case ITEM_RIGHT_LOCATION:
//                return new LCIMChatItemLocationHolder(parent.getContext(), parent, false);
            default:
                return new ChatTextHolder(parent.getContext(), parent, true);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ChatCommonHolder) holder).bindData(messageList.get(position));
        if (holder instanceof ChatBaseHolder) {
            ((ChatBaseHolder) holder).showTimeView(shouldShowTime(position));
            ((ChatBaseHolder) holder).showAvatar(isShowAvatar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessageBean message = messageList.get(position);
        boolean isMe = fromMe(message);
        switch (message.getMsgType()) {
            case TEXT: {
                return isMe ? ITEM_RIGHT_TEXT : ITEM_LEFT_TEXT;
            }
            case IMAGE: {
                return isMe ? ITEM_RIGHT_IMAGE : ITEM_LEFT_IMAGE;
            }
            case AUDIO: {
                return isMe ? ITEM_RIGHT_AUDIO : ITEM_LEFT_AUDIO;
            }
            default: {
                return isMe ? ITEM_RIGHT : ITEM_LEFT;
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    /**
     * item 是否应该展示时间
     *
     * @param position
     * @return
     */
    private boolean shouldShowTime(int position) {
        if (position == 0) {
            return true;
        }
        long lastTime = messageList.get(position - 1).getTimestamp();
        long curTime = messageList.get(position).getTimestamp();
        return curTime - lastTime > TIME_INTERVAL;
    }

    /**
     * item 是否展示头像
     * 因为
     *
     * @param isShow
     */
    public void showAvatar(boolean isShow) {
        isShowAvatar = isShow;
    }

    /**
     * 因为 RecyclerView 中的 item 缓存默认最大为 5，造成会重复的 create item 而卡顿
     * 所以这里根据不同的类型设置不同的缓存值，经验值，不同 app 可以根据自己的场景进行更改
     */
    public void resetRecycledViewPoolSize(RecyclerView recyclerView) {
        recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_LEFT_TEXT, 15);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_LEFT_IMAGE, 10);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_LEFT_AUDIO, 5);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_LEFT_LOCATION, 10);

        recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_RIGHT_TEXT, 15);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_RIGHT_IMAGE, 5);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_RIGHT_AUDIO, 15);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_RIGHT_LOCATION, 10);
    }

    /**
     * 是不是当前用户发送的数据
     *
     * @param msg
     * @return
     */
    protected boolean fromMe(MessageBean msg) {
//        String selfId = LCChatKit.getInstance().getCurrentUserId();
//        return msg.getSender() != null && msg.getSender().equals(selfId);
        return msg.getSender() != null && msg.getSender().equals("0");
    }
}
