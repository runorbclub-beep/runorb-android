package com.cloud.runball.model;

import java.io.Serializable;

/**
 * 服务器定义的设备
 */
public class DeviceWithServerModel implements Serializable {
    private long user_device_id;
    private String device_uid;

    public long getUser_device_id() {
        return user_device_id;
    }

    public void setUser_device_id(int user_device_id) {
        this.user_device_id = user_device_id;
    }

    public String getDevice_uid() {
        return device_uid;
    }

    public void setDevice_uid(String device_uid) {
        this.device_uid = device_uid;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public boolean isIs_select() {
        return is_select;
    }

    public void setIs_select(boolean is_select) {
        this.is_select = is_select;
    }

    private String device_name;
    private boolean is_select;
    private boolean isChecked;
    public void setChecked(boolean isChecked){
        this.isChecked=isChecked;
    }

    public boolean getChecked(){
        return this.isChecked;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
