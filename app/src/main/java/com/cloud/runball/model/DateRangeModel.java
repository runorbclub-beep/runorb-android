package com.cloud.runball.model;

import com.cloud.runball.bean.DateRange;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ns467
 */
public class DateRangeModel implements Serializable {

    public List<DateRange> getDate_range() {
        return date_range;
    }

    public void setDate_range(List<DateRange> date_range) {
        this.date_range = date_range;
    }

    List<DateRange> date_range=new ArrayList<>();

}
