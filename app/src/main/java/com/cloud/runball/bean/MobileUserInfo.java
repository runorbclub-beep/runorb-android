package com.cloud.runball.bean;

import com.cloud.runball.model.AchievementDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: MobileUserInfo
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/22 13:29
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/22 13:29
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MobileUserInfo implements Serializable {

    /**
     * user_id : 28691273070153728
     * status : 1
     * user_name : 吧哦哦
     * self_description : 来了个性网
     * user_name_change : 0
     * user_img_change : 0
     * user_img : https://api-all-sporter.megacombine.com/wx_sources/default_user.png
     * token : bf03eb4433e9f2fb2c867d7ae90fb13b
     * sys_sex_id : 1791224340025344
     * sex_name : 男
     * device_uid : 72f1e1d850242592
     * sys_country_id : 1795738841387008
     * name_cn : 中国
     * sys_user_type_id : 1809649523232768
     * user_type_name : 游客
     * phone : 13544061760
     * access_token : bf03eb4433e9f2fb2c867d7ae90fb13b
     */

    private String user_id;
    private int status;
    private String user_name;
    private String self_description;
    private int user_name_change;
    private int user_img_change;
    private String user_img;
    private String token;
    private String sys_sex_id;
    private int sys_sex_id_change;
    private String sex_name;
    private String device_uid;
    private String sys_country_id;
    private String name_cn;
    private String sys_user_type_id;
    private String user_type_name;
    private String phone;
    private String access_token;
    private int is_group;

    private AchievementDTO achievement;

    public AchievementDTO getAchievement() {
        return achievement;
    }

    public void setAchievement(AchievementDTO achievement) {
        this.achievement = achievement;
    }

    private List<MedalInfo> my_medal;

    public List<MedalInfo> getMy_medal() {
        return my_medal;
    }

    public void setMy_medal(List<MedalInfo> my_medal) {
        this.my_medal = my_medal;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

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

    public String getSys_sex_id() {
        return sys_sex_id;
    }

    public void setSys_sex_id(String sys_sex_id) {
        this.sys_sex_id = sys_sex_id;
    }

    public int getSys_sex_id_change() {
        return sys_sex_id_change;
    }

    public void setSys_sex_id_change(int sys_sex_id_change) {
        this.sys_sex_id_change = sys_sex_id_change;
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

    public String getSys_country_id() {
        return sys_country_id;
    }

    public void setSys_country_id(String sys_country_id) {
        this.sys_country_id = sys_country_id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int isGroup() {
        return is_group;
    }

    public void setGroup(int is_group) {
        this.is_group = is_group;
    }
}
