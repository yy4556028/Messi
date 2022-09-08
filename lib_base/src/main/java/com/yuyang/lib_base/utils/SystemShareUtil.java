package com.yuyang.lib_base.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.yuyang.lib_base.R;

import java.util.ArrayList;

public class SystemShareUtil {

    public static void shareText(Context ctx, String title, String content) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");// 纯文本
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(Intent.createChooser(sendIntent, ctx.getString(R.string.app_name)));
    }

    public static void shareImage(Context ctx, Uri uri) {
        Intent sendIntent = new Intent();
        sendIntent.setType("image/jpeg");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "msg");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "title");
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(Intent.createChooser(sendIntent, "分享至"));
    }

    public static void shareImageList(Context ctx, ArrayList<Uri> uris) {
        Intent sendIntent = new Intent();
        sendIntent.setType("image/*");
        sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uris);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "msg");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "title");
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(Intent.createChooser(sendIntent, "分享至"));
    }
}
