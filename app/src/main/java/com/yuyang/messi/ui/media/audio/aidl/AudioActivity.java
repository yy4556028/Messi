package com.yuyang.messi.ui.media.audio.aidl;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.aidl_audioplayer.AudioBean;
import com.yuyang.aidl_audioplayer.AudioPlayService;
import com.yuyang.aidl_audioplayer.IAudioPlayerAidlInterface;
import com.yuyang.aidl_audioplayer.IClientCallback;
import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.LogUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.AudioRecyclerAdapter;
import com.yuyang.messi.helper.AudioHelper;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.view.AlwaysMarqueeTextView;

import java.util.ArrayList;

public class AudioActivity extends AppBaseActivity {

    private static final String TAG = AudioActivity.class.getSimpleName();

    private AudioRecyclerAdapter recyclerAdapter;

    private View bottomLyt;

    // 播放进度条
    private ProgressBar musicProgress;

    // 歌曲icon
    private ImageView musicIcon;

    // 歌名
    private AlwaysMarqueeTextView musicTitle;

    // 歌手
    private AlwaysMarqueeTextView musicArtist;
    // 上一首
    private ImageView musicPre;

    // 播放
    private ImageView musicPlay;

    // 下一首
    private ImageView musicNext;

    private ScanSDCardReceiver scanSDCardReceiver;

    private IAudioPlayerAidlInterface iAudioPlayerAidlInterface;

    private boolean mBound = false;

    private final IClientCallback mClientCallback = new IClientCallback.Stub() {

        @Override
        public void onPlayChange(AudioBean audioBean, int currentPosition, boolean isPlaying) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI(audioBean, isPlaying);
                    recyclerAdapter.setCurrentAudioBean(audioBean);
                    musicProgress.setMax(audioBean.getLength());
                    musicProgress.setProgress(currentPosition);
                }
            });
        }

        @Override
        public void onProgress(int second) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    musicProgress.setProgress(second);
                }
            });
        }
    };

    private final ServiceConnection aidlPlayServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            iAudioPlayerAidlInterface = IAudioPlayerAidlInterface.Stub.asInterface(iBinder);
            try {
                iAudioPlayerAidlInterface.registerCallback(mClientCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mBound = true;

//            try {
//                iBinder.linkToDeath(new IBinder.DeathRecipient() {
//                    @Override
//                    public void binderDied() {
//
//                    }
//                }, 0);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                iAudioPlayerAidlInterface.unregisterCallback(mClientCallback);
                iAudioPlayerAidlInterface = null;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mBound = false;
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_audio;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 注册广播
        scanSDCardReceiver = new ScanSDCardReceiver();

        initViews();
        initEvents();

        Bundle audioStoreArgs = new Bundle();
        audioStoreArgs.putInt(AudioHelper.ARG_DURATION, 1500);
        AudioHelper.loadAudio(getActivity(), audioStoreArgs, new AudioHelper.AudioResultCallback() {
            @Override
            public void onResultCallback(ArrayList<AudioBean> beanList) {
                recyclerAdapter.setData(beanList);
            }
        });
        registerMyReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtil.isServiceRunning(AudioPlayService.class.getName())) {
            bindService(new Intent(getActivity(), AudioPlayService.class), aidlPlayServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBound) {
            unbindService(aidlPlayServiceConnection);
            mBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanSDCardReceiver != null)
            unregisterReceiver(scanSDCardReceiver);
    }

    private void initViews() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("音乐播放器");
        RecyclerView recyclerView = findViewById(R.id.activity_audio_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter = new AudioRecyclerAdapter(getActivity()));

        bottomLyt = findViewById(R.id.activity_audio_bottom);
        musicProgress = findViewById(R.id.activity_audio_progress);
        musicIcon = findViewById(R.id.activity_audio_icon);
        musicTitle = findViewById(R.id.activity_audio_title);
        musicArtist = findViewById(R.id.activity_audio_artist);
        musicPre = findViewById(R.id.activity_audio_pre);
        musicPlay = findViewById(R.id.activity_audio_play);
        musicNext = findViewById(R.id.activity_audio_next);
    }

    private void initEvents() {
        recyclerAdapter.setOnItemClickListener(new AudioRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == -1) return;
                AudioPlayService.startService(getActivity(), recyclerAdapter.audioBeanList, recyclerAdapter.audioBeanList.get(position), 0);
                if (!mBound) {
                    bindService(new Intent(getActivity(), AudioPlayService.class), aidlPlayServiceConnection, Context.BIND_AUTO_CREATE);
                }
            }
        });

        musicIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AudioPlayActivity.class));
            }
        });

        musicPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iAudioPlayerAidlInterface == null) {
                    AudioPlayService.startService(getActivity(), recyclerAdapter.audioBeanList, recyclerAdapter.getCurrentAudioBean(), musicProgress.getProgress());
                } else {
                    try {
                        if (iAudioPlayerAidlInterface.isPlaying()) {
                            iAudioPlayerAidlInterface.pauseAudio();
                        } else {
                            iAudioPlayerAidlInterface.resumeAudio();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        musicPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iAudioPlayerAidlInterface.prevAudio();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        musicNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iAudioPlayerAidlInterface.nextAudio();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateUI(AudioBean audioBean, boolean isPlaying) {

        LogUtil.d("updateUIupdateUI", "mBound = " + (mBound));
        if (audioBean == null) {
            bottomLyt.setVisibility(View.GONE);
            return;
        }

        bottomLyt.setVisibility(View.VISIBLE);

        if (!getActivity().isDestroyed()) {
            GlideApp.with(getActivity())
                    .load(audioBean.getImage())
                    .error(R.mipmap.ic_launcher)
                    .into(musicIcon);
        }

        musicTitle.setText(audioBean.getTitle());
        musicArtist.setText(audioBean.getArtist());
        musicProgress.setMax(audioBean.getLength());

        if (isPlaying) {
            musicPlay.setImageDrawable(ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_media_pause, null));
        } else {
            musicPlay.setImageDrawable(ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_media_play, null));
        }
    }

    class ScanSDCardReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
                AudioHelper.initAudioList(30000);
                recyclerAdapter.notifyDataSetChanged();
            }
        }
    }

    private void registerMyReceiver() {
        scanSDCardReceiver = new ScanSDCardReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addDataScheme("file");
        registerReceiver(scanSDCardReceiver, filter);
    }
}