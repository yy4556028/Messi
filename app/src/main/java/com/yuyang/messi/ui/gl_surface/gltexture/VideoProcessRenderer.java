package com.yuyang.messi.ui.gl_surface.gltexture;


import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.util.Log;

import androidx.annotation.OptIn;
import androidx.media3.common.util.GlUtil;
import androidx.media3.common.util.UnstableApi;

import com.mobvoi.lib_avatar.video_process.filter.GPUImageFilter;
import com.mobvoi.lib_avatar.video_process.utils.Rotation;
import com.mobvoi.lib_avatar.video_process.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VideoProcessRenderer implements GLTextureView.Renderer {
    public static final float[] CUBE = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    private final GPUImageFilter filter;
    private int texture;
    public final Object surfaceChangedWaiter = new Object();

    private SurfaceTexture surfaceTexture = null;
    private final FloatBuffer glCubeBuffer;
    private final FloatBuffer glTextureBuffer;

    private final Queue<Runnable> runOnDraw;
    private final Queue<Runnable> runOnDrawEnd;
    private Rotation rotation;
    private boolean flipHorizontal;
    private boolean flipVertical;
    private float backgroundRed = 0;
    private float backgroundGreen = 0;
    private float backgroundBlue = 0;
    VideoProcessingGLTextureView videoProcessingGLTextureView;

    public VideoProcessRenderer(VideoProcessingGLTextureView videoProcessingGLTextureView, final GPUImageFilter filter) {
        this.filter = filter;
        this.videoProcessingGLTextureView = videoProcessingGLTextureView;
        runOnDraw = new LinkedList<>();
        runOnDrawEnd = new LinkedList<>();

        glCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        glCubeBuffer.put(CUBE).position(0);
        glTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        setRotation(Rotation.NORMAL, false, false);
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
        GLES20.glClearColor(backgroundRed, backgroundGreen, backgroundBlue, 0);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        filter.ifNeedInit();

        try {
            texture = GlUtil.createExternalTexture();
        } catch (Exception e) {
            Log.e("VideoProcessRenderer", "Failed to create an external texture", e);
        }
        surfaceTexture = new SurfaceTexture(texture);
        surfaceTexture.setOnFrameAvailableListener(
                surfaceTexture -> {
                    videoProcessingGLTextureView.requestRender();
                });

        videoProcessingGLTextureView.onSurfaceTextureAvailable(surfaceTexture);
    }

    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        GLES20.glViewport(0, 0, width, height);
        GLES20.glUseProgram(filter.getProgram());
        filter.onOutputSizeChanged(width, height);
        adjustImageScaling();
        synchronized (surfaceChangedWaiter) {
            surfaceChangedWaiter.notifyAll();
        }
    }

    @Override
    public boolean onDrawFrame(final GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        runAll(runOnDraw);
        filter.onDraw(texture, glCubeBuffer, glTextureBuffer);
        runAll(runOnDrawEnd);
        if (surfaceTexture != null) {
            surfaceTexture.updateTexImage();
        }
        return true;
    }

    @Override
    public void onSurfaceDestroyed() {

    }

    /**
     * Sets the background color
     *
     * @param red   red color value
     * @param green green color value
     * @param blue  red color value
     */
    public void setBackgroundColor(float red, float green, float blue) {
        backgroundRed = red;
        backgroundGreen = green;
        backgroundBlue = blue;
    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }

    private void adjustImageScaling() {
        float[] cube = CUBE;
        float[] textureCords = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);
        glCubeBuffer.clear();
        glCubeBuffer.put(cube).position(0);
        glTextureBuffer.clear();
        glTextureBuffer.put(textureCords).position(0);
    }

    public void setRotation(final Rotation rotation) {
        this.rotation = rotation;
        adjustImageScaling();
    }

    public void setRotation(final Rotation rotation,
                            final boolean flipHorizontal, final boolean flipVertical) {
        this.flipHorizontal = flipHorizontal;
        this.flipVertical = flipVertical;
        setRotation(rotation);
    }
}
