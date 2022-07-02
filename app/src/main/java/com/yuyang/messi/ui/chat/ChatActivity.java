package com.yuyang.messi.ui.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.yamap.lib_chat.FuncGridView;
import com.yamap.lib_chat.data.MessageBean;
import com.yamap.lib_chat.enums.ChatType;
import com.yamap.lib_chat.events.ChatKeyboardFuncClickEvent;
import com.yuyang.lib_base.utils.KeyboardTool;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.ui.media.AlbumActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class ChatActivity extends com.yamap.lib_chat.ui.ChatActivity {

    private final ActivityResultLauncher<Intent> capture = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (RESULT_OK != result.getResultCode()) return;
        List<String> selectImages = result.getData().getStringArrayListExtra(AlbumActivity.SELECTED_PHOTOS);
        for (int i = 0; i < selectImages.size(); i++) {
            if (!TextUtils.isEmpty(selectImages.get(i))) {
                MessageBean bean = new MessageBean();
                bean.setMsgType(MessageBean.MessageType.IMAGE);
                bean.setAvatarUrl("http://cdn.lizhi.fm/radio_cover/2014/05/31/11956415063962116.jpg");
                bean.setSender("0");
                bean.setRecipient("1");
                bean.setLocalFilePath(selectImages.get(i));
                bean.setTimestamp(System.currentTimeMillis());
                sendMessage(bean);

                bean = new MessageBean();
                bean.setMsgType(MessageBean.MessageType.IMAGE);
                bean.setAvatarUrl("http://cdn.duitang.com/uploads/item/201508/29/20150829213408_zeQdA.thumb.224_0.jpeg");
                bean.setSender("1");
                bean.setRecipient("0");
                bean.setLocalFilePath(selectImages.get(i));
                bean.setTimestamp(System.currentTimeMillis());
                sendMessage(bean);
            }
        }
    });

    public static void startChat(String toChatId, ChatType chatType, Activity activity) {

        if (TextUtils.isEmpty(toChatId)) {
            ToastUtil.showToast("找不到好友~");
            return;
        }

        if (chatType != null && chatType != ChatType.SINGLE) {
            ToastUtil.showToast("暂不支持该类型聊天~");
            return;
        }

        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(TO_CHAT_ID, toChatId);
        intent.putExtra(CHAT_TYPE, chatType);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyboardTool.setKeyboardChangeListener(this, new KeyboardTool.OnKeyboardChangeListener() {
            @Override
            public void onKeyboardChange(boolean isShow, int keyboardHeight) {
            }
        });
//        ScreenUtil.fullScreen_immersive(this, false, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void onEvent(final ChatKeyboardFuncClickEvent event) {
        /**
         * position 由 {@link FuncGridView#init()}定义
         */
        switch (event.getPosition()) {
            case 0: {//拍照
                capture.launch(AlbumActivity.getLaunchIntent(ChatActivity.this, 7, true, true, false, null));
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                break;
            }
        }
    }
}
