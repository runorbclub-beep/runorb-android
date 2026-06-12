package com.cloud.runball.model;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: AchievementDTO
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/7 18:02
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/7 18:02
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class AchievementDTO implements Serializable {
    /**
     * duration : 665.5
     * speed_max : 8340
     * circle_count : 6993
     * endurance_max : 0
     * play_count : 10
     */

    private double duration;
    private int speed_max;
    private int circle_count;
    private int endurance_max;
    private int play_count;
    private int thrmin;
    private float half_marathon;
    private int distance_max;
    private float runball_exponent;
    private int marathon;
    private float exponent_molecular;

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
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

    public int getPlay_count() {
        return play_count;
    }

    public void setPlay_count(int play_count) {
        this.play_count = play_count;
    }

    public float getRunball_exponent() {
        return runball_exponent;
    }

    public void setRunball_exponent(float runball_exponent) {
        this.runball_exponent = runball_exponent;
    }

    public int getThrmin() {
        return thrmin;
    }

    public void setThrmin(int thrmin) {
        this.thrmin = thrmin;
    }

    public float getHalf_marathon() {
        return half_marathon;
    }

    public void setHalf_marathon(float half_marathon) {
        this.half_marathon = half_marathon;
    }

    public int getDistance_max() {
        return distance_max;
    }

    public void setDistance_max(int distance_max) {
        this.distance_max = distance_max;
    }

    public int getMarathon() {
        return marathon;
    }

    public void setMarathon(int marathon) {
        this.marathon = marathon;
    }

    public float getExponent_molecular() {
        return exponent_molecular;
    }

    public void setExponent_molecular(float exponent_molecular) {
        this.exponent_molecular = exponent_molecular;
    }
}
