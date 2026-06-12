package com.cloud.runball.bean;

import java.util.List;

public class UserPlay {


    /**
     * user_play_id : 4986295411544064
     * status : 1
     * created_uid : 2524170583805952
     * start_time : 1610936209
     * circle_detail : []
     */

    private long user_play_id;
    private int status;
    private transient long created_uid;
    private int start_time;
    private transient List<?> circle_detail;

    public UserPlay(long user_play_id,int start_time){
        this.user_play_id=user_play_id;
        this.start_time=start_time;
    }

    public long getUser_play_id() {
        return user_play_id;
    }

    public void setUser_play_id(long user_play_id) {
        this.user_play_id = user_play_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreated_uid() {
        return created_uid;
    }

    public void setCreated_uid(long created_uid) {
        this.created_uid = created_uid;
    }

    public int getStart_time() {
        return start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public List<?> getCircle_detail() {
        return circle_detail;
    }

    public void setCircle_detail(List<?> circle_detail) {
        this.circle_detail = circle_detail;
    }
}
