package com.yamap.lib_chat.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.yamap.lib_chat.R;
import com.yamap.lib_chat.utils.AudioPlayerHelper;

/**
 * Created by lzw on 14-9-22.
 * 语音播放按钮
 */
public class AudioPlayButton extends TextView {

    private String path;
    private boolean leftSide;
    private AnimationDrawable anim;

    public AudioPlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        String tag = (String) getTag();
        leftSide = tag.equalsIgnoreCase("left");
        setLeftSide(leftSide);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AudioPlayerHelper.getInstance().isPlaying() && AudioPlayerHelper.getInstance().getAudioPath().equals(path)) {
                    AudioPlayerHelper.getInstance().pausePlayer();
                    stopRecordAnimation();
                } else {
                    AudioPlayerHelper.getInstance().playAudio(path);
                    AudioPlayerHelper.getInstance().addFinishCallback(new AudioPlayerHelper.AudioFinishCallback() {
                        @Override
                        public void onFinish() {
                            stopRecordAnimation();
                        }
                    });
                    startRecordAnimation();
                }
            }
        });
    }

    /**
     * 设置语音按钮的方向
     * 因为聊天中左右 item 都可能有语音
     *
     * @param leftSide
     */
    public void setLeftSide(boolean leftSide) {
        this.leftSide = leftSide;
        stopRecordAnimation();
    }

    /**
     * 设置语音文件位置
     *
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
        stopRecordAnimation();
        if (isPlaying()) {
            AudioPlayerHelper.getInstance().addFinishCallback(new AudioPlayerHelper.AudioFinishCallback() {
                @Override
                public void onFinish() {
                    stopRecordAnimation();
                }
            });
            startRecordAnimation();
        }
    }

    private boolean isPlaying() {
        return AudioPlayerHelper.getInstance().isPlaying() && AudioPlayerHelper.getInstance().getAudioPath().equals(path);
    }

    /**
     * 开始播放语音动画
     */
    private void startRecordAnimation() {
        setCompoundDrawablesWithIntrinsicBounds(
                leftSide ? R.drawable.chat_anim_voice_left : 0,
                0,
                !leftSide ? R.drawable.chat_anim_voice_right : 0,
                0);
        anim = (AnimationDrawable) getCompoundDrawables()[leftSide ? 0 : 2];
        anim.start();
    }

    /**
     * 停止播放语音动画
     */
    private void stopRecordAnimation() {
        setCompoundDrawablesWithIntrinsicBounds(
                leftSide ? R.drawable.chat_voice_right3 : 0,
                0,
                !leftSide ? R.drawable.chat_voice_left3 : 0,
                0);
        if (anim != null) {
            anim.stop();
        }
    }
}
