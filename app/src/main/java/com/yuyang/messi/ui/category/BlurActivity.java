package com.yuyang.messi.ui.category;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yamap.lib_chat.keyboard.utils.KeyboardUtil;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.BitmapUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.HookUtil;

public class BlurActivity extends AppBaseActivity {

    private EditText editRadius;
    private TextView blurBtn;

    private ImageView normalImage;
    private ImageView blurImage;

    private Bitmap srcBitmap;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_blur;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvents();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("Blur");

        editRadius = findViewById(R.id.activity_blur_edit_radius);
        blurBtn = findViewById(R.id.activity_blur_text_blur);
        normalImage = findViewById(R.id.activity_blur_image_normal);
        blurImage = findViewById(R.id.activity_blur_image_blur);

        normalImage.setImageResource(R.drawable.start_bc);
        getBlurImage();
    }

    private void initEvents() {
        blurBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtil.showHideIme(getActivity(), false);
                String str = editRadius.getText().toString();
                if (TextUtils.isEmpty(str)) {
                    str = "0";
                }
                int radius;
                try {
                    radius = Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    radius = 0;
                }

                if (radius == 0) {
                    blurImage.setBackground(null);
                } else {
                    blurImage(radius, blurImage);
                }
            }
        });
//        blurBtn.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                ToastUtil.showToast("Long Blur");
//                return false;
//            }
//        });
        try {
            HookUtil.hookViewClickTest(blurBtn);
        } catch (Exception e) {
            ToastUtil.showToast("hook失败\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void getBlurImage() {
        normalImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                normalImage.getViewTreeObserver().removeOnPreDrawListener(this);
                normalImage.buildDrawingCache();

                srcBitmap = normalImage.getDrawingCache();
                return true;
            }
        });
    }

    /**
     * 先通过缩小图片，使其丢失一些像素点，
     * 接着进行模糊化处理，然后再放大到原来尺寸。
     * 由于图片缩小后再进行模糊处理，
     * 需要处理的像素点和半径都变小，
     * 从而使得模糊处理速度加快
     *
     * @param radius
     * @param view
     */
    private void blurImage(int radius, View view) {
        long startMs = System.currentTimeMillis();

        float scaleFactor = 1;
        if (radius > 1) {
            int minLength = Math.min(view.getMeasuredWidth(), view.getMeasuredHeight());
            scaleFactor = Math.min(minLength, radius / 2);
            radius = (int) (radius / scaleFactor);
        }

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        overlay = BitmapUtil.fastBlur(getActivity(), overlay, radius);
        view.setBackground(new BitmapDrawable(getResources(), overlay));

        ToastUtil.showToast("cost " + (System.currentTimeMillis() - startMs) + "ms");
    }

}

