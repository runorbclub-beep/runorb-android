package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: matchStageData
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/22 17:54
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/22 17:54
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchStageData implements Serializable {

    /**
     * matchs_stage_id : 49000214572306432
     * match_stage_title : 第一赛段
     * match_stage_start_time : 1621395920
     * match_stage_stop_time : 1621735524
     * start_time : 2021-05-19 11:45
     * stop_time : 2021-05-23 10:05
     * this_stage : 1
     */

    private String matchs_stage_id;
    private String match_stage_title;
    private String match_stage_start_time;
    private String match_stage_stop_time;
    private String start_time;
    private String stop_time;
    private int this_stage;

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

    public int getThis_stage() {
        return this_stage;
    }

    public void setThis_stage(int this_stage) {
        this.this_stage = this_stage;
    }
}
