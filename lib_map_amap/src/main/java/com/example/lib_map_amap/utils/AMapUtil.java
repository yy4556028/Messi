/**
 *
 */
package com.example.lib_map_amap.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.widget.EditText;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteRailwayItem;
import com.yuyang.lib_base.bean.PopBean;
import com.yuyang.lib_base.ui.view.picker.BottomChooseDialog;
import com.yuyang.lib_base.utils.ToastUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class AMapUtil {

    private static final String TAG = "AMapUtil";

    /**
     * 判断edittext是否null
     */
    public static String checkEditText(EditText editText) {

        if (editText != null && editText.getText() != null
                && !(editText.getText().toString().trim().equals(""))) {
            return editText.getText().toString().trim();
        } else {
            return "";
        }
    }

    public static Spanned stringToSpan(String src) {

        return src == null ? null : Html.fromHtml(src.replace("\n", "<br />"));
    }

    public static String colorFont(String src, String color) {

        StringBuffer strBuf = new StringBuffer();

        strBuf.append("<font color=").append(color).append(">").append(src)
                .append("</font>");
        return strBuf.toString();
    }

    public static String makeHtmlNewLine() {

        return "<br />";
    }

    public static String makeHtmlSpace(int number) {

        final String space = "&nbsp;";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < number; i++) {
            result.append(space);
        }
        return result.toString();
    }

    public static String getFriendlyLength(int lenMeter) {

        if (lenMeter > 10000) // 10 km
        {
            int dis = lenMeter / 1000;
            return dis + /*ChString.Kilometer*/"km";
        }

        if (lenMeter > 1000) {
            float dis = (float) lenMeter / 1000;
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dstr = fnum.format(dis);
            return dstr + /*ChString.Kilometer*/"km";
        }

       /* if (lenMeter > 100) {
            int dis = lenMeter / 50 * 50;
            return dis + *//*ChString.Meter*//*"m";
        }

        int dis = lenMeter / 10 * 10;
        if (dis == 0) {
            dis = 10;
        }*/

        return lenMeter + /*ChString.Meter*/"m";
    }

    public static boolean IsEmptyOrNullString(String s) {

        return (s == null) || (s.trim().length() == 0);
    }

    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {

        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {

        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }


    public static String loc2Str(double location) {

        return String.format(Locale.CHINA, "%.6f", location);
    }

    public static Double str2Double(String num) {

        try {
            return Double.valueOf(num);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * 把集合体的LatLonPoint转化为集合体的LatLng
     */
    public static ArrayList<LatLng> convertArrList(List<LatLonPoint> shapes) {

        ArrayList<LatLng> lineShapes = new ArrayList<LatLng>();
        for (LatLonPoint point : shapes) {
            LatLng latLngTemp = AMapUtil.convertToLatLng(point);
            lineShapes.add(latLngTemp);
        }
        return lineShapes;
    }

    /**
     * long类型时间格式化
     */
    public static String convertToTime(long time) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        return df.format(date);
    }

    public static final String HtmlBlack = "#000000";
    public static final String HtmlGray = "#808080";

    public static String getFriendlyTime(int second) {

        if (second > 3600) {
            int hour = second / 3600;
            int miniate = (second % 3600) / 60;
            return hour + "小时" + miniate + "分钟";
        }
        if (second >= 60) {
            int miniate = second / 60;
            return miniate + "分钟";
        }
        return second + "秒";
    }

    //路径规划方向指示和图片对应
//    public static int getDriveActionID(String actionName) {
//
//        if (actionName == null || actionName.equals("")) {
//            return R.drawable.dir3;
//        }
//        if ("左转".equals(actionName)) {
//            return R.drawable.dir2;
//        }
//        if ("右转".equals(actionName)) {
//            return R.drawable.dir1;
//        }
//        if ("向左前方行驶".equals(actionName) || "靠左".equals(actionName)) {
//            return R.drawable.dir6;
//        }
//        if ("向右前方行驶".equals(actionName) || "靠右".equals(actionName)) {
//            return R.drawable.dir5;
//        }
//        if ("向左后方行驶".equals(actionName) || "左转调头".equals(actionName)) {
//            return R.drawable.dir7;
//        }
//        if ("向右后方行驶".equals(actionName)) {
//            return R.drawable.dir8;
//        }
//        if ("直行".equals(actionName)) {
//            return R.drawable.dir3;
//        }
//        if ("减速行驶".equals(actionName)) {
//            return R.drawable.dir4;
//        }
//        return R.drawable.dir3;
//    }

//    public static int getWalkActionID(String actionName) {
//
//        if (actionName == null || actionName.equals("")) {
//            return R.drawable.dir13;
//        }
//        if ("左转".equals(actionName)) {
//            return R.drawable.dir2;
//        }
//        if ("右转".equals(actionName)) {
//            return R.drawable.dir1;
//        }
//        if ("向左前方".equals(actionName) || "靠左".equals(actionName) || actionName.contains("向左前方")) {
//            return R.drawable.dir6;
//        }
//        if ("向右前方".equals(actionName) || "靠右".equals(actionName) || actionName.contains("向右前方")) {
//            return R.drawable.dir5;
//        }
//        if ("向左后方".equals(actionName) || actionName.contains("向左后方")) {
//            return R.drawable.dir7;
//        }
//        if ("向右后方".equals(actionName) || actionName.contains("向右后方")) {
//            return R.drawable.dir8;
//        }
//        if ("直行".equals(actionName)) {
//            return R.drawable.dir3;
//        }
//        if ("通过人行横道".equals(actionName)) {
//            return R.drawable.dir9;
//        }
//        if ("通过过街天桥".equals(actionName)) {
//            return R.drawable.dir11;
//        }
//        if ("通过地下通道".equals(actionName)) {
//            return R.drawable.dir10;
//        }
//
//        return R.drawable.dir13;
//    }

    public static String getBusPathTitle(BusPath busPath) {

        if (busPath == null) {
            return String.valueOf("");
        }
        List<BusStep> busSetps = busPath.getSteps();
        if (busSetps == null) {
            return String.valueOf("");
        }
        StringBuffer sb = new StringBuffer();
        for (BusStep busStep : busSetps) {
            StringBuffer title = new StringBuffer();
            if (busStep.getBusLines().size() > 0) {
                for (RouteBusLineItem busline : busStep.getBusLines()) {
                    if (busline == null) {
                        continue;
                    }

                    String buslineName = getSimpleBusLineName(busline.getBusLineName());
                    title.append(buslineName);
                    title.append(" / ");
                }
//					RouteBusLineItem busline = busStep.getBusLines().get(0);

                sb.append(title.substring(0, title.length() - 3));
                sb.append(" > ");
            }
            if (busStep.getRailway() != null) {
                RouteRailwayItem railway = busStep.getRailway();
                sb.append(railway.getTrip() + "(" + railway.getDeparturestop().getName()
                        + " - " + railway.getArrivalstop().getName() + ")");
                sb.append(" > ");
            }
        }
        return sb.substring(0, sb.length() - 3);
    }

    public static String getBusPathDes(BusPath busPath) {

        if (busPath == null) {
            return String.valueOf("");
        }
        long second = busPath.getDuration();
        String time = getFriendlyTime((int) second);
        float subDistance = busPath.getDistance();
        String subDis = getFriendlyLength((int) subDistance);
        float walkDistance = busPath.getWalkDistance();
        String walkDis = getFriendlyLength((int) walkDistance);
        return String.valueOf(time + " | " + subDis + " | 步行" + walkDis);
    }

    public static String getSimpleBusLineName(String busLineName) {

        if (busLineName == null) {
            return String.valueOf("");
        }
        return busLineName.replaceAll("\\(.*?\\)", "");
    }

    public static double calculateDistance(double lon1, double lat1, double lon2, double lat2) {

        return AMapUtils.calculateLineDistance(new LatLng(lat1, lon1), new LatLng(lat2, lon2));
    }


    public static void navigateToMap(final Activity activity, final String lat, final String lon, final String address) {

        try {
            if (checkApkExist(activity, "com.autonavi.minimap") && checkApkExist(activity, "com.baidu.BaiduMap")) {
                BottomChooseDialog.showSingle(activity,
                        null,
                        Arrays.asList(new PopBean(null, "百度地图"), new PopBean(null, "高德地图")),
                        new BottomChooseDialog.SingleChoiceListener() {
                            @Override
                            public void onItemClick(int index, PopBean popBean) {
                                if (index == 0) {
                                    goToAMap(activity, lat, lon, address);
                                } else if (index == 1) {
                                    goToBDMap(activity, lat, lon, address);
                                }
                            }
                        });
            } else if (checkApkExist(activity, "com.autonavi.minimap")) {//高德
                goToAMap(activity, lat, lon, address);
            } else if (checkApkExist(activity, "com.baidu.BaiduMap")) {//百度
                goToBDMap(activity, lat, lon, address);
            } else {
                ToastUtil.showToast("您暂未安装地图App，请先下载（支持高德/百度）");
                /*showConfirmDialog(getString(R.string.plz_install_maps_title), getString(R.string.plz_install_maps_content),
                        getString(R.string.process_know), null);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast("导航出错了！");
            /*showWarningMessage(getString(R.string.navigations_fail));*/
        }
    }

    private static boolean checkApkExist(Context context, String packageName) {

        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static void goToAMap(Context context, String lat, String lon, String address) {

        StringBuffer uriData = new StringBuffer("amapuri://route/plan/?")
                .append("dlat=").append(lat)
                .append("&dlon=").append(lon)
                .append("&dname=").append(address)
                .append("&dev=").append(0)
                .append("&t=").append(3)
                .append("&rideType=").append("elebike");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriData.toString()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setPackage("com.autonavi.minimap");
        context.startActivity(intent);
    }

    private static void goToBDMap(Context context, String lat, String lon, String address) {
        //高德转百度
        double x = Double.parseDouble(lon), y = Double.parseDouble(lat);
        double z = sqrt(x * x + y * y) + 0.00002 * sin(y * Math.PI);
        double theta = atan2(y, x) + 0.000003 * cos(x * Math.PI);
        lon = String.valueOf(z * cos(theta) + 0.0065);
        lat = String.valueOf(z * sin(theta) + 0.006);

        StringBuffer uriData = new StringBuffer("baidumap://map/direction?")
                .append("destination=latlng:").append(lat)
                .append(",").append(lon)
                .append("|name:").append(address)
                .append("&mode=riding&src=com.suning.crowdsourcing");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriData.toString()));
        context.startActivity(intent);
    }

}
