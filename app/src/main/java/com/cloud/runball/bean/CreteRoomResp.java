package com.cloud.runball.bean;

import java.io.Serializable;

import retrofit2.http.PUT;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: CreteRoomResp
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/7 10:25
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/7 10:25
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CreteRoomResp implements Serializable {


    /**
     * pk_room_id : 33613942248968192
     * pk_room_title :
     * pk_room_number : 000007
     * pk_type : 0
     * pk_result_type : 1
     * user_id : 28691273070153728
     * created_uid : 28691273070153728
     * status : 1
     * time_long : 5
     */

    private long pk_room_id;
    private String pk_room_title;
    private String pk_room_number;
    private String pk_type;
    private String pk_result_type;
    private long user_id;
    private long created_uid;
    private int status;
    private int time_long;

    private int pk_max_person;

    public int getPk_max_person() {
        return pk_max_person;
    }

    public void setPk_max_person(int pk_max_person) {
        this.pk_max_person = pk_max_person;
    }

    public String getGroup_red_title() {
        return group_red_title;
    }

    public void setGroup_red_title(String group_red_title) {
        this.group_red_title = group_red_title;
    }

    public String getGroup_blue_title() {
        return group_blue_title;
    }

    public void setGroup_blue_title(String group_blue_title) {
        this.group_blue_title = group_blue_title;
    }

    private String group_red_title;
    private String group_blue_title;


    public long getPk_room_id() {
        return pk_room_id;
    }

    public void setPk_room_id(long pk_room_id) {
        this.pk_room_id = pk_room_id;
    }

    public String getPk_room_title() {
        return pk_room_title;
    }

    public void setPk_room_title(String pk_room_title) {
        this.pk_room_title = pk_room_title;
    }

    public String getPk_room_number() {
        return pk_room_number;
    }

    public void setPk_room_number(String pk_room_number) {
        this.pk_room_number = pk_room_number;
    }

    public String getPk_type() {
        return pk_type;
    }

    public void setPk_type(String pk_type) {
        this.pk_type = pk_type;
    }

    public String getPk_result_type() {
        return pk_result_type;
    }

    public void setPk_result_type(String pk_result_type) {
        this.pk_result_type = pk_result_type;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getCreated_uid() {
        return created_uid;
    }

    public void setCreated_uid(long created_uid) {
        this.created_uid = created_uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTime_long() {
        return time_long;
    }

    public void setTime_long(int time_long) {
        this.time_long = time_long;
    }

    @Override
    public String toString(){
        return "pk_room_id="+pk_room_id+";pk_room_number="+pk_room_number+";pk_type="+pk_type+";user_id="+user_id+";created_uid="+created_uid+";time_long="+time_long
                +";pk_max_person="+pk_max_person+";group_red_title="+group_red_title+";group_blue_title="+group_blue_title;
    }

}
