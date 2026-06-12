package com.cloud.runball.utils;

import android.text.TextUtils;

import com.cloud.runball.model.AppDataManager;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: CheckPhoner
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/8 10:30
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/8 10:30
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CheckHelper {

    public static final int NONE=0;
    public static final int NO_PHONE=1;
    public static final int PHONE=2;

    public static int onCheckFunc(){
        if(AppDataManager.getInstance().getUserInfoModel()!=null && AppDataManager.getInstance().getUserInfoModel().getUser_info()!=null){
            //测试用户开启不登录
            if(!"游客".equals(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_type_name())){
                return NO_PHONE;
            }
        }else{
            return NONE;
        }
        return PHONE;
    }

}
