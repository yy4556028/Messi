package com.yuyang.messi.net.retrofit;

import com.yuyang.messi.BuildConfig;

/**
 * 图书与电影使用的api来源：豆瓣
 * <p>
 * https://developers.douban.com/
 */
public class Apis {

    /**
     * EDS服务端 URL
     * prexg 测试环境 "http://edsprexg.cnsuning.com/";
     */
    public static final String BASE_URL = BuildConfig.HTTPS_HOST;

    /**
     * 请求格式： http://api.douban.com/v2/book/26637801
     */
    public static String GetBookInfoApi = "https://api.douban.com/v2/book/";

    //以下api来自http://gank.io/api

    //分类数据:  http://gank.io/api/data/数据类型/请求个数/第几页
    //数据类型： 福利 | Android | iOS | 休息视频 | 拓展资源 | 前端 | all
    //example:  http://gank.io/api/data/Android/10/1

    public static String GanHuo = "https://gank.io/api/data";

    /***************** football ********************/
    public static String API_JUHE_APPKEY = "eca50a3dc70ad93057ce111442bdaeb1";
    public static String FOOTBALL_LEAGUE = "http://op.juhe.cn/onebox/football/league";

    /***************** football ********************/

    /***************** 百度搜图 ********************/
//    http://image.baidu.com/channel/listjson?pn=0&rn=30&tag1=美女&tag2=全部&ie=utf8
    public static final String Baidu_Image = "https://image.baidu.com/search/acjson";
    /***************** 百度搜图 ********************/

    /******************** 9158直播 ********************/
    /**
     * http://live.9158.com/Room/GetHotLive_v2?lon=0.0&province=&lat=0.0&page=1&type=1
     */
    public static String live_url = "http://live.9158.com/Room/GetHotLive_v2";

    /**
     * id   long    图库的id
     */
    public static String tn_imageShow = "http://www.tngou.net/tnfs/api/show";
    /******************** 天狗美女图片********************/

    /************************* 美拍 ***************************/
    /**
     * id :话题 id 或频道 id
     * topic_name : 话题名称（若传了话题名称则根据话题名称搜索不根据 id 搜索，id 字段可不传）
     * count : 默认为 20，最多 100
     * page : 返回结果的页码，默认为1
     * feature : 排序方式，new 表示最新，hot 表示最热，频道默认为最新，话题默认为最热
     * max_id :
     */
    public static String meipai_list = "http://newapi.meipai.com/output/channels_topics_timeline.json";
}
