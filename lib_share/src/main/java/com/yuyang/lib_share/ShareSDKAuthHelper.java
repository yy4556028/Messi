package com.yuyang.lib_share;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mob.MobSDK;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;

public class ShareSDKAuthHelper {

    private static final String TAG = ShareSDKAuthHelper.class.getSimpleName();
    public static final String PLATFORM_Wechat = Wechat.NAME;
    public static final String PLATFORM_SinaWeibo = SinaWeibo.NAME;
    public static final String PLATFORM_Facebook = Facebook.NAME;
    public static final String PLATFORM_Twitter = Twitter.NAME;

    private static final int MSG_AUTH_CANCEL = 1;
    private static final int MSG_AUTH_ERROR = 2;
    private static final int MSG_AUTH_COMPLETE = 3;

    private LoginHandler loginHandler = new LoginHandler();

    private String platformName;

    public ShareSDKAuthHelper() {
    }

    public void auth(Context context, String platformName) {
        MobSDK.submitPolicyGrantResult(true,null);
        this.platformName = platformName;
        Platform platform = ShareSDK.getPlatform(this.platformName);
//        CookieSyncManager.createInstance(platform.getContext());
//        CookieManager cookieManager = CookieManager.getAppContext();
//        cookieManager.removeAllCookie();
//        CookieSyncManager.getAppContext().sync();
//        platform.removeAccount(true);
        authorize(platform);
    }

    //执行授权,获取用户信息
    private void authorize(Platform platform) {
        if (platform == null) {
            return;
        }

        //判断指定平台是否已经完成授权
        if (platform.isAuthValid()) {
            String userId = platform.getDb().getUserId();
//            plat.removeAccount(true);
            if (userId != null) {
//                ToastUtil.showToast("userId = " + userId);
//                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
//                login(plat.getName(), userId, null);
                return;
            }
        }

        platform.setPlatformActionListener(platformActionListener);

        // true不使用SSO授权，false使用SSO授权
        platform.SSOSetting(false);
        //获取用户资料
        platform.showUser(null);
    }

    private final PlatformActionListener platformActionListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
            Log.d(TAG, "onComplete ---->  登录成功" + platform.getDb().exportData());
            if (action == Platform.ACTION_USER_INFOR) {
                Message msg = new Message();
                msg.what = MSG_AUTH_COMPLETE;
                msg.obj = new Object[]{platform.getName(), hashMap};
                loginHandler.sendMessage(msg);
            }
        }

        @Override
        public void onError(Platform platform, int action, Throwable throwable) {
            Log.d(TAG, "onError ---->  登录失败" + throwable.toString());
            Log.d(TAG, "onError ---->  登录失败" + throwable.getStackTrace().toString());
            Log.d(TAG, "onError ---->  登录失败" + throwable.getMessage());
            if (action == Platform.ACTION_USER_INFOR) {
                loginHandler.sendEmptyMessage(MSG_AUTH_ERROR);
            }
            throwable.printStackTrace();
        }

        @Override
        public void onCancel(Platform platform, int action) {
            Log.d("ShareSDK", "onCancel ---->  登录取消");
            if (action == Platform.ACTION_USER_INFOR) {
                loginHandler.sendEmptyMessage(MSG_AUTH_CANCEL);
            }
        }
    };

    private class LoginHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case MSG_AUTH_CANCEL: {
                    //取消授权
                    ShareSDK.getPlatform(platformName).removeAccount(true);
                    platformName = null;
                    if (onAuthListener != null) {
                        onAuthListener.onCancel();
                    }
                }
                break;

                case MSG_AUTH_ERROR: {
                    //授权失败
                    ShareSDK.getPlatform(platformName).removeAccount(true);
                    platformName = null;
                    if (onAuthListener != null) {
                        onAuthListener.onError();
                    }
                }
                break;

                case MSG_AUTH_COMPLETE: {
                    //授权成功
                    Object[] objs = (Object[]) msg.obj;
                    String platformName = (String) objs[0];
                    HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
                    if (onAuthListener != null) {
                        onAuthListener.onComplete(platformName, res);
                    }

                    final Platform platform = ShareSDK.getPlatform(platformName);
                    if (platform.getDb().getUserIcon() != null && !platform.getDb().getUserIcon().equals("")) {
                        platform.getDb().getUserIcon();
                    }

                    platform.getDb().getUserId();

                    if (res.get("first_name") != null)
                        res.get("first_name").toString();

                    if (res.get("last_name") != null)
                        res.get("last_name").toString();
                }
                break;

            }
        }
    }

    public interface OnAuthListener {
        void onCancel();

        void onError();

        void onComplete(String platform, HashMap<String, Object> res);
    }

    private OnAuthListener onAuthListener;

    public void setOnAuthListener(OnAuthListener l) {
        onAuthListener = l;
    }
}
