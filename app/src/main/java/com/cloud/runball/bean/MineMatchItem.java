package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: MineMatchItem
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/12 11:35
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/12 11:35
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MineMatchItem implements Serializable {

    public MineMatchItem(String id,String title){
        this.match_event_id=id;
        this.match_event_title=title;
    }

    private String match_event_id;

    public String getMatch_event_id() {
        return match_event_id;
    }

    public void setMatch_event_id(String match_event_id) {
        this.match_event_id = match_event_id;
    }

    public String getMatch_event_title() {
        return match_event_title;
    }

    public void setMatch_event_title(String match_event_title) {
        this.match_event_title = match_event_title;
    }

    private String match_event_title;

}
