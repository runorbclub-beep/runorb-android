package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * 成就
 */
public class Achievement implements Serializable {


    /**
     * circle_count : 21895
     * circle_count_format : 21.895
     * circle_count_unit : 千圈
     * distance : 3631.856968655
     * distance_format : 3.632
     * distance_unit : 千米
     * duration : 884
     * duration_format : 00:14:44
     * endurance_max : 288
     * endurance_max_unit : s
     * speed_max : 11040
     * speed_max_format : 11,040
     * speed_max_unit : rpm
     *
     */

    private int circle_count;
    private String circle_count_format;
    private String circle_count_unit;
    private double distance;
    private String distance_format;
    private String distance_unit;
    private int duration;
    private String duration_format;
    private int endurance_max;
    private String endurance_max_unit;
    private int speed_max;
    private String speed_max_format;
    private String speed_max_unit;
    private int win_num;

    public int getWin_num() {
        return win_num;
    }

    public void setWin_num(int win_num) {
        this.win_num = win_num;
    }

    @Override
    public String toString(){
        return "duration="+duration+";speed_max="+speed_max+";endurance_max="+endurance_max+";win_num="+win_num;
    }

    public int getCircle_count() {
        return circle_count;
    }

    public void setCircle_count(int circle_count) {
        this.circle_count = circle_count;
    }

    public String getCircle_count_format() {
        return circle_count_format;
    }

    public void setCircle_count_format(String circle_count_format) {
        this.circle_count_format = circle_count_format;
    }

    public String getCircle_count_unit() {
        return circle_count_unit;
    }

    public void setCircle_count_unit(String circle_count_unit) {
        this.circle_count_unit = circle_count_unit;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDistance_format() {
        return distance_format;
    }

    public void setDistance_format(String distance_format) {
        this.distance_format = distance_format;
    }

    public String getDistance_unit() {
        return distance_unit;
    }

    public void setDistance_unit(String distance_unit) {
        this.distance_unit = distance_unit;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDuration_format() {
        return duration_format;
    }

    public void setDuration_format(String duration_format) {
        this.duration_format = duration_format;
    }

    public int getEndurance_max() {
        return endurance_max;
    }

    public void setEndurance_max(int endurance_max) {
        this.endurance_max = endurance_max;
    }

    public String getEndurance_max_unit() {
        return endurance_max_unit;
    }

    public void setEndurance_max_unit(String endurance_max_unit) {
        this.endurance_max_unit = endurance_max_unit;
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

    public String getSpeed_max_unit() {
        return speed_max_unit;
    }

    public void setSpeed_max_unit(String speed_max_unit) {
        this.speed_max_unit = speed_max_unit;
    }
}
