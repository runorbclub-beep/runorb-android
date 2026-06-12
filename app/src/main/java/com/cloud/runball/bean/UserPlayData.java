package com.cloud.runball.bean;

import java.io.Serializable;
import java.util.List;

public class UserPlayData implements Serializable {

    /**
     * user_play_id : 28929846335770624
     * duration : 27
     * speed_max : 6000
     * circle_count : 4789
     * endurance_max : 0
     * compare_last : 0
     * start_time : 1616644796
     * stop_time : 1616644823
     * status : 1
     * distance : 794.399
     * start_time_format : 03/25 11:59:56
     * stop_date_format : 03/25 12:00:23
     * circle_count_format : 4.789
     * circle_count_unit : 千圈
     * distance_format : 0.794
     * distance_unit : km
     * duration_format : 00:00:27
     */

    private String user_play_id;
    private int duration;
    private int speed_max;
    private int circle_count;
    private int endurance_max;
    private int compare_last;
    private int start_time;
    private int stop_time;
    private int status;
    private double distance;
    private int exponent_molecular;
    private int exponent_denominator;
    private float exponent;
    private int source;
    private int is_abnormal;
    private String marathon;
    private String start_time_format;
    private String stop_date_format;
    private double circle_count_format;
    private String circle_count_unit;
    private String distance_format;
    private String distance_unit;
    private String duration_format;
    private List<UserPlayDetailDTO> user_play_detail;
    private List<SectionDurationDTO> section_duration;

    public List<UserPlayDetailDTO> getUser_play_detail() {
        return user_play_detail;
    }

    public void setUser_play_detail(List<UserPlayDetailDTO> user_play_detail) {
        this.user_play_detail = user_play_detail;
    }

    public List<SectionDurationDTO> getSection_duration() {
        return section_duration;
    }

    public void setSection_duration(List<SectionDurationDTO> section_duration) {
        this.section_duration = section_duration;
    }

    public String getUser_play_id() {
        return user_play_id;
    }

    public void setUser_play_id(String user_play_id) {
        this.user_play_id = user_play_id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getExponent_molecular() {
        return exponent_molecular;
    }

    public void setExponent_molecular(int exponent_molecular) {
        this.exponent_molecular = exponent_molecular;
    }

    public int getExponent_denominator() {
        return exponent_denominator;
    }

    public void setExponent_denominator(int exponent_denominator) {
        this.exponent_denominator = exponent_denominator;
    }

    public float getExponent() {
        return exponent;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getIs_abnormal() {
        return is_abnormal;
    }

    public void setIs_abnormal(int is_abnormal) {
        this.is_abnormal = is_abnormal;
    }

    public String getMarathon() {
        return marathon;
    }

    public void setMarathon(String marathon) {
        this.marathon = marathon;
    }

    public String getStart_time_format() {
        return start_time_format;
    }

    public void setStart_time_format(String start_time_format) {
        this.start_time_format = start_time_format;
    }

    public String getStop_date_format() {
        return stop_date_format;
    }

    public void setStop_date_format(String stop_date_format) {
        this.stop_date_format = stop_date_format;
    }

    public double getCircle_count_format() {
        return circle_count_format;
    }

    public void setCircle_count_format(double circle_count_format) {
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

    public String getDuration_format() {
        return duration_format;
    }

    public void setDuration_format(String duration_format) {
        this.duration_format = duration_format;
    }

    public static class UserPlayDetailDTO {
        /**
         * moment : 1610728526000
         * speed : 5520
         */

        private long moment;
        private int speed;

        public long getMoment() {
            return moment;
        }

        public void setMoment(long moment) {
            this.moment = moment;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }
    }

    public static class SectionDurationDTO implements Serializable{
        /**
         * start_section : 0
         * stop_section : 2000
         * section_duration : 3
         * percentage : 16
         */

        private int start_section;
        private int stop_section;
        private float section_duration;
        private int percentage;

        public int getStart_section() {
            return start_section;
        }

        public void setStart_section(int start_section) {
            this.start_section = start_section;
        }

        public int getStop_section() {
            return stop_section;
        }

        public void setStop_section(int stop_section) {
            this.stop_section = stop_section;
        }

        public float getSection_duration() {
            return section_duration;
        }

        public void setSection_duration(float section_duration) {
            this.section_duration = section_duration;
        }

        public int getPercentage() {
            return percentage;
        }

        public void setPercentage(int percentage) {
            this.percentage = percentage;
        }
    }
}
