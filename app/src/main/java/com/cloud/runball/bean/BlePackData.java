package com.cloud.runball.bean;

import android.util.ArrayMap;

import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.BleByteHelper;

import java.util.Arrays;
import java.util.Map;

public class BlePackData {

    public static final int PLOWER=0x02;
    public static final int CLOSE=0x04;
    public static final int SPEED=0x05;
    public static final int RPM_SPEED=0x06;
    public static final int VER=0xFF;
    public static final int NONE=0x00;

    //新版本协议
    public static final boolean VER_NEW=true;


    public static boolean isWanliqiu(byte[] data) {
        if (data == null || data.length <= 17) {
            return false;
        }

        //02 01 06 03 03 30 AF 0C FF D6 05 08 00 4A 4C 41 49 53 44 4B 11 09 57 4C 51 5F 44 42 30 31 34 39 33 37 36 46 31 33 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
        if (data[0] == 0x02 && data[1] == 0x01 && data[2] == 0x06) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为当前设备，服务那边也有这个方法来判断
     * @param name
     * @return
     */
    public static boolean isWanliqiuName(String name) {
        //02 01 06 03 03 30 AF 0C FF D6 05 08 00 4A 4C 41 49 53 44 4B 11 09 57 4C 51 5F 44 42 30 31 34 39 33 37 36 46 31 33 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
        if (name.trim().startsWith("RunB") || name.startsWith("Run") || name.startsWith("run") || name.startsWith("WLQ")) {
            return true;
        }
        return false;
    }

    /**
     * 清理蓝牙数据
     * @return
     */
    public static byte[] requestClearData() {
        //新协议
        byte data[]={(byte) 0xA1,0x06,0x10, 0x00,0x00,0x00,0x00, 0x00,0x00,0x00,0x00, 0x00,0x00,0x00,0x00, 0x00,0x00,0x00,0x00, 0x55};
        return data;
    }


    public  static byte[] requestInitData(){
        byte data[]={(byte) 0xA1,0x06,0x10, 0x00,0x00,0x00,0x00, 0x00,0x00,0x00,0x00, 0x00,0x00,0x00,0x00, 0x00,0x00,0x00,0x00, 0x55};
        return data;
    }

    public static int getCmd(byte[] data){
        if(data!=null && data.length==20){
            if(data[0]==(byte) 0x55 && data[19]==(byte) 0xAA){
                return data[1];
            }
            return NONE;
        }else if(data!=null && data.length==5){
            if(data[0]==(byte) 0x55 && data[4]==(byte) 0xAA){
                return data[1];
            }
        }
        return NONE;
    }



    /**
     * 初始化关机时间，默认为10008*60毫秒
     * @return
     */
    public  static byte[] requestClose(){
        byte data[]={(byte) 0xA1,0x04,0x04,0x00,0x00,(byte)0xEA,0x60,0x55};
        return data;
    }

    /**
     * 初始化关机时间
     * @param time
     * @return
     */
    public  static byte[] requestClose(int time){
        byte[] tempTime=BleByteHelper.intToByte(time);
        byte data[]={(byte) 0xA1,0x04,0x04,0x00,0x00,(byte)0xEA,0x60,0x55};
        System.arraycopy(tempTime,0,data,3,4);
        return data;
    }

    /**
     * 发送获取电量
     * @return
     */
    public  static byte[] requestElectricity(){
        byte data[]={(byte) 0xA1,0x02,0x01,0x00,0x55};
        return data;
    }

    /**
     * 读取电量
     * @param data
     * @return
     */
    public  static int getElectricity(byte[] data){
        if(data==null || data.length<5){
            return 0;
        }
        return data[3];
    }

    /**
     * 获取单片机版本
     * @return
     */
    public  static byte[] requestVer(){
        byte data[]={(byte) 0xA1,(byte) 0xFF,0x01,0x00,0x55};
        return data;
    }


    @Deprecated
    /**
     * 新版本协议以及不使用
     */
    public static int getCurlTime(byte[] data) {
        byte[] circleData = new byte[4];
        System.arraycopy(data, 7, circleData, 0, 4);
        int circle = BleByteHelper.byteArrayToInt(circleData);
        return circle;
    }

    @Deprecated
    /**
     * 新版本协议以及不使用
     */
    public static int getCurlCircle(byte[] data) {
        byte[] circleData = new byte[4];
        System.arraycopy(data, 3, circleData, 0, 4);
        int circle = BleByteHelper.byteArrayToInt(circleData);
        return circle;
    }


    public static int getRpmSpeed(byte[] data) {
        byte[] speedData = new byte[4];
        System.arraycopy(data, 3, speedData, 0, 4);
        int speed = BleByteHelper.byteArrayToInt(speedData);
        return speed;
    }


    public static int getSpeed1(byte[] data) {
        byte[] speedData = new byte[4];
        System.arraycopy(data, 7, speedData, 0, 4);
        int speed = BleByteHelper.byteArrayToInt(speedData);
        return speed;
    }

    public static int getSpeed2(byte[] data) {
        byte[] speedData = new byte[4];
        System.arraycopy(data, 11, speedData, 0, 4);
        int speed = BleByteHelper.byteArrayToInt(speedData);
        return speed;
    }

    public static int getTotalCircle(byte[] data) {
        byte[] circleData = new byte[4];
        if(VER_NEW){
            System.arraycopy(data, 15, circleData, 0, 4);
        }else{
            System.arraycopy(data, 11, circleData, 0, 4);
        }
        int circle = BleByteHelper.byteArrayToInt(circleData);
        return circle;
    }
}
