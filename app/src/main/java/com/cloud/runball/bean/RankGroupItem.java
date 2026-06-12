package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: RankGroupItem
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/18 10:32
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/18 10:32
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankGroupItem implements Serializable {


    /**
     * distince : 958.10029168128
     * distance_percentage : 0.96
     * distance_poor : 0.04km
     * user_group_id : 46026472443351040
     * user_group_name : 南昌大学队
     * user_id :
     * user_name :
     * is_group : 1
     * matchs_stage_id : 49000214572306432
     * this_user_group : 1
     * is_end : 0
     */

    private double distince;
    private double distance_percentage;
    private String distance_poor;
    private String user_group_id;
    private String user_group_name;
    private String user_id;
    private String user_name;
    private int is_group;
    private String matchs_stage_id;
    private int this_user_group;
    private int is_end;

    public double getDistince() {
        return distince;
    }

    public void setDistince(double distince) {
        this.distince = distince;
    }

    public double getDistance_percentage() {
        return distance_percentage;
    }

    public void setDistance_percentage(double distance_percentage) {
        this.distance_percentage = distance_percentage;
    }

    public String getDistance_poor() {
        return distance_poor;
    }

    public void setDistance_poor(String distance_poor) {
        this.distance_poor = distance_poor;
    }

    public String getUser_group_id() {
        return user_group_id;
    }

    public void setUser_group_id(String user_group_id) {
        this.user_group_id = user_group_id;
    }

    public String getUser_group_name() {
        return user_group_name;
    }

    public void setUser_group_name(String user_group_name) {
        this.user_group_name = user_group_name;
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

    public int getIs_group() {
        return is_group;
    }

    public void setIs_group(int is_group) {
        this.is_group = is_group;
    }

    public String getMatchs_stage_id() {
        return matchs_stage_id;
    }

    public void setMatchs_stage_id(String matchs_stage_id) {
        this.matchs_stage_id = matchs_stage_id;
    }

    public int getThis_user_group() {
        return this_user_group;
    }

    public void setThis_user_group(int this_user_group) {
        this.this_user_group = this_user_group;
    }

    public int getIs_end() {
        return is_end;
    }

    public void setIs_end(int is_end) {
        this.is_end = is_end;
    }
}
