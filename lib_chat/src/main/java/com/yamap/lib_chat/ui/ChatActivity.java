package com.yamap.lib_chat.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yamap.lib_chat.Constants;
import com.yamap.lib_chat.FuncGridView;
import com.yamap.lib_chat.R;
import com.yamap.lib_chat.data.ConversationBean;
import com.yamap.lib_chat.data.MemberBean;
import com.yamap.lib_chat.data.MessageBean;
import com.yamap.lib_chat.enums.ChatType;
import com.yamap.lib_chat.events.ChatKeyboardFuncClickEvent;
import com.yamap.lib_chat.keyboard.ChatKeyBoard;
import com.yamap.lib_chat.keyboard.data.EmoticonEntity;
import com.yamap.lib_chat.keyboard.interfaces.EmoticonClickListener;
import com.yamap.lib_chat.keyboard.widget.ChatEditText;
import com.yamap.lib_chat.keyboard.widget.ExpandLayout;
import com.yamap.lib_chat.utils.CommonUtil;
import com.yamap.lib_chat.utils.TempUtil;
import com.yamap.lib_chat.widget.RecordButton;
import com.yuyang.lib_base.utils.StorageUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public abstract class ChatActivity extends AppCompatActivity {

    protected static final String TO_CHAT_ID = "to_chat_id";
    protected static final String CHAT_TYPE = "chat_type";

    protected Toolbar toolbar;

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    protected LinearLayoutManager layoutManager;
    protected ChatRecyclerAdapter recyclerAdapter;

    protected ChatKeyBoard chatKeyBoard;

    protected ChatType chatType = ChatType.UNKNOWN;//聊天类型
    protected String toChatIdString;//聊天人或群id
    protected MemberBean toChatMember;//聊天用户
    protected ConversationBean conversation;
    protected int pageSize = 20;

    protected List<MemberBean> taggedMembers = new ArrayList<>();

    private String userid = "13921400723";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chat);
        initViews();
        initChat();
        EventBus.getDefault().register(this);

        // 测试 SDK 是否正常工作的代码
//        AVObject testObject = new AVObject("TestObject");
//        testObject.put("words","Hello World!");
//        testObject.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(AVException e) {
//                if(e == null){
//                    Log.d("saved","success!");
//                }
//            }
//        });
//        avimClient.createConversation(Arrays.asList(getIntent().getStringExtra(TO_CHAT_ID)), "测试中", null,
//                new AVIMConversationCreatedCallback() {
//
//                    @Override
//                    public void done(AVIMConversation avimConversation, AVIMException e) {
//                        if (e == null) {
//                            avimConversation.sendMessage();
//                        }
//                    }
//                });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (chatKeyBoard != null)
            chatKeyBoard.reset();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (CommonUtil.isFullScreen(this)) {
            boolean isConsume = chatKeyBoard.dispatchKeyEventInFullScreen(event);
            return isConsume ? isConsume : super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    private void initChat() {
        chatType = ChatType.SINGLE;
        toChatIdString = "1";
        conversation = new ConversationBean();

        switch (chatType) {
            case SINGLE: {
                toChatMember = TempUtil.getMemberById(toChatIdString);
                toolbar.setTitle(toChatMember.getUserName());
                initMsgList();
                break;
            }
            case GROUP: {
                // TODO: 2016/11/23  监听当前会话的群聊解散被T事件
                // TODO: 2016/11/23  控制右上角图标
                break;
            }
        }
    }

    private void initMsgList() {
        conversation.getMessageList().addAll(TempUtil.getMessagesById(toChatIdString));
        //刷新UI
        recyclerAdapter.setMessageList(conversation.getMessageList());
        scrollToBottom();
        //获取message
        // TODO: 2016/11/23  把此会话的未读数置为0
    }

    private void initViews() {
        toolbar = findViewById(R.id.activity_chat_toolbar);
        swipeRefreshLayout = findViewById(R.id.activity_chat_swipeRefresh);
        recyclerView = findViewById(R.id.activity_chat_recyclerView);
        chatKeyBoard = findViewById(R.id.activity_chat_keyboard);
        initEmoticonsKeyBoardBar();
        initRecyclerView();

        chatKeyBoard.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (chatType != ChatType.GROUP) {
                    return;
                }
//                if (count == 1 && s.charAt(start) == '@' && isListener) {
//
//                    chatKeyBoard.reset();
//
//                    EMGroup group = EMGroupManager.getInstance().getGroup(toChatUserIdString);
//                    if (group != null) {
//                        ArrayList<DBFreeMember> members = new ArrayList<>();
//                        for (int i = 0; i < group.getMembers().size(); i++) {
//                            DBFreeMember member = MemberController.getInstance().createMember(Long.parseLong(group.getMembers().get(i)));
//                            if (!TextUtils.isEmpty(member.getUsername())) {
//                                members.add(member);
//                            }
//                        }
//
//                        if (members.size() > 0) {
//                            Intent intent = new Intent();
//                            intent.putExtra(IntentUsedConstants.SELECT_MEMBER_EVENT_TYPE, SelectCrewEventType.chat_input);
//                            intent.putExtra(SelectMemberActivity.SHOW_MEMBERS, members);
//                            intent.setClass(ChatActivity.this, SelectMemberActivity.class);
//                            intent.putExtra(IntentUsedConstants.SPECIAL_MEMBER_TYPE, SpecialMemberType.CHAT_GROUP);
//                            startActivity(intent);
//                        }
//                    }
//
//                    if (start != s.length()) {
//                        s = s.toString().substring(0, start) + s.toString().substring(start + 1) + "@";
//                        isListener = false;
//                        chatInputMenu.inputEditText.setText(s.toString());
//                        chatInputMenu.inputEditText.setSelection(s.length());
//                        isListener = true;
//                    }
//                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initRecyclerView() {
        swipeRefreshLayout.setEnabled(false);
        recyclerView.setLayoutManager(layoutManager = new LinearLayoutManager(this));
        recyclerAdapter = new ChatRecyclerAdapter();
        recyclerAdapter.resetRecycledViewPoolSize(recyclerView);
        recyclerView.setAdapter(recyclerAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MessageBean message = recyclerAdapter.getFirstMessage();
                if (null == message) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
//                    imConversation.queryMessages(message.getMessageId(), message.getTimestamp(), 20, new AVIMMessagesQueryCallback() {
//                        @Override
//                        public void done(List<MessageBean> list, AVIMException e) {
                    swipeRefreshLayout.setRefreshing(false);
//                            if (filterException(e)) {
//                                if (null != list && list.size() > 0) {
//                                    recyclerAdapter.addMessageList(list);
//                                    recyclerAdapter.notifyDataSetChanged();
//                                    layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
//                                }
//                            }
//                        }
//                    });
                }
            }
        });

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                switch (newState) {
//                    case RecyclerView.SCROLL_STATE_DRAGGING:
//                        chatKeyBoard.reset();
//                        break;
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                chatKeyBoard.reset();
                return false;
            }
        });
    }

    private void initEmoticonsKeyBoardBar() {
        CommonUtil.initEmoticonsEditText(chatKeyBoard.getEditText());
        chatKeyBoard.setAdapter(CommonUtil.getCommonAdapter(this, emoticonClickListener));
        chatKeyBoard.addOnFuncKeyBoardListener(new ExpandLayout.OnFuncKeyBoardListener() {
            @Override
            public void OnFuncPop(int height) {
                scrollToBottom();
            }

            @Override
            public void OnFuncClose() {

            }
        });
        chatKeyBoard.addFuncView(new FuncGridView(this));

        chatKeyBoard.getEditText().setOnSizeChangedListener(new ChatEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                scrollToBottom();
            }
        });
        chatKeyBoard.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSendBtnClick(chatKeyBoard.getEditText().getText().toString());
                chatKeyBoard.getEditText().setText("");
            }
        });
        chatKeyBoard.getRecordBtn().setSaveDir(StorageUtil.getExternalFilesDir("chatrRecord").getAbsolutePath());
        chatKeyBoard.getRecordBtn().setRecordEventListener(new RecordButton.RecordEventListener() {
            @Override
            public void onFinishedRecord(String audioPath, long voiceTimeLength) {
                if (!TextUtils.isEmpty(audioPath)) {
                    MessageBean bean = new MessageBean();
                    bean.setMsgType(MessageBean.MessageType.AUDIO);
                    bean.setAvatarUrl("http://cdn.lizhi.fm/radio_cover/2014/05/31/11956415063962116.jpg");
                    bean.setSender("0");
                    bean.setRecipient("1");
                    bean.setLocalFilePath(audioPath);
                    bean.setDuration(voiceTimeLength);
                    bean.setTimestamp(System.currentTimeMillis());
                    sendMessage(bean);

                    bean = new MessageBean();
                    bean.setMsgType(MessageBean.MessageType.AUDIO);
                    bean.setSender("1");
                    bean.setRecipient("0");
                    bean.setAvatarUrl("http://cdn.duitang.com/uploads/item/201508/29/20150829213408_zeQdA.thumb.224_0.jpeg");
                    bean.setLocalFilePath(audioPath);
                    bean.setDuration(voiceTimeLength);
                    bean.setTimestamp(System.currentTimeMillis());
                    sendMessage(bean);
                }
            }

            @Override
            public void onStartRecord() {

            }
        });
        chatKeyBoard.getEmoticonsToolBarView().addFixedToolItemView(false, R.mipmap.icon_add, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "ADD", Toast.LENGTH_SHORT).show();
            }
        });
        chatKeyBoard.getEmoticonsToolBarView().addToolItemView(R.mipmap.icon_setting, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "SETTING", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void scrollToBottom() {
//        listView.requestLayout();
//        listView.post(new Runnable() {
//            @Override
//            public void run() {
//                listView.setSelection(listView.getBottom());
//            }
//        });
        recyclerView.requestLayout();
        if (recyclerAdapter.getItemCount() > 0)
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    layoutManager.scrollToPosition(recyclerAdapter.getItemCount() - 1);
//                    recyclerView.smoothScrollToPosition(recyclerAdapter.getItemCount() - 1);
                }
            });
    }

    private void OnSendBtnClick(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            MessageBean bean = new MessageBean();
            bean.setMsgType(MessageBean.MessageType.TEXT);
            bean.setAvatarUrl("http://cdn.lizhi.fm/radio_cover/2014/05/31/11956415063962116.jpg");
            bean.setSender("0");
            bean.setRecipient("1");
            bean.setContent(msg);
            bean.setTimestamp(System.currentTimeMillis());
            sendMessage(bean);

            bean = new MessageBean();
            bean.setMsgType(MessageBean.MessageType.TEXT);
            bean.setSender("1");
            bean.setRecipient("0");
            bean.setAvatarUrl("http://cdn.duitang.com/uploads/item/201508/29/20150829213408_zeQdA.thumb.224_0.jpeg");
            bean.setContent(msg);
            bean.setTimestamp(System.currentTimeMillis());
            sendMessage(bean);
        }
    }

    private void OnSendImage(String image) {
        if (!TextUtils.isEmpty(image)) {
            OnSendBtnClick("[img]" + image);
        }
    }

    /**
     * 复制消息内容到剪切板
     *
     * @param message
     */
    private void copyMsg(MessageBean message) {
        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setPrimaryClip(ClipData.newPlainText("", message.getContent()));
    }

    /**
     * 删除消息
     *
     * @param message
     */
    private void deleteMsg(MessageBean message) {

        // TODO: 2016/11/23  删除消息文件 如 图片视频等
        conversation.deleteMsg(message.getMsdId());
        recyclerAdapter.setMessageList(conversation.getMessageList());
        recyclerAdapter.notifyDataSetChanged();
    }

    /**
     * 重发消息
     *
     * @param message
     */
    private void resendMsg(MessageBean message) {

//        message.setMessageStatus(MessageBean.MessageStatus.Sending);
        conversation.getMessageList().add(message);
        recyclerAdapter.setMessageList(conversation.getMessageList());
        recyclerAdapter.notifyDataSetChanged();
    }

    protected void sendMessage(MessageBean message) {

        // 如果是群聊，设置chattype,默认是单聊
//        if (chatType == EMConstant.CHATTYPE_GROUP) {
//            message.setChatType(EMMessage.ChatType.GroupChat);
//            message.isAcked = true;
//            message.setAttribute(EMConstant.CHAT_GROUP_SENDER_NAME, MemberController.getMe().getUsername());
//            message.setAttribute(EMConstant.CHAT_GROUP_SENDER_AVATAR_URL, MemberController.getMe().getAvatar_url());
//        } else if (chatType == EMConstant.CHATTYPE_CHATROOM) {
//            message.setChatType(EMMessage.ChatType.ChatRoom);
//        }

        /**
         * 苹果推送
         */
//        try {
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("s", 1);
//            jsonObject.put("em_push_title", SobrrActionUtil.getSobrrAppInstance().getEMHelper().notifier.getNotifyText(message));
//
//            message.setAttribute("em_apns_ext", jsonObject.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        // TODO: 2016/11/24 通知设置静态消息
//        if (message.getIntAttribute(EMConstant.CHAT_TYPE, EMConstant.CHAT_TYPE_TEXT) == EMConstant.CHAT_TYPE_NOTIFY)
//            message.setAttribute("silent_msg", "Y");

        if (chatType == ChatType.SINGLE) {//如果是单聊

            String recipientId = message.getRecipient();

            if (!recipientId.equals("0")) {// 如果不是自己聊天

                if (recipientId.length() > 2) {// 如果是陌生人
                    Toast.makeText(this, "你们还不是好友", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        // TODO: 2016/11/24 发送消息
        conversation.getMessageList().add(message);
        //刷新UI
        recyclerAdapter.setMessageList(conversation.getMessageList());
        scrollToBottom();
    }

    EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {

            if (isDelBtn) {
                CommonUtil.delClick(chatKeyBoard.getEditText());
            } else {
                if (o == null) {
                    return;
                }
                if (actionType == Constants.EMOTICON_CLICK_BIG_IMAGE) {
                    if (o instanceof EmoticonEntity) {
                        OnSendImage(((EmoticonEntity) o).getIconUri());
                    }
                } else {
//                    String content = null;
//                    if (o instanceof EmojiBean) {
//                        content = ((EmojiBean) o).emoji;
//                    } else if (o instanceof EmoticonEntity) {
//                        content = ((EmoticonEntity) o).getContent();
//                    }
//
//                    if (TextUtils.isEmpty(content)) {
//                        return;
//                    }
//                    int index = chatKeyBoard.getEditText().getSelectionStart();
//                    Editable editable = chatKeyBoard.getEditText().getText();
//                    editable.insert(index, content);
                }
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void onEvent(final ChatKeyboardFuncClickEvent event) {
        /**
         * position 由 {@link FuncGridView#init()}定义
         */
        switch (event.getPosition()) {
            case 0: {//拍照
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
