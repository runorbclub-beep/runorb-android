package com.cloud.runball.bean;

import android.content.Intent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageEvent implements Serializable {

    //public static final int UPDATE_USER_INFO = 0;       //更新用户信息
    public static final int PLAY_START = 1;         //开始运动
    public static final int PLAY_ING = 2;           //运动中
    public static final int PLAY_OVER = 3;          //停止运动
    public static final int BINDING_DEVICES_BACK = 4;   //服务器返回绑定设备列表
    public static final int NETWORK_ERROR = 6;          //网络断开连接
    public static final int NETWORK_ONAVAILABLE = 7;    //网络重新连接上
    public static final int ACTION_REQUEST_ENABLE = 9;  //跳转打开蓝牙设备
    public static final int ACTION_VERIFY_REQUEST_PERMISSION = 10;  //申请权限去扫描


    public static final int ACTION_BLUETOOTH_DEVICE = 12;     //扫描到设备
    public static final int ACTION_SCAN_FINISHED = 15;        //扫描结束

    public static final int STATE_DISCONNECTED = 16;       //设备断开连接
    public static final int STATE_CONNECTING = 17;         //设备正在连接
    public static final int STATE_CONNECTED = 18;          //设备已经连接
    public static final int STATE_DISCONNECTING = 19;      //设备正在断开连接

    public static final int GATT_SUCCESS = 20;               //发现服务
    public static final int ON_CHARACTERISTIC_READ = 21;     //获得BLE下发数据
    public static final int ON_POWER_ELE = 22;               //获得BLE下发电量

    public static final int ON_SEND_PLAY_READY = 23;         //下发运动准备
    public static final int ON_SEND_PLAY_TIME = 24;          //下发运动时间
    public static final int ON_SEND_PLAY_DATA = 25;          //下发真正解析后的数据
    public static final int ON_INIT_DATA = 26;               //开始前先初始化UI
    public static final int ON_UPDATE_NOT_CONNECTED = 27;    //更新未连接的设备

    public static final int ON_PKListChange=28;
    public static final int ON_PKStart =29;
    public static final int ON_PKResult=30;
    public static final int ON_PKError=31;
    public static final int ON_PKBetween=32;
    public static final int ON_PKConnected=33;
    public static final int ON_bind_again=34;
    public static final int ON_CONNECT_SUCCESS=35;
    public static final int ON_SEND_PLAY_START=36;
    public static final int ON_SEND_HIDDEN_TIME=37;
    public static final int ON_SEND_PLAY_TIME_2 = 38;               //比赛定时器
    public static final int ON_SEND_PLAY_MATCH_READY = 39;          //开赛运动准备
    public static final int ON_SEND_RANK_MATCH = 40;                //发送获取竞标赛

    public static final int STATE_APP_TO_BACKSTAGE = 50; //app 进入后台
    public static final int STATE_APP_TO_FOREGROUND = 51; //app 进入前台
    public static final int EDIT_NICKNAME = 52;
    public static final int REFRESH_RANK_LIST = 53; // 刷新排行榜

    public static final int REFRESH = 54; // 刷新俱乐部信息

    private int evetId;

    public int getEvetId() {
        return evetId;
    }

    public void setEvetId(int evetId) {
        this.evetId = evetId;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    private Object object;

    public MessageEvent(int eventId, Object object) {
        this.evetId = eventId;
        this.object = object;
    }


    List<Integer> tempSpeeds = new ArrayList<>();
    public MessageEvent(int eventId, List<Integer> circles,List<Integer> speeds) {
        this.evetId = eventId;
        this.tempCircles.clear();
        this.tempSpeeds.clear();
        this.tempCircles.addAll(circles);
        this.tempSpeeds.addAll(speeds);
    }

    private String deviceName;
    private String mac;
    public MessageEvent(int eventId, String deviceName,String mac) {
        this.evetId = eventId;
        this.deviceName=deviceName;
        this.mac=mac;
    }

    public String getDeviceName(){
        return this.deviceName;
    }

    public String getMac(){
        return this.mac;
    }

    public List<Integer> getSpeeds(){
        return tempSpeeds;
    }


    List<Integer> tempCircles = new ArrayList<>();
    public List<Integer> getCircles(){
        return tempCircles;
    }

    //也可以当作电量使用，在不同eventId中代表意思不同
    private int keepTime;
    private int keepTime2;
    public MessageEvent(int eventId, int keepTime) {
        this.evetId = eventId;
        this.keepTime = keepTime;
    }


    public MessageEvent(int eventId, int keepTime,int keepTime2) {
        this.evetId = eventId;
        this.keepTime = keepTime;
        this.keepTime2=keepTime2;
    }

    public int getKeepTime() {
        return this.keepTime;
    }

    public int getKeepTime2(){
        return this.keepTime2;
    }

    private int rpm = 0;
    private int speed1 = 0;
    private int speed2 = 0;
    private int maxSpeed=0;
    private int totalCircle = 0;

    public MessageEvent(int eventId, int rpm, int speed1,int speed2, int maxSpeed,int totalCircle) {
        this.evetId = eventId;
        this.rpm = rpm;
        this.speed1 = speed1;
        this.speed2 = speed2;
        this.maxSpeed=maxSpeed;
        this.totalCircle = totalCircle;
    }

    public int getRpm(){
        return this.rpm;
    }

    public int getSpeed1(){
        return this.speed1;
    }

    public int getSpeed2(){
        return this.speed2;
    }

    public int getTotalCircle(){
        return this.totalCircle;
    }

    public int getMaxSpeed(){
        return this.maxSpeed;
    }

    private Intent intent;

    public MessageEvent(int eventId, Intent intent) {
        this.evetId = eventId;
        this.intent = intent;
    }

    public Intent getIntent() {
        return intent;
    }

    public MessageEvent(int eventId) {
        this.evetId = eventId;
    }


    @Override
    public String toString() {
        return "evetId=" + evetId;
    }

}
