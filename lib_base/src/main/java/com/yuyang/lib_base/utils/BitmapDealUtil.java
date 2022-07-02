package com.yuyang.lib_base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import java.util.Random;

public class BitmapDealUtil {

    /**
     * 灰白处理
     *
     * @param bmpOriginal
     * @return
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);//设置饱和度
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        canvas.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * 黑白处理
     * 对于每个像素的rgb值求平均数，如果高于100算白色，低于100算黑色
     * 如果感觉100这个标准值太大了，导致图片白色区域太多，把它降低点可能效果会更好
     *
     * @param mBitmap
     * @return
     */
    public static Bitmap toHeibai(Bitmap mBitmap) {
        int mBitmapWidth = mBitmap.getWidth();
        int mBitmapHeight = mBitmap.getHeight();
        Bitmap bmpReturn = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888);
        int iPixel = 0;
        for (int i = 0; i < mBitmapWidth; i++) {
            for (int j = 0; j < mBitmapHeight; j++) {
                int curr_color = mBitmap.getPixel(i, j);

                int avg = (Color.red(curr_color) + Color.green(curr_color) + Color.blue(curr_color)) / 3;
                if (avg >= 100) {
                    iPixel = 255;
                } else {
                    iPixel = 0;
                }
                int modify_color = Color.argb(255, iPixel, iPixel, iPixel);

                bmpReturn.setPixel(i, j, modify_color);
            }
        }
        return bmpReturn;
    }

    /**
     * 镜像处理
     *
     * @param bitmap
     * @return
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {

        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int reflectionHeight = (int) (height * 0.7f);

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap,
                0, height - reflectionHeight,
                width, reflectionHeight, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + reflectionHeight), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawRect(0, height, width, height + reflectionGap, new Paint());

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        //镜像图片线性变淡
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight() + reflectionGap,
                0, bitmapWithReflection.getHeight(),
                0x70ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height + reflectionGap, width, bitmapWithReflection.getHeight(), paint);
        return bitmapWithReflection;
    }

    /**
     * 加旧泛黄处理
     *
     * @param bitmap
     * @return
     */
    public static Bitmap toOld(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        /**
         * rgb * 1，rg 各加 50，rg混合得到黄色
         */
        float[] array = {
                1, 0, 0, 0, 50,
                0, 1, 0, 0, 50,
                0, 0, 1, 0, 0,
                0, 0, 0, 1, 0};
        cm.set(array);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return output;
    }

    /**
     * 浮雕效果
     * 观察浮雕就不难发现，其实浮雕的特点就是在颜色有跳变的地方就刻条痕迹。
     * 127，127,127为深灰色，近似于石头的颜色，此处取该颜色为底色。
     * 算法是将上一个点的rgba值减去当前点的rgba值然后加上127得到当前点的颜色
     *
     * @param mBitmap
     * @return
     */
    public static Bitmap toFuDiao(Bitmap mBitmap) {
        int mBitmapWidth = mBitmap.getWidth();
        int mBitmapHeight = mBitmap.getHeight();
        Bitmap bmpReturn = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.RGB_565);
        int preColor = 0;
        int prepreColor = 0;
        preColor = mBitmap.getPixel(0, 0);

        for (int i = 0; i < mBitmapWidth; i++) {
            for (int j = 0; j < mBitmapHeight; j++) {
                int curr_color = mBitmap.getPixel(i, j);
                int r = Color.red(curr_color) - Color.red(prepreColor) + 127;
                int g = Color.green(curr_color) - Color.red(prepreColor) + 127;
                int b = Color.green(curr_color) - Color.blue(prepreColor) + 127;
                int a = Color.alpha(curr_color);
                int modify_color = Color.argb(a, r, g, b);
                bmpReturn.setPixel(i, j, modify_color);
                prepreColor = preColor;
                preColor = curr_color;
            }
        }

        Canvas c = new Canvas(bmpReturn);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpReturn, 0, 0, paint);

        return bmpReturn;
    }

    /**
     * 底片处理
     * 每个点grb值取为255之差，效果就是是底片效果
     * @param mBitmap
     * @return
     */
    public static Bitmap toDipian(Bitmap mBitmap) {
        int mBitmapWidth = mBitmap.getWidth();
        int mBitmapHeight = mBitmap.getHeight();
        Bitmap bmpReturn = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < mBitmapWidth; i++) {
            for (int j = 0; j < mBitmapHeight; j++) {
                int curr_color = mBitmap.getPixel(i, j);
                int modify_color = Color.argb(255,
                        255 - Color.red(curr_color), 255 - Color.green(curr_color), 255 - Color.blue(curr_color));

                bmpReturn.setPixel(i, j, modify_color);
            }
        }
        return bmpReturn;
    }

    /**
     * 油画效果
     * 算法实现就是取一个一定范围内的随机数，每个点的颜色是该点减去随机数坐标后所得坐标的颜色
     * @param bmpSource
     * @return
     */
    public static Bitmap toYouHua(Bitmap bmpSource) {
        Bitmap bmpReturn = Bitmap.createBitmap(bmpSource.getWidth(),
                bmpSource.getHeight(), Bitmap.Config.RGB_565);
        int color = 0;
        int width = bmpSource.getWidth();
        int height = bmpSource.getHeight();

        Random rnd = new Random();
        int iModel = 10;
        int i = width - iModel;
        while (i > 1) {
            int j = height - iModel;
            while (j > 1) {
                int iPos = rnd.nextInt(100000) % iModel;
                color = bmpSource.getPixel(i + iPos, j + iPos);
                bmpReturn.setPixel(i, j, color);
                j = j - 1;
            }
            i = i - 1;
        }
        return bmpReturn;
    }

    public static Bitmap blur(Context context, Bitmap image, int blurRatio) {
        // 计算图片缩小后的长宽
        int width = Math.round(image.getWidth());
        int height = Math.round(image.getHeight());

        // 将缩小后的图片做为预渲染的图片。
        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        // 创建一张渲染后的输出图片。
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        // 创建RenderScript内核对象
        RenderScript rs = RenderScript.create(context);
        // 创建一个模糊效果的RenderScript的工具对象
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去。
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        // 设置渲染的模糊程度, 25f是最大模糊度
        blurScript.setRadius(blurRatio);
        // 设置blurScript对象的输入内存
        blurScript.setInput(tmpIn);
        // 将输出数据保存到输出内存中
        blurScript.forEach(tmpOut);

        // 将数据填充到Allocation中
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    /**
     * 增加水印
     *
     * @param bitMap
     * @param waterMark
     * @param isLeft
     * @return
     */
    public static Bitmap addBitmapWaterMark(Bitmap bitMap, Bitmap waterMark, Boolean isLeft) {
        if (bitMap == null) return null;
        int bitMapWidth = bitMap.getWidth();
        int bitMapHeight = bitMap.getHeight();
        int waterMarkWidth = waterMark.getWidth();
        int waterMarkHeight = waterMark.getHeight();

        Bitmap mergeBitmap = Bitmap.createBitmap(bitMapWidth, bitMapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mergeBitmap);
        canvas.drawBitmap(bitMap, 0, 0, null);
        if (isLeft) {
            canvas.drawBitmap(waterMark, 8, bitMapHeight - waterMarkHeight - 8, null);
        } else {
            canvas.drawBitmap(waterMark, bitMapWidth - waterMarkWidth - 8, bitMapHeight - waterMarkHeight - 8, null);
        }
//        canvas.save();
//        canvas.restore();

        return mergeBitmap;
    }
}
