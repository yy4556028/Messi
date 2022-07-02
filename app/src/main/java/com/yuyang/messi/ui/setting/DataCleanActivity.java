package com.yuyang.messi.ui.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.FileUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.messi.utils.data.DataExternalUtil;
import com.yuyang.messi.utils.data.DataInternalUtil;

public class DataCleanActivity extends AppBaseActivity {

    private View clearData;//清除数据
    private TextView clearDataSize;//清除数据大小

    private View clearInternalFiles;//清除内部文件
    private TextView clearInternalFilesSize;//内部文件大小

    private View clearInternalCache;//清除内部缓存
    private TextView clearInternalCacheSize;//内部缓存大小

    private View clearExternalFiles;//清除外部文件
    private TextView clearExternalFilesSize;//外部文件大小

    private View clearExternalCache;//清除外部缓存
    private TextView clearExternalCacheSize;//外部缓存大小

    private View clearExternalStorage;//清除sd根目录本应用文件
    private TextView clearExternalStorageSize;//sd根目录本应用文件大小

    @Override
    protected int getLayoutId() {
        return R.layout.activity_data_clean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSize();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("清除数据");

        clearData = findViewById(R.id.activity_data_clear_data);
        clearDataSize = findViewById(R.id.activity_data_clear_data_size);
        clearInternalFiles = findViewById(R.id.activity_data_clear_internal_files);
        clearInternalFilesSize = findViewById(R.id.activity_data_clear_internal_files_size);
        clearInternalCache = findViewById(R.id.activity_data_clear_internal_cache);
        clearInternalCacheSize = findViewById(R.id.activity_data_clear_internal_cache_size);
        clearExternalFiles = findViewById(R.id.activity_data_clear_external_files);
        clearExternalFilesSize = findViewById(R.id.activity_data_clear_external_files_size);
        clearExternalCache = findViewById(R.id.activity_data_clear_external_cache);
        clearExternalCacheSize = findViewById(R.id.activity_data_clear_external_cache_size);
        clearExternalStorage = findViewById(R.id.activity_data_clear_external_storage);
        clearExternalStorageSize = findViewById(R.id.activity_data_clear_external_storage_size);
    }

    public void initEvents() {
        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil.deleteDir(DataInternalUtil.getDatabaseFile(), false);
                FileUtil.deleteDir(DataInternalUtil.getSharedPreference(), false);
                initSize();
            }
        });
        clearInternalFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil.deleteDir(DataInternalUtil.getInternalFileDir(), false);
                initSize();
            }
        });
        clearInternalCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil.deleteDir(DataInternalUtil.getInternalCacheDir(), false);
                initSize();
            }
        });
        clearExternalFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil.deleteDir(DataExternalUtil.getExternalFilesDir(null), false);
                initSize();
            }
        });
        clearExternalCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil.deleteDir(DataExternalUtil.getExternalCacheDir(), false);
                initSize();
            }
        });
        clearExternalStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil.deleteDir(StorageUtil.getPublicFile(), false);
                initSize();
            }
        });
    }

    private void initSize() {
        long dataSize = FileUtil.getFolderSize(DataInternalUtil.getDatabaseFile()) + FileUtil.getFolderSize(DataInternalUtil.getSharedPreference());
        clearDataSize.setText(FileUtil.getFormatSize(dataSize));

        long internalFilesSize = FileUtil.getFolderSize(DataInternalUtil.getInternalFileDir());
        clearInternalFilesSize.setText(FileUtil.getFormatSize(internalFilesSize));

        long internalCacheSize = FileUtil.getFolderSize(DataInternalUtil.getInternalCacheDir());
        clearInternalCacheSize.setText(FileUtil.getFormatSize(internalCacheSize));

        long externalFilesSize = FileUtil.getFolderSize(DataExternalUtil.getExternalFilesDir(null));
        clearExternalFilesSize.setText(FileUtil.getFormatSize(externalFilesSize));

        long externalCacheSize = FileUtil.getFolderSize(DataExternalUtil.getExternalCacheDir());
        clearExternalCacheSize.setText(FileUtil.getFormatSize(externalCacheSize));

        long externalStorageSize = FileUtil.getFolderSize(StorageUtil.getPublicFile());
        clearExternalStorageSize.setText(FileUtil.getFormatSize(externalStorageSize));
    }
}
