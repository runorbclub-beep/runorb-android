package com.cloud.runball.model;

import com.cloud.runball.bean.OtherMatchInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: OtherMatchModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/22 14:45
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/22 14:45
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class OtherMatchModel extends  BasicResponse<OtherMatchModel> implements Serializable {

    private int status;
    private String start_time;
    private String stop_time;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getStop_time() {
        return stop_time;
    }

    public void setStop_time(String stop_time) {
        this.stop_time = stop_time;
    }

    public String getBanner_img() {
        return banner_img;
    }

    public void setBanner_img(String banner_img) {
        this.banner_img = banner_img;
    }

    public String getBanner_link() {
        return banner_link;
    }

    public void setBanner_link(String banner_link) {
        this.banner_link = banner_link;
    }

    public List<OtherMatchInfo> getMy_logs() {
        return my_logs;
    }

    public void setMy_logs(List<OtherMatchInfo> my_logs) {
        this.my_logs = my_logs;
    }

    private String banner_img;
    private String banner_link;

    private List<OtherMatchInfo> my_logs;

}
