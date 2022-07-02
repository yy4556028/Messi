package com.yamap.lib_chat.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yamap.lib_chat.R;
import com.yamap.lib_chat.data.MessageBean;
import com.yamap.lib_chat.keyboard.utils.imageloader.ImageBase;
import com.yamap.lib_chat.utils.CommonUtil;
import com.yamap.lib_chat.utils.ImageLoadUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.yamap.lib_chat.data.MessageBean.MessageType.IMAGE;
import static com.yamap.lib_chat.data.MessageBean.MessageType.TEXT;
import static com.yamap.lib_chat.data.MessageBean.MessageType.UNKNOWN;


/**
 * @deprecated
 */
public class ChatListAdapter extends BaseAdapter {

    private final int VIEW_TYPE_COUNT = 8;
    private final int VIEW_TYPE_LEFT_TEXT = 0;
    private final int VIEW_TYPE_LEFT_IMAGE = 1;

    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<MessageBean> msgList;

    public ChatListAdapter(Activity activity) {
        this.mActivity = activity;
        mInflater = LayoutInflater.from(activity);
    }

    public void addMsg(List<MessageBean> msgList) {
        if (msgList == null || msgList.size() == 0) {
            return;
        }
        if (this.msgList == null) {
            this.msgList = new ArrayList<>();
        }
        for (MessageBean bean : msgList) {
            addMsg(bean, false, false);
        }
        this.notifyDataSetChanged();
    }

    public void addMsg(MessageBean bean, boolean isNotifyDataSetChanged, boolean isFromHead) {
        if (bean == null) {
            return;
        }
        if (msgList == null) {
            msgList = new ArrayList<>();
        }

        if (bean.getMsgType() == UNKNOWN) {
            String content = bean.getContent();
            if (content != null) {
                if (content.indexOf("[img]") >= 0) {
                    bean.setImage(content.replace("[img]", ""));
                    bean.setMsgType(IMAGE);
                } else {
                    bean.setMsgType(TEXT);
                }
            }
        }

        if (isFromHead) {
            msgList.add(0, bean);
        } else {
            msgList.add(bean);
        }

        if (isNotifyDataSetChanged) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return msgList == null ? 0 : msgList.size();
    }

    @Override
    public MessageBean getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        MessageBean msgBean = msgList.get(position);
        if (msgBean == null) {
            return -1;
        }
//        return msgBean.getMsgType();//tempppppp
        return msgBean.getMsgType() == TEXT ? VIEW_TYPE_LEFT_TEXT : VIEW_TYPE_LEFT_IMAGE;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MessageBean bean = msgList.get(position);
        int type = getItemViewType(position);
        View holderView = null;
        switch (type) {
            case VIEW_TYPE_LEFT_TEXT:
                ViewHolderLeftText holder;
                if (convertView == null) {
                    holder = new ViewHolderLeftText();
                    holderView = mInflater.inflate(R.layout.activity_chat_list_item, null);
                    holderView.setFocusable(true);
                    holder.iv_avatar = (ImageView) holderView.findViewById(R.id.activity_chat_list_item_avatar);
                    holder.tv_content = (TextView) holderView.findViewById(R.id.activity_chat_list_item_content);
                    holderView.setTag(holder);
                    convertView = holderView;
                } else {
                    holder = (ViewHolderLeftText) convertView.getTag();
                }
                disPlayLeftTextView(position, convertView, holder, bean);
                break;
            case VIEW_TYPE_LEFT_IMAGE:
                ViewHolderLeftImage imageHolder;
                if (convertView == null) {
                    imageHolder = new ViewHolderLeftImage();
                    holderView = mInflater.inflate(R.layout.listitem_chat_left_image, null);
                    holderView.setFocusable(true);
                    imageHolder.iv_avatar = (ImageView) holderView.findViewById(R.id.activity_chat_list_item_avatar);
                    imageHolder.iv_image = (ImageView) holderView.findViewById(R.id.iv_image);
                    holderView.setTag(imageHolder);
                    convertView = holderView;
                } else {
                    imageHolder = (ViewHolderLeftImage) convertView.getTag();
                }
                disPlayLeftImageView(position, convertView, imageHolder, bean);
                break;
            default:
                convertView = new View(mActivity);
                break;
        }
        return convertView;
    }

    public void disPlayLeftTextView(int position, View view, ViewHolderLeftText holder, MessageBean bean) {
        setContent(holder.tv_content, bean.getContent());
    }

    public void setContent(TextView tv_content, String content) {
        tv_content.setText(content);
    }

    public void disPlayLeftImageView(int position, View view, ViewHolderLeftImage holder, MessageBean bean) {
        try {
            if (ImageBase.Scheme.FILE == ImageBase.Scheme.ofUri(bean.getImage())) {
                String filePath = ImageBase.Scheme.FILE.crop(bean.getImage());
                Glide.with(holder.iv_image.getContext())
                        .load(filePath)
                        .into(holder.iv_image);
            } else {
                ImageLoadUtils.getInstance(mActivity).displayImage(bean.getImage(), holder.iv_image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final class ViewHolderLeftText {
        public ImageView iv_avatar;
        public TextView tv_content;
    }

    public final class ViewHolderLeftImage {
        public ImageView iv_avatar;
        public ImageView iv_image;
    }
}