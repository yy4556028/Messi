
package com.yuyang.messi.ui.gl_surface.gltexture;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.Surface;

import androidx.annotation.Nullable;
import androidx.media3.exoplayer.ExoPlayer;

import com.yuyang.messi.ui.gl_surface.filter.GPUImageChromaKeyBlendFilter;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * {@link GLSurfaceView} that creates a GL context (optionally for protected content).
 *
 * <p>This view must be created programmatically, as it is necessary to specify whether a context
 * supporting protected content should be created at construction time.
 */
public final class VideoProcessingGLTextureView extends GLTextureView {
    private static final int EGL_PROTECTED_CONTENT_EXT = 0x32C0;
    private static final String TAG = "VPGlSurfaceView";
    private final VideoProcessRenderer renderer;
    private final Handler mainHandler;
    private Path clippingPath;

    @Nullable
    private SurfaceTexture surfaceTexture;
    @Nullable
    private Surface surface;
    @Nullable
    private ExoPlayer player;
    private final GPUImageChromaKeyBlendFilter filter;
    /**
     * Creates a new instance. Pass {@code true} for {@code requireSecureContext} if the {@link
     * GLSurfaceView GLSurfaceView's} associated GL context should handle secure content (if the
     * device supports it).
     *
     * @param context              The {@link Context}.
     * @param requireSecureContext Whether a GL context supporting protected content should be
     *                             created, if supported by the device.
     */


    public VideoProcessingGLTextureView(
            Context context, boolean requireSecureContext) {
        super(context);
        filter = new GPUImageChromaKeyBlendFilter();
        renderer = new VideoProcessRenderer(this, filter);
        mainHandler = new Handler();
        clippingPath = new Path();
        setEGLContextClientVersion(2);
        setEGLConfigChooser(
                /* redSize= */ 8,
                /* greenSize= */ 8,
                /* blueSize= */ 8,
                /* alphaSize= */ 8,
                /* depthSize= */ 16,
                /* stencilSize= */ 0);
        setOpaque(false);
        setEGLContextFactory(
                new EGLContextFactory() {
                    @Override
                    public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
                        int[] glAttributes;
                        if (requireSecureContext) {
                            glAttributes =
                                    new int[]{
                                            EGL14.EGL_CONTEXT_CLIENT_VERSION,
                                            2,
                                            EGL_PROTECTED_CONTENT_EXT,
                                            EGL14.EGL_TRUE,
                                            EGL14.EGL_NONE
                                    };
                        } else {
                            glAttributes = new int[]{EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE};
                        }
                        return egl.eglCreateContext(
                                display, eglConfig, /* share_context= */ EGL10.EGL_NO_CONTEXT, glAttributes);
                    }

                    @Override
                    public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
                        egl.eglDestroyContext(display, context);
                    }
                });
        setEGLWindowSurfaceFactory(
                new EGLWindowSurfaceFactory() {
                    @Override
                    public EGLSurface createWindowSurface(
                            EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
                        int[] attribsList =
                                requireSecureContext
                                        ? new int[]{EGL_PROTECTED_CONTENT_EXT, EGL14.EGL_TRUE, EGL10.EGL_NONE}
                                        : new int[]{EGL10.EGL_NONE};
                        return egl.eglCreateWindowSurface(display, config, nativeWindow, attribsList);
                    }

                    @Override
                    public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
                        egl.eglDestroySurface(display, surface);
                    }
                });
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        requestRender();
    }

    public void setBackgroundColor(float [] rgb){
        if(rgb == null){
            return;
        }
        renderer.setBackgroundColor(rgb[0],rgb[1],rgb[2]);
    }

    public void setBackgroundImage(int bgImage){
        if(bgImage == -1){
            return;
        }
        filter.setBitmap(BitmapFactory.decodeResource(this.getResources(), bgImage));
    }

    /**
     * Attaches or detaches (if {@code player} is {@code null}) this view from the player.
     *
     * @param player The new player, or {@code null} to detach this view.
     */
    public void setPlayer(@Nullable ExoPlayer player) {
        if (player == this.player) {
            return;
        }
        if (this.player != null) {
            if (surface != null) {
                this.player.clearVideoSurface(surface);
            }
        }
        this.player = player;
        if (this.player != null) {
            this.player.setVideoSurface(surface);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Post to make sure we occur in order with any onSurfaceTextureAvailable calls.
        mainHandler.post(
                () -> {
                    if (surface != null) {
                        if (player != null) {
                            player.setVideoSurface(null);
                        }
                        releaseSurface(surfaceTexture, surface);
                        surfaceTexture = null;
                        surface = null;
                    }
                });
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture) {
        mainHandler.post(
                () -> {
                    SurfaceTexture oldSurfaceTexture = this.surfaceTexture;
                    Surface oldSurface = VideoProcessingGLTextureView.this.surface;
                    this.surfaceTexture = surfaceTexture;
                    this.surface = new Surface(surfaceTexture);
                    releaseSurface(oldSurfaceTexture, oldSurface);
                    if (player != null) {
                        player.setVideoSurface(surface);
                    }
                });
    }

    private static void releaseSurface(
            @Nullable SurfaceTexture oldSurfaceTexture, @Nullable Surface oldSurface) {
        if (oldSurfaceTexture != null) {
            oldSurfaceTexture.release();
        }
        if (oldSurface != null) {
            oldSurface.release();
        }
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        if (w != oldw || h != oldh) {
//            int radius = Math.min(w, h)/2;
//            clippingPath = new Path();
//            clippingPath.addCircle(w/2, h/2, radius, Path.Direction.CW);
//        }
//    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        int radius = w > h ? h / 2 : w / 2;
        clippingPath.addCircle(w/2, h/2, radius, Path.Direction.CW);

        int count = canvas.save();
        canvas.clipPath(clippingPath);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(count);
    }
}
