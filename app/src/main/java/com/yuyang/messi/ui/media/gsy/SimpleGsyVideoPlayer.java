package com.yuyang.messi.ui.media.gsy;

import android.content.Context;
import android.util.AttributeSet;

import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.yuyang.messi.R;

public class SimpleGsyVideoPlayer extends StandardGSYVideoPlayer {
    public SimpleGsyVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public SimpleGsyVideoPlayer(Context context) {
        super(context);
    }

    public SimpleGsyVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_simple_gsy_video_player;
    }
}
