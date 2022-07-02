package com.yuyang.messi.ui.media.audio.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.yuyang.aidl_audioplayer.AudioBean;
import com.yuyang.aidl_audioplayer.AudioPlayService;
import com.yuyang.aidl_audioplayer.IAudioPlayerAidlInterface;
import com.yuyang.aidl_audioplayer.IClientCallback;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.BitmapUtil;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.statusbar.MyStatusBarUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.AudioPlayPagerAdapter;
import com.yuyang.messi.helper.AudioHelper;
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

    private IAudioPlayerAidlInterface iAudioPlayerAidlInterface;

    private boolean mBound = false;

    private int screenWidth;

    private AudioBean currentAudioBean;

    private final IClientCallback clientCallback = new IClientCallback.Stub() {

        @Override
        public void onPlayChange(AudioBean audioBean, int currentPosition, boolean isPlaying) throws RemoteException {
            if (currentAudioBean != audioBean){
                currentAudioBean = audioBean;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI(audioBean, isPlaying);
                    currentTime.setText(AudioHelper.convertTime(currentPosition));
                    seekBar.setProgress(currentPosition);
                    if (cdView.lrcView.hasLrc()) cdView.lrcView.changeCurrent(currentPosition);
                    if (lrcView.lrcView.hasLrc()) lrcView.lrcView.changeCurrent(currentPosition);
                }
            });
        }

        @Override
        public void onProgress(int second) throws RemoteException {
            currentTime.setText(AudioHelper.convertTime(second));
            seekBar.setProgress(second);
            if (cdView.lrcView.hasLrc()) cdView.lrcView.changeCurrent(second);
            if (lrcView.lrcView.hasLrc()) lrcView.lrcView.changeCurrent(second);
        }
    };

    private final ServiceConnection playServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            iAudioPlayerAidlInterface = IAudioPlayerAidlInterface.Stub.asInterface(iBinder);
            try {
                iAudioPlayerAidlInterface.registerCallback(clientCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                iAudioPlayerAidlInterface.unregisterCallback(clientCallback);
                iAudioPlayerAidlInterface = null;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
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
        initView();
        initEvent();
    }

    @Override
    public void setStatusBar() {
        MyStatusBarUtil.setTranslucentStatus(getActivity());
        MyStatusBarUtil.setRootViewFitsSystemWindows(getActivity(), true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getActivity(), AudioPlayService.class), playServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBound) {
            try {
                iAudioPlayerAidlInterface.unregisterCallback(clientCallback);
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
                try {
                    iAudioPlayerAidlInterface.prevAudio();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iAudioPlayerAidlInterface.nextAudio();
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
                try {
                    iAudioPlayerAidlInterface.seekTo(seekBar.getProgress());
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