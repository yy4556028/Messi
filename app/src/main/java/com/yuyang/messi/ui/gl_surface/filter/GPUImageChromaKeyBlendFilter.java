package com.yuyang.messi.ui.gl_surface.filter;

import android.opengl.GLES20;

/**
 * Selectively replaces a color in the first image with the second image
 * 很多滤镜都在这里
 * https://github.com/BradLarson/GPUImage
 * https://github.com/BradLarson/GPUImage/blob/master/framework/Source/GPUImageChromaKeyFilter.m
 */
public class GPUImageChromaKeyBlendFilter extends GPUImageTwoInputFilter {
    public static final String CHROMA_KEY_BLEND_FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    " precision highp float;\n" +
                    " \n" +
                    " varying highp vec2 textureCoordinate;\n" +
                    " varying highp vec2 textureCoordinate2;\n" +
                    "\n" +
                    " uniform float thresholdSensitivity;\n" +
                    " uniform float smoothing;\n" +
                    " uniform vec3 colorToReplace;\n" +
                    " uniform samplerExternalOES inputImageTexture;\n" +
                    " uniform sampler2D inputImageTexture2;\n" +
                    " \n" +
                    " void main()\n" +
                    " {\n" +
                    "     vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                    "     vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
                    "     \n" +
                    "     float maskY = 0.2989 * colorToReplace.r + 0.5866 * colorToReplace.g + 0.1145 * colorToReplace.b;\n" +
                    "     float maskCr = 0.7132 * (colorToReplace.r - maskY);\n" +
                    "     float maskCb = 0.5647 * (colorToReplace.b - maskY);\n" +
                    "     \n" +
                    "     float Y = 0.2989 * textureColor.r + 0.5866 * textureColor.g + 0.1145 * textureColor.b;\n" +
                    "     float Cr = 0.7132 * (textureColor.r - Y);\n" +
                    "     float Cb = 0.5647 * (textureColor.b - Y);\n" +
                    "     \n" +

                    "     float distanceToColor = distance(vec3(textureColor.rgb), colorToReplace);\n" +
                    "     float blendValue = smoothstep(thresholdSensitivity, thresholdSensitivity + smoothing, distanceToColor);\n" +
                    "     gl_FragColor = blendValue < 0.5 ? vec4(0.0) : textureColor;\n" +

//            "     float blendValue = 1.0 - smoothstep(thresholdSensitivity, thresholdSensitivity + smoothing, distance(vec2(Cr, Cb), vec2(maskCr, maskCb)));\n" +
//            "     gl_FragColor = mix(textureColor, vec4(0.0, 0.0, 0.0, 0.0), blendValue);\n" +
                    " }";

    public static final String CHROMA_KEY_BLEND_FRAGMENT_SHADER2 =
            "#extension GL_OES_EGL_image_external : require\n" +
                    " precision highp float;\n" +
                    " \n" +
                    " varying vec2 textureCoordinate;\n" +
                    "\n" +
                    " uniform float thresholdSensitivity;\n" +
                    " uniform float smoothing;\n" +
                    " uniform vec3 colorToReplace;\n" +
                    " uniform samplerExternalOES inputImageTexture;\n" +
                    " \n" +
                    " void main()\n" +
                    " {\n" +
                    "     vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                    "     \n" +
                    "     float maskY = 0.2989 * colorToReplace.r + 0.5866 * colorToReplace.g + 0.1145 * colorToReplace.b;\n" +
                    "     float maskCr = 0.7132 * (colorToReplace.r - maskY);\n" +
                    "     float maskCb = 0.5647 * (colorToReplace.b - maskY);\n" +
                    "     \n" +
                    "     float Y = 0.2989 * textureColor.r + 0.5866 * textureColor.g + 0.1145 * textureColor.b;\n" +
                    "     float Cr = 0.7132 * (textureColor.r - Y);\n" +
                    "     float Cb = 0.5647 * (textureColor.b - Y);\n" +
                    "     \n" +

                    "     float blendValue = smoothstep(thresholdSensitivity, thresholdSensitivity + smoothing, distance(vec2(Cr, Cb), vec2(maskCr, maskCb)));\n" +
                    "     gl_FragColor = blendValue < 0.5 ? vec4(0) : textureColor;\n" +
                    " }";

    private int thresholdSensitivityLocation;
    private int smoothingLocation;
    private int colorToReplaceLocation;
    private float thresholdSensitivity = 0.01f;
    private float smoothing = 0.1f;
    private float[] colorToReplace = new float[]{0.0f, 0.0f, 0.0f};

    public GPUImageChromaKeyBlendFilter() {
        super(CHROMA_KEY_BLEND_FRAGMENT_SHADER2);

    }

    @Override
    public void onInit() {
        super.onInit();
        thresholdSensitivityLocation = GLES20.glGetUniformLocation(getProgram(), "thresholdSensitivity");
        smoothingLocation = GLES20.glGetUniformLocation(getProgram(), "smoothing");
        colorToReplaceLocation = GLES20.glGetUniformLocation(getProgram(), "colorToReplace");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setSmoothing(smoothing);
        setThresholdSensitivity(thresholdSensitivity);
        setColorToReplace(colorToReplace[0], colorToReplace[1], colorToReplace[2]);
    }

    /**
     * The degree of smoothing controls how gradually similar colors are replaced in the image
     * The default value is 0.1
     */
    public void setSmoothing(final float smoothing) {
        this.smoothing = smoothing;
        setFloat(smoothingLocation, this.smoothing);
    }

    /**
     * The threshold sensitivity controls how similar pixels need to be colored to be replaced
     * The default value is 0.3
     */
    public void setThresholdSensitivity(final float thresholdSensitivity) {
        this.thresholdSensitivity = thresholdSensitivity;
        setFloat(thresholdSensitivityLocation, this.thresholdSensitivity);
    }

    /**
     * The color to be replaced is specified using individual red, green, and blue components (normalized to 1.0).
     * The default is green: (0.0, 1.0, 0.0).
     *
     * @param redComponent   Red component of color to be replaced
     * @param greenComponent Green component of color to be replaced
     * @param blueComponent  Blue component of color to be replaced
     */
    public void setColorToReplace(float redComponent, float greenComponent, float blueComponent) {
        colorToReplace = new float[]{redComponent, greenComponent, blueComponent};
        setFloatVec3(colorToReplaceLocation, colorToReplace);
    }
}
