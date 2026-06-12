package com.cloud.runball.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: PkInfoModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/9 10:09
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/9 10:09
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class PkInfoModel implements Serializable {

    /**
     * status : 1
     * user_id : 26358638318718976
     * pk_room_id : 34335966650961920
     * user_pk_list_id : 34336006165499904
     * user_group : red
     * user_name : 崔琰
     * user_img : https://api-all-sporter.megacombine.com/wx_sources/default_user.png
     * pk_type : 0
     * time_long : 180
     * pk_result_type : 1
     * distance_value : 0
     */

    private int status;
    private long user_id;
    private String pk_room_id;
    private int pk_type;
    private int time_long;
    private String pk_result_type;
    private String pk_room_title;
    private String pk_room_number;

    public int getPk_max_person() {
        return pk_max_person;
    }

    public void setPk_max_person(int pk_max_person) {
        this.pk_max_person = pk_max_person;
    }

    private int pk_max_person;

    public List<UserGroupModel> getGroupModels() {
        return groupModels;
    }

    public void setGroupModels(List<UserGroupModel> groupModels) {
        this.groupModels = groupModels;
    }

    private List<UserGroupModel> groupModels=new ArrayList<>();

    private List<PkUserDataModel> redList=new ArrayList<>();

    private List<PkUserDataModel> blueList=new ArrayList<>();

    public List<PkUserDataModel> getRedList() {
        return redList;
    }

    public void setRedList(List<PkUserDataModel> redList) {
        this.redList = redList;
    }

    public List<PkUserDataModel> getBlueList() {
        return blueList;
    }

    public void setBlueList(List<PkUserDataModel> blueList) {
        this.blueList = blueList;
    }

    public String getCreated_uid() {
        return created_uid;
    }

    public void setCreated_uid(String created_uid) {
        this.created_uid = created_uid;
    }

    private String created_uid;

    public String getPk_room_title() {
        return pk_room_title;
    }

    public void setPk_room_title(String pk_room_title) {
        this.pk_room_title = pk_room_title;
    }

    public String getPk_room_number() {
        return pk_room_number;
    }

    public void setPk_room_number(String pk_room_number) {
        this.pk_room_number = pk_room_number;
    }


    public int getMax_person_num() {
        return max_person_num;
    }

    public void setMax_person_num(int max_person_num) {
        this.max_person_num = max_person_num;
    }

    private int max_person_num;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getPk_room_id() {
        return pk_room_id;
    }

    public void setPk_room_id(String pk_room_id) {
        this.pk_room_id = pk_room_id;
    }



    public int getPk_type() {
        return pk_type;
    }

    public void setPk_type(int pk_type) {
        this.pk_type = pk_type;
    }

    public int getTime_long() {
        return time_long;
    }

    public void setTime_long(int time_long) {
        this.time_long = time_long;
    }


    public String getPk_result_type() {
        return pk_result_type;
    }

    public void setPk_result_type(String pk_result_type) {
        this.pk_result_type = pk_result_type;
    }



    @Override
    public String toString(){
        return "status="+status+";user_id="+user_id+";pk_room_id="+pk_room_id+
                "pk_type="+pk_type+";time_long="+time_long+";pk_result_type="+pk_result_type;
    }

}
