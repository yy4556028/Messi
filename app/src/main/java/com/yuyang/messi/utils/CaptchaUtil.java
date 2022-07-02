package com.yuyang.messi.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

public class CaptchaUtil {

    private static final int CAPTCHA_LENGTH = 4;//验证码的长度  这里是4位
    private static final int DEFAULT_FONT_SIZE = 60;//字体大小
    private static final int DEFAULT_LINE_NUMBER = 3;//多少条干扰线
    private static final int BASE_PADDING_LEFT = 20; //左边距
    private static final int RANGE_PADDING_LEFT = 35;//左边距范围值
    private static final int BASE_PADDING_TOP = 42;//上边距
    private static final int RANGE_PADDING_TOP = 15;//上边距范围值

    private static final int DEFAULT_WIDTH = 200, DEFAULT_HEIGHT = 100;//图片的宽高

    public static Random random = new Random();

    private static int padding_left, padding_top;
    private static String captcha;

    // 不包括 0 O 1 I
    public static final char[] CHARS = {'2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N',
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static String getRandomString(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return sb.toString();
    }

    public static String getCaptcha() {
        return captcha;
    }

    public static Bitmap createBitmap() {
        padding_left = 0; //每次生成验证码图片时初始化
        padding_top = 0;

        Bitmap bitmap = Bitmap.createBitmap(DEFAULT_WIDTH, DEFAULT_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        captcha = getRandomString(CAPTCHA_LENGTH);

        int bgColor = ColorUtil.getRandomColor();
        int textColor = ColorUtil.getReverseColor(bgColor);

        canvas.drawColor(bgColor);
        Paint paint = new Paint();
        paint.setColor(textColor);
        paint.setTextSize(DEFAULT_FONT_SIZE);

        for (int i = 0; i < captcha.length(); i++) {
            randomTextStyle(paint);
            randomPadding();
            canvas.drawText(captcha.charAt(i) + "", padding_left, padding_top, paint);
        }

        for (int i = 0; i < DEFAULT_LINE_NUMBER; i++) {
            drawLine(canvas, paint);
        }

        return bitmap;
    }

    private static void randomTextStyle(Paint paint) {
        paint.setFakeBoldText(random.nextBoolean());  //true为粗体，false为非粗体
        float skewX = random.nextInt(11) / 10;
        skewX = random.nextBoolean() ? skewX : -skewX;
        paint.setTextSkewX(skewX); //float类型参数，负数表示右斜，整数左斜
//      paint.setUnderlineText(true); //true为下划线，false为非下划线
//      paint.setStrikeThruText(true); //true为删除线，false为非删除线
    }

    private static void randomPadding() {
        padding_left += BASE_PADDING_LEFT + random.nextInt(RANGE_PADDING_LEFT);
        padding_top = BASE_PADDING_TOP + random.nextInt(RANGE_PADDING_TOP);
    }

    private static void drawLine(Canvas canvas, Paint paint) {
        int startX = random.nextInt(DEFAULT_WIDTH);
        int startY = random.nextInt(DEFAULT_HEIGHT);
        int stopX = random.nextInt(DEFAULT_WIDTH);
        int stopY = random.nextInt(DEFAULT_HEIGHT);
        paint.setStrokeWidth(1);
        paint.setColor(ColorUtil.getRandomColor());
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

}
