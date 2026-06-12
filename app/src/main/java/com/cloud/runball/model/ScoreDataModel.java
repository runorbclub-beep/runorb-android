package com.cloud.runball.model;

import com.cloud.runball.bean.Achievement;
import com.cloud.runball.bean.ChartData;

import java.io.Serializable;
import java.util.List;

/**
 * 成就数据
 */
public class ScoreDataModel implements Serializable {

    Achievement achievement;
    List<ChartData> chart_data;

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    public List<ChartData> getChart_data() {
        return chart_data;
    }

    public void setChart_data(List<ChartData> chart_data) {
        this.chart_data = chart_data;
    }


   @Override
    public String toString(){
        return "achievement="+achievement.toString()+";chart_data="+chart_data.toString();
   }
}
