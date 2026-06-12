package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: UserJoinStatusDTO
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/13 14:11
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/13 14:11
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class UserJoinStatusDTO implements Serializable {
    /**
     * is_join : 0
     * user_group_id :
     * group_title :
     * group_num :
     */

    private int is_join;
    private String user_group_id;
    private String group_title;
    private String group_num;

    public int getIs_join() {
        return is_join;
    }

    public void setIs_join(int is_join) {
        this.is_join = is_join;
    }

    public String getUser_group_id() {
        return user_group_id;
    }

    public void setUser_group_id(String user_group_id) {
        this.user_group_id = user_group_id;
    }

    public String getGroup_title() {
        return group_title;
    }

    public void setGroup_title(String group_title) {
        this.group_title = group_title;
    }

    public String getGroup_num() {
        return group_num;
    }

    public void setGroup_num(String group_num) {
        this.group_num = group_num;
    }
}
