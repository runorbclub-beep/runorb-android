package com.cloud.runball.model;

import com.cloud.runball.bean.MatchRankData;
import com.cloud.runball.bean.MyGrade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: MatchRankModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/22 17:58
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/22 17:58
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRankModel extends BasicResponse<MatchRankModel> implements Serializable {

    public List<MatchRankData> getRankingList() {
        return list;
    }

    public void setRankingList(List<MatchRankData> rankingList) {
        this.list = rankingList;
    }

    private List<MatchRankData> list=new ArrayList<>();

    public MyGrade getMy_grade() {
        return my_grade;
    }

    public void setMy_grade(MyGrade my_grade) {
        this.my_grade = my_grade;
    }

    private MyGrade my_grade;

    public int getIs_exponent() {
        return is_exponent;
    }

    public void setIs_exponent(int is_exponent) {
        this.is_exponent = is_exponent;
    }

    private int is_exponent;

}
