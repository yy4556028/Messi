package com.yamap.lib_chat.utils;

import com.yamap.lib_chat.data.MemberBean;
import com.yamap.lib_chat.data.MessageBean;

import java.util.ArrayList;
import java.util.List;

public class TempUtil {

    public static MemberBean getMemberById(String id) {
        MemberBean memberBean = new MemberBean();
        memberBean.setUserId(id);
        memberBean.setUserName("小明");
        return memberBean;
    }

    public static List<MessageBean> getMessagesById(String id) {
        List<MessageBean> msgList = new ArrayList<>();
        MessageBean messageBean;

        messageBean = new MessageBean();
        messageBean.setAvatarUrl("http://cdn.duitang.com/uploads/item/201508/29/20150829213408_zeQdA.thumb.224_0.jpeg");
        messageBean.setSender("1");
        messageBean.setRecipient("0");
        messageBean.setContent("测试会话001");
        msgList.add(messageBean);

        messageBean = new MessageBean();
        messageBean.setAvatarUrl("http://cdn.lizhi.fm/radio_cover/2014/05/31/11956415063962116.jpg");
        messageBean.setSender("0");
        messageBean.setRecipient("1");
        messageBean.setContent("测试会话002");
        msgList.add(messageBean);

        return msgList;
    }
}
