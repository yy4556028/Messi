package com.yuyang.messi.view.scroll.folder;

import android.content.Context;
import android.content.res.Resources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuyang.messi.R;
import com.yuyang.messi.view.scroll.CustomLinearLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author:yuy
 * Date:2017/1/6
 */
public class FolderView extends LinearLayout {

    private static int ITEM_LENGTH_DP = 36;
    private static int ITEM_LENGTH_PX = (int) (ITEM_LENGTH_DP * (Resources.getSystem().getDisplayMetrics().densityDpi / 160) + 0.5f);

    private View folderLyt;
    private ImageView arrowImage;
    private ImageView iconImage;
    private TextView nameText;
    private RecyclerView listRecycler;

    private int textColor;

    private File rootFile;

    public FolderView(Context context) {
        this(context, null);
    }

    public FolderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FolderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        textColor = ContextCompat.getColor(getContext(), R.color.white);

        inflate(getContext(), R.layout.view_folder, this);
        folderLyt = findViewById(R.id.view_folder_lyt);
        arrowImage = (ImageView) findViewById(R.id.view_folder_arrow);
        iconImage = (ImageView) findViewById(R.id.view_folder_icon);
        nameText = (TextView) findViewById(R.id.view_folder_name);
        listRecycler = (RecyclerView) findViewById(R.id.view_folder_recycler);

        arrowImage.getLayoutParams().width = ITEM_LENGTH_PX;
        arrowImage.getLayoutParams().height = ITEM_LENGTH_PX;
        arrowImage.setPadding(ITEM_LENGTH_PX / 6, ITEM_LENGTH_PX / 6, ITEM_LENGTH_PX / 6, ITEM_LENGTH_PX / 6);

        iconImage.getLayoutParams().width = ITEM_LENGTH_PX;
        iconImage.getLayoutParams().height = ITEM_LENGTH_PX;
        iconImage.setPadding(ITEM_LENGTH_PX / 6, ITEM_LENGTH_PX / 6, ITEM_LENGTH_PX / 6, ITEM_LENGTH_PX / 6);

        nameText.setTextColor(textColor);
    }

    public void initData(File rootFile) {
        this.rootFile = rootFile;
        nameText.setText(rootFile.getName());
        if (rootFile.isDirectory()) {
            arrowImage.setVisibility(VISIBLE);
            iconImage.setImageResource(R.drawable.folder_close);
//
            folderLyt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (arrowImage.getRotation() == 0) {
                        if (listRecycler.getLayoutManager() == null) {
                            listRecycler.setLayoutManager(new CustomLinearLayoutManager(getContext(), VERTICAL, false));
                            listRecycler.setAdapter(new FolderViewAdapter(FolderView.this.rootFile));
                        }
                        listRecycler.setVisibility(VISIBLE);
                        iconImage.setImageResource(R.drawable.folder_open);
                        arrowImage.setRotation(90);
//                        ObjectAnimator.ofFloat(arrowImage, "rotation", 0, 90).start();
                    } else if (arrowImage.getRotation() == 90) {
                        listRecycler.setVisibility(GONE);
                        iconImage.setImageResource(R.drawable.folder_close);
                        arrowImage.setRotation(0);
//                        ObjectAnimator.ofFloat(arrowImage, "rotation", 90, 0).start();
                    }

                }
            });

        } else {
            arrowImage.setVisibility(INVISIBLE);
            String fileName = rootFile.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

            if (mimeType == null) {
                iconImage.setImageResource(R.drawable.folder_unknown);
            } else if (mimeType.startsWith("text")){
                iconImage.setImageResource(R.drawable.folder_txt);
            } else if (mimeType.startsWith("image")){
                iconImage.setImageResource(R.drawable.folder_image);
            } else if (mimeType.startsWith("audio")) {
                iconImage.setImageResource(R.drawable.folder_audio);
            } else if (mimeType.startsWith("video")) {
                iconImage.setImageResource(R.drawable.folder_video);
            } else if ((mimeType.startsWith("application/vnd.android.package-archive"))) {
                iconImage.setImageResource(R.drawable.folder_apk);
            } else {
                iconImage.setImageResource(R.drawable.folder_unknown);
            }

            folderLyt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (folderListener != null) {
                        folderListener.onFileClick(mimeType, FolderView.this.rootFile);
                    }
                }
            });
        }
    }

    private class FolderViewAdapter extends RecyclerView.Adapter<FolderViewAdapter.MyHolder> {

        private List<File> fileList;

        FolderViewAdapter(File rootFile) {
            if (rootFile.isDirectory()) {
                File[] fileArray = rootFile.listFiles();
                if (fileArray != null && fileArray.length > 0) {
                    fileList = new ArrayList<>();
                    fileList.addAll(Arrays.asList(fileArray));
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyHolder(new FolderView(getContext()));
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            holder.folderView.initData(fileList.get(position));
        }

        @Override
        public int getItemCount() {
            return fileList == null ? 0 : fileList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            FolderView folderView;

            public MyHolder(FolderView folderView) {
                super(folderView);
                this.folderView = folderView;
            }
        }
    }

    public interface OnFolderListener{
        void onFileClick(String mimeType, File file);
    }

    private static OnFolderListener folderListener;

    public void setOnFolderListener(OnFolderListener listener) {
        folderListener = listener;
    }
}
