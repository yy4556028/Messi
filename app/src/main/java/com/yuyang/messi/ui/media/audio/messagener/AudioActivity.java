package com.yuyang.messi.ui.media.audio.messagener;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.aidl_audioplayer.AudioBean;
import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.AudioRecyclerAdapter;
import com.yuyang.messi.helper.AudioHelper;
import com.yuyang.messi.service.AudioPlayMessengerService;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.ui.media.audio.aidl.AudioPlayActivity;
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

    private Messenger mServerMessenger = new Messenger(new AudioClientHandler());

    private Messenger mService = null;

    private boolean mBound = false;

    private class AudioClientHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case AudioPlayMessengerService.MSG_INIT:{
                    AudioBean audioBean = (AudioBean) msg.getData().getSerializable(AudioBean.class.getSimpleName());
                    int currentPos = msg.getData().getInt(AudioPlayMessengerService.KEY_CURRENT_POS, -1);
                    updateUI(audioBean, false);
                    musicProgress.setProgress(currentPos);
                    recyclerAdapter.setCurrentAudioBean(audioBean);
                    break;
                }
                case AudioPlayMessengerService.MSG_PLAY: {
                    AudioBean audioBean = (AudioBean) msg.getData().getSerializable(AudioBean.class.getSimpleName());
                    int currentPos = msg.getData().getInt(AudioPlayMessengerService.KEY_CURRENT_POS, -1);
                    updateUI(audioBean, true);
                    musicProgress.setProgress(currentPos);
                    recyclerAdapter.setCurrentAudioBean(audioBean);
                    break;
                }
                case AudioPlayMessengerService.MSG_PAUSE: {
                    updateUI(recyclerAdapter.getCurrentAudioBean(), false);
                    break;
                }
                case AudioPlayMessengerService.MSG_RESUME: {
                    updateUI(recyclerAdapter.getCurrentAudioBean(), true);
                    break;
                }
                case AudioPlayMessengerService.MSG_PROGRESS: {
                    musicProgress.setProgress(msg.arg1);
                    break;
                }
            }
        }
    }

    private ServiceConnection playServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = new Messenger(iBinder);
            Message msg = Message.obtain(null, AudioPlayMessengerService.MSG_INIT);
            msg.replyTo = mServerMessenger;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {//连接意外中断时调用该方法,取消绑定时不会调用该方法
            mService = null;
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
        bindService(new Intent(getActivity(), AudioPlayMessengerService.class), playServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBound) {
            Message msg = Message.obtain(null, AudioPlayMessengerService.MSG_CLEAR);
            msg.replyTo = mServerMessenger;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(playServiceConnection);
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
                AudioPlayMessengerService.startService(getActivity(), recyclerAdapter.audioBeanList, recyclerAdapter.audioBeanList.get(position), 0);
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
                if (musicPlay.getTag() != null && (boolean) musicPlay.getTag()) {   //如果播放中，暂停

                    Message msg = Message.obtain(null, AudioPlayMessengerService.MSG_PAUSE);
                    msg.replyTo = mServerMessenger;
                    try {
                        mService.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {    //如果未播放
                    AudioPlayMessengerService.startService(getActivity(), recyclerAdapter.audioBeanList, recyclerAdapter.getCurrentAudioBean(), musicProgress.getProgress());
                }
            }
        });

        musicPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = Message.obtain(null, AudioPlayMessengerService.MSG_PREV);
                msg.replyTo = mServerMessenger;
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        musicNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = Message.obtain(null, AudioPlayMessengerService.MSG_NEXT);
                msg.replyTo = mServerMessenger;
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateUI(AudioBean audioBean, boolean play) {

        musicPlay.setTag(play);

        if (audioBean == null) {
            bottomLyt.setVisibility(View.GONE);
            return;
        }

        bottomLyt.setVisibility(View.VISIBLE);

        GlideApp.with(getActivity())
                .load(audioBean.getImage())
                .error(R.mipmap.ic_launcher)
                .into(musicIcon);
        musicTitle.setText(audioBean.getTitle());
        musicArtist.setText(audioBean.getArtist());
        musicProgress.setMax(audioBean.getLength());

        if (play) {
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