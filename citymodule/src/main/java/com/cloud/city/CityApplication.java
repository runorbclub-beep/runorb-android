package com.cloud.city;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cloud.runball.basecomm.app.BaseApplication;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.city
 * @ClassName: CityApplication
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/17 10:22
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/17 10:22
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CityApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化ARouter
        ARouter.init(this);
    }

    @Override
    public void init(Application application) {

    }

    @Override
    public void lazyInit(Application application) {

    }

}
