package com.cloud.runball.model;

import com.cloud.runball.bean.MedalInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ns467
 */
public class MedalInfoModel implements Serializable {

    public List<MedalInfo> getUser_medal() {
        return user_medal;
    }

    public void setUser_medal(List<MedalInfo> user_medal) {
        this.user_medal = user_medal;
    }

    List<MedalInfo> user_medal=new ArrayList<>();

    @Override
    public String toString(){
        return user_medal!=null?"MedalInfo.size="+user_medal.size():"MedalInfo.size="+0;
    }

}
