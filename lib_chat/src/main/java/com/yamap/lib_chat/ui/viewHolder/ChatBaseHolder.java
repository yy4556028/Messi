package com.yamap.lib_chat.ui.viewHolder;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yamap.lib_chat.R;
import com.yamap.lib_chat.data.MessageBean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wli on 15/9/17.
 * 聊天 item 的 holder
 */
public abstract class ChatBaseHolder extends ChatCommonHolder {

    protected boolean isLeft;

    protected MessageBean message;
    protected ImageView avatarView;
    protected TextView timeView;
    protected LinearLayout conventLayout;
    protected FrameLayout statusLayout;
    protected ProgressBar progressBar;
    protected TextView statusView;
    protected ImageView errorView;

    public ChatBaseHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft ? R.layout.chat_item_left_layout : R.layout.chat_item_right_layout);
        this.isLeft = isLeft;
        initView();
    }

    public void initView() {
        if (isLeft) {
            avatarView = (ImageView) itemView.findViewById(R.id.chat_left_iv_avatar);
            timeView = (TextView) itemView.findViewById(R.id.chat_left_tv_time);
            conventLayout = (LinearLayout) itemView.findViewById(R.id.chat_left_layout_content);
            statusLayout = (FrameLayout) itemView.findViewById(R.id.chat_left_layout_status);
            statusView = (TextView) itemView.findViewById(R.id.chat_left_tv_status);
            progressBar = (ProgressBar) itemView.findViewById(R.id.chat_left_progressbar);
            errorView = (ImageView) itemView.findViewById(R.id.chat_left_tv_error);
        } else {
            avatarView = (ImageView) itemView.findViewById(R.id.chat_right_iv_avatar);
            timeView = (TextView) itemView.findViewById(R.id.chat_right_tv_time);
            conventLayout = (LinearLayout) itemView.findViewById(R.id.chat_right_layout_content);
            statusLayout = (FrameLayout) itemView.findViewById(R.id.chat_right_layout_status);
            progressBar = (ProgressBar) itemView.findViewById(R.id.chat_right_progressbar);
            errorView = (ImageView) itemView.findViewById(R.id.chat_right_tv_error);
            statusView = (TextView) itemView.findViewById(R.id.chat_right_tv_status);
        }

        setAvatarClickEvent();
        setContentClickEvent();
        setResendClickEvent();
    }

    @Override
    public void bindData(Object o) {
        message = (MessageBean) o;
        timeView.setText(millisecsToDateString(message.getTimestamp()));
        Glide.with(getContext())
                .load(message.getAvatarUrl())
                .thumbnail(0.5f)
                .into(avatarView);
//        LCIMProfileCache.getInstance().getCachedUser(message.getFrom(), new AVCallback<LCChatKitUser>() {
//            @Override
//            protected void internalDone0(LCChatKitUser userProfile, AVException e) {
//                if (null != e) {
//                    LCIMLogUtils.logException(e);
//                } else if (null != userProfile) {
//                    nameView.setText(userProfile.getUserName());
//                    final String avatarUrl = userProfile.getAvatarUrl();
//                    if (!TextUtils.isEmpty(avatarUrl)) {
//                        Glide.with(getContext()).load(avatarUrl)
//                                .placeholder(R.drawable.lcim_default_avatar_icon).into(avatarView);
//                    }
//                }
//            }
//        });

        switch (message.getMessageStatus()) {
            case Failed:
                statusLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                statusView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                break;
            case Sent:
                statusLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                statusView.setVisibility(View.VISIBLE);
                statusView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                break;
            case Sending:
                statusLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                statusView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                break;
            case None:
            case Receipt:
                statusLayout.setVisibility(View.GONE);
                break;
        }
    }

    public void showTimeView(boolean isShow) {
        timeView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void showAvatar(boolean isShow) {
        avatarView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        avatarView.getLayoutParams().width = isShow ? avatarView.getLayoutParams().height : 0;
    }

    /**
     * 设置头像点击按钮的事件
     */
    private void setAvatarClickEvent() {
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    Intent intent = new Intent();
//                    intent.setPackage(getContext().getPackageName());
//                    intent.setAction(LCIMConstants.AVATAR_CLICK_ACTION);
//                    intent.addCategory(Intent.CATEGORY_DEFAULT);
//                    getContext().startActivity(intent);
                } catch (ActivityNotFoundException exception) {
//                    Log.i(LCIMConstants.LCIM_LOG_TAG, exception.toString());
                }
            }
        });
    }

    protected void setContentClickEvent(){

    }

    /**
     * 设置发送失败的叹号按钮的事件
     */
    private void setResendClickEvent() {
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LCIMMessageResendEvent event = new LCIMMessageResendEvent();
//                event.message = message;
//                EventBus.getDefault().post(event);
            }
        });
    }

    //TODO 展示更人性一点
    private static String millisecsToDateString(long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(new Date(timestamp));
    }
}
