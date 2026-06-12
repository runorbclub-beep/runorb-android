package com.cloud.runball.basecomm.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: LaunchApp
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/1 16:11
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/1 16:11
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LaunchApp {

    public static final String YINGYONGBAO="com.tencent.android.qqdownloader";
    public static final String XIAOMI="com.xiaomi.market";
    public static final String QIHOO="com.qihoo.appstore";
    public static final String BAIDU="com.baidu.appsearch";
    public static final String HUAWEI="com.huawei.appmarket";
    public static final String OPPO="com.oppo.market";
    public static final String VIVO="com.bbk.appstore";


    /**
     * 判断应用市场是否存在的方法
     *
     * @param context
     * @param packageName
     *
     * 主流应用商店对应的包名
     * com.android.vending    -----Google Play
     * com.tencent.android.qqdownloader     -----应用宝
     * com.qihoo.appstore    -----360手机助手
     * com.baidu.appsearch    -----百度手机助
     * com.xiaomi.market    -----小米应用商店
     * com.wandoujia.phoenix2    -----豌豆荚
     * com.huawei.appmarket    -----华为应用市场
     * com.taobao.appcenter    -----淘宝手机助手
     * com.hiapk.marketpho    -----安卓市场
     * cn.goapk.market        -----安智市场
     * com.oppo.market       ------OPPO应用商店
     * com.bbk.appstore      ------vivo应用商店
     */
    public static boolean isAvilible(Context context, String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> pName = new ArrayList<String>();
        // 从pinfo中将包名字取出
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pf = pinfo.get(i).packageName;
                pName.add(pf);
            }
        }
        // 判断pName中是否有目标程序的包名，有true，没有false
        return pName.contains(packageName);
    }


    /**
     * 启动到应用商店app详情界面
     *
     * @param appPkg    目标App的包名
     * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面
     */
    public static void launchAppDetail(Context mContext, String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg)) {
                return;
            }
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void launchAppDetail(Context mContext, String marketPkg) {
        try {
            if (TextUtils.isEmpty(mContext.getPackageName())) {
                return;
            }
            Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void launchBrowser(Context mContext, String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mContext.startActivity(intent);
    }

    /**
     * 跳转google play
     */
    public static void openGooglePlay(Context context) {
        String playPackage = "com.android.vending";
        try {
            String currentPackageName = context.getPackageName();
            if (currentPackageName != null) {
                Uri currentPackageUri = Uri.parse("market://details?id="+context.getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, currentPackageUri);
                intent.setPackage(playPackage);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Uri currentPackageUri = Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, currentPackageUri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
