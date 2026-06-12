package com.cloud.runball.model;

/**
 * @author ns467
 */
public class BasicResponse<T> {
    protected int code;
    protected T data;
    protected String msg;

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
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public boolean isSuccessful() {
        return code == 1;
    }


    @Override
    public String toString() {
        return "BaseDataModel{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
