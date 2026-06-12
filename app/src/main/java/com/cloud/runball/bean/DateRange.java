package com.cloud.runball.bean;

import java.io.Serializable;

public class DateRange implements Serializable {


    /**
     * start_date : 2021-01-15
     * stop_date : 2021-01-15
     * title : 今天
     */

    private String start_date;
    private String stop_date;
    private String title;

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getStop_date() {
        return stop_date;
    }

    public void setStop_date(String stop_date) {
        this.stop_date = stop_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
