package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: MineMatchData
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/12 13:41
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/12 13:41
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankMatchData implements Serializable {


    /**
     * sys_match_id : 46093147616317440
     * match_champion_prize_description : 现金奖励1万元
     * match_title : 大学生两万五千里挑战赛
     * match_image : https://all-sporter-manager.test/matchs_image/2021/03/2021-03-23/banner.png
     * match_user_type_description : 不限
     * match_user_sex_description : 不限
     * match_start_time : 1621036800
     * match_stop_time : 1622376000
     * status : 1
     * match_user_sign_count : 0
     * match_status : 1
     * start_time : 2021-05-15 08:00
     * stop_time : 2021-05-30 20:00
     * pass_join : 1
     * match_status_title : 未开始
     * user_join_status : 0
     */

    private String sys_match_id;
    private String match_champion_prize_description;
    private String match_title;
    private String match_image;
    private String match_user_type_description;
    private String match_user_sex_description;
    private String match_start_time;
    private String match_stop_time;
    private int status;
    private int match_user_sign_count;
    private int match_status;
    private String start_time;
    private String stop_time;
    private int pass_join;
    private String match_status_title;
    private int user_join_status;

    public String getSys_match_id() {
        return sys_match_id;
    }

    public void setSys_match_id(String sys_match_id) {
        this.sys_match_id = sys_match_id;
    }

    public String getMatch_champion_prize_description() {
        return match_champion_prize_description;
    }

    public void setMatch_champion_prize_description(String match_champion_prize_description) {
        this.match_champion_prize_description = match_champion_prize_description;
    }

    public String getMatch_title() {
        return match_title;
    }

    public void setMatch_title(String match_title) {
        this.match_title = match_title;
    }

    public String getMatch_image() {
        return match_image;
    }

    public void setMatch_image(String match_image) {
        this.match_image = match_image;
    }

    public String getMatch_user_type_description() {
        return match_user_type_description;
    }

    public void setMatch_user_type_description(String match_user_type_description) {
        this.match_user_type_description = match_user_type_description;
    }

    public String getMatch_user_sex_description() {
        return match_user_sex_description;
    }

    public void setMatch_user_sex_description(String match_user_sex_description) {
        this.match_user_sex_description = match_user_sex_description;
    }

    public String getMatch_start_time() {
        return match_start_time;
    }

    public void setMatch_start_time(String match_start_time) {
        this.match_start_time = match_start_time;
    }

    public String getMatch_stop_time() {
        return match_stop_time;
    }

    public void setMatch_stop_time(String match_stop_time) {
        this.match_stop_time = match_stop_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMatch_user_sign_count() {
        return match_user_sign_count;
    }

    public void setMatch_user_sign_count(int match_user_sign_count) {
        this.match_user_sign_count = match_user_sign_count;
    }

    public int getMatch_status() {
        return match_status;
    }

    public void setMatch_status(int match_status) {
        this.match_status = match_status;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getStop_time() {
        return stop_time;
    }

    public void setStop_time(String stop_time) {
        this.stop_time = stop_time;
    }

    public int getPass_join() {
        return pass_join;
    }

    public void setPass_join(int pass_join) {
        this.pass_join = pass_join;
    }

    public String getMatch_status_title() {
        return match_status_title;
    }

    public void setMatch_status_title(String match_status_title) {
        this.match_status_title = match_status_title;
    }

    public int getUser_join_status() {
        return user_join_status;
    }

    public void setUser_join_status(int user_join_status) {
        this.user_join_status = user_join_status;
    }
}
