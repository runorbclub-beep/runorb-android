package com.cloud.runball.share;

import android.app.Application;

import com.cloud.runball.basecomm.app.IApplication;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

/**
 * @ProjectName: wristball
 * @Description:
 * @Author: hwl
 * @CreateDate: 2021/7/11 15:39
 * @Version: 1.0
 */
public class ShareModule implements IApplication {

    public void init(Application application) {
        //初始化组件化基础库, 所有友盟业务SDK都必须调用此初始化接口。
        //建议在宿主App的Application.onCreate函数中调用基础组件库初始化函数。
        //2021-07-30友盟新规
        UMConfigure.preInit(application, null, null);
        if (!(Boolean)SPUtils.get(application, "isFirstUse", true)) {
            lazyInit(application);
        }
    }

    @Override
    public void lazyInit(Application application) {
        UMConfigure.init(application, null, null, UMConfigure.DEVICE_TYPE_PHONE, "");

        UMConfigure.setLogEnabled(false);
        //建议在宿主App的Application.onCreate函数中调用此函数。
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        PlatformConfig.setWeixin("wx3028503127138b41","18c378787a52bb8c1fc6f247ec8ab0d6");
        PlatformConfig.setWXFileProvider("cloud.runball.bazu.fileprovider");
//        PlatformConfig.setWXFileProvider("cloud.runball.bazutest.fileprovider");
    }

}
