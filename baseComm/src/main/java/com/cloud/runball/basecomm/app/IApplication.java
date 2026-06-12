package com.cloud.runball.basecomm.app;

import android.app.Application;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.basecomm
 * @ClassName: IApplication
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/11 15:44
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/11 15:44
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public interface IApplication {
    /**
     * Module单独使用到的初始化在这里进行
     */
    void init(Application application);

    /**
     * Module中需在application中非立即初始化在这里进行
     * @param application
     */
    void lazyInit(Application application);

}
