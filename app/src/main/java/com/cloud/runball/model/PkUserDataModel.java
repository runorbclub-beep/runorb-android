package com.cloud.runball.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: PkUserDataModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/10 13:29
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/10 13:29
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class PkUserDataModel implements Parcelable , Serializable {

    private int status;
    private String user_id;
    private String pk_room_id;
    private long user_pk_list_id;
    private String user_group;
    private String user_name;
    private String user_img;
    private int fd;

    private boolean virtualSeat;

    public boolean isVirtualSeat(){
        return virtualSeat;
    }

    public PkUserDataModel(){}

    public PkUserDataModel(boolean isVirtualSeat,String user_group,long user_pk_list_id){
        this.virtualSeat=isVirtualSeat;
        this.user_group = user_group;
        this.user_pk_list_id=user_pk_list_id;
        if(isVirtualSeat){
            user_img="";
        }
    }

    public PkUserDataModel(boolean isVirtualSeat,String user_group){
        this.virtualSeat=isVirtualSeat;
        this.user_group = user_group;
        if(isVirtualSeat){
            user_img="";
        }
    }

    protected PkUserDataModel(Parcel in) {
        status = in.readInt();
        user_id = in.readString();
        pk_room_id = in.readString();
        user_pk_list_id = in.readLong();
        user_group = in.readString();
        user_name = in.readString();
        user_img = in.readString();
        fd = in.readInt();
        is_stop = in.readInt();
        is_ready = in.readInt();
        circle_count = in.readInt();
        duration = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeString(user_id);
        dest.writeString(pk_room_id);
        dest.writeLong(user_pk_list_id);
        dest.writeString(user_group);
        dest.writeString(user_name);
        dest.writeString(user_img);
        dest.writeInt(fd);
        dest.writeInt(is_stop);
        dest.writeInt(is_ready);
        dest.writeInt(circle_count);
        dest.writeInt(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PkUserDataModel> CREATOR = new Creator<PkUserDataModel>() {
        @Override
        public PkUserDataModel createFromParcel(Parcel in) {
            return new PkUserDataModel(in);
        }

        @Override
        public PkUserDataModel[] newArray(int size) {
            return new PkUserDataModel[size];
        }
    };

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPk_room_id() {
        return pk_room_id;
    }

    public void setPk_room_id(String pk_room_id) {
        this.pk_room_id = pk_room_id;
    }

    public long getUser_pk_list_id() {
        return user_pk_list_id;
    }

    public void setUser_pk_list_id(long user_pk_list_id) {
        this.user_pk_list_id = user_pk_list_id;
    }

    public String getUser_group() {
        return user_group;
    }

    public void setUser_group(String user_group) {
        this.user_group = user_group;
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

    public int getFd() {
        return fd;
    }

    public void setFd(int fd) {
        this.fd = fd;
    }

    public int getIs_stop() {
        return is_stop;
    }

    public void setIs_stop(int is_stop) {
        this.is_stop = is_stop;
    }

    public int getIs_ready() {
        return is_ready;
    }

    public void setIs_ready(int is_ready) {
        this.is_ready = is_ready;
    }

    public int getCircle_count() {
        return circle_count;
    }

    public void setCircle_count(int circle_count) {
        this.circle_count = circle_count;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private int is_stop;
    private int is_ready;
    private int circle_count;
    private int duration;

}
