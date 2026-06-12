package com.cloud.runball.model;

import com.cloud.runball.bean.PlayData;
import java.io.Serializable;
import java.util.List;

public class MineDataModel implements Serializable {
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PlayData> getPlay_data() {
        return play_data;
    }

    public void setPlay_data(List<PlayData> play_data) {
        this.play_data = play_data;
    }

    private List<PlayData> play_data;

    @Override
    public String toString(){
        return ""+play_data.toString();
    }

}
