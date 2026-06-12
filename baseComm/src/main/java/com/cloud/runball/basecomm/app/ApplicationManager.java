package com.cloud.runball.basecomm.app;

import com.cloud.runball.basecomm.app.IApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.basecomm
 * @ClassName: ApplicationManager
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/11 16:12
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/11 16:12
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ApplicationManager {
    private static List<IApplication> applicationList = new ArrayList<>();

    private static void register(IApplication iApplication) {
        if (iApplication != null) {
            applicationList.add(iApplication);
        }
    }

    public static void init() {
        for (IApplication application : applicationList) {
//            application.init();
        }
    }
}
