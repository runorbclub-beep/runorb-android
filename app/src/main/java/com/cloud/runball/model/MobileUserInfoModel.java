package com.cloud.runball.model;

import com.cloud.runball.bean.MobileUserInfo;
import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: MobileUserInfoModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/22 13:29
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/22 13:29
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MobileUserInfoModel extends BasicResponse<MobileUserInfoModel> implements Serializable {

    public MobileUserInfo getUser_info() {
        return user_info;
    }

    public void setUser_info(MobileUserInfo user_info) {
        this.user_info = user_info;
    }

    private MobileUserInfo user_info;

    @Override
    public String toString(){
        return "user_info="+user_info.toString();
    }

}
