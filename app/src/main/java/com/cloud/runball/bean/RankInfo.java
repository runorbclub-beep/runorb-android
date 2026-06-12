package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: RankInfo
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/13 14:07
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/13 14:07
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankInfo implements Serializable {


    /**
     * user_rank_list_id :
     * title : 深圳市个人成年榜
     * user_age_type : 0
     * user_type : 0
     * address : 深圳市
     */

    private String user_rank_list_id;
    private String title;
    private String user_age_type;
    private String user_type;
    private String address;
    private String sys_sex_id;

    public String getUser_rank_list_id() {
        return user_rank_list_id;
    }

    public void setUser_rank_list_id(String user_rank_list_id) {
        this.user_rank_list_id = user_rank_list_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser_age_type() {
        return user_age_type;
    }

    public void setUser_age_type(String user_age_type) {
        this.user_age_type = user_age_type;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSys_sex_id() {
        return sys_sex_id;
    }

    public void setSys_sex_id(String sys_sex_id) {
        this.sys_sex_id = sys_sex_id;
    }
}
