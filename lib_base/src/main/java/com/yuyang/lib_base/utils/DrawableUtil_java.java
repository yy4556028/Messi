package com.yuyang.lib_base.utils;

import android.content.res.ColorStateList;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * https://juejin.cn/post/7064364261685854245
 */
public class DrawableUtil_java {

    /**
     * 是可绘制对象列表组成的可绘制对象。列表中的每个可绘制对象均按照列表顺序绘制，列表中的最后一个可绘制对象绘于顶部
     */
    public static LayerDrawable createLayerDrawableDemo(Drawable[] layers) {
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        //设置每个layer的 偏移（像素）
        layerDrawable.setLayerInset(0, 0, 0, 0, 0);
        return layerDrawable;
    }

    /**
     * 会根据对象状态，使用多个不同的图像来表示同一个图形
     * 顺序遍历，符合return
     */
    public static ColorStateList createColorStateList(@ColorInt int normal, @ColorInt int pressed, @ColorInt int selected, @ColorInt int checked, @ColorInt int unable) {
        int[] colors = new int[]{normal, pressed, selected, checked, unable};
        int[][] states = new int[5][];
        states[0] = new int[]{android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed};
        states[2] = new int[]{android.R.attr.state_enabled, android.R.attr.state_selected};
        states[3] = new int[]{android.R.attr.state_enabled, android.R.attr.state_checked};
        states[4] = new int[]{-android.R.attr.state_enabled};//-代表此属性为false
        return new ColorStateList(states, colors);
    }

    /**
     * 会根据对象状态，使用多个不同的图像来表示同一个图形
     * 顺序遍历，符合return
     */
    public static StateListDrawable createStateListDrawable(Drawable normal, Drawable pressed, Drawable selected, Drawable checked, Drawable unable) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, normal);
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed}, pressed);
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_selected}, selected);
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_checked}, checked);
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, unable);//-代表此属性为false
//        stateListDrawable.addState(StateSet.WILD_CARD, );
//        stateListDrawable.addState(StateSet.NOTHING, );
        return stateListDrawable;
    }

    /**
     * 管理可绘制对象列表，每个可绘制对象都有设置Level等级限制
     * 当使用setImageLevel()时，会加载级别列表中 android:maxLevel 值大于或等于传递至方法的值的可绘制对象资源。
     */
    public static LevelListDrawable createLevelListDrawable() {
        LevelListDrawable levelListDrawable = new LevelListDrawable();
        levelListDrawable.addLevel(0, 1, new ColorDrawable());
        levelListDrawable.addLevel(0, 2, new ColorDrawable());
        levelListDrawable.addLevel(0, 3, new ColorDrawable());
        ImageView imageView = new ImageView(null);
        imageView.setImageDrawable(levelListDrawable);
        imageView.setImageLevel(0);
        return levelListDrawable;
    }

    /**
     * 转换可绘制对象：可在两种可绘制对象资源之间交错淡出
     * 未验证：不能超过两个Item
     */
    public static TransitionDrawable createTransitionDrawable(Drawable[] layers) {
        TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
        transitionDrawable.startTransition(3000);//向前转换
        transitionDrawable.reverseTransition(3000);//向后转换
        transitionDrawable.resetTransition();
        return transitionDrawable;
    }

    /**
     * 插入可绘制对象：以指定距离插入其他可绘制对象，当视图需要小于视图实际边界的背景时，此类可绘制对象很有用
     */
    public static InsetDrawable createInsetDrawable(Drawable drawable,
                                                    int insetLeft, int insetTop, int insetRight, int insetBottom) {
        return new InsetDrawable(drawable, insetLeft, insetTop, insetRight, insetBottom);
    }

    /**
     * 裁剪可绘制对象：
     * 根据level等级对可绘制对象进行裁剪，可以根据level与gravity来控制子可绘制对象的宽度与高度
     * 最后通过设置level等级来实现裁剪，level 默认级别为 0，即完全裁剪，使图像不可见。当级别为 10,000 时，图像不会裁剪，而是完全可见
     */
    public static ClipDrawable createClipDrawable() {
        //左右开门关门效果
        return new ClipDrawable(new ColorDrawable(),
                Gravity.CENTER,
                ClipDrawable.HORIZONTAL);
    }

    /**
     * 根据level等级来更改其可绘制对象大小
     */
    public static ScaleDrawable createScaleDrawable() {
        return new ScaleDrawable(new ColorDrawable(),
                Gravity.CENTER,//指定缩放后的重力位置
                1f,//百分比。缩放高度
                1f);//百分比。缩放宽度
    }

    public static ShapeDrawable createShapeDrawable(@Nullable float[] outerRadii, @Nullable RectF inset, @Nullable float[] innerRadii) {

        ArcShape onArcShape = new ArcShape(180, 180);//扇形
        OvalShape oval = new OvalShape();
        RoundRectShape roundRectShape = new RoundRectShape(
                outerRadii,
                inset,
                innerRadii);

        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setShape(roundRectShape);
//        shapeDrawable.getPaint().setColor();
//        shapeDrawable.setTint();
//        shapeDrawable.getPaint().setPathEffect();
        return shapeDrawable;
    }

    /**
     * 实现渐变颜色效果。其实也是属于ShapeDrawable
     */
    public static GradientDrawable createGradientDrawable() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        /**
         * GradientDrawable.LINEAR_GRADIENT 线性渐变
         * GradientDrawable.SWEEP_GRADIENT  扫描式渐变
         * RADIAL_GRADIENT                  圆形渐变
         */
        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
//        gradientDrawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
        gradientDrawable.setColors(null);
        gradientDrawable.setGradientRadius(100);
//        gradientDrawable.setStroke();
        return gradientDrawable;
    }

    /**
     * 逐帧动画可绘制对象
     */
    public static AnimationDrawable createAnimationDrawable(Drawable[] drawables, int duration) {
        AnimationDrawable animationDrawable = new AnimationDrawable();
        for (Drawable drawable : drawables) {
            animationDrawable.addFrame(drawable, duration);
        }
        return animationDrawable;
    }

    public static void createCustomDrawable() {
        // TODO: 自定义drawable to
    }
}