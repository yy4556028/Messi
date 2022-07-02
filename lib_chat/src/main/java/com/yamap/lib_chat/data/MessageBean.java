package com.yamap.lib_chat.data;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageBean {

    public enum MessageStatus{
        Sent, Sending, Failed, Receipt, None
    }

    public enum MessageType{
        UNKNOWN, TEXT, IMAGE, AUDIO, VIDEO, NOTIFY, SHARE, GIF, LOCATION, GIFT
    }

    public final static int CHAT_SENDER_OTHER= 0;
    public final static int CHAT_SENDER_ME = 1;

    public final static int CHAT_MSGTYPE_TEXT = 11;
    public final static int CHAT_MSGTYPE_IMG = 12;

    private String sender;
    private String recipient;
    private String msdId;
    private MessageType msgType = MessageType.UNKNOWN;
    private String avatarUrl;
    private int senderType;
    private long timestamp;
    private String image;
    private int imageWidth;
    private int imageHeight;
    private String name;
    private String content;

    private Map<String, String> Attribute = new HashMap<>();

    private MessageStatus messageStatus = MessageStatus.None;

    private String localFilePath;

    private String fileUrl;

    // audio video
    private long duration;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMsdId() {
        return msdId;
    }

    public void setMsdId(String msdId) {
        this.msdId = msdId;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getSenderType() {
        return senderType;
    }

    public void setSenderType(int senderType) {
        this.senderType = senderType;
    }

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getAttribute(String key) {
        return Attribute.get(key);
    }

    public void setAttribute(String key, String value) {
        Attribute.put(key, value);
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public static class CommentRequestData {
        private int count;

        private int result;

        private ArrayList<MessageBean> data;

        public int getCount() {
            return count;
        }

        public int getResult() {
            return result;
        }

        public ArrayList<MessageBean> getData() {
            return data;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MessageBean) {
            MessageBean msg = (MessageBean) o;
            if (TextUtils.isEmpty(msg.getMsdId()) || TextUtils.isEmpty(getMsdId())) {
                return false;
            } else {
                return msg.getMsdId().equals(getMsdId());
            }
        } else {
            return false;
        }
    }
}
