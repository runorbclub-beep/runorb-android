package com.cloud.runball.model;

import com.cloud.runball.bean.UserPlayData;

import java.io.Serializable;

public class UserPlayDataModel implements Serializable {
    public UserPlayData getUser_play() {
        return user_play;
    }

    public void setUser_play(UserPlayData user_play) {
        this.user_play = user_play;
    }

    private UserPlayData user_play;
}
