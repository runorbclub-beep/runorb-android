package com.cloud.runball.model;

import com.cloud.runball.bean.RankGroupItem;
import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: RankMatchInfo
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/13 16:12
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/13 16:12
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankMatchInfo extends BasicResponse<RankMatchInfo> implements Serializable {

    /**
     * count : 2
     * index : 0
     * ranking : 0 / 2
     * distince : 1042.1634704745602
     * distince_format : 1.04
     * distance_poor : 0km
     * distance_percentage : 1
     * all_distince_value : 1000
     * all_distince_value_format : 1
     * matchs_stage_id : 49000214572306432
     * is_end : 1
     * matchs_end_tips : 完成挑战
     * final_result_time : 比赛将于2021-05-23 11:45公布最终结果
     * user_group_name : 麦凯莱张华东队
     * match_user_join_num : 2
     * my_duration : 36
     * my_distance : 0.41
     * residue_time : 154154
     */

    private int count;
    private int index;
    private int code;
    private String ranking;
    private String distince;
    private String distince_format;
    private String distance_poor;
    private double distance_percentage;
    private int all_distince_value;
    private String all_distince_value_format;
    private String matchs_stage_id;
    private int is_end;
    private String matchs_end_tips;
    private String final_result_time;
    private String user_group_name;
    private int match_user_join_num;
    private int my_duration;
    private double my_distance;
    private int residue_time;


    @Override
    public int getCode() {
        return code;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }

    public List<RankGroupItem> getAll_group() {
        return all_group;
    }

    public void setAll_group(List<RankGroupItem> all_group) {
        this.all_group = all_group;
    }

    private List<RankGroupItem> all_group;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getDistince() {
        return distince;
    }

    public void setDistince(String distince) {
        this.distince = distince;
    }

    public String getDistince_format() {
        return distince_format;
    }

    public void setDistince_format(String distince_format) {
        this.distince_format = distince_format;
    }

    public String getDistance_poor() {
        return distance_poor;
    }

    public void setDistance_poor(String distance_poor) {
        this.distance_poor = distance_poor;
    }

    public double getDistance_percentage() {
        return distance_percentage;
    }

    public void setDistance_percentage(double distance_percentage) {
        this.distance_percentage = distance_percentage;
    }

    public int getAll_distince_value() {
        return all_distince_value;
    }

    public void setAll_distince_value(int all_distince_value) {
        this.all_distince_value = all_distince_value;
    }

    public String getAll_distince_value_format() {
        return all_distince_value_format;
    }

    public void setAll_distince_value_format(String all_distince_value_format) {
        this.all_distince_value_format = all_distince_value_format;
    }

    public String getMatchs_stage_id() {
        return matchs_stage_id;
    }

    public void setMatchs_stage_id(String matchs_stage_id) {
        this.matchs_stage_id = matchs_stage_id;
    }

    public int getIs_end() {
        return is_end;
    }

    public void setIs_end(int is_end) {
        this.is_end = is_end;
    }

    public String getMatchs_end_tips() {
        return matchs_end_tips;
    }

    public void setMatchs_end_tips(String matchs_end_tips) {
        this.matchs_end_tips = matchs_end_tips;
    }

    public String getFinal_result_time() {
        return final_result_time;
    }

    public void setFinal_result_time(String final_result_time) {
        this.final_result_time = final_result_time;
    }

    public String getUser_group_name() {
        return user_group_name;
    }

    public void setUser_group_name(String user_group_name) {
        this.user_group_name = user_group_name;
    }

    public int getMatch_user_join_num() {
        return match_user_join_num;
    }

    public void setMatch_user_join_num(int match_user_join_num) {
        this.match_user_join_num = match_user_join_num;
    }

    public int getMy_duration() {
        return my_duration;
    }

    public void setMy_duration(int my_duration) {
        this.my_duration = my_duration;
    }

    public double getMy_distance() {
        return my_distance;
    }

    public void setMy_distance(double my_distance) {
        this.my_distance = my_distance;
    }

    public int getResidue_time() {
        return residue_time;
    }

    public void setResidue_time(int residue_time) {
        this.residue_time = residue_time;
    }

    @Override
    public String toString(){
        return  "is_end="+is_end+";matchs_stage_id="+matchs_stage_id;
    }

}
