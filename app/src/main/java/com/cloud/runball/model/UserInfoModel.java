package com.cloud.runball.model;

import com.cloud.runball.bean.UserInfo;

import java.io.Serializable;

/**
 * @author ns467
 */
public class UserInfoModel extends BasicResponse implements Serializable {

    public UserInfo getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfo user_info) {
        this.user_info = user_info;
    }

    private UserInfo user_info;

    @Override
    public String toString(){
        return "user_info="+user_info.toString();
    }
}
