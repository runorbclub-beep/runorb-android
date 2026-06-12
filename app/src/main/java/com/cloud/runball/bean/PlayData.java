package com.cloud.runball.bean;

import java.io.Serializable;

public class PlayData implements Serializable {


    /**
     * user_id : 41654277864689664
     * user_play_id : 44541985331089408
     * status : 1
     * duration : 8
     * speed_max : 1500
     * circle_count : 94
     * endurance_max : 0
     * compare_last : 0
     * start_time : 1620367020
     * stop_time : 1620367028
     * distance : 15.5927
     * start_time_format : 05/07 13:57
     * circle_count_format : 0.094
     * circle_count_unit : 千圈
     * distance_format : 0.016
     * distance_unit : km
     * speed_max_format : 1,500
     * speed_max_unit : rpm
     * duration_format : 00:00:08
     */

    private String user_id;
    private String user_play_id;
    private int status;
    private int duration;
    private int speed_max;
    private int circle_count;
    private int endurance_max;
    private int compare_last;
    private int start_time;
    private int stop_time;
    private double distance;
    private String start_time_format;
    private String circle_count_format;
    private String circle_count_unit;
    private String distance_format;
    private String distance_unit;
    private String speed_max_format;
    private String speed_max_unit;
    private String duration_format;

    @Override
    public String toString(){
        return "distance_format="+distance_format+";start_time_format="+start_time_format+";circle_count_format="+circle_count_format+";duration_format="+duration_format+";speed_max_format="+speed_max_format+";circle_count="+circle_count+"\n";
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_play_id() {
        return user_play_id;
    }

    public void setUser_play_id(String user_play_id) {
        this.user_play_id = user_play_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSpeed_max() {
        return speed_max;
    }

    public void setSpeed_max(int speed_max) {
        this.speed_max = speed_max;
    }

    public int getCircle_count() {
        return circle_count;
    }

    public void setCircle_count(int circle_count) {
        this.circle_count = circle_count;
    }

    public int getEndurance_max() {
        return endurance_max;
    }

    public void setEndurance_max(int endurance_max) {
        this.endurance_max = endurance_max;
    }

    public int getCompare_last() {
        return compare_last;
    }

    public void setCompare_last(int compare_last) {
        this.compare_last = compare_last;
    }

    public int getStart_time() {
        return start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public int getStop_time() {
        return stop_time;
    }

    public void setStop_time(int stop_time) {
        this.stop_time = stop_time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getStart_time_format() {
        return start_time_format;
    }

    public void setStart_time_format(String start_time_format) {
        this.start_time_format = start_time_format;
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

    public String getDuration_format() {
        return duration_format;
    }

    public void setDuration_format(String duration_format) {
        this.duration_format = duration_format;
    }
}
