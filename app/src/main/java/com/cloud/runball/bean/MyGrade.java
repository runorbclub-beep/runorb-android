package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: MyGrade
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/28 15:24
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/28 15:24
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MyGrade implements Serializable {

    /**
     * match_grade : 3130.7
     * match_ranking : 3
     */

    private String match_grade;
    private int match_ranking;

    private String created_time;
    private String user_id;
    private String user_name;
    private String user_img;
    private String address;
    private String sys_sex_id;

    public String getMatch_grade() {
        return match_grade;
    }

    public void setMatch_grade(String match_grade) {
        this.match_grade = match_grade;
    }

    public int getMatch_ranking() {
        return match_ranking;
    }

    public void setMatch_ranking(int match_ranking) {
        this.match_ranking = match_ranking;
    }


    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSys_sex_id() {
        return sys_sex_id;
    }

    public void setSys_sex_id(String sys_sex_id) {
        this.sys_sex_id = sys_sex_id;
    }
}
