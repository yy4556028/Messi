package com.yuyang.messi.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import com.yuyang.messi.MessiApp;

public class FontUtil {
    private static Typeface lightItalicTypeFace, regularTypeFace, lightTypeface, BoldTypeFace;

    private final static AssetManager asset = MessiApp.getInstance().getAssets();

    /**
     * 细体
     * @return
     */
    public static Typeface getLightTypeFace(){
        if (lightTypeface == null){
            lightTypeface = Typeface.createFromAsset(asset, "fonts/OpenSans_Light.ttf");
        }
        return lightTypeface;
    }

    /**
     * 习题斜体
     * @return
     */
    public static Typeface getLightItalicTypeFace(){
        if (lightItalicTypeFace == null){
            lightItalicTypeFace = Typeface.createFromAsset(asset, "fonts/OpenSans_LightItalic.ttf");
        }
        return lightItalicTypeFace;
    }

    /**
     * 正体
     * @return
     */
    public static Typeface getRegularTypeFace(){
        if (regularTypeFace == null){
            regularTypeFace = Typeface.createFromAsset(asset, "fonts/OpenSans_Regular.ttf");
        }
        return regularTypeFace;
    }

    /**
     * 粗体
     * @return
     */
    public static Typeface getBoldTypeFace(){
        if (BoldTypeFace == null){
            BoldTypeFace = Typeface.createFromAsset(asset, "fonts/OpenSans_Bold.ttf");
        }
        return BoldTypeFace;
    }
}
