package com.yuyang.messi.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.palette.graphics.Palette;
import androidx.palette.graphics.Palette.Swatch;
import java.util.Random;

public class ColorUtil {

    public static Random random = new Random();

    public static int getRandomColor() {
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
//        return 0xff000000 | random.nextInt(0x00ffffff);
    }

    public static int getReverseColor(int color) {
        return Color.rgb(255 - Color.red(color), 255 - Color.green(color), 255 - Color.blue(color));
    }

    /**
     * 转换为6位十六进制颜色代码，不含“#”
     *
     * @param color the color
     * @param includeAlpha the include alpha
     * @return string string
     */
    public static String toColorString(int color, boolean includeAlpha) {
        String alpha = Integer.toHexString(Color.alpha(color));
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));
        if (alpha.length() == 1) {
            alpha = "0" + alpha;
        }
        if (red.length() == 1) {
            red = "0" + red;
        }
        if (green.length() == 1) {
            green = "0" + green;
        }
        if (blue.length() == 1) {
            blue = "0" + blue;
        }
        String colorString;
        if (includeAlpha) {
            colorString = alpha + red + green + blue;
        } else {
            colorString = red + green + blue;
        }
        return colorString;
    }

    public static void getPalette(Bitmap bitmap, PaletteListener paletteListener) {
        Palette.Builder builder = Palette.from(bitmap);
//        builder.setRegion()
//        builder.maximumColorCount()//一共需要提取多少个颜色特征点
        builder.generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                //充满活力的
                Swatch vibrant = palette.getVibrantSwatch();
                Swatch vibrantDark = palette.getDarkVibrantSwatch();
                Swatch vibrantLight = palette.getLightVibrantSwatch();
                //柔和的
                Swatch muted = palette.getMutedSwatch();
                Swatch mutedDark = palette.getDarkMutedSwatch();
                Swatch mutedLight = palette.getLightMutedSwatch();

//                vibrant.getPopulation()//特征点的权重值，表示该特征点在选定的bitmap区域的重要程度‘

                if (paletteListener != null) {
                    paletteListener.getDominant(palette.getDominantSwatch());
                    paletteListener.getVibrant(vibrant, vibrantDark, vibrantLight);
                    paletteListener.getMuted(muted, mutedDark, mutedLight);

                }
            }
        });
    }

    public static abstract class PaletteListener {

        public void getDominant(Swatch dominantSwatch) {

        }

        public void getVibrant(Swatch vibrant, Swatch vibrantDark, Swatch vibrantLight) {

        }

        public void getMuted(Swatch muted, Swatch mutedDark, Swatch mutedLight) {

        }
    }
}
