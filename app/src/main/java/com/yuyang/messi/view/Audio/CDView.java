package com.yuyang.messi.view.Audio;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuyang.messi.R;

/**
 * Created by YuYang on 2015/8/10.
 */
public class CDView extends LinearLayout {

    public TextView artistText;

    public AudioCDView cdView;

    public AudioLrcView lrcView;

    public CDView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.activity_audio_play_view_cd, this);

        initView();
    }

    private void initView() {
        artistText = (TextView) findViewById(R.id.activity_audio_play_view_cd_artist);
        cdView = (AudioCDView) findViewById(R.id.activity_audio_play_view_cd_cdView);
        lrcView = (AudioLrcView) findViewById(R.id.activity_audio_play_view_cd_lrcView);
    }

}
