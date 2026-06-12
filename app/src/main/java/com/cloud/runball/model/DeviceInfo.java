package com.cloud.runball.model;

import com.cloud.runball.module_bluetooth.data.DeviceBallInfo;

import java.io.Serializable;

/**
 * @author ns467
 */
public class DeviceInfo implements Serializable ,Comparable<DeviceInfo>{

    private int rssi;
    private long id=-1;
    private String mac;
    private String name;

    private boolean isConnected;
    private boolean isChecked;

    private DeviceBallInfo deviceBallInfo;

    public DeviceInfo(String name,String address){
        this.name = name;
        this.mac = address;
        this.isConnected = false;
        this.isChecked = false;
    }

    public DeviceInfo(String name, String address, DeviceBallInfo deviceBallInfo){
        this.name = name;
        this.mac = address;
        this.isConnected = false;
        this.isChecked = false;
        this.deviceBallInfo = deviceBallInfo;
    }

    public DeviceInfo(String name, String address, int rssi){
        this.name = name;
        this.mac = address;
        this.isConnected = false;
        this.isChecked = false;
        this.rssi = rssi;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
       this.id=id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceBallInfo getDeviceBallInfo() {
        return deviceBallInfo;
    }

    public void setDeviceBallInfo(DeviceBallInfo deviceBallInfo) {
        this.deviceBallInfo = deviceBallInfo;
    }

    public boolean getConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceInfo deviceInfo = (DeviceInfo) o;

        if (!mac.equalsIgnoreCase(deviceInfo.mac)) return false;
        return mac.equals(deviceInfo.mac);
    }

    @Override
    public int hashCode() {
        int result = mac.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "name:"+name+";mac="+mac+";isConnected="+isConnected+";isChecked="+isChecked+";rssi="+rssi;
    }

    @Override
    public int compareTo(DeviceInfo info) {
        return rssi-info.rssi;
    }
}
