package com.cloud.runball.model;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: ErrSpeed
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/30 17:29
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/30 17:29
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ErrSpeed implements Serializable {

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public int getMax_speed() {
        return max_speed;
    }

    public void setMax_speed(int max_speed) {
        this.max_speed = max_speed;
    }

    private float time;
    private int max_speed;

}
