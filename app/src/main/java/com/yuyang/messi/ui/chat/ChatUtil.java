package com.yuyang.messi.ui.chat;

import android.content.Context;
import android.text.TextUtils;

import cn.leancloud.LeanCloud;
import cn.leancloud.im.v2.LCIMMessageHandler;
import cn.leancloud.im.v2.LCIMMessageManager;
import cn.leancloud.im.v2.LCIMTypedMessage;


public class ChatUtil {

    private static ChatUtil chatUtil;

    public static synchronized ChatUtil getInstance() {
        if (null == chatUtil) {
            chatUtil = new ChatUtil();
        }
        return chatUtil;
    }

    public void init(Context context, String appId, String appKey) {
        if (TextUtils.isEmpty(appId)) {
            throw new IllegalArgumentException("appId can not be empty!");
        }
        if (TextUtils.isEmpty(appKey)) {
            throw new IllegalArgumentException("appKey can not be empty!");
        }

        LeanCloud.initialize(context.getApplicationContext(), appId, appKey);

        // 消息处理 handler
        LCIMMessageManager.registerMessageHandler(LCIMTypedMessage.class, new LCIMMessageHandler());

        // 与网络相关的 handler
//        AVIMClient.setClientEventHandler(LCIMClientEventHandler.getAppContext());
        // TODO: 2019/5/23  

        // 和 Conversation 相关的事件的 handler
//        AVIMMessageManager.setConversationEventHandler(LCIMConversationHandler.getAppContext());
        // TODO: 2019/5/23  

        // 默认设置为离线消息仅推送数量
//        LCIMClient.setOfflineMessagePush(true);
    }
}
