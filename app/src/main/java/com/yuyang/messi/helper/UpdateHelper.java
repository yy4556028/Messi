package com.yuyang.messi.helper;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.Formatter;

public class UpdateHelper {

    public static void update(
            Context context,
            String newVersion,
            long size,
            String updateContent,
            boolean isCritical,
            final String url) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("应用更新");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        String sizeStr = Formatter.formatShortFileSize(context, size);
        String content = String.format("最新版本：%1$s\n新版本大小：%2$s\n", newVersion, sizeStr);
        if (!TextUtils.isEmpty(updateContent)) {
            content += "\n更新内容\n" + updateContent;
        }
        if (isCritical) {
            dialog.setMessage("您需要更新应用才能继续使用\n\n" + content);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    downloadApk(url);
                }
            });
        } else {
            dialog.setMessage(content);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "立即更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "以后再说", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                }
            });
//            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "忽略该版", listener);
        }
        dialog.show();
    }

    private static void downloadApk(String url) {

    }
}
