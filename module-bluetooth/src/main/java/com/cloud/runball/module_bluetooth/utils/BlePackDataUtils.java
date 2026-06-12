package com.cloud.runball.module_bluetooth.utils;

public class BlePackDataUtils {

  public static final int ELECTRICITY = 0x02;
  public static final int CLOSE = 0x04;
  public static final int SPEED = 0x05;
  public static final int RPM_SPEED = 0x06;
  public static final int VER = 0xFF;
  public static final int NONE = 0x00;

  public static int getPackType(byte[] data){
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

  //字节数组转化为int
  public static int byteArrayToInt(byte[] arr) {
    return (arr[0]&0xff)<<24|(arr[1]&0xff)<<16|(arr[2]&0xff)<<8|(arr[3]&0xff);
  }

  public static int analysisRpmSpeed(byte[] data) {
    byte[] speedData = new byte[4];
    System.arraycopy(data, 3, speedData, 0, 4);
    return byteArrayToInt(speedData);
  }

  /**
   * 获取单片机版本
   * @return
   */
  public static byte[] requestVer(){
    return new byte[]{(byte) 0xA1,(byte) 0xFF,0x01,0x00,0x55};
  }

  /**
   * 据说是实时的速度，但抖动较大所以不用
   * @param data
   * @return
   */
  public static int analysisSpeed1(byte[] data) {
    byte[] speedData = new byte[4];
    System.arraycopy(data, 7, speedData, 0, 4);
    return byteArrayToInt(speedData);
  }

  /**
   * 据说这速度是用贝塞尔公式调整过的，起伏比较平滑
   * @param data
   * @return
   */
  public static int analysisSpeed2(byte[] data) {
    byte[] speedData = new byte[4];
    System.arraycopy(data, 11, speedData, 0, 4);
    return byteArrayToInt(speedData);
  }

  /**
   * 解析圈数
   * @param data
   * @return
   */
  public static int analysisCircle(byte[] data) {
    byte[] circleData = new byte[4];
    System.arraycopy(data, 15, circleData, 0, 4);
//    System.arraycopy(data, 11, circleData, 0, 4); // 旧版本协议
    return byteArrayToInt(circleData);
  }

  /**
   * 清理设备缓存
   * @return
   */
  public static byte[] requestResetCache() {
    return new byte[]{(byte) 0xA1,0x06,0x10, 0x00,0x00,0x00,0x00, 0x00,0x00,0x00,0x00, 0x00,0x00,0x00,0x00, 0x00,0x00,0x00,0x00, 0x55};
  }

  /**
   * 发送获取电量
   * @return
   */
  public static byte[] requestElectricity(){
    return new byte[]{(byte) 0xA1,0x02,0x01,0x00,0x55};
  }

  public static int analysisElectricity(byte[] data){
    if(data==null || data.length < 5){
      return 0;
    }
    return data[3];
  }

}
