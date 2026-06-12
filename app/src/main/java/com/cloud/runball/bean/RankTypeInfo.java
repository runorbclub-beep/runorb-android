package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: RankTypeInfo
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/17 15:36
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/17 15:36
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankTypeInfo implements Serializable {


    /**
     * title_zh : 摇跑转速
     * title_en : YP rpm
     * type : max_speed
     * index : 1
     */

    private String title_zh;
    private String title_en;
    private String type;
    private int index;

    public String getTitle_zh() {
        return title_zh;
    }

    public void setTitle_zh(String title_zh) {
        this.title_zh = title_zh;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
