package com.yuyang.messi.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.util.Log;

import com.google.gson.Gson;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.bean.UserBean;

public class AccountUtil {

    public static final String ACCOUNT_TYPE_PHONE = "phone";
    public static final String ACCOUNT_TYPE_WEIBO = "sinaweibo";
    public static final String ACCOUNT_TYPE_WECHAT = "wechat";
    public static final String ACCOUNT_TYPE_QQ = "qq";

    private static final String ACCOUNT_TYPE = "com.yuyang.songhq.account_type";
    private static final String AUTH_TYPE = "songhq.authentication_type";

    private static AccountManager myAccountManager;

    public static AccountManager getMyAccountManager() {
        if (myAccountManager == null) {
            myAccountManager = AccountManager.get(MessiApp.getInstance());
        }
        return myAccountManager;
    }

    public static void addAccount(UserBean userBean) {
        clearAccount();
        AccountManager accMgr = getMyAccountManager();

        Account account = new Account(userBean.getUsername(), ACCOUNT_TYPE);
        accMgr.addAccountExplicitly(account, null, null);

        accMgr.setAuthToken(account, AUTH_TYPE, userBean.getToken());
        accMgr.setUserData(account, UserBean.TAG, new Gson().toJson(userBean));
    }

    public static void updateAccount(UserBean userBean) {
        AccountManager accMgr = getMyAccountManager();
        Account[] accounts = accMgr.getAccountsByType(ACCOUNT_TYPE);
        if (accounts.length > 0) {
            try {
                accMgr.setUserData(accounts[0], UserBean.TAG, new Gson().toJson(userBean));
            } catch (Exception e) {
                Log.e("update account error", e.getMessage());
            }
        }
    }

    public static void createMyAccount() throws Exception {
        AccountManager accMgr = getMyAccountManager();
        Account[] accounts = accMgr.getAccountsByType(ACCOUNT_TYPE);
        if (accounts.length > 0) {
            MemberController.createMe(accMgr.getUserData(accounts[0], UserBean.TAG));
        } else {
            throw new Exception();
        }
    }

    public static void clearAccount() {
//        if (MemberController.getMe() != null) {
//            switch(MemberController.getMe().getAccountType()) {
//                case ACCOUNT_TYPE_WEIBO:
//                    Platform sinaWeibo = ShareSDK.getPlatform(MuzhikeApp.getAppContext(), SinaWeibo.NAME);
//                    if (sinaWeibo.isValid ()) {
//                        sinaWeibo.removeAccount();
//                    }
//                    break;
//                case ACCOUNT_TYPE_QQ:
//                    Platform qq = ShareSDK.getPlatform(MuzhikeApp.getAppContext(), QQ.NAME);
//                    if (qq.isValid ()) {
//                        qq.removeAccount();
//                    }
//                    break;
//                case ACCOUNT_TYPE_WECHAT:
//                    Platform weChat = ShareSDK.getPlatform(MuzhikeApp.getAppContext(), Wechat.NAME);
//                    if (weChat.isValid ()) {
//                        weChat.removeAccount();
//                    }
//                    break;
//            }
//        }

        AccountManager accMgr = getMyAccountManager();
        Account[] accounts = accMgr.getAccountsByType(ACCOUNT_TYPE);
        for (int i = 0; i < accounts.length; i++) {
            accMgr.removeAccount(accounts[i], null, null);
        }
    }
}
