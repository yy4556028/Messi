package com.yuyang.messi.view.Audio;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuyang.messi.R;

public class LrcView extends LinearLayout {

    private TextView artistText;

    public AudioLrcView lrcView;

    public LrcView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.activity_audio_play_view_lrc, this);

        initView();
    }

    private void initView() {
        artistText = (TextView) findViewById(R.id.activity_audio_play_view_lrc_artist);
        lrcView = (AudioLrcView) findViewById(R.id.activity_audio_play_view_lrc_lrcView);
    }

}
