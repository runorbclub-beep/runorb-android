package com.cloud.runball.service.websocket;

import android.os.Build;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

import javax.net.ssl.SSLParameters;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: WebSocketClient
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/1 10:20
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/1 10:20
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class JWebSocketClient extends WebSocketClient {

    public JWebSocketClient(URI serverUri, Map<String,String> httpHeaders) {
        super(serverUri, new Draft_6455(),httpHeaders);
    }

    public JWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    /**
     * If you want to target Android API lower 24, you should do the following:
     * @param sslParameters
     */
    @Override
    protected void onSetSSLParameters(SSLParameters sslParameters) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                super.onSetSSLParameters(sslParameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        //super.onSetSSLParameters(sslParameters);
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }
}
