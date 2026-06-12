package com.cloud.runball.service.websocket;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.service.websocket
 * @ClassName: WebSocketEvent
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/1 10:22
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/1 10:22
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class WebSocketEvent {
    private String message;

    public WebSocketEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
