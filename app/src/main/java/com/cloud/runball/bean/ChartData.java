package com.cloud.runball.bean;

import java.io.Serializable;

public class ChartData implements Serializable {

    /**
     * timestamp : 1610035200
     * date_format : 01/08
     * speed_max : 0
     * speed_max_format : 0.000
     */

    private int timestamp;
    private String date_format;
    private int speed_max;
    private String speed_max_format;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate_format() {
        return date_format;
    }

    public void setDate_format(String date_format) {
        this.date_format = date_format;
    }

    public int getSpeed_max() {
        return speed_max;
    }

    public void setSpeed_max(int speed_max) {
        this.speed_max = speed_max;
    }

    public String getSpeed_max_format() {
        return speed_max_format;
    }

    public void setSpeed_max_format(String speed_max_format) {
        this.speed_max_format = speed_max_format;
    }
}
