package com.yuyang.lib_share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.mob.MobSDK;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**
 * Created by Yamap on 2017/3/10.
 */

public class ShareSDKUtil {

    private static final String TAG = ShareSDKUtil.class.getSimpleName();

    /**
     * @param context
     * @param platform 指定平台分享，null为弹出选择
     */
    public static void showShare(Context context, String platform, String title, String imageUrl, String webUrl) {
        MobSDK.submitPolicyGrantResult(true, null);
        OnekeyShare oks = new OnekeyShare();

        if (platform != null) {
            oks.setPlatform(platform);
        }

        //一键分享指的是通过OneKeyShare九宫格界面分享多个平台，不同平台分享不同的数据类型请参考
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams shareParams) {
                //微博分享链接和图文
                if ("SinaWeibo".equals(platform.getName())) {
                    shareParams.setText("玩美夏日，护肤也要肆意玩酷！" + "www.mob.com");
                    shareParams.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                }
                //微信好友分享网页
                if ("Wechat".equals(platform.getName())) {
                    shareParams.setTitle("标题");
                    shareParams.setText("我是共用的参数，这几个平台都有text参数要求，提取出来啦");
                    shareParams.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                    shareParams.setUrl("http://sharesdk.cn");
                    shareParams.setShareType(Platform.SHARE_WEBPAGE);
                    Log.d("ShareSDK", shareParams.toMap().toString());
                }
                //微信朋友圈分享图片
                if ("WechatMoments".equals(platform.getName())) {
                    shareParams.setTitle("标题");
                    shareParams.setText("我是共用的参数，这几个平台都有text参数要求，提取出来啦");
            /*Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            paramsToShare.setImageData(imageData);*/
                    shareParams.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                    shareParams.setShareType(Platform.SHARE_IMAGE);
                    Log.d("ShareSDK", shareParams.toMap().toString());
                }
                //QQ分享链接
                if ("QQ".equals(platform.getName())) {
                    shareParams.setTitle("标题");
                    shareParams.setTitleUrl("http://sharesdk.cn");
                    shareParams.setText("我是共用的参数，这几个平台都有text参数要求，提取出来啦");
                    shareParams.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                    Log.d("ShareSDK", shareParams.toMap().toString());
                }
                //支付宝好友分享网页
                if ("Alipay".equals(platform.getName())) {
                    shareParams.setTitle("标题");
                    shareParams.setText("我是共用的参数，这几个平台都有text参数要求，提取出来啦");
                    shareParams.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                    shareParams.setUrl("http://sharesdk.cn");
                    shareParams.setShareType(Platform.SHARE_WEBPAGE);
                    Log.d("ShareSDK", shareParams.toMap().toString());
                }
                //Facebook以卡片形式分享链接
                if ("Facebook".equals(platform.getName())) {
                    //          paramsToShare.setText("我是共用的参数，这几个平台都有text参数要求，提取出来啦");
                    shareParams.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                    shareParams.setUrl("http://www.mob.com");
                    shareParams.setShareType(Platform.SHARE_WEBPAGE);
                    shareParams.setQuote("我是共用的参数");
                    shareParams.setHashtag("测试话题分享");
                }
                //Twitter分享链接
                if ("Twitter".equals(platform.getName())) {
                    shareParams.setText("我是共用的参数，这几个平台都有text参数要求，提取出来啦");
                    shareParams.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                    shareParams.setUrl("http://sharesdk.cn");
                }
                //WhatsApp分享图片
                if ("WhatsApp".equals(platform.getName())) {
                    shareParams.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                }
                //短信分享文本
                if ("ShortMessage".equals(platform.getName())) {
                    shareParams.setText("我是共用的参数，这几个平台都有text参数要求，提取出来啦");
                    shareParams.setTitle("标题");
                    shareParams.setAddress("17625325208");
                }
            }
        });

        oks.setDisappearShareToast(true);//关闭“分享操作正在后台进行”的提示
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl(webUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText("因为那时候我还不明白");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl(imageUrl);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(webUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(context.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(webUrl);

        if (false) {//自定义平台该接口只能添加一个平台，默认是最后九宫格最后一位展现
            Bitmap logo = BitmapFactory.decodeResource(MobSDK.getContext().getResources(), R.drawable.icon);
            String label = "ShareSDK";
            View.OnClickListener listener = new View.OnClickListener() {
                public void onClick(View v) {
                    //添加自定义平台对应的图片点击事件
                }
            };
            oks.setCustomerLogo(logo, label, listener);
        }

        // 启动分享GUI
        oks.show(context);
    }
}
