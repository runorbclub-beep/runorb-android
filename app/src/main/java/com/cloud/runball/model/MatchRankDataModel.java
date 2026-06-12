package com.cloud.runball.model;

import com.cloud.runball.bean.MatchRankItem;
import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: MatchRankDataModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/2 17:55
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/2 17:55
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRankDataModel extends  BasicResponse<MatchRankDataModel> implements Serializable {

    private String ranking_img;
    private String ranking_img_en;

    private int count;

    private List<MatchRankItem> list;

    private int my_ranking;

    private MatchRankItem my_ranking_info;

    public String getRanking_img() {
        return ranking_img;
    }

    public void setRanking_img(String ranking_img) {
        this.ranking_img = ranking_img;
    }

    public String getRanking_img_en() {
        return ranking_img_en;
    }

    public void setRanking_img_en(String ranking_img_en) {
        this.ranking_img_en = ranking_img_en;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<MatchRankItem> getList() {
        return list;
    }

    public void setList(List<MatchRankItem> list) {
        this.list = list;
    }

    public int getMy_ranking() {
        return my_ranking;
    }

    public void setMy_ranking(int my_ranking) {
        this.my_ranking = my_ranking;
    }

    public MatchRankItem getMy_ranking_info() {
        return my_ranking_info;
    }

    public void setMy_ranking_info(MatchRankItem my_ranking_info) {
        this.my_ranking_info = my_ranking_info;
    }
}
