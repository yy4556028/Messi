package com.yuyang.lib_base.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.yuyang.lib_base.BaseApp;

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class ClipboardUtil {

    private static ClipboardManager clipboardManager;


    private static void instance() {
        if (clipboardManager == null) {
            // 获取系统剪贴板
            clipboardManager = (ClipboardManager) BaseApp.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
        }
    }

    /**
     * 为剪切板设置内容
     *
     * @param text
     */
    public static void setText(CharSequence text) {
        instance();
        // Creates a new text crop to put on the clipboard
        // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
        ClipData clip = ClipData.newPlainText(null, text);

        // Set the clipboard's primary crop.
        // 把数据集设置（复制）到剪贴板
        clipboardManager.setPrimaryClip(clip);
    }

    /**
     * 获取剪切板的内容
     *
     * @param context
     * @return
     */
    public static CharSequence getText(Context context) {
        StringBuilder sb = new StringBuilder();
        instance();
        if (!clipboardManager.hasPrimaryClip()) {
            return sb.toString();
        } else {
            ClipData clipData = (clipboardManager).getPrimaryClip();
            int count = clipData.getItemCount();

            for (int i = 0; i < count; ++i) {

                ClipData.Item item = clipData.getItemAt(i);
                CharSequence str = item.coerceToText(context);
                sb.append(str);
            }
        }

        return sb.toString();
    }

    public static String getCopy(Context context) {
        // 获取系统剪贴板
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 返回数据
        ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            // 从数据集中获取（粘贴）第一条文本数据
            return clipData.getItemAt(0).getText().toString();
        }
        return null;
    }
}
