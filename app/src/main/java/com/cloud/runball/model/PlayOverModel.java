package com.cloud.runball.model;

import androidx.annotation.NonNull;

import com.cloud.runball.bean.MedalInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 运动结束
 */
public class PlayOverModel implements Serializable {
    public List<MedalInfo> getNew_medal() {
        return new_medal;
    }

    public void setNew_medal(List<MedalInfo> new_medal) {
        this.new_medal = new_medal;
    }

    List<MedalInfo> new_medal=new ArrayList<>();


    @NonNull
    @Override
    public String toString() {
        return "new_medal.size="+new_medal.size();
    }
}
