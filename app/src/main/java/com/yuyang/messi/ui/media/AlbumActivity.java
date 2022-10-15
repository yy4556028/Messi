package com.yuyang.messi.ui.media;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.lib_base.helper.SelectPhotoHelper;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.header.HeaderRightBean;
import com.yuyang.lib_base.ui.header.HeaderRightItem;
import com.yuyang.lib_base.utils.CollectionUtil;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.AlbumDirListAdapter;
import com.yuyang.messi.adapter.AlbumRecyclerAdapter;
import com.yuyang.messi.bean.ImageBean;
import com.yuyang.messi.bean.PhotoDirectory;
import com.yuyang.messi.helper.AlbumHelper;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.ui.common.photo.PhotoGalleryActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-23
 * 创建时间: 15:00
 * AlbumActivity: 相册
 *
 * @author yuyang
 * @version 1.0
 */
public class AlbumActivity extends AppBaseActivity {

    public final static String SELECTED_PHOTOS = "SELECTED_PHOTOS";
    public final static String EXTRA_ORIGINAL_PHOTOS = "ORIGINAL_PHOTOS";//已选中图片
    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String EXTRA_COLUMN = "EXTRA_COLUMN";
    public final static String EXTRA_GIF = "EXTRA_GIF";
    public final static String EXTRA_CAMERA = "EXTRA_CAMERA";
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";
    public final static String EXTRA_SUPPORT_PREVIEW = "SUPPORT_PREVIEW";//支持预览，点击右上角选择 | 不支持，点击任何位置选中

    private final static int DEFAULT_MAX_COUNT = 9;

    private HeaderRightItem menuDoneItem;//完成

    private int maxCount = DEFAULT_MAX_COUNT;
    private int column;
    private ArrayList<ImageBean> originalPhotos = null;
    private boolean showGif = true;
    private boolean showCamera = true;
    private boolean supportPreview = true;

    private AlbumRecyclerAdapter recyclerAdapter;

    private RelativeLayout bottomLyt;
    private TextView chooseDirText;
    private TextView totalCountText;

    private ListPopupWindow listPopupWindow;
    private AlbumDirListAdapter listAdapter;

    private final List<PhotoDirectory> directories = new ArrayList<>();//所有photos的路径

    private final SelectPhotoHelper selectPhotoHelper = new SelectPhotoHelper(getActivity());

    public static Intent getLaunchIntent(Context context, int maxCount, boolean showGif, boolean showCamera, boolean supportPreview, ArrayList<ImageBean> originalImageBeans) {
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.putExtra(AlbumActivity.EXTRA_MAX_COUNT, maxCount);
        intent.putExtra(AlbumActivity.EXTRA_GIF, showGif);
        intent.putExtra(AlbumActivity.EXTRA_CAMERA, showCamera);
        intent.putExtra(AlbumActivity.EXTRA_ORIGINAL_PHOTOS, originalImageBeans);
        intent.putExtra(EXTRA_SUPPORT_PREVIEW, supportPreview);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_album;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initIntent();
        initViews();
        initEvents();

        Bundle mediaStoreArgs = new Bundle();
        mediaStoreArgs.putBoolean(EXTRA_SHOW_GIF, showGif);

        AlbumHelper.loadPhoto(this,
                mediaStoreArgs,
                new AlbumHelper.PhotosResultCallback() {
                    @Override
                    public void onResultCallback(List<PhotoDirectory> dirs) {
                        directories.clear();
                        directories.addAll(dirs);
                        recyclerAdapter.notifyDataSetChanged();
                        listAdapter.notifyDataSetChanged();

                        chooseDirText.setText(directories.get(0).getName());
                        totalCountText.setText(directories.get(0).getImageBeans().size() + "张");
                    }
                });
    }

    private void initIntent() {
        maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
        column = getIntent().getIntExtra(EXTRA_COLUMN, 3);
        showGif = getIntent().getBooleanExtra(EXTRA_GIF, true);
        showCamera = getIntent().getBooleanExtra(EXTRA_CAMERA, true);
        supportPreview = getIntent().getBooleanExtra(EXTRA_SUPPORT_PREVIEW, true);
        originalPhotos = (ArrayList<ImageBean>) getIntent().getSerializableExtra(EXTRA_ORIGINAL_PHOTOS);
    }

    private void initViews() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showTitle("相册");
        headerLayout.showLeftBackButton();

        List<HeaderRightBean> rightBeanList = new ArrayList<>();
        rightBeanList.add(new HeaderRightBean("完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(SELECTED_PHOTOS, recyclerAdapter.getSelectedImageBeans());
                setResult(RESULT_OK, intent);
                finish();
            }
        }));
        menuDoneItem = headerLayout.setRight(rightBeanList).get(0);

        if (CollectionUtil.isEmpty(originalPhotos)) {
            menuDoneItem.setEnabled(false);
        } else {
            menuDoneItem.setEnabled(true);
            menuDoneItem.setText(String.format("完成(%s/%s)", originalPhotos.size(), maxCount));
        }

        RecyclerView recyclerView = findViewById(R.id.activity_album_recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, column);
        //调用以下方法让RecyclerView的第一个条目仅为1列
//        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                //如果位置是0，那么这个条目将占用SpanCount()这么多的列数，再此也就是3
//                //而如果不是0，则说明不是Header，就占用1列即可
//                return recyclerAdapter.isHeader(position) ? layoutManager.getSpanCount() : 1;
//            }
//        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter = new AlbumRecyclerAdapter(
                this,
                directories,
                originalPhotos,
                column,
                showCamera,
                supportPreview));

        bottomLyt = findViewById(R.id.activity_album_bottom);
        chooseDirText = findViewById(R.id.activity_album_choose_dir);
        totalCountText = findViewById(R.id.activity_album_total_count);

        listPopupWindow = new ListPopupWindow(getActivity());
        listPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        listPopupWindow.setAnchorView(bottomLyt);
        listPopupWindow.setAdapter(listAdapter = new AlbumDirListAdapter(getActivity(), directories));
        listPopupWindow.setModal(true);
        listPopupWindow.setDropDownGravity(Gravity.BOTTOM);
    }

    private void initEvents() {

        recyclerAdapter.setOnGalleryGirdListener(new AlbumRecyclerAdapter.OnGalleryGirdListener() {
            @Override
            public void onCameraClick() {
                selectPhotoHelper.setOnResultListener(new SelectPhotoHelper.OnResultListener() {
                    @Override
                    public void onPhotoResult(Bitmap bitmap, Uri uri, String path) {
                        PhotoDirectory directory = directories.get(0);
                        ImageBean imageBean = new ImageBean(uri, path);
                        if (directory.getImageBeans().contains(imageBean)) {
                            directory.getImageBeans().remove(imageBean);
                        }
                        directory.getImageBeans().add(0, imageBean);
                        directory.setCoverImageBean(imageBean);
                        recyclerAdapter.notifyDataSetChanged();
                    }
                });
                selectPhotoHelper.takePhoto(new File(StorageUtil.getExternalFilesDir(StorageUtil.PICTURE), "Album_Capture.jpg"), true);
            }

            @Override
            public void onPhotoClick(View v, int position, boolean showCamera) {
                final int index = showCamera ? position - 1 : position;
                PhotoGalleryActivity.launchActivity(getActivity(), recyclerAdapter.getCurrentPhotos(), index);
//                PhotoShowActivity.launchActivity(getActivity(), recyclerAdapter.getCurrentPhotos().get(index), v);
            }

            @Override
            public boolean OnItemCheck(int position, ImageBean imageBean, boolean isCheck, int selectedItemCount) {
                int total = selectedItemCount + (isCheck ? -1 : 1);

                menuDoneItem.setEnabled(total > 0);

                if (maxCount <= 1) {//如果是单选或无选择 清空
                    ArrayList<ImageBean> imageBeans = recyclerAdapter.getSelectedImageBeans();
                    if (!imageBeans.contains(imageBean)) {
                        imageBeans.clear();
                        recyclerAdapter.notifyDataSetChanged();
                    }
                    return true;
                }

                if (total > maxCount) {
                    ToastUtil.showToast(String.format("你最多可以选择%s张图片", maxCount));
                    return false;
                }
                menuDoneItem.setText(String.format("完成(%s/%s)", total, maxCount));
                return true;
            }
        });

        bottomLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPopupWindow.isShowing()) {
                    listPopupWindow.dismiss();
                } else if (!getActivity().isFinishing()) {
                    int count = listAdapter.getCount();
                    count = count < 4 ? count : 4;
                    listPopupWindow.setHeight((count * PixelUtils.dp2px(80)));
                    listPopupWindow.show();
                }
            }
        });

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();

                PhotoDirectory directory = directories.get(position);
                chooseDirText.setText(directory.getName());
                totalCountText.setText(directory.getImageBeans().size() + "张");
                recyclerAdapter.setCurrentDirectoryIndex(position);
                recyclerAdapter.notifyDataSetChanged();
            }
        });

    }
}



