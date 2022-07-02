package com.yuyang.messi.ui.category;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyang.lib_base.helper.SelectPhotoHelper;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.BitmapUtil;
import com.yuyang.lib_base.utils.FileUtil;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-24
 * 创建时间: 21:14
 *
 * @author yuyang
 * @version 1.0
 */

public class CompressActivity extends AppBaseActivity {

    private EditText requestWidthEdit;
    private EditText requestHeightEdit;
    private EditText sizeLimitEdit;

    private TextView inSampleSizeText;
    private TextView selectImageText;
    private TextView compressText;

    private TextView originWHText;
    private TextView compressWHText;
    private ImageView normalImage;
    private ImageView compressImage;

    private final SelectPhotoHelper selectPhotoHelper = new SelectPhotoHelper(getActivity());

    private Uri imageUri;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_compress;
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
        headerLayout.showTitle("图片压缩");

        requestWidthEdit = findViewById(R.id.activity_compress_request_width);
        requestHeightEdit = findViewById(R.id.activity_compress_request_height);
        sizeLimitEdit = findViewById(R.id.activity_compress_compress_sizeLimitEdit);
        inSampleSizeText = findViewById(R.id.activity_compress_inSampleSize_text);
        selectImageText = findViewById(R.id.activity_compress_select_image);
        compressText = findViewById(R.id.activity_compress_compress);
        originWHText = findViewById(R.id.activity_compress_original_info);
        compressWHText = findViewById(R.id.activity_compress_compress_info);
        normalImage = findViewById(R.id.activity_compress_image_normal);
        compressImage = findViewById(R.id.activity_compress_image_compress);
    }

    private void initEvents() {
        selectImageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoHelper.setOnResultListener(new SelectPhotoHelper.OnResultListener() {
                    @Override
                    public void onPhotoResult(Bitmap bitmap, Uri uri, String photoPath) {
                        imageUri = uri;
                        getBitmapInfo();
                    }
                });
                selectPhotoHelper.takeGallery(null, false);
            }
        });
        compressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int reqWidth = Integer.parseInt(requestWidthEdit.getText().toString());
                    int reqHeight = Integer.parseInt(requestHeightEdit.getText().toString());
                    int sizeLimit = Integer.parseInt(sizeLimitEdit.getText().toString());

                    File compressFile = new File(StorageUtil.getPrivateCache(), "Compress.png");
                    BitmapUtil.compressImage(imageUri, compressFile.getAbsolutePath(), reqWidth, reqHeight, sizeLimit * 1024);
                    if (!compressFile.exists()) return;

                    Bitmap bitmap = BitmapFactory.decodeFile(compressFile.getAbsolutePath());

                    compressImage.getLayoutParams().width = compressImage.getMeasuredHeight() * bitmap.getWidth() / bitmap.getHeight();
                    compressImage.setImageBitmap(bitmap);
                    compressWHText.setText(bitmap.getWidth() + "×" + bitmap.getHeight() + " " + FileUtil.getFormatSize(compressFile.length()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getBitmapInfo() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBitmap = null;
        try {
            originalBitmap = BitmapFactory.decodeStream(MessiApp.getInstance().getContentResolver().openInputStream(imageUri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        originWHText.setText(options.outWidth + "×" + options.outHeight + " " + FileUtil.getFormatSize(new File(imageUri.getPath()).length()));
        normalImage.getLayoutParams().width = normalImage.getMeasuredHeight() * options.outWidth / options.outHeight;
        normalImage.setImageBitmap(originalBitmap);
    }
}

