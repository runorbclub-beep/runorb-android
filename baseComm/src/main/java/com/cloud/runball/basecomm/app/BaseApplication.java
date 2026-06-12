package com.cloud.runball.basecomm.app;

import android.app.Application;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.basecomm
 * @ClassName: BaseApplication
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/11 15:44
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/11 15:44
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public abstract  class BaseApplication extends Application implements IApplication{

    @Override
    public void onCreate() {
        super.onCreate();
        init(this);
    }

}
