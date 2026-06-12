package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: PKDataResp
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/15 20:32
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/15 20:32
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class PKDataResp implements Serializable {


    /**
     * pk_room_id : 44215087753334784
     * user_play_id : 44215153335472128
     * speed_max : 0
     * distance : 0
     * group_win : blue
     * user_pk_list_id : 44215089183592448
     * user_group : red
     * pk_type : 0
     * start_date : 2021.05.06 16:21
     * is_win : 0
     */

    private String pk_room_id;
    private String user_play_id;
    private String speed_max;
    private String distance;
    private String group_win;
    private String user_pk_list_id;
    private String user_group;
    private int pk_type;
    private String start_date;
    private int is_win;

    public String getPk_room_id() {
        return pk_room_id;
    }

    public void setPk_room_id(String pk_room_id) {
        this.pk_room_id = pk_room_id;
    }

    public String getUser_play_id() {
        return user_play_id;
    }

    public void setUser_play_id(String user_play_id) {
        this.user_play_id = user_play_id;
    }

    public String getSpeed_max() {
        return speed_max;
    }

    public void setSpeed_max(String speed_max) {
        this.speed_max = speed_max;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getGroup_win() {
        return group_win;
    }

    public void setGroup_win(String group_win) {
        this.group_win = group_win;
    }

    public String getUser_pk_list_id() {
        return user_pk_list_id;
    }

    public void setUser_pk_list_id(String user_pk_list_id) {
        this.user_pk_list_id = user_pk_list_id;
    }

    public String getUser_group() {
        return user_group;
    }

    public void setUser_group(String user_group) {
        this.user_group = user_group;
    }

    public int getPk_type() {
        return pk_type;
    }

    public void setPk_type(int pk_type) {
        this.pk_type = pk_type;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public int getIs_win() {
        return is_win;
    }

    public void setIs_win(int is_win) {
        this.is_win = is_win;
    }
}
