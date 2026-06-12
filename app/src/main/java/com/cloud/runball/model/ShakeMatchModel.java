package com.cloud.runball.model;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: ShakeMatchModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/23 11:17
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/23 11:17
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ShakeMatchModel extends BasicResponse<ShakeMatchModel> implements Serializable {


    /**
     * sys_shake_id : 72070910097494016
     * title : 搖加油
     * datetime : 1626883200
     * start_time : 1626904800
     * stop_time : 1626962400
     * each_integral : 1000
     * status : 3
     * created_time : 1626930427
     * updated_time : 1626930427
     * created_uid : 0
     * updated_uid : 0
     * countdown : -1
     */

    private long sys_shake_id;
    private String title;
    private String datetime;
    private String date;
    private int start_time;
    private int stop_time;
    private int each_integral;
    private int status;
    private int created_time;
    private int updated_time;
    private int created_uid;
    private int updated_uid;
    private int countdown;

    public int getCode() {
        return super.code;
    }
    public void setCode(int code) {
        super.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ShakeItem> getGroup_list() {
        return group_list;
    }

    public void setGroup_list(List<ShakeItem> group_list) {
        this.group_list = group_list;
    }

    public ShakeSelf getMy_info() {
        return my_info;
    }

    public void setMy_info(ShakeSelf my_info) {
        this.my_info = my_info;
    }

    private List<ShakeItem> group_list;
    private ShakeSelf my_info=null;

    public long getSys_shake_id() {
        return sys_shake_id;
    }

    public void setSys_shake_id(long sys_shake_id) {
        this.sys_shake_id = sys_shake_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
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

    public int getEach_integral() {
        return each_integral;
    }

    public void setEach_integral(int each_integral) {
        this.each_integral = each_integral;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCreated_time() {
        return created_time;
    }

    public void setCreated_time(int created_time) {
        this.created_time = created_time;
    }

    public int getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(int updated_time) {
        this.updated_time = updated_time;
    }

    public int getCreated_uid() {
        return created_uid;
    }

    public void setCreated_uid(int created_uid) {
        this.created_uid = created_uid;
    }

    public int getUpdated_uid() {
        return updated_uid;
    }

    public void setUpdated_uid(int updated_uid) {
        this.updated_uid = updated_uid;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public static class ShakeSelf implements Serializable{

        private String shake_group_user_id;
        private String sys_shake_id;
        private long shake_group_id;
        private String user_id;
        private String distance;
        private int integral;
        private int duration;
        private int index;
        private String title;
        private int datetime;
        private int num;

        public String getShake_group_user_id() {
            return shake_group_user_id;
        }

        public void setShake_group_user_id(String shake_group_user_id) {
            this.shake_group_user_id = shake_group_user_id;
        }

        public String getSys_shake_id() {
            return sys_shake_id;
        }

        public void setSys_shake_id(String sys_shake_id) {
            this.sys_shake_id = sys_shake_id;
        }

        public long getShake_group_id() {
            return shake_group_id;
        }

        public void setShake_group_id(long shake_group_id) {
            this.shake_group_id = shake_group_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public int getIntegral() {
            return integral;
        }

        public void setIntegral(int integral) {
            this.integral = integral;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
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

        public int getDatetime() {
            return datetime;
        }

        public void setDatetime(int datetime) {
            this.datetime = datetime;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }

    public static class ShakeItem implements Serializable{


        /**
         * shake_group_id : 72459232305221633
         * sys_shake_id : 72459232154226688
         * title : 的卢马
         * start_time : 0
         * stop_time : 0
         * num : 0
         * distance : 0
         * integral : 0
         * status : 1
         * index : 1
         * datetime : 1626969600
         * ranking : 0
         */

        private long shake_group_id;
        private long sys_shake_id;
        private String title;
        private int start_time;
        private int stop_time;
        private int num;
        private double distance;
        private int integral;
        private int status;
        private int index;
        private int datetime;
        private int ranking;

        public long getShake_group_id() {
            return shake_group_id;
        }

        public void setShake_group_id(long shake_group_id) {
            this.shake_group_id = shake_group_id;
        }

        public long getSys_shake_id() {
            return sys_shake_id;
        }

        public void setSys_shake_id(long sys_shake_id) {
            this.sys_shake_id = sys_shake_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public int getIntegral() {
            return integral;
        }

        public void setIntegral(int integral) {
            this.integral = integral;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getDatetime() {
            return datetime;
        }

        public void setDatetime(int datetime) {
            this.datetime = datetime;
        }

        public int getRanking() {
            return ranking;
        }

        public void setRanking(int ranking) {
            this.ranking = ranking;
        }
    }
}
