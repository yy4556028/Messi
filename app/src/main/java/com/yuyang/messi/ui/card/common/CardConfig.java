package com.yuyang.messi.ui.card.common;

import com.yuyang.lib_base.utils.PixelUtils;

public class CardConfig {

    //屏幕上最多同时显示几个Item
    public static int MAX_SHOW_COUNT = 5;

    //每一级Scale相差0.05f，translationY相差7dp左右
    public static float SCALE_GAP = 0.05f;
    public static int TRANS_Y_GAP = PixelUtils.dp2px(15);

}
