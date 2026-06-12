package com.cloud.runball.model;

import com.cloud.runball.bean.UserPlay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserPlayModel implements Serializable {

    public UserPlay getUser_play() {
        return user_play;
    }

    public void setUser_play(UserPlay user_play) {
        this.user_play = user_play;
    }

    UserPlay user_play;


    public List<ErrSpeed> getErr_speed() {
        return err_speed;
    }

    public void setErr_speed(List<ErrSpeed> err_speed) {
        this.err_speed = err_speed;
    }

    public List<ErrSpeed> err_speed=new ArrayList<>();



}
