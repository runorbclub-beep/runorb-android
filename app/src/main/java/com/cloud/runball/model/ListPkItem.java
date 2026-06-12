package com.cloud.runball.model;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: ListPkItem
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/29 13:29
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/29 13:29
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ListPkItem implements Serializable {
    /**
     * user_pk_list_id : 40932192271994880
     * duration : 178
     * user_group : red
     * group_win :
     * distance : 0
     * speed_max : 0
     * user_id : 34717147481509888
     * user_name : Wangke
     * pk_type : 0
     * created_time : 1619506574
     * user_img : https://api-all-sporter.megacombine.com/user_image/2021/04/2021-04-24/user_img
     * start_date : 2021.04.27 14:56
     * is_win : 0
     */

    private String user_pk_list_id;
    private int duration;
    private String user_group;
    private String group_win;
    private String distance;
    private int speed_max;
    private String user_id;
    private String user_name;
    private int pk_type;
    private int created_time;
    private String user_img;
    private String start_date;
    private int is_win;

    public String getUser_pk_list_id() {
        return user_pk_list_id;
    }

    public void setUser_pk_list_id(String user_pk_list_id) {
        this.user_pk_list_id = user_pk_list_id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUser_group() {
        return user_group;
    }

    public void setUser_group(String user_group) {
        this.user_group = user_group;
    }

    public String getGroup_win() {
        return group_win;
    }

    public void setGroup_win(String group_win) {
        this.group_win = group_win;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getSpeed_max() {
        return speed_max;
    }

    public void setSpeed_max(int speed_max) {
        this.speed_max = speed_max;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getPk_type() {
        return pk_type;
    }

    public void setPk_type(int pk_type) {
        this.pk_type = pk_type;
    }

    public int getCreated_time() {
        return created_time;
    }

    public void setCreated_time(int created_time) {
        this.created_time = created_time;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
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
