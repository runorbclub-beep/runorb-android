package com.cloud.runball.bean;

public class DeviceNicknameData {

    private String deviceName;
    private String nickname;

    public DeviceNicknameData(String deviceName, String nickname) {
        this.deviceName = deviceName;
        this.nickname = nickname;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
