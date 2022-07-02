package com.yamap.lib_chat.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天会话模拟类，模拟方法，无法实用
 */
public class ConversationBean {

    private List<MessageBean> messageList = new ArrayList<>();

    public List<MessageBean> getMessageList() {
        return messageList;
    }

    public void deleteMsg(String id) {
        MessageBean messageBean = new MessageBean();
        messageBean.setMsdId(id);
        messageList.remove(messageBean);
    }
}
