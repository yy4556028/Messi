package com.yuyang.lib_base.utils.media;

import android.media.MediaPlayer;

import java.io.IOException;

public class MediaPlayerController {

  private static volatile MediaPlayerController mediaPlayerController;

  private MediaPlayer mediaPlayer;

  private MediaPlayerListener mMediaPlayerListener;

  private MediaPlayerController() {}

  public static MediaPlayerController getInstance() {
    if (mediaPlayerController == null) {
      synchronized (MediaPlayerController.class) {
        if (mediaPlayerController == null) {
          mediaPlayerController = new MediaPlayerController();
        }
      }
    }
    return mediaPlayerController;
  }

  public void setMediaPlayerListener(MediaPlayerListener mMediaPlayerListener) {
    this.mMediaPlayerListener = mMediaPlayerListener;
  }

  public void setPath(String path) {
    try {
      if (mediaPlayer == null) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                  @Override
                  public void onCompletion(MediaPlayer mp) {
                    releaseMediaPlayer();
                  }
                });
      }
      mediaPlayer.reset();
      mediaPlayer.setDataSource(path);
      mediaPlayer.prepare();
//      mediaPlayer.prepareAsync();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public MediaPlayer getMediaPlayer() {
    if (mediaPlayer != null) {
      return mediaPlayer;
    }
    return null;
  }

  public void releaseMediaPlayer() {
    if (mediaPlayer != null) {
      if (mediaPlayer.isPlaying()) {
        mediaPlayer.stop();
      }
      mediaPlayer.reset();
      mediaPlayer.release();
      mediaPlayer = null;
    }
  }

  public interface MediaPlayerListener {
  }
}
