package com.cloud.runball.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: UserGroupModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/8 16:35
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/8 16:35
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class UserGroupModel implements Parcelable,Serializable {

    public UserGroupModel(String user_group,String user_group_title){
        this.user_group=user_group;
        this.user_group_title=user_group_title;
    }



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

    private String user_group;
    private String user_group_title;


    protected UserGroupModel(Parcel in) {
        user_group = in.readString();
        user_group_title = in.readString();
    }

    // 注意这里的write顺序跟下面的read顺序一定要一样
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_group);
        dest.writeString(user_group_title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserGroupModel> CREATOR = new Creator<UserGroupModel>() {
        @Override
        public UserGroupModel createFromParcel(Parcel in) {
            return new UserGroupModel(in);
        }

        @Override
        public UserGroupModel[] newArray(int size) {
            return new UserGroupModel[size];
        }
    };


}
