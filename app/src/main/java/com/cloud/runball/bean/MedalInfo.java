package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * 奖章
 */
public class MedalInfo implements Serializable {


    /**
     * medal_image_active : https://api-all-sporter.megacombine.com/medal_image/medal/最高转速/speed_lv1.png
     * is_get : false
     * user_medal_name_cn : 最高转速
     * user_medal_name_en : Max Speed
     * description_cn : 最高转速达到 1888 rpm
     * description_en : Max Speed More Than 1888 rpm
     * level_name : level.1
     */

    private String medal_image_active;
    private boolean is_get;
    private String user_medal_name_cn;
    private String user_medal_name_en;
    private String description_cn;
    private String description_en;
    private String level_name;

    public String getMedal_image_active() {
        return medal_image_active;
    }

    public void setMedal_image_active(String medal_image_active) {
        this.medal_image_active = medal_image_active;
    }

    public boolean isIs_get() {
        return is_get;
    }

    public void setIs_get(boolean is_get) {
        this.is_get = is_get;
    }

    public String getUser_medal_name_cn() {
        return user_medal_name_cn;
    }

    public void setUser_medal_name_cn(String user_medal_name_cn) {
        this.user_medal_name_cn = user_medal_name_cn;
    }

    public String getUser_medal_name_en() {
        return user_medal_name_en;
    }

    public void setUser_medal_name_en(String user_medal_name_en) {
        this.user_medal_name_en = user_medal_name_en;
    }

    public String getDescription_cn() {
        return description_cn;
    }

    public void setDescription_cn(String description_cn) {
        this.description_cn = description_cn;
    }

    public String getDescription_en() {
        return description_en;
    }

    public void setDescription_en(String description_en) {
        this.description_en = description_en;
    }

    public String getLevel_name() {
        return level_name;
    }

    public void setLevel_name(String level_name) {
        this.level_name = level_name;
    }
}
