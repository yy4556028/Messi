package com.yuyang.messi.ui.media;

import android.Manifest;
import android.app.AlertDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.VideoRecyclerAdapter;
import com.yuyang.messi.bean.VideoBean;
import com.yuyang.messi.helper.VideoHelper;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.messi.utils.PermissionUtil;
import com.yuyang.messi.view.Progress.CircleProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VideoActivity extends AppBaseActivity {

    public static final int SPAN_COUNT = 3;

    private SurfaceView surfaceView;
    private CircleProgressBar progressBar;
    private VideoRecyclerAdapter adapter;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private final ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                        List<String> deniedAskList = new ArrayList<>();
                        List<String> deniedNoAskList = new ArrayList<>();
                        for (Map.Entry<String, Boolean> stringBooleanEntry : result.entrySet()) {
                            if (!stringBooleanEntry.getValue()) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), stringBooleanEntry.getKey())) {
                                    deniedAskList.add(stringBooleanEntry.getKey());
                                } else {
                                    deniedNoAskList.add(stringBooleanEntry.getKey());
                                }
                            }
                        }
                        if (deniedAskList.isEmpty() && deniedNoAskList.isEmpty()) { //全通过
                            progressBar.setVisibility(View.VISIBLE);
                            VideoHelper.loadVideo(getActivity(), null, 0, 0, new VideoHelper.VideoResultCallback() {
                                @Override
                                public void onResultCallback(List<VideoBean> beanList) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    if (beanList == null || beanList.isEmpty()) {
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("Video No Found")
                                                .setMessage("Video No Found")
                                                .setCancelable(false)
                                                .setPositiveButton("确定", (dialog, which) -> finish())
                                                .create()
                                                .show();
                                    } else {
                                        adapter.setData(beanList);
                                        setSurfaceLocation(adapter.videoBeanList.get(0).getWidth(), adapter.videoBeanList.get(0).getHeight());
                                        play((adapter.videoBeanList.get(0)).getVideoUri());
                                    }
                                }
                            });
                        } else if (!deniedNoAskList.isEmpty()) {
                            PermissionUtil.showMissingPermissionDialog(this);
                        } else {
                            ToastUtil.showToast("您拒绝了部分权限");
                            finish();
                        }
                    }
            );

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionsLauncher.launch(new String[]{
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED});
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsLauncher.launch(new String[]{
                    Manifest.permission.READ_MEDIA_VIDEO});

        } else {
            permissionsLauncher.launch(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE});
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void initViews() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("Video");

        surfaceView = findViewById(R.id.activity_video_surface);
        progressBar = findViewById(R.id.activity_video_progress);
        RecyclerView recyclerView = findViewById(R.id.activity_video_recycleView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        recyclerView.setAdapter(adapter = new VideoRecyclerAdapter(this, null));
    }

    private void initEvents() {
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
        });

        adapter.setOnItemClickListener(new VideoRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                VideoBean videoBean = adapter.videoBeanList.get(position);
                setSurfaceLocation(videoBean.getWidth(), videoBean.getHeight());
                play(videoBean.getVideoUri());
            }

            @Override
            public void onItemLongClick(int position) {
                VideoBean videoBean = adapter.videoBeanList.get(position);
                ToastUtil.showToast(videoBean.getPath());
            }
        });
    }

    private void setSurfaceLocation(int width, int height) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
        if (width >= height) {
            params.width = CommonUtil.getScreenWidth();
            params.height = CommonUtil.getScreenWidth() * height / width;
            params.setMargins(0, (params.width - params.height) / 2, 0, (params.width - params.height) / 2);
        } else {
            params.width = CommonUtil.getScreenWidth() * width / height;
            params.height = CommonUtil.getScreenWidth();
            params.setMargins((params.height - params.width) / 2, 0, (params.height - params.width) / 2, 0);
        }
        surfaceView.setLayoutParams(params);
    }

    private void play(Uri videoUri) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //设置需要播放的视频
            mediaPlayer.setDataSource(this, videoUri);
            //把视频画面输出到SurfaceView
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.prepare();
            //播放
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
