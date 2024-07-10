package com.yuyang.messi.ui.media;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.rtmp.RtmpDataSource;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;
import androidx.media3.extractor.Extractor;
import androidx.media3.extractor.ExtractorsFactory;
import androidx.media3.extractor.flv.FlvExtractor;
import androidx.media3.extractor.mp4.Mp4Extractor;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.VideoRecyclerAdapter;
import com.yuyang.messi.bean.VideoBean;
import com.yuyang.messi.databinding.ActivityVideoExoBinding;
import com.yuyang.messi.helper.VideoHelper;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.util.List;

public class VideoExoActivity extends AppBaseActivity {

    public static final String TAG = "VideoExoActivity";

    private ActivityVideoExoBinding binding;

    public static final int SPAN_COUNT = 3;

    private VideoRecyclerAdapter adapter;
    private ExoPlayer mExoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoExoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
        initEvents();
        initPlayer();
        binding.circleProgressBar.setVisibility(View.VISIBLE);
        VideoHelper.loadVideo(getActivity(), null, 0, 0, new VideoHelper.VideoResultCallback() {
            @Override
            public void onResultCallback(List<VideoBean> beanList) {
                binding.circleProgressBar.setVisibility(View.INVISIBLE);
                if (beanList == null || beanList.isEmpty()) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Video No Found")
                            .setMessage("Video No Found")
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create()
                            .show();
                } else {
                    adapter.setData(beanList);
                    setPlayerLocation(adapter.videoBeanList.get(0).getWidth(), adapter.videoBeanList.get(0).getHeight());
                    play((adapter.videoBeanList.get(0)).getVideoUri());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
    }

    private void initViews() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("VideoExo");

        binding.recycleView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        binding.recycleView.setAdapter(adapter = new VideoRecyclerAdapter(this, null));
    }

    private void initEvents() {
        binding.exoPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExoPlayer.isPlaying()) {
                    mExoPlayer.pause();
                } else {
                    mExoPlayer.play();
                }
            }
        });

        adapter.setOnItemClickListener(new VideoRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                VideoBean videoBean = adapter.videoBeanList.get(position);
                setPlayerLocation(videoBean.getWidth(), videoBean.getHeight());
                play(videoBean.getVideoUri());
            }

            @Override
            public void onItemLongClick(int position) {
                VideoBean videoBean = adapter.videoBeanList.get(position);
                ToastUtil.showToast(videoBean.getPath());
            }
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initPlayer() {
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);
//        renderersFactory.setMediaCodecSelector(MediaCodecSelector.DEFAULT);
//        renderersFactory.setMediaCodecSelector((mimeType, requiresSecureDecoder, requiresTunnelingDecoder) ->
//        {
//            var decoderInfoList = MediaCodecUtil.getDecoderInfos(
//                    mimeType,//video/avc
//                    requiresSecureDecoder,
//                    requiresTunnelingDecoder
//            );
//            List<MediaCodecInfo> filterList = new ArrayList<>();
//            for (MediaCodecInfo mediaCodecInfo : decoderInfoList) {
//                Log.d("MediaCodecInfo", mimeType + "Found hardware decoder: " + mediaCodecInfo.name);
//                if (mediaCodecInfo.name.startsWith("OMX")) {
//                    filterList.add(mediaCodecInfo);
//                }
//                if (mediaCodecInfo.hardwareAccelerated) {
//                }
//            }
//            return filterList;
//        });
//        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);

        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
//                .setAllocator(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
                .setBackBuffer(1500, true)
                .setBufferDurationsMs(500, 600, 500, 500)
//                .setTargetBufferBytes(C.LENGTH_UNSET)
//                .setPrioritizeTimeOverSizeThresholds(true)
                .build();
        ExtractorsFactory extractorsFactory = new ExtractorsFactory() {
            @NonNull
            @Override
            public Extractor[] createExtractors() {
                return new Extractor[]{new Mp4Extractor(), new FlvExtractor()};
            }
        };

        MediaSource.Factory mediaSourceFactory =
                new DefaultMediaSourceFactory(this, extractorsFactory);

        AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(
                this,
                videoTrackSelectionFactory
        );
        trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd());

        binding.exoPlayerView.setShutterBackgroundColor(Color.WHITE);
        mExoPlayer = new ExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(this))
                .setLoadControl(loadControl)
                .setRenderersFactory(renderersFactory)
                .setMediaSourceFactory(mediaSourceFactory)
                .build();
        binding.exoPlayerView.setPlayer(mExoPlayer);
        binding.exoPlayerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
    }

    private void setPlayerLocation(int width, int height) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) binding.exoPlayerView.getLayoutParams();
        if (width >= height) {
            params.width = CommonUtil.getScreenWidth();
            params.height = CommonUtil.getScreenWidth() * height / width;
            params.setMargins(0, (params.width - params.height) / 2, 0, (params.width - params.height) / 2);
        } else {
            params.width = CommonUtil.getScreenWidth() * width / height;
            params.height = CommonUtil.getScreenWidth();
            params.setMargins((params.height - params.width) / 2, 0, (params.height - params.width) / 2, 0);
        }
        binding.exoPlayerView.setLayoutParams(params);
    }

    private void play(Uri videoUri) {
//            mExoPlayer.removeListener();
        mExoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        mExoPlayer.clearMediaItems();
        mExoPlayer.addMediaItem(MediaItem.fromUri(videoUri));
        mExoPlayer.setPlayWhenReady(true);
        mExoPlayer.prepare();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void playLocalByteString() {
        Log.d(TAG, "playLocalByteString");
        DataSource.Factory dataSourceFactory = new DataSource.Factory() {
            @NonNull
            @Override
            public DataSource createDataSource() {
//                AvatarDataSource(true)
                return null;
            }
        };
        MediaItem mediaItem = MediaItem.fromUri("");
        ProgressiveMediaSource.Factory factory = new ProgressiveMediaSource.Factory(dataSourceFactory);
        ProgressiveMediaSource videoSource = factory.createMediaSource(mediaItem);
        mExoPlayer.setMediaSource(videoSource);

        mExoPlayer.setPlayWhenReady(true);
        mExoPlayer.prepare();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void playRtmp(String rtmpUrl) {
        Log.d(TAG, "playRtmp");
        mExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        mExoPlayer.clearMediaItems();
        ProgressiveMediaSource videoSource = new ProgressiveMediaSource.Factory(new RtmpDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(Uri.parse(rtmpUrl)));
        mExoPlayer.setMediaSource(videoSource);
        mExoPlayer.setPlayWhenReady(true);
        mExoPlayer.prepare();
    }
}
