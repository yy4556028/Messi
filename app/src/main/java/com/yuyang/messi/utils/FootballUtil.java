package com.yuyang.messi.utils;


import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yuyang.messi.R;

public class FootballUtil {

    public static void displayTeamIconByTeamName(String teamName, ImageView imageView) {
        String iconUrl = null;
        switch (teamName) {
            //中超


            //英超
            case "曼联":{
                iconUrl = "https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=2181549488,3047915060&fm=58";
                break;
            }
            case "切尔西": {
                iconUrl = "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=683344462,1738295945&fm=58";
                break;
            }
            case "阿森纳" :{
                iconUrl = "https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=674112551,3667566855&fm=58";
                break;
            }
            case "曼城":{
                iconUrl = "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=1002962304,3697119945&fm=58";
                break;
            }


            //西甲
            case "巴塞罗那": {
                iconUrl = "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=668083465,736946662&fm=58";
                break;
            }
            case "皇家马德里": {
                iconUrl = "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=527365068,2154775773&fm=58";
                break;
            }
            case "马德里竞技":{
                iconUrl = "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3923983002,3305056787&fm=58";
                break;
            }
            case "比利亚雷亚尔":{
                iconUrl = "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=3185204332,3877274042&fm=58";
                break;
            }

            //意甲
            case "尤文图斯":{
                iconUrl = "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=2662032600,1743927776&fm=58";
                break;
            }
            case "国际米兰": {
                iconUrl = "https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=3880199741,3854322934&fm=58";
                break;
            }

            //德甲
            case "拜仁慕尼黑": {
                iconUrl = "https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=2759144096,686581894&fm=58";
                break;
            }
            case "多特蒙德":{
                iconUrl = "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3411676395,1567213196&fm=58";
                break;
            }
            case "沃尔夫斯堡": {
                iconUrl = "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=4157915629,3789555103&fm=58";
                break;
            }

            case "沙尔克04": {
                iconUrl = "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=2258854995,186673245&fm=58";
                break;
            }


            //法甲
            case "里昂": {
                iconUrl = "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=2650411351,1137673191&fm=58";
                break;
            }
        }

        if (TextUtils.isEmpty(iconUrl)) {
            Glide.with(imageView.getContext())
                    .load(R.mipmap.ic_launcher)
                    .into(imageView);
        } else {
            Glide.with(imageView.getContext())
                    .load(iconUrl)
                    .into(imageView);
        }

    }
}
