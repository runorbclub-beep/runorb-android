package com.cloud.runball.module_bluetooth.data.event;

public class ServiceSendEvent<T> {

  private int code;
  private T data;

  public ServiceSendEvent(int code) {
    this.code = code;
  }

  public ServiceSendEvent(int code, T data) {
    this.code = code;
    this.data = data;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }
}
