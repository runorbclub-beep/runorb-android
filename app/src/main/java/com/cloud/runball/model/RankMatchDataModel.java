package com.cloud.runball.model;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: MineMatchDataModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/12 13:43
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/12 13:43
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankMatchDataModel implements Serializable {


    /**
     * sys_match_id : 48994755022950400
     * match_champion_prize_description :
     * match_title : 大学生长征挑战赛
     * match_user_type_description : 不限
     * match_user_sex_description : 不限
     * match_start_time : 1621428461
     * match_stop_time : 1621741520
     * status : 1
     * match_user_sign_count : 18
     * match_status : 2
     * is_group : 1
     * sys_sys_match_id : 48988132351152128
     * match_image : http://10.20.73.103:89/matchs_image/2021/05/2021-05-20/competition.png
     * start_time : 2021-05-19 20:47
     * stop_time : 2021-05-23 11:45
     * pass_join : 1
     * match_status_title : 进行中
     * user_join_status : {"is_join":1,"user_group_id":"46395305377140736","group_title":"麦凯莱张华东队","group_num":"111111"}
     * matchs_stage_id : 49000214572306432
     * view_type : 1
     */

    private String sys_match_id;
    private String match_champion_prize_description;
    private String match_title;
    private String match_user_type_description;
    private String match_user_sex_description;
    private String match_start_time;
    private String match_stop_time;
    private int status;
    private int match_user_sign_count;
    private int match_status;
    private int is_group;
    private String sys_sys_match_id;
    private String match_image;
    private String start_time;
    private String stop_time;
    private int pass_join;
    private String match_status_title;
    private UserJoinStatusDTO user_join_status;
    private String matchs_stage_id;
    // 0：不存在，1：标准锦标赛，2：摇跑赛
    private int view_type;



    private int is_exponent;
    private int join_status;

    public int getJoin_status() {
        return join_status;
    }

    public void setJoin_status(int join_status) {
        this.join_status = join_status;
    }

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

    public String getMatch_user_type_description() {
        return match_user_type_description;
    }

    public void setMatch_user_type_description(String match_user_type_description) {
        this.match_user_type_description = match_user_type_description;
    }

    public int getIs_exponent() {
        return is_exponent;
    }

    public void setIs_exponent(int is_exponent) {
        this.is_exponent = is_exponent;
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

    public int getIs_group() {
        return is_group;
    }

    public void setIs_group(int is_group) {
        this.is_group = is_group;
    }

    public String getSys_sys_match_id() {
        return sys_sys_match_id;
    }

    public void setSys_sys_match_id(String sys_sys_match_id) {
        this.sys_sys_match_id = sys_sys_match_id;
    }

    public String getMatch_image() {
        return match_image;
    }

    public void setMatch_image(String match_image) {
        this.match_image = match_image;
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

    public UserJoinStatusDTO getUser_join_status() {
        return user_join_status;
    }

    public void setUser_join_status(UserJoinStatusDTO user_join_status) {
        this.user_join_status = user_join_status;
    }

    public String getMatchs_stage_id() {
        return matchs_stage_id;
    }

    public void setMatchs_stage_id(String matchs_stage_id) {
        this.matchs_stage_id = matchs_stage_id;
    }

    public int getView_type() {
        return view_type;
    }

    public void setView_type(int view_type) {
        this.view_type = view_type;
    }

    public static class UserJoinStatusDTO {
        /**
         * is_join : 1
         * user_group_id : 46395305377140736
         * group_title : 麦凯莱张华东队
         * group_num : 111111
         */

        private int is_join;
        private String user_group_id;
        private String group_title;
        private String group_num;

        public int getIs_join() {
            return is_join;
        }

        public void setIs_join(int is_join) {
            this.is_join = is_join;
        }

        public String getUser_group_id() {
            return user_group_id;
        }

        public void setUser_group_id(String user_group_id) {
            this.user_group_id = user_group_id;
        }

        public String getGroup_title() {
            return group_title;
        }

        public void setGroup_title(String group_title) {
            this.group_title = group_title;
        }

        public String getGroup_num() {
            return group_num;
        }

        public void setGroup_num(String group_num) {
            this.group_num = group_num;
        }
    }
}
