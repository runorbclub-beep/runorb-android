package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: MatchRankItem
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/2 17:54
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/2 17:54
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRankItem implements Serializable {


    private String value;
    private String unit;
    private String time;
    private String user_id;
    private String user_name;
    private String user_img;
    private int index;
    private String sys_sex_id;
    private String address;


    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getSys_sex_id() {
        return sys_sex_id;
    }

    public void setSys_sex_id(String sys_sex_id) {
        this.sys_sex_id = sys_sex_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString(){
        return "user_img="+user_img;
    }

}
