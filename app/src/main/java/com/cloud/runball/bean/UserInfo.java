package com.cloud.runball.bean;


import com.cloud.runball.model.AchievementDTO;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserInfo implements Serializable {


  public String getUser_id() {
    return user_id;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  /**
   * status : 1
   * user_name : 3_runball
   * self_description : 多运动更健康，大家快来一起运动吧！
   * user_name_change : 0
   * user_img_change : 0
   * user_img : wx_sources/default_user.png
   * token : 5a2e918e7c8d6a0e508e31f5532ecaa9
   * sys_sex_id : 1791224340025344
   * sex_name : 男
   * device_uid : ad38df424a21d06b
   * name_cn : 中国
   * sys_user_type_id : 1809649523232768
   * user_type_name : 游客
   * achievement : {"duration":665.5,"speed_max":8340,"circle_count":6993,"endurance_max":0,"play_count":10}
   * my_medal : [{"medal_image_active":"https://api-all-sporter.megacombine.com/medal_sources/shoucitiaozhan_active.png","is_get":true,"user_medal_name":"首次挑战","description":"首次挑战 level.1, 累计运动次数>1","level_name":"level.1"},{"medal_image_active":"https://api-all-sporter.megacombine.com/medal_sources/dayousuocheng.png","is_get":false,"user_medal_name":"大有所成","description":"大有所成 level.1,持续时间>300s 最高转速>10000 rpm 圈数>20000圈 and 耐力>20s 累计运动次数>100次","level_name":"level.1"},{"medal_image_active":"https://api-all-sporter.megacombine.com/medal_sources/zhengmingziji.png","is_get":false,"user_medal_name":"证明自己","description":"证明自己 level.1,持续时间>600s 最高转速>20000 rpm  ","level_name":"level.1"},{"medal_image_active":"https://api-all-sporter.megacombine.com/medal_sources/beishouzhumu.png","is_get":false,"user_medal_name":"备受瞩目","description":"备受瞩目 level.1,持续时间>300s  最高转速>10000 rpm  圈数>20000圈  耐力>20s  累计运动次数>100次","level_name":"level.1"}]
   * sys_medal_count : 6
   */

  private String user_id;
  private int status;
  private String user_name;
  private String self_description;
  private int user_name_change;
  private int user_img_change;
  private String user_img;
  private String token;
  private int sys_sex_id_change;
  private String sys_sex_id;
  private String sex_name;
  private String device_uid;
  private String name_cn;
  private String sys_user_type_id;
  private String user_type_name;
  private AchievementDTO achievement;
  private int sys_medal_count;

  private int is_members;
  private int members_status;

  private String members_exptitle;
  private String share_code;
  private int show_members_entrance;
  private String members_entrance_url;

  private String live_platform;
  private String live_id;
  private String wechart_id;
  private String real_name;
  private String address_detail;
  private String email;
  private int user_birthday_change=3;
  private int user_city_change=3;

  private int is_yang;           //青年： 1   成年：0
  private int is_group;          // 团队： 1  个人：0
  private int integral;          //积分
  private String shop_url;

  public String getShop_url() {
    return shop_url;
  }

  public void setShop_url(String shop_url) {
    this.shop_url = shop_url;
  }



  public int getIntegral() {
    return integral;
  }

  public void setIntegral(int integral) {
    this.integral = integral;
  }




  public String getLive_platform() {
    return live_platform;
  }

  public void setLive_platform(String live_platform) {
    this.live_platform = live_platform;
  }

  public String getLive_id() {
    return live_id;
  }

  public void setLive_id(String live_id) {
    this.live_id = live_id;
  }

  public String getWechart_id() {
    return wechart_id;
  }

  public void setWechart_id(String wechart_id) {
    this.wechart_id = wechart_id;
  }

  public String getReal_name() {
    return real_name;
  }

  public void setReal_name(String real_name) {
    this.real_name = real_name;
  }

  public String getAddress_detail() {
    return address_detail;
  }

  public void setAddress_detail(String address_detail) {
    this.address_detail = address_detail;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public int getUser_birthday_change() {
    return user_birthday_change;
  }

  public void setUser_birthday_change(int user_birthday_change) {
    this.user_birthday_change = user_birthday_change;
  }

  public int getUser_city_change() {
    return user_city_change;
  }

  public void setUser_city_change(int user_city_change) {
    this.user_city_change = user_city_change;
  }

  public int getIs_yang() {
    return is_yang;
  }

  public void setIs_yang(int is_yang) {
    this.is_yang = is_yang;
  }

  public int getIs_group() {
    return is_group;
  }

  public void setIs_group(int is_group) {
    this.is_group = is_group;
  }






  public String getMembers_exptitle() {
    return members_exptitle;
  }

  public void setMembers_exptitle(String members_exptitle) {
    this.members_exptitle = members_exptitle;
  }

  public String getShare_code() {
    return share_code;
  }

  public void setShare_code(String share_code) {
    this.share_code = share_code;
  }

  public int getShow_members_entrance() {
    return show_members_entrance;
  }

  public void setShow_members_entrance(int show_members_entrance) {
    this.show_members_entrance = show_members_entrance;
  }

  public String getMembers_entrance_url() {
    return members_entrance_url;
  }

  public void setMembers_entrance_url(String members_entrance_url) {
    this.members_entrance_url = members_entrance_url;
  }




  private List<MedalInfo> my_medal=new ArrayList<>();
  public int getIs_members() {
    return is_members;
  }

  public void setIs_members(int is_members) {
    this.is_members = is_members;
  }

  public int getMembers_status() {
    return members_status;
  }

  public void setMembers_status(int members_status) {
    this.members_status = members_status;
  }

  public String getMembers_exptime() {
    return members_exptime;
  }

  public void setMembers_exptime(String members_exptime) {
    this.members_exptime = members_exptime;
  }

  private String members_exptime="";




  private String birthday;

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  private String address;




  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  private String phone;

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getUser_name() {
    return user_name;
  }

  public void setUser_name(String user_name) {
    this.user_name = user_name;
  }

  public String getSelf_description() {
    return self_description;
  }

  public void setSelf_description(String self_description) {
    this.self_description = self_description;
  }

  public int getUser_name_change() {
    return user_name_change;
  }

  public void setUser_name_change(int user_name_change) {
    this.user_name_change = user_name_change;
  }

  public int getUser_img_change() {
    return user_img_change;
  }

  public void setUser_img_change(int user_img_change) {
    this.user_img_change = user_img_change;
  }

  public String getUser_img() {
    return user_img;
  }

  public void setUser_img(String user_img) {
    this.user_img = user_img;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public int getSys_sex_id_change() {
    return sys_sex_id_change;
  }

  public void setSys_sex_id_change(int sys_sex_id_change) {
    this.sys_sex_id_change = sys_sex_id_change;
  }

  public String getSys_sex_id() {
    return sys_sex_id;
  }

  public void setSys_sex_id(String sys_sex_id) {
    this.sys_sex_id = sys_sex_id;
  }

  public String getSex_name() {
    return sex_name;
  }

  public void setSex_name(String sex_name) {
    this.sex_name = sex_name;
  }

  public String getDevice_uid() {
    return device_uid;
  }

  public void setDevice_uid(String device_uid) {
    this.device_uid = device_uid;
  }

  public String getName_cn() {
    return name_cn;
  }

  public void setName_cn(String name_cn) {
    this.name_cn = name_cn;
  }

  public String getSys_user_type_id() {
    return sys_user_type_id;
  }

  public void setSys_user_type_id(String sys_user_type_id) {
    this.sys_user_type_id = sys_user_type_id;
  }

  public String getUser_type_name() {
    return user_type_name;
  }

  public void setUser_type_name(String user_type_name) {
    this.user_type_name = user_type_name;
  }

  public AchievementDTO getAchievement() {
    return achievement;
  }

  public void setAchievement(AchievementDTO achievement) {
    this.achievement = achievement;
  }

  public int getSys_medal_count() {
    return sys_medal_count;
  }

  public void setSys_medal_count(int sys_medal_count) {
    this.sys_medal_count = sys_medal_count;
  }

  public List<MedalInfo> getMy_medal() {
    return my_medal;
  }

  public void setMy_medal(List<MedalInfo> my_medal) {
    this.my_medal = my_medal;
  }


  @Override
  public String toString(){
    return "user_id="+user_id+";token="+token+";phone="+phone;
  }
}
