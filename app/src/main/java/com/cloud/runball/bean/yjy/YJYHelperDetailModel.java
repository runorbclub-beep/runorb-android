package com.cloud.runball.bean.yjy;

import com.cloud.runball.model.BasicResponse;

import java.io.Serializable;
import java.util.List;

public class YJYHelperDetailModel extends BasicResponse<YJYHelperDetailModel> implements Serializable {

  private MyInfo my_info;
  private ShakeList shake_list;

  public MyInfo getMy_info() {
    return my_info;
  }

  public void setMy_info(MyInfo my_info) {
    this.my_info = my_info;
  }

  public ShakeList getShake_list() {
    return shake_list;
  }

  public void setShake_list(ShakeList shake_list) {
    this.shake_list = shake_list;
  }

  public class MyInfo {
    private String sys_shake_id;
    private String shake_group_id;
    private String shake_group_user_id;
    private int integral;
    private String distance;
    private int index;
    private String title;
    private int integral_join;
    private String datetime;
    private String user_id;
    private String sys_sex_id;
    private String user_name;
    private String user_img;

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

    public String getDatetime() {
      return datetime;
    }

    public void setDatetime(String datetime) {
      this.datetime = datetime;
    }

    public String getUser_id() {
      return user_id;
    }

    public void setUser_id(String user_id) {
      this.user_id = user_id;
    }

    public String getSys_sex_id() {
      return sys_sex_id;
    }

    public void setSys_sex_id(String sys_sex_id) {
      this.sys_sex_id = sys_sex_id;
    }

    public String getUser_name() {
      return user_name;
    }

    public void setUser_name(String user_name) {
      this.user_name = user_name;
    }

    public String getUser_img() {
      return user_img;
    }

    public void setUser_img(String user_img) {
      this.user_img = user_img;
    }
  }

  public class ShakeList {
    private int count;
    private List<ShakeInfo> list;

    public int getCount() {
      return count;
    }

    public void setCount(int count) {
      this.count = count;
    }

    public List<ShakeInfo> getList() {
      return list;
    }

    public void setList(List<ShakeInfo> list) {
      this.list = list;
    }
  }

  public class ShakeInfo {
    private String sys_shake_id;
    private String shake_group_id;
    private String shake_group_user_id;
    private int integral;
    private String distance;
    private int index;
    private String title;
    private int integral_join;
    private String datetime;
    private String user_id;
    private UsrUser usr_user;

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

    public String getDatetime() {
      return datetime;
    }

    public void setDatetime(String datetime) {
      this.datetime = datetime;
    }

    public String getUser_id() {
      return user_id;
    }

    public void setUser_id(String user_id) {
      this.user_id = user_id;
    }

    public UsrUser getUsr_user() {
      return usr_user;
    }

    public void setUsr_user(UsrUser usr_user) {
      this.usr_user = usr_user;
    }
  }

  public class UsrUser {
    private String user_id;
    private String sys_sex_id;
    private String user_name;
    private String user_img;

    public String getUser_id() {
      return user_id;
    }

    public void setUser_id(String user_id) {
      this.user_id = user_id;
    }

    public String getSys_sex_id() {
      return sys_sex_id;
    }

    public void setSys_sex_id(String sys_sex_id) {
      this.sys_sex_id = sys_sex_id;
    }

    public String getUser_name() {
      return user_name;
    }

    public void setUser_name(String user_name) {
      this.user_name = user_name;
    }

    public String getUser_img() {
      return user_img;
    }

    public void setUser_img(String user_img) {
      this.user_img = user_img;
    }
  }

}
