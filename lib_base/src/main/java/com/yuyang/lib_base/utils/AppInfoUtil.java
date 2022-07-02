package com.yuyang.lib_base.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.yuyang.lib_base.BaseApp;
import com.yuyang.lib_base.utils.security.MD5Util;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yy on 2015.2.3
 */
public class AppInfoUtil {

    /**
     * 获取版本号
     */
    public static int getAppVersionCode() {
        try {
            return BaseApp.getInstance().getPackageManager().getPackageInfo(BaseApp.getInstance().getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取版本号信息名称
     */
    public static String getAppVersionName() {
        return getAppVersionName(BaseApp.getInstance().getPackageName());
    }

    /**
     * 获取版本号信息名称
     */
    public static String getAppVersionName(String packageName) {
        try {
            return BaseApp.getInstance().getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "0.0.0";
        }
    }

    public static boolean isVersionOutdated(String backendVersion) {
        String[] currentVersionParts = getAppVersionName().split("\\.");
        String[] backendVersionParts = backendVersion.split("\\.");

        for (int i = 0; i < 3; i++) {
            if (Integer.parseInt(backendVersionParts[i]) > Integer.parseInt(currentVersionParts[i])) {
                return true;
            } else if (Integer.parseInt(backendVersionParts[i]) < Integer.parseInt(currentVersionParts[i])) {
                return false;
            }
        }
        return false;
    }

    public static boolean isVersionOutdated(String currentVersion, String newVersion) {
        String[] currentVersionParts = currentVersion.split("\\.");
        String[] newVersionParts = newVersion.split("\\.");

        for (int i = 0; i < Math.min(currentVersionParts.length, newVersionParts.length); i++) {
            if (Integer.parseInt(newVersionParts[i]) > Integer.parseInt(currentVersionParts[i])) {
                return true;
            } else if (Integer.parseInt(newVersionParts[i]) < Integer.parseInt(currentVersionParts[i])) {
                return false;
            }
        }
        return newVersionParts.length > currentVersionParts.length;
    }

    public static List<ApplicationInfo> getAllAppInfoList() {
        List<ApplicationInfo> appInfoList = new ArrayList<>();
        // 管理应用程序包
        PackageManager packageManager = BaseApp.getInstance().getPackageManager();
        // 获取手机内所有应用
        List<ApplicationInfo> infoList = packageManager.getInstalledApplications(0);
        for (int i = 0; i < infoList.size(); i++) {
            ApplicationInfo info = infoList.get(i);
            // 判断是否为非系统预装的应用程序
            // 这里还可以添加系统自带的，这里就先不添加了，如果有需要可以自己添加
            // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
            if ((info.flags & info.FLAG_SYSTEM) <= 0) {
                // 添加自己已经安装的应用程序
                appInfoList.add(info);
            }
        }
        return appInfoList;
    }

    // 这是获取apk包的签名信息
    public static Signature getAppSignature(String packageName) {
        try {
            PackageInfo packageInfo = BaseApp.getInstance().getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if (packageInfo != null) {
                return packageInfo.signatures[0];
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String getPublicKey(Signature signature) {

        if (signature == null)
            return null;

        try {
            StringBuffer sb = new StringBuffer();

            String res = MD5Util.bytesToHexString(signature.toByteArray());
            sb.append("apk md5 = " + res);

            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            sha1.update(signature.toByteArray());
            String res2 = MD5Util.bytesToHexString(sha1.digest());
            sb.append("\napk SHA1 = " + res2);

            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature.toByteArray()));


            return sb.toString();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断 App 是否安装
     *
     * @param context
     * @param uri
     * @return
     */
    public static boolean isAppInstalledByUri(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    /**
     * 判断 App 是否安装
     * 推荐
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstalledByPackageName(Context context, String packageName) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        if (packages != null && packages.size() > 0) {
            for (PackageInfo packageInfo : packages) {
                if (packageInfo.packageName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isApkInstalled(Context context, String packageName) {
        PackageManager localPackageManager = context.getPackageManager();
        try {
            PackageInfo localPackageInfo = localPackageManager.getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            return false;
        }
    }

    public static Intent getAppLaunchIntent(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = BaseApp.getInstance().getPackageManager().getLaunchIntentForPackage(packageName);
        return intent;
    }

    public static Intent getAppLaunchIntent2(String packageName) {
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageName);
        List<ResolveInfo> resolveInfoList = BaseApp.getInstance().getPackageManager().queryIntentActivities(resolveIntent, 0);

        if (resolveInfoList != null && resolveInfoList.size() > 0) {
            ResolveInfo resolveInfo = resolveInfoList.get(0);
            String activityPackageName = resolveInfo.activityInfo.packageName;
            String className = resolveInfo.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName componentName = new ComponentName(activityPackageName, className);

            intent.setComponent(componentName);

            return intent;
        }
        return null;
    }

    public static Drawable getUninstallAPKIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        Resources res = null;

        res = getResources(context, apkPath);
        if (res == null) return null;

        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            return res.getDrawable(appInfo.icon);
        }

        return null;
    }

    public static Resources getResources(Context context, String apkPath) {
        try {
            String PATH_AssetManager = "android.content.res.AssetManager";
            Class assetMagCls = Class.forName(PATH_AssetManager);
            Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);
            Object assetMag = assetMagCt.newInstance((Object[]) null);
            Class[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath", typeArgs);
            Object[] valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
            Resources res = context.getResources();
            typeArgs = new Class[3];
            typeArgs[0] = assetMag.getClass();
            typeArgs[1] = res.getDisplayMetrics().getClass();
            typeArgs[2] = res.getConfiguration().getClass();
            Constructor resCt = Resources.class.getConstructor(typeArgs);
            valueArgs = new Object[3];
            valueArgs[0] = assetMag;
            valueArgs[1] = res.getDisplayMetrics();
            valueArgs[2] = res.getConfiguration();
            res = (Resources) resCt.newInstance(valueArgs);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * 采用了新的办法获取APK图标，之前的失败是因为android中存在的一个BUG,通过
     * appInfo.publicSourceDir = apkPath;来修正这个问题，详情参见:
     * http://code.google.com/p/android/issues/detail?id=9151
     */
    public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
            }
        }
        return null;
    }

    public static String getApkLabel(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            return appInfo.loadLabel(pm).toString();
        }
        return null;
    }

    public static String getApkPackageName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
//            appInfo.sourceDir = apkPath;
//            appInfo.publicSourceDir = apkPath;
            return appInfo.packageName;
        }
        return null;
    }

    public static String getApkPackageVersion(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;

            String version = info.versionName == null ? "0" : info.versionName;
            return version;
        }
        return "";
    }

    /**
     * 判断Apk文件是否完整
     *
     * @param apkFilePath
     * @return
     */
    public boolean checkApkFile(String apkFilePath) {
        boolean result = false;
        try {
            PackageManager pManager = BaseApp.getInstance().getPackageManager();
            PackageInfo pInfo = pManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
            if (pInfo == null) {
                result = false;
            } else {
                result = true;
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }
}
