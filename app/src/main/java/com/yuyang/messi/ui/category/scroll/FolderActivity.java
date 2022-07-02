package com.yuyang.messi.ui.category.scroll;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yuyang.lib_base.utils.FileUtil;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.lib_base.utils.SystemBarUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.view.scroll.folder.FolderView;

import java.io.File;

public class FolderActivity extends AppBaseActivity {

    private FolderView folderView;
    private View rootLyt;
    private View leftView;
    private View anchorView;
    private FrameLayout showLyt;

    private TextView showText;
    private ImageView imageView;

    private int downX;
    private int preWidth;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_folder;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    @Override
    public void setStatusBar() {
        SystemBarUtil.fullScreen_immersive(getActivity(), false, false, true, false, true);
    }

    private void initView() {
        folderView = findViewById(R.id.activity_folder_folderView);
        rootLyt = findViewById(R.id.activity_folder_rootLyt);
        leftView = findViewById(R.id.activity_folder_leftLyt);
        anchorView = findViewById(R.id.activity_folder_anchor);
        showLyt = findViewById(R.id.activity_folder_show_lyt);
        showText = findViewById(R.id.activity_folder_show_textView);
        imageView = findViewById(R.id.activity_folder_show_imageView);

        folderView.initData(StorageUtil.getExternalFilesDir(null));
    }

    private void initEvent() {
        folderView.setOnFolderListener(new FolderView.OnFolderListener() {
            @Override
            public void onFileClick(String mimeType, File file) {
                String fileName = file.getAbsolutePath();
                showNull();
                if (fileName.toLowerCase().endsWith(".txt") ||
                        fileName.toLowerCase().endsWith(".java")) {
                    showTxt(fileName);
                } else if (fileName.toLowerCase().endsWith(".jpg") ||
                        fileName.toLowerCase().endsWith(".jpeg") ||
                        fileName.toLowerCase().endsWith(".png") ||
                        fileName.toLowerCase().endsWith(".bmp")) {
                    showImage(fileName);
                }
            }
        });
        anchorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float currentX = event.getRawX();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        downX = (int) event.getRawX();
                        preWidth = leftView.getLayoutParams().width;
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP: {
                        leftView.getLayoutParams().width = (int) (preWidth + (currentX - downX));

                        if (leftView.getLayoutParams().width < 0) {
                            leftView.getLayoutParams().width = 0;
                        } else if (leftView.getLayoutParams().width > rootLyt.getMeasuredWidth() - anchorView.getMeasuredWidth()) {
                            leftView.getLayoutParams().width = rootLyt.getMeasuredWidth() - anchorView.getMeasuredWidth();
                        }
                        leftView.requestLayout();
                        break;
                    }
                }
                return true;
            }
        });
    }

    private void showNull() {
        showText.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
    }

    private void showTxt(String fileName) {
        showText.setText(FileUtil.readFile(fileName));
        showText.setVisibility(View.VISIBLE);
    }

    private void showImage(String fileName) {
        Glide.with(this).load(new File(fileName)).into(imageView);
        imageView.setVisibility(View.VISIBLE);
    }

}