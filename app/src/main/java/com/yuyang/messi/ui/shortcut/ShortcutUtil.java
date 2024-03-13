package com.yuyang.messi.ui.shortcut;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;

public class ShortcutUtil {

    /**
     * 不设置跳转页面，而统一跳转到一个路由页面，通过shortcutId判断
     */
    public static void addShortcut(Context context, String title, int iconRes, String shortcutId) {
//        Context context = App.getAppContext();

        Intent actionIntent = new Intent(context, ShortcutRouterActivity.class);
        actionIntent.setAction(Intent.ACTION_VIEW);
        actionIntent.putExtra(ShortcutRouterActivity.KEY_SHORTCUT_ID, shortcutId);

        actionIntent.addCategory(Intent.CATEGORY_LAUNCHER);//添加categoryCATEGORY_LAUNCHER 应用被卸载时快捷方式也随之删除
//        actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            for (ShortcutInfo pinnedShortcut : shortcutManager.getPinnedShortcuts()) {
                if (TextUtils.equals(pinnedShortcut.getId(), shortcutId)) {
                    ToastUtil.showToast("已添加");
                    return;
                }
            }

            ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(context, shortcutId)
                .setShortLabel(title)
                .setLongLabel(title)
                .setIcon(IconCompat.createWithResource(context, iconRes))
                .setIntent(actionIntent)
                .build();

            //当添加快捷方式的确认弹框弹出来时，将被回调
            PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(
                context,
                0,
                ShortcutManagerCompat.createShortcutResultIntent(context, shortcutInfo),
                PendingIntent.FLAG_UPDATE_CURRENT);
            ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, shortcutCallbackIntent.getIntentSender());
        } else {

            Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            // 不允许重复创建（不一定有效）小米系统无效果 不是根据快捷方式的名字判断重复
            // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
            // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
            // 屏幕上没有空间时会提示
            // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式
            shortcutIntent.putExtra("duplicate", false);
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);//快捷方式的名字
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, iconRes));//快捷方式的图标

            //点击快捷方式打开的页面
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
            context.sendBroadcast(shortcutIntent); //设置完毕后发送广播给系统。
        }
    }

    /**
     * 添加快捷方式
     * @deprecated
     */
    @Deprecated
    public static void addShortcut1(String title, int iconRes, Class launcherClass, String shortcutId) {
        Context context = MessiApp.getInstance();

        Intent actionIntent = new Intent(context, launcherClass);
        actionIntent.setAction(Intent.ACTION_MAIN);
//        actionIntent.putExtra("shortcutId", shortcutId);
        actionIntent.addCategory(Intent.CATEGORY_LAUNCHER);//添加categoryCATEGORY_LAUNCHER 应用被卸载时快捷方式也随之删除
//        actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            for (ShortcutInfo pinnedShortcut : shortcutManager.getPinnedShortcuts()) {
                if (TextUtils.equals(pinnedShortcut.getId(), shortcutId)) {
                    ToastUtil.showToast("已添加");
                    return;
                }
            }

            ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(context, shortcutId)
                .setShortLabel(title)
                .setLongLabel(title)
                .setIcon(IconCompat.createWithResource(context, iconRes))
                .setIntent(actionIntent)
                .build();

            //当添加快捷方式的确认弹框弹出来时，将被回调
            PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(
                context,
                0,
                ShortcutManagerCompat.createShortcutResultIntent(context, shortcutInfo),
                PendingIntent.FLAG_UPDATE_CURRENT);
            ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, shortcutCallbackIntent.getIntentSender());
        } else {
            Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            // 不允许重复创建（不一定有效）小米系统无效果 不是根据快捷方式的名字判断重复
            // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
            // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
            // 屏幕上没有空间时会提示
            // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式
            shortcutIntent.putExtra("duplicate", false);
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);//快捷方式的名字
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, iconRes));//快捷方式的图标

            //点击快捷方式打开的页面
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
            context.sendBroadcast(shortcutIntent); //设置完毕后发送广播给系统。
        }
    }

    /**
     * 删除当前应用的桌面快捷方式 在android7.0 上测试无效
     */
    public static void delShortcut(String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        } else {
            Intent shortcutIntent = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
            shortcutIntent.putExtra("duplicate", false);// 不允许重复创建（不一定有效）小米系统无效果
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);//快捷方式的名字
            Intent.ShortcutIconResource shortcutIconResource = Intent.ShortcutIconResource.fromContext(MessiApp.getInstance(), R.drawable.ssdk_oks_classic_alipay);
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIconResource);//快捷方式的图标
            //点击快捷方式打开的页面
            Intent actionIntent = new Intent(Intent.ACTION_MAIN);
            actionIntent.setClass(MessiApp.getInstance(), ShortcutRouterActivity.class);
            actionIntent.addCategory(Intent.CATEGORY_LAUNCHER);//添加categoryCATEGORY_LAUNCHER 应用被卸载时快捷方式也随之删除
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
            MessiApp.getInstance().sendBroadcast(shortcutIntent); //设置完毕后发送广播给系统。
        }
    }

    /**
     * 判断当前应用在桌面是否有桌面快捷方式
     */
    public static boolean hasShortcut(String title) {
        boolean result = false;

        final String uriStr;
        if (Build.VERSION.SDK_INT < 8) {
            uriStr = "content://com.android.launcher.settings/favorites?notify=true";
        } else if (Build.VERSION.SDK_INT < 19) {
            uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
        } else {
            uriStr = "content://com.android.launcher3.settings/favorites?notify=true";
        }
        final Uri CONTENT_URI = Uri.parse(uriStr);
        final Cursor c = MessiApp.getInstance().getContentResolver().query(CONTENT_URI, null,
            "title=?", new String[]{title}, null);
        if (c != null && c.getCount() > 0) {
            result = true;
        }
        return result;
    }
}
