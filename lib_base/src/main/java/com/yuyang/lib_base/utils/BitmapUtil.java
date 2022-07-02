package com.yuyang.lib_base.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.yuyang.lib_base.BaseApp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtil {

    /**
     * 通知系统 以便在相册显示
     */
    public static void insertBitmap(Context context, String path) {
        MediaScannerConnection.scanFile(context,
                new String[]{path},
                new String[]{"image/jpeg"},
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }

    /**
     * 通知系统 以便在相册显示
     */
    public static void insertBitmap(Context context, Bitmap bitmap) {

        /**
         * 插入到系统图库
         * 不能指定插入照片的名字，而是系统给了我们一个当前时间的毫秒数为名字
         */
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "", "");

        /**
         * 使用MediaStore存储的方式不需要通知系统扫描媒体数据库
         * File方式需要扫描
         */
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File("/sdcard/image.jpg"));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * convert Bitmap to round corner
     *
     * @param bitmap
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#FF0000"));
        canvas.drawCircle(width / 2 + 0.7f, height / 2 + 0.7f, width / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffff0000;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 缩略图.centerCrop 方式取
     *
     * @param bitmap
     * @param with
     * @param height
     * @return
     */
    public static Bitmap getBitmapThumbnail(Bitmap bitmap, int with, int height) {
        return ThumbnailUtils.extractThumbnail(bitmap, with, height);
    }

    /**
     * convert Bitmap to Drawable
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(bitmap);
    }

    /**
     * convert Drawable to Bitmap
     */
    public static Bitmap drawableToBitmap0(Drawable drawable) {
        return drawable == null ? null : ((BitmapDrawable) drawable).getBitmap();
    }

    /**
     * drawable 转 bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap1(Drawable drawable) {

        if (drawable == null) {
            return null;
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // 获取 drawable 的颜色各式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;

        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 压缩图片
     */
    public static void compressImage(Uri uri, String newPath, int minWidth, int minHeight, int sizeLimit) {
        InputStream inputStream;
        Bitmap sampleBitmap;
        try {
            inputStream = BaseApp.getInstance().getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            options.inSampleSize = calculateInSampleSize(options, minWidth, minHeight);
            options.inJustDecodeBounds = false;
            inputStream = BaseApp.getInstance().getContentResolver().openInputStream(uri);
            sampleBitmap = BitmapFactory.decodeStream(inputStream, null, options);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String suffix = FileUtil.getSuffix(uri.getPath());
        if (TextUtils.isEmpty(suffix)) suffix = "jpg";

        Bitmap.CompressFormat compressFormat;
        if ("PNG".equalsIgnoreCase(suffix)) {
            compressFormat = Bitmap.CompressFormat.PNG;
        } else {
            compressFormat = Bitmap.CompressFormat.JPEG;
        }

        ByteArrayOutputStream baos = null;
        FileOutputStream fos = null;
        try {
            baos = new ByteArrayOutputStream();

            sampleBitmap.compress(compressFormat, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            LogUtil.d("compressBitmap", String.format("quality = %s, size = %s", 100, baos.toByteArray().length));
            int quality = 90;
            while (baos.toByteArray().length > sizeLimit && quality >= 0) { // 循环判断如果压缩后图片是否大于sizeLimit,大于继续压缩
                baos.reset();
                sampleBitmap.compress(compressFormat, quality, baos);
                LogUtil.d("compressBitmap", String.format("quality = %s, size = %s", quality, baos.toByteArray().length));
                quality = adjustmentQuality(quality);
            }

            fos = new FileOutputStream(newPath);
            fos.write(baos.toByteArray());
            fos.flush();
            baos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 压缩图片
     */
    public static void compressImage(Bitmap oldBitmap, String newPath, int sizeLimit) {

        ByteArrayOutputStream baos = null;
        FileOutputStream fos = null;
        try {
            baos = new ByteArrayOutputStream();

            oldBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            LogUtil.d("compressBitmap", String.format("quality = %s, size = %s", 100, baos.toByteArray().length));
            int quality = 90;
            while (baos.toByteArray().length > sizeLimit && quality >= 0) { // 循环判断如果压缩后图片是否大于sizeLimit,大于继续压缩
                baos.reset();
                oldBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                LogUtil.d("compressBitmap", String.format("quality = %s, size = %s", quality, baos.toByteArray().length));
                quality = adjustmentQuality(quality);
            }

            fos = new FileOutputStream(newPath);
            fos.write(baos.toByteArray());
            fos.flush();
            baos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException ignored) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static Bitmap compressSizeImage(Bitmap image, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        /*ByteBuffer buffer = ByteBuffer.allocate(image.getByteCount());
        image.copyPixelsToBuffer(buffer);*/

            BitmapFactory.Options bitMapOptions = new BitmapFactory.Options();
            bitMapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            bitMapOptions.inSampleSize = image.getHeight() / 60;
            image = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size(), bitMapOptions);
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 90;
            while (baos.size() / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset(); // 重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;// 每次都减少10
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
    }

    private static int adjustmentQuality(int quality) {
        if (quality > 10) {
            return quality - 10;
        } else {
            return quality - 2;
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(byte[] data, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampledBitmapFromFile(File file, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Rect rect = new Rect(0, 0, reqWidth, reqHeight);
        try {
            BitmapFactory.decodeStream(new FileInputStream(file), rect, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeStream(new FileInputStream(file), rect, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 原图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比例
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择的宽和高中最小的比率作为 inSampleSize 值，这样可以保证最终图片的宽高一定会大于等于目标的宽高
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照指定的角度进行旋转
     *
     * @param bitmap 需要旋转的图片
     * @param degree 指定的旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bitmap, int degree) {
        if (bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            // 创建新的图片
            Bitmap rotateBitmap = null;

            try {
                // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
                rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            if (rotateBitmap == null) {
                rotateBitmap = bitmap;
            }
            if (bitmap != rotateBitmap) {
                bitmap.recycle();
            }
            return rotateBitmap;
        }
        return null;
    }

    public static Bitmap cropBitmap(Bitmap bitmap, int dstWidth, int dstHeight, boolean keepScale) {
        if (bitmap == null || dstWidth <= 0 || dstHeight <= 0) return null;
        if (!keepScale) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
            if (scaledBitmap != bitmap) {
                bitmap.recycle();
            }
            return scaledBitmap;
        } else {
            int srcRatio = bitmap.getHeight() / bitmap.getWidth();
            int dstRatio = dstHeight / dstWidth;
            Bitmap scaleBitmap;
            if (srcRatio < dstRatio) {
                scaleBitmap = Bitmap.createScaledBitmap(bitmap, dstHeight / srcRatio, dstHeight, true);
            } else {
                scaleBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstWidth * srcRatio, true);
            }
            if (scaleBitmap != bitmap) {
                bitmap.recycle();
            }
            Bitmap cropBitmap = Bitmap.createBitmap(scaleBitmap, 0, 0, dstWidth, dstHeight);
            if (cropBitmap != scaleBitmap) {
                scaleBitmap.recycle();
            }
            return cropBitmap;
        }
    }

    /**
     * 保存图片
     */
    public static boolean saveBitmap(Bitmap bitmap, File file) {
        return saveBitmap(bitmap, file, 100);
    }

    /**
     * 保存图片
     */
    public static boolean saveBitmap(Bitmap bitmap, File file, int quality) {

        if (bitmap == null) return false;
        FileOutputStream fos = null;
        try {
            if (!file.getParentFile().exists() || !file.getParentFile().isDirectory()) {
                file.getParentFile().mkdirs();
            }

            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            fos = new FileOutputStream(file);

            if ("PNG".equalsIgnoreCase(FileUtil.getSuffix(file.getAbsolutePath()))) {
                bitmap.compress(Bitmap.CompressFormat.PNG, quality, fos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            }

            fos.flush();
            boolean isInsertGallery = true;
            if (isInsertGallery) {
                try {
                    MediaStore.Images.Media.insertImage(BaseApp.getInstance().getContentResolver(), file.getAbsolutePath(), file.getName(), null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 先通过缩小图片，使其丢失一些像素点，
     * 接着进行模糊化处理，然后再放大到原来尺寸。
     * 由于图片缩小后再进行模糊处理，
     * 需要处理的像素点和半径都变小，
     * 从而使得模糊处理速度加快
     *
     * @param context
     * @param srcBitmap 需要被模糊的源 bitmap
     * @param radius    模糊度
     * @param view      要呈现模糊的 view
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void blurImage(Context context, Bitmap srcBitmap, int radius, View view) {

//        long startMs = System.currentTimeMillis();

        float scaleFactor = 1;
        if (radius > 1) {//如果 scaleFactor > view长度 下面的 createBitmap 参数将为 0
            int minLength = Math.min(view.getMeasuredWidth(), view.getMeasuredHeight());
            scaleFactor = Math.min(minLength, radius / 2);
            radius = (int) (radius / scaleFactor);
        }

        if (scaleFactor > Math.min(view.getMeasuredWidth(), view.getMeasuredHeight())) {
            scaleFactor = Math.min(view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        overlay = fastBlur(context, overlay, radius);
        view.setBackground(new BitmapDrawable(context.getResources(), overlay));
    }

    /**
     * 模糊算法
     *
     * @param context
     * @param sentBitmap
     * @param radius
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap fastBlur(Context context, Bitmap sentBitmap, int radius) {

        // > 16 or > 18?
        if (DeviceUtil.getSdkVersion() > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

                RenderScript rs = RenderScript.create(context);// Create the Renderscript instance that will do the work.
                Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                        Allocation.USAGE_SCRIPT);// Allocate memory for Renderscript to work with
                Allocation output = Allocation.createTyped(rs, input.getType());
                ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));// Load up an instance of the specific script that we want to use.
                script.setRadius(radius);// Set the blur radius
                script.setInput(input);
                script.forEach(output);// Start the ScriptIntrinisicBlur
                output.copyTo(bitmap);// Copy the output to the blurred bitmap

                if (rs != null) {
                    rs.destroy();
                }
                return bitmap;
            } catch (RSRuntimeException e) {
                // continue
            }
        }

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    public static Bitmap convertViewToBitmap(View v) {
        if (v.getMeasuredWidth() == 0) {
            v.measure(View.MeasureSpec.makeMeasureSpec(
                    (int) (CommonUtil.getScreenWidth() * 0.85), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(
                            (int) (CommonUtil.getScreenHeight() * 0.6), View.MeasureSpec.EXACTLY));
        }

        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 纵向拼接Bitmap
     */
    public static Bitmap spliceBitmap_vertical(Bitmap... bitmaps) {
        int width = 0;
        int height = 0;

        for (Bitmap bitmap : bitmaps) {
            width = Math.max(width, bitmap.getWidth());
            height += bitmap.getHeight();
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        int curHeight = 0;
        for (Bitmap bitmap : bitmaps) {
            canvas.drawBitmap(bitmap, 0, curHeight, null);
            curHeight += bitmap.getHeight();
        }

        return result;
    }

    /**
     * 当cache过大时，getDrawingCache返回null，用 getViewBitmap2()
     */
    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    public static Bitmap getViewBitmap2(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
//        if (Build.VERSION.SDK_INT >= 11) {
//            view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
//            view.layout((int) view.getX(), (int) view.getY(), (int) view.getX() + view.getMeasuredWidth(), (int) view.getY() + view.getMeasuredHeight());
//        } else {
//            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        }
        view.draw(canvas);
        return bitmap;
    }
}
