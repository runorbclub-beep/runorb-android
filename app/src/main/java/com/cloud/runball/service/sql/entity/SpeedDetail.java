package com.cloud.runball.service.sql.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "tb_speed_detail")
public class SpeedDetail {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tb_id")
    private long tbId;

    @SerializedName("userPlayId")
    @ColumnInfo(name = "tb_user_play_id")
    private long userPlayId;

    @SerializedName("speed")
    @ColumnInfo(name = "tb_speed")
    private int speed;

    @SerializedName("circle")
    @ColumnInfo(name = "tb_circle")
    private int circle;

    public long getTbId() {
        return tbId;
    }

    public void setTbId(long tbId) {
        this.tbId = tbId;
    }

    public long getUserPlayId() {
        return userPlayId;
    }

    public void setUserPlayId(long userPlayId) {
        this.userPlayId = userPlayId;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getCircle() {
        return circle;
    }

    public void setCircle(int circle) {
        this.circle = circle;
    }
}
