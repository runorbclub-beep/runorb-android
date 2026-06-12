package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: MatchRankData
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/22 17:58
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/22 17:58
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRankData implements Serializable {


    /**
     * match_grade : 10.5
     * match_ranking : 1
     * name : 麦凯莱王珂队
     * image : http://10.20.73.103:89/matchs_image/2021/03/2021-03-23/banner.png
     */


    private double match_grade;
    private int match_ranking;
    private String name;
    private String image;
    private String matchs_user_grade_id;


    public double getMatch_grade() {
        return match_grade;
    }

    public void setMatch_grade(double match_grade) {
        this.match_grade = match_grade;
    }

    public int getMatch_ranking() {
        return match_ranking;
    }

    public void setMatch_ranking(int match_ranking) {
        this.match_ranking = match_ranking;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMatchs_user_grade_id() {
        return matchs_user_grade_id;
    }

    public void setMatchs_user_grade_id(String matchs_user_grade_id) {
        this.matchs_user_grade_id = matchs_user_grade_id;
    }
}
