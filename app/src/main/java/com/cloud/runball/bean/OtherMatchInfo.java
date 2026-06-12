package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: OtherMatchInfo
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/22 14:43
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/22 14:43
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class OtherMatchInfo implements Serializable {



    /**
     * shake_group_user_id : 72071543290597376
     * integral : 600
     * distance : 3088.61
     * datetime : 1626883200
     * index : 0
     * title : 1
     * ranking : 1
     * date : 2021-07-22
     */

    private String sys_shake_id;
    private String shake_group_user_id;
    private int integral;
    private String distance;
    private String datetime;
    private int index;
    private String title;
    private int ranking;
    private String date;


    public String getSys_shake_id() {
        return sys_shake_id;
    }

    public void setSys_shake_id(String sys_shake_id) {
        this.sys_shake_id = sys_shake_id;
    }

    public String getShake_group_user_id() {
        return shake_group_user_id;
    }

    public void setShake_group_user_id(String shake_group_user_id) {
        this.shake_group_user_id = shake_group_user_id;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
