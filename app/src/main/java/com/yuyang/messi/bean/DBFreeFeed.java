package com.yuyang.messi.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aaron on 11/12/14.
 */
public class DBFreeFeed {

    public static final long S_IN_24HR = 60*60*24;

    private String body;
    private String picture;
    private String thumbnail;
    private Short my_attitude;
    private Short type;
    private Integer cheer_number;
    private Integer comment_number;
    private Integer dgaf_number;
    private Long created_at;
    private String web_url;
    private Long remote_id;
    private Double latitude;
    private Double longitude;
    private String sender_remote_id;
    private String category;
    private String video;

    private ArrayList<String> taggedMembers = new ArrayList<>();
    private UserBean userBean;


    public Long getRemote_id() {
        return this.remote_id;
    }

    public void destroy() {
//        SobrrActionUtil.getLruCacheInstance().clearKeyUri(getPicture());
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof DBFreeFeed) {
            return ((DBFreeFeed) anObject).getRemote_id().equals(getRemote_id());
        }
        return super.equals(anObject);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DBFreeFeed [");
        buffer.append("remote id=" + remote_id);
        buffer.append(" ");
        buffer.append(" body=" + (body == null ? "no body" : "has body"));
        buffer.append(" ");
        buffer.append("picture=" + picture);
        buffer.append(" ");
        buffer.append("my_attitude=" + my_attitude);
        buffer.append("]");
        return buffer.toString();
    }

    /*----------------------*/

    /**
     * @return String 缩略图视频帧图片url
     * @name transformToSmallQiniuVideoFrameUrl
     * @description 将存储在七牛视频帧图片等比转为缩略图
     * @params [String originalQiniuVideoFrameUrl 原始视频帧图片url]
     * @created_at 15/11/26,@author joker
     */
    public static String transformToSmallQiniuVideoFrameUrl(String originalQiniuVideoFrameUrl) {
        Pattern dimPattern = Pattern.compile("(.*/w/)([0-9]+)(.*/h/)([0-9]+)(.*)");
        Matcher dimMatcher = dimPattern.matcher(originalQiniuVideoFrameUrl);
        String smallQiniuVideoFrameUrl = originalQiniuVideoFrameUrl;
        if (dimMatcher.matches()) {
            int origWidth = Integer.parseInt(dimMatcher.group(2));
            int origHeight = Integer.parseInt(dimMatcher.group(4));
            int smallHeight = 160;
            int smallWidth = smallHeight * origWidth / origHeight;
            smallQiniuVideoFrameUrl = dimMatcher.group(1) + smallWidth + dimMatcher.group(3) + smallHeight + dimMatcher.group(5);
        }
        return smallQiniuVideoFrameUrl;
    }

    public static String transformToSmallPicUrl(String originalUrl) {
        if (validateUrl(originalUrl)) {
            if (originalUrl.startsWith("https://s3-us") ||
                    originalUrl.startsWith("http://s3-us")) {
                int dotPosition = originalUrl.lastIndexOf(".");
                return originalUrl.substring(0, dotPosition) + "_sm.jpeg";
            } else {
                if (isQiniuVideoFrameUrl(originalUrl)) {
                    return transformToSmallQiniuVideoFrameUrl(originalUrl);
                } else {
                    return originalUrl.concat("?imageView2/2/format/webp/w/160/h/160");
                }
            }
        } else {
            return "null";
        }
    }

    public static boolean isQiniuVideoFrameUrl(String originalQiniuVideoFrameUrl) {
        Pattern dimPattern = Pattern.compile("(.*/w/)([0-9]+)(.*/h/)([0-9]+)(.*)");
        Matcher dimMatcher = dimPattern.matcher(originalQiniuVideoFrameUrl);
        return dimMatcher.matches();
    }

    public static boolean validateUrl(String url) {
        if (url == null) return false;
        if (url.lastIndexOf(".") == -1) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean nowIsWithin24HrsAfterDate(long dateLong) {
        Date now = new Date();
        if ((now.getTime()/1000L - dateLong) < S_IN_24HR) {
            return true;
        } else {
            return false;
        }
    }
}
