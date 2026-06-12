package com.cloud.runball.model;

import com.cloud.runball.bean.OtherMatchDetailInfo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: OtherMatchDetailModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/22 17:40
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/22 17:40
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class OtherMatchDetailModel extends  BasicResponse<OtherMatchDetailModel> implements Serializable {

    private List<OtherMatchDetailInfo> group_list;

    public List<OtherMatchDetailInfo> getGroup_list() {
        return group_list;
    }

    public void setGroup_list(List<OtherMatchDetailInfo> group_list) {
        this.group_list = group_list;
    }

    public SelfDetailInfo getMy_info() {
        return my_info;
    }

    public void setMy_info(SelfDetailInfo my_info) {
        this.my_info = my_info;
    }

    private SelfDetailInfo my_info;


    public class SelfDetailInfo{

        /**
         * sys_shake_id : 72070910097494016
         * shake_group_id : 72070910156214272
         * shake_group_user_id : 72071543290597376
         * integral : 600
         * distance : 3088.61
         * index : 0
         * title : 1
         */

        private String sys_shake_id;
        private String shake_group_id;
        private String shake_group_user_id;
        private int integral;
        private String distance;
        private int index;
        private String title;
        private int integral_join;
        private String date;
        private int duration;

        public String getSys_shake_id() {
            return sys_shake_id;
        }

        public void setSys_shake_id(String sys_shake_id) {
            this.sys_shake_id = sys_shake_id;
        }

        public String getShake_group_id() {
            return shake_group_id;
        }

        public void setShake_group_id(String shake_group_id) {
            this.shake_group_id = shake_group_id;
        }

        public String getShake_group_user_id() {
            return shake_group_user_id;
        }

        public void setShake_group_user_id(String shake_group_user_id) {
            this.shake_group_user_id = shake_group_user_id;
        }

        public int getIntegral() {
            return integral;
        }

        public void setIntegral(int integral) {
            this.integral = integral;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getIntegral_join() {
            return integral_join;
        }

        public void setIntegral_join(int integral_join) {
            this.integral_join = integral_join;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }

}
