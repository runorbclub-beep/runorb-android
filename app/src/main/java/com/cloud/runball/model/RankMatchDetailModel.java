package com.cloud.runball.model;

import com.cloud.runball.bean.UserJoinStatusDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: RankMatchDetail
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/12 15:46
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/12 15:46
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankMatchDetailModel extends BasicResponse<RankMatchDetailModel> implements Serializable  {


    /**
     * match_title : 大学生两万五千里挑战赛
     * match_status : 2
     * match_status_title : 进行中
     * user_join_status : {"is_join":0,"user_group_id":"","group_title":"","group_num":""}
     * match_join_pass : 1
     * is_group : 1
     * match_image : http://10.20.73.103:89/matchs_image/2021/03/2021-03-23/banner.png
     * form_array : [{"label":"比赛时间","value":"1970.01.01 08:00 - 1970.01.01 08:00","icon":"http://10.20.73.103:89/matchs_image/matchs_sources/time.png"},{"label":"奖金/奖品","value":"现金奖励1万元","icon":"http://10.20.73.103:89/matchs_image/matchs_sources/bonus.png"},{"label":"已报名","value":0,"icon":"http://10.20.73.103:89/matchs_image/matchs_sources/join.png"},{"label":"报名要求","value":"团队参赛","icon":"http://10.20.73.103:89/matchs_image/matchs_sources/require.png"},{"label":"赛事说明","value":"<p><strong>锦标赛，锦标赛锦标赛，锦标赛锦标赛，锦标赛锦标赛，锦标赛<\/strong><\/p><p><strong><u>士大夫打发顺丰<\/u><\/strong><\/p><p><strong><u>发生的发发的所发生的发<\/u><\/strong><\/p><h1>发的发生的发生的发的是<\/h1><p>发送到发达发<\/p><p>sfafdas<\/p><p><br><\/p><p>发大水发的说法<\/p>","icon":"http://10.20.73.103:89/matchs_image/matchs_sources/info.png"},{"label":"联系方式","value":"13545220341","icon":"http://10.20.73.103:89/matchs_image/matchs_sources/phone.png"},{"label":"联系邮箱","value":"pengjl@megacombine.com","icon":"http://10.20.73.103:89/matchs_image/matchs_sources/email.png"}]
     */


    private String sys_sys_match_id;
    private String sys_match_id;

    private String match_title;
    private int match_status;
    private String match_status_title;
    private UserJoinStatusDTO user_join_status;
    private int match_join_pass;
    private int is_group;
    private String match_image;

    private String matchs_stage_id;
    private int view_type;
    private int join_status;

    public int getIs_exponent() {
        return is_exponent;
    }

    public void setIs_exponent(int is_exponent) {
        this.is_exponent = is_exponent;
    }

    private int is_exponent;
    public int getJoin_status() {
        return join_status;
    }

    public void setJoin_status(int join_status) {
        this.join_status = join_status;
    }

    private List<RankMatchFormItem> form_array;
    private List<RankMatchStateItem> stage;

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

    public String getSys_sys_match_id() {
        return sys_sys_match_id;
    }

    public void setSys_sys_match_id(String sys_sys_match_id) {
        this.sys_sys_match_id = sys_sys_match_id;
    }

    public String getSys_match_id() {
        return sys_match_id;
    }

    public void setSys_match_id(String sys_match_id) {
        this.sys_match_id = sys_match_id;
    }

    public String getMatch_title() {
        return match_title;
    }

    public void setMatch_title(String match_title) {
        this.match_title = match_title;
    }

    public int getMatch_status() {
        return match_status;
    }

    public void setMatch_status(int match_status) {
        this.match_status = match_status;
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

    public int getMatch_join_pass() {
        return match_join_pass;
    }

    public void setMatch_join_pass(int match_join_pass) {
        this.match_join_pass = match_join_pass;
    }

    public int getIs_group() {
        return is_group;
    }

    public void setIs_group(int is_group) {
        this.is_group = is_group;
    }

    public String getMatch_image() {
        return match_image;
    }

    public void setMatch_image(String match_image) {
        this.match_image = match_image;
    }

    public List<RankMatchFormItem> getForm_array() {
        return form_array;
    }

    public void setForm_array(List<RankMatchFormItem> form_array) {
        this.form_array = form_array;
    }

    public List<RankMatchStateItem> getStage() {
        return stage;
    }

    public void setStage(List<RankMatchStateItem> stage) {
        this.stage = stage;
    }



    public static class RankMatchFormItem implements Serializable{
        /**
         * label : 比赛时间
         * value : 1970.01.01 08:00 - 1970.01.01 08:00
         * icon : http://10.20.73.103:89/matchs_image/matchs_sources/time.png
         */

        private String label;
        private String value;
        private String icon;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    public static class RankMatchStateItem implements Serializable{

        private String matchs_stage_id;
        private String match_stage_title;
        private String match_stage_start_time;
        private String match_stage_stop_time;
        private int max_integral;
        private int sub_integral;
        private int get_integral_type;
        private int get_integral_value;
        private int match_promotion_type;
        private int match_promotion_value;
        private String sys_sys_match_id;
        private int matchs_stage_status;
        private int match_stage_distance;
        private int view_type;
        private int this_stage;
        private String start_time;
        private String stop_time;
        private String match_stage_promotion_rule;
        private int is_exponent;


        public int getIs_exponent() {
            return is_exponent;
        }

        public void setIs_exponent(int is_exponent) {
            this.is_exponent = is_exponent;
        }



        public String getMatch_stage_promotion_rule() {
            return match_stage_promotion_rule;
        }

        public void setMatch_stage_promotion_rule(String match_stage_promotion_rule) {
            this.match_stage_promotion_rule = match_stage_promotion_rule;
        }

        public int getMatchs_stage_status() {
            return matchs_stage_status;
        }

        public void setMatchs_stage_status(int matchs_stage_status) {
            this.matchs_stage_status = matchs_stage_status;
        }

        public String getMatchs_stage_id() {
            return matchs_stage_id;
        }

        public void setMatchs_stage_id(String matchs_stage_id) {
            this.matchs_stage_id = matchs_stage_id;
        }

        public String getMatch_stage_title() {
            return match_stage_title;
        }

        public void setMatch_stage_title(String match_stage_title) {
            this.match_stage_title = match_stage_title;
        }

        public String getMatch_stage_start_time() {
            return match_stage_start_time;
        }

        public void setMatch_stage_start_time(String match_stage_start_time) {
            this.match_stage_start_time = match_stage_start_time;
        }

        public String getMatch_stage_stop_time() {
            return match_stage_stop_time;
        }

        public void setMatch_stage_stop_time(String match_stage_stop_time) {
            this.match_stage_stop_time = match_stage_stop_time;
        }

        public int getMax_integral() {
            return max_integral;
        }

        public void setMax_integral(int max_integral) {
            this.max_integral = max_integral;
        }

        public int getSub_integral() {
            return sub_integral;
        }

        public void setSub_integral(int sub_integral) {
            this.sub_integral = sub_integral;
        }

        public int getGet_integral_type() {
            return get_integral_type;
        }

        public void setGet_integral_type(int get_integral_type) {
            this.get_integral_type = get_integral_type;
        }

        public int getGet_integral_value() {
            return get_integral_value;
        }

        public void setGet_integral_value(int get_integral_value) {
            this.get_integral_value = get_integral_value;
        }

        public int getMatch_promotion_type() {
            return match_promotion_type;
        }

        public void setMatch_promotion_type(int match_promotion_type) {
            this.match_promotion_type = match_promotion_type;
        }

        public int getMatch_promotion_value() {
            return match_promotion_value;
        }

        public void setMatch_promotion_value(int match_promotion_value) {
            this.match_promotion_value = match_promotion_value;
        }

        public String getSys_sys_match_id() {
            return sys_sys_match_id;
        }

        public void setSys_sys_match_id(String sys_sys_match_id) {
            this.sys_sys_match_id = sys_sys_match_id;
        }

        public int getMatch_stage_distance() {
            return match_stage_distance;
        }

        public void setMatch_stage_distance(int match_stage_distance) {
            this.match_stage_distance = match_stage_distance;
        }

        public int getView_type() {
            return view_type;
        }

        public void setView_type(int view_type) {
            this.view_type = view_type;
        }

        public int getThis_stage() {
            return this_stage;
        }

        public void setThis_stage(int this_stage) {
            this.this_stage = this_stage;
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
    }

}
