package com.yuyang.messi.ui.common;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.LogUtil;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.view.AutoScrollTextView;
import com.yuyang.messi.view.CircleLayout;
import com.yuyang.messi.view.StickyView.StickyViewHelper;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class NullActivity extends AppBaseActivity {

    private final int[] imgs = new int[]{
            R.drawable.album
            , R.drawable.music
            , R.drawable.video
            , R.drawable.contacts
            , R.drawable.camera
            , R.drawable.camera};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
        test();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("Null");

        AutoScrollTextView a = findViewById(R.id.AutoScrollTextView);
        a.startScroll();

        CircleLayout circleMenu = findViewById(R.id.circle);
        circleMenu.setCanScroll(true);
    }

    private void initEvent() {
        StickyViewHelper stickyViewHelper = new StickyViewHelper(this, findViewById(R.id.image));
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title" + System.currentTimeMillis(), null);
        if (TextUtils.isEmpty(path)) {
            return Uri.parse("");
        }
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /**
     * 根据字体size获取字体高度
     */
    public static int getFontHeightBySize() {
        Paint paint = new Paint();
        paint.setTextSize(18);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) (Math.ceil(fm.descent - fm.ascent));
    }

    private void test() {
        String str = "2021/12/23 14:19";
        TextView textView1 = findViewById(R.id.textView1);
        TextView textView2 = findViewById(R.id.textView2);
        TextView textView3 = findViewById(R.id.textView3);

        textView1.setText(str);

        TextView textView = new TextView(this);
        textView.setTextSize(13);
        float newPaintWidth = textView.getPaint().measureText(str);
        textView2.getLayoutParams().width = Math.round(newPaintWidth);
        textView2.setText(str);

        TextPaint mPaint = new TextPaint();
        float textSize = PixelUtils.sp2px(13);
        mPaint.setTextSize(textSize);
        mPaint.setFakeBoldText(true);
        mPaint.setAntiAlias(true);
        textView3.getLayoutParams().width = Math.round(mPaint.measureText(str));
        textView3.setText(str);

        textView1.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("123123 1", textView1.getMeasuredWidth() + "");
                LogUtil.e("123123 2", textView2.getMeasuredWidth() + "");
                LogUtil.e("123123 3", textView3.getMeasuredWidth() + "");

                LogUtil.e("123123 4", textView1.getWidth() + "");
                LogUtil.e("123123 5", textView2.getWidth() + "");
                LogUtil.e("123123 6", textView3.getWidth() + "");
            }
        });

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("111", "yuyang111");
        hashMap.put(null, "yuyangnull");
        hashMap.put("222", "yuyang222");
        ToastUtil.showToast(hashMap.get(null));
    }
}
