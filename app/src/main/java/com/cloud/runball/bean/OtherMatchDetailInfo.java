package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: OtherMatchDetailInfo
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/22 17:39
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/22 17:39
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class OtherMatchDetailInfo implements Serializable {

    /**
     * shake_group_id : 72070910156214272
     * title : 1
     * index : 0
     * num : 1
     * distance : 3088.61
     * integral : 1000
     */

    private String shake_group_id;
    private String title;
    private int index;
    private int num;
    private String distance;
    private int integral;
    private int ranking;

    public String getShake_group_id() {
        return shake_group_id;
    }

    public void setShake_group_id(String shake_group_id) {
        this.shake_group_id = shake_group_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
}
