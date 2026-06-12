package com.cloud.runball.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: GroupInfoModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/10 16:10
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/10 16:10
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class GroupInfoModel implements Parcelable {


    private String user_group;

    public GroupInfoModel(){

    }

    protected GroupInfoModel(Parcel in) {
        user_group = in.readString();
        user_group_title = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_group);
        dest.writeString(user_group_title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupInfoModel> CREATOR = new Creator<GroupInfoModel>() {
        @Override
        public GroupInfoModel createFromParcel(Parcel in) {
            return new GroupInfoModel(in);
        }

        @Override
        public GroupInfoModel[] newArray(int size) {
            return new GroupInfoModel[size];
        }
    };

    public String getUser_group() {
        return user_group;
    }

    public void setUser_group(String user_group) {
        this.user_group = user_group;
    }

    public String getUser_group_title() {
        return user_group_title;
    }

    public void setUser_group_title(String user_group_title) {
        this.user_group_title = user_group_title;
    }

    private String user_group_title;
}
