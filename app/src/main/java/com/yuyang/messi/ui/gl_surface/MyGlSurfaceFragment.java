package com.yuyang.messi.ui.gl_surface;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;

import com.yuyang.messi.ui.gl_surface.glsurface.VideoProcessingGLSurfaceView;


/**
 * A Fragment to display avatar video.
 */
public class MyGlSurfaceFragment extends Fragment {
    @Nullable
    private VideoProcessingGLSurfaceView videoProcessingGLSurfaceView;

    private ExoPlayer exoPlayer;

    public MyGlSurfaceFragment() {
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.videoProcessingGLSurfaceView = new VideoProcessingGLSurfaceView(
                getContext(), false);
        if (this.exoPlayer != null) {
            this.videoProcessingGLSurfaceView.setPlayer(this.exoPlayer);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FrameLayout root = new FrameLayout(container.getContext());
        root.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                1080 / 2, 1920 / 2);
        layoutParams.gravity = Gravity.CENTER;
        root.addView(videoProcessingGLSurfaceView, layoutParams);
        return root;
    }

    public void bindPlayer(ExoPlayer exoPlayer) {
        this.exoPlayer = exoPlayer;
        if (videoProcessingGLSurfaceView != null) {
            videoProcessingGLSurfaceView.setPlayer(exoPlayer);
        }
    }
}