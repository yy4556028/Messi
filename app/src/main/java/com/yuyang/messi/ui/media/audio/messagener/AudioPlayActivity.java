package com.yuyang.messi.ui.media.audio.messagener;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.yuyang.aidl_audioplayer.AudioBean;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.BitmapUtil;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.SystemBarUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.AudioPlayPagerAdapter;
import com.yuyang.messi.helper.AudioHelper;
import com.yuyang.messi.service.AudioPlayMessengerService;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.view.Audio.CDView;
import com.yuyang.messi.view.Audio.LrcView;
import com.yuyang.messi.view.Indicator.vp_indicator.CirclePageIndicator;
import com.yuyang.messi.view.PageTransFormer.PlayPageTransformer;

import java.util.ArrayList;

public class AudioPlayActivity extends AppBaseActivity {

    private HeaderLayout headerLayout;

    private LinearLayout lyt;

    private ViewPager viewPager;

    private CDView cdView;

    private LrcView lrcView;

    private CirclePageIndicator indicator;

    private AppCompatSeekBar seekBar;

    private TextView currentTime;

    private TextView totalTime;

    private ImageButton preBtn;

    private ImageButton playBtn;

    private ImageButton nextBtn;

    private Messenger mServerMessenger = new Messenger(new AudioClientHandler());

    private Messenger mService = null;

    private boolean mBound = false;

    private int screenWidth;

    private AudioBean currentAudioBean;

    private class AudioClientHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case AudioPlayMessengerService.MSG_INIT: {
                    currentAudioBean = (AudioBean) msg.getData().getSerializable(AudioBean.class.getSimpleName());
                    int duration = msg.getData().getInt(AudioPlayMessengerService.KEY_CURRENT_POS, -1);
                    updateUI(currentAudioBean, false);
                    currentTime.setText(AudioHelper.convertTime(duration));
                    seekBar.setProgress(duration);
                    if (cdView.lrcView.hasLrc()) cdView.lrcView.changeCurrent(msg.arg1);
                    if (lrcView.lrcView.hasLrc()) lrcView.lrcView.changeCurrent(msg.arg1);
                    break;
                }
                case AudioPlayMessengerService.MSG_PLAY: {
                    currentAudioBean = (AudioBean) msg.getData().getSerializable(AudioBean.class.getSimpleName());
                    int duration = msg.getData().getInt(AudioPlayMessengerService.KEY_CURRENT_POS, -1);
                    updateUI(currentAudioBean, true);
                    currentTime.setText(AudioHelper.convertTime(duration));
                    seekBar.setProgress(duration);
                    if (cdView.lrcView.hasLrc()) cdView.lrcView.changeCurrent(msg.arg1);
                    if (lrcView.lrcView.hasLrc()) lrcView.lrcView.changeCurrent(msg.arg1);
                    break;
                }
                case AudioPlayMessengerService.MSG_PAUSE: {
                    updateUI(null, false);
                    break;
                }
                case AudioPlayMessengerService.MSG_RESUME: {
                    updateUI(null, true);
                    break;
                }
                case AudioPlayMessengerService.MSG_PROGRESS: {
                    currentTime.setText(AudioHelper.convertTime(msg.arg1));
                    seekBar.setProgress(msg.arg1);
                    if (cdView.lrcView.hasLrc()) cdView.lrcView.changeCurrent(msg.arg1);
                    if (lrcView.lrcView.hasLrc()) lrcView.lrcView.changeCurrent(msg.arg1);
                    break;
                }
            }
        }
    }

    private ServiceConnection playServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
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
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_audio_play;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemBarUtil.configBar(getActivity(), true, false, true, true, false);

        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getActivity(), AudioPlayMessengerService.class), playServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
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

    private void initView() {
        ((AppBarLayout) findViewById(R.id.common_header_layout_appBarLayout)).setTargetElevation(0);
        headerLayout = findViewById(R.id.headerLayout);
        headerLayout.setBackgroundResource(R.color.transparent);
        headerLayout.showLeftBackButton();

        lyt = findViewById(R.id.activity_audio_play_lyt);
        viewPager = findViewById(R.id.activity_audio_play_viewPager);
        indicator = findViewById(R.id.activity_audio_play_indicator);
        seekBar = findViewById(R.id.activity_audio_play_seekBar);
        currentTime = findViewById(R.id.activity_audio_play_current_time);
        totalTime = findViewById(R.id.activity_audio_play_total_time);
        preBtn = findViewById(R.id.activity_audio_play_pre);
        playBtn = findViewById(R.id.activity_audio_play_play);
        nextBtn = findViewById(R.id.activity_audio_play_next);

        ArrayList<View> views = new ArrayList<>();
        cdView = new CDView(getActivity());
        lrcView = new LrcView(getActivity());
        views.add(cdView);
        views.add(lrcView);
        viewPager.setAdapter(new AudioPlayPagerAdapter(views));
        viewPager.setPageTransformer(true, new PlayPageTransformer());
        viewPager.setOffscreenPageLimit(1);
        indicator.setViewPager(viewPager, 0);

        screenWidth = CommonUtil.getScreenWidth();
    }

    private void initEvent() {

        preBtn.setOnClickListener(new View.OnClickListener() {
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

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playBtn.getTag() != null && (boolean) playBtn.getTag()) {   //如果播放中，暂停

                    Message msg = Message.obtain(null, AudioPlayMessengerService.MSG_PAUSE);
                    msg.replyTo = mServerMessenger;
                    try {
                        mService.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {    //如果未播放
                    Message msg = Message.obtain(null, AudioPlayMessengerService.MSG_RESUME);
                    msg.replyTo = mServerMessenger;
                    try {
                        mService.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
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

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentTime.setText(AudioHelper.convertTime((int) (currentAudioBean.getLength() * (float) progress / seekBar.getMax())));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Message msg = Message.obtain(null, AudioPlayMessengerService.MSG_PROGRESS, seekBar.getProgress(), 0);
                msg.replyTo = mServerMessenger;
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                cdView.lrcView.onDrag(seekBar.getProgress());
                lrcView.lrcView.onDrag(seekBar.getProgress());
            }
        });
    }

    private void updateUI(final AudioBean audioBean, boolean play) {

        playBtn.setTag(play);

        if (audioBean != null) {
            // 设置 title 歌手 进度等信息
            headerLayout.showTitle(audioBean.getTitle());
            cdView.artistText.setText(audioBean.getArtist());
            seekBar.setMax(audioBean.getLength());
            totalTime.setText(AudioHelper.convertTime(audioBean.getLength()));
            // 设置歌词
            String lrcPath = AudioHelper.getLrcDir() + audioBean.getTitle() + ".lrc";
            cdView.lrcView.setLrcPath(lrcPath);
            lrcView.lrcView.setLrcPath(lrcPath);

            // 设置模糊背景
            lyt.post(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = BitmapFactory.decodeFile(audioBean.getImage());

                    if (bitmap == null) {
                        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    }

                    // 设置 activity 模糊背景
                    Bitmap blurBitmap = BitmapUtil.fastBlur(getActivity(), bitmap, 60);
                    lyt.setBackground(new BitmapDrawable(getResources(), blurBitmap));

                    cdView.cdView.setImage(Bitmap.createScaledBitmap(bitmap, (int) (screenWidth * 0.8), (int) (screenWidth * 0.8), true));
                }
            });
        }

        if (play) {
            cdView.cdView.start();
            playBtn.setImageResource(R.drawable.activity_audio_play_pause_selector);
        } else {
            cdView.cdView.pause();
            playBtn.setImageResource(R.drawable.activity_audio_play_play_selector);
        }
    }
}