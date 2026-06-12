package com.cloud.runball.service.websocket;

import android.os.Handler;
import android.util.Log;

import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.service.websocket
 * @ClassName: WebSocketServiceManager
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/1 10:23
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/1 10:23
 * @UpdateRemark: 更新说明  https://www.jianshu.com/p/6ff879706d9c?utm_campaign=hugo
 * @Version: 1.0
 */
public class WebSocketServiceManager {

  private final static String TAG = WebSocketServiceManager.class.getSimpleName();
  private final static int GRAY_SERVICE_ID = 1001;
  //连接断开或者连接错误立即重连
  private static final long CLOSE_RECON_TIME = 100;

  public JWebSocketClient client;

  static WebSocketServiceManager instance;

  private WebSocketServiceManager() {

  }

  public static WebSocketServiceManager getInstance() {
    synchronized (WebSocketServiceManager.class) {
      if (null == instance) {
        instance = new WebSocketServiceManager();
      }
    }
    return instance;
  }


  Map<String,String> httpHeaders=new HashMap<String,String>();
  /**
   * 初始化websocket连接
   */
  public void initSocketClient(Map<String,String> httpHeaders) {
    String url = Constant.getWsUrl();

    this.httpHeaders.clear();
    for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {
      this.httpHeaders.put(entry.getKey(),entry.getValue());
    }

    URI uri = URI.create(url);
    client = new JWebSocketClient(uri,this.httpHeaders) {
      @Override
      public void onMessage(String message) {
        //message就是接收到的消息
        AppLogger.d("PRETTY_LOGGER WebSocketService --- 收到的消息：message=" + message);

        String serviceEvent="";
        String serviceData="";
        try{
          JSONObject msgObject=new JSONObject(message);
          msgObject.optInt("code",0);
          if(msgObject.optJSONObject("data")!=null){
            serviceEvent=msgObject.optJSONObject("data").optString("event");
          }else if(msgObject.optString("event").equalsIgnoreCase("connection_success")){
            serviceEvent="connection_success";
          }
          serviceData=message;
        }catch (Exception ex){
          ex.printStackTrace();
        }

        if(message.startsWith("42[\"|error|\"") || message.startsWith("[\"|error|\"")){
          EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_PKError, serviceData));
        }else{
          if(serviceEvent.equalsIgnoreCase("pkListChange")){
            EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_PKListChange, serviceData));
          }else if(serviceEvent.equalsIgnoreCase("pkStart")){
            Log.d("hhhh_pkStart", serviceData);
            EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_PKStart, serviceData));
          }else if(serviceEvent.equalsIgnoreCase("pkResult")){
            Log.d("hhhh_pkResult", serviceData);
            EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_PKResult, serviceData));
          }else if(serviceEvent.equalsIgnoreCase("between_play")){
            EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_PKBetween, serviceData));
          }else if(serviceEvent.equalsIgnoreCase("bind_again")){
            EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_bind_again, serviceData));
          }else if(serviceEvent.equalsIgnoreCase("connection_success")){
            Log.d("hhhh_connection_success", serviceData);
            //连接成功
            EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_CONNECT_SUCCESS, serviceData));
          }
        }

        //AppLogger.d("WebSocketService收到的消息：serviceEvent" + serviceEvent+";serviceData="+serviceData);
      }

      @Override
      public void onOpen(ServerHandshake handShakeData) {
        AppLogger.d("WebSocket 连接成功");
        mHandler.removeCallbacks(heartBeatRunnable);
        mHandler.postDelayed(heartBeatRunnable, CLOSE_RECON_TIME);
      }

      @Override
      public void onClose(int code, String reason, boolean remote) {
        AppLogger.d("onClose() 连接断开_reason：" + reason);
        mHandler.removeCallbacks(heartBeatRunnable);
        if(!closeSelf){
          mHandler.postDelayed(heartBeatRunnable, CLOSE_RECON_TIME* 100);
        }
      }

      @Override
      public void onError(Exception ex) {
        AppLogger.d("onError() 连接出错：" + ex.getMessage());
        mHandler.removeCallbacks(heartBeatRunnable);
        mHandler.postDelayed(heartBeatRunnable, CLOSE_RECON_TIME * 100);
      }
    };
    connect();
  }

  /**
   * 连接WebSocket
   */
  private void connect() {
    new Thread() {
      @Override
      public void run() {
        try {
          //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
          client.connectBlocking();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }.start();
  }

  /**
   * 发送消息
   */
  public void sendMsg(String msg) {
    if (null != client && client.isOpen()) {
      AppLogger.d("PRETTY_LOGGER WebSocketService --- 发送的消息：message=" + msg);
      try {
        client.send(msg);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  //主动断开连接
  private boolean closeSelf=false;
  /**
   * 断开连接
   */
  public void closeConnect() {
    closeSelf=true;
    mHandler.removeCallbacks(heartBeatRunnable);
    mHandler.removeCallbacksAndMessages(null);
    try {
      if (null != client) {
        client.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      client = null;
    }
  }

  //    -------------------------------------WebSocket心跳检测------------------------------------------------
  //每隔10秒进行一次对长连接的心跳检测
  private static final long HEART_BEAT_RATE = 30 * 1000;

  private boolean isBreakLine=false;

  private Handler mHandler = new Handler();
  private Runnable heartBeatRunnable = new Runnable() {
    @Override
    public void run() {
      if (client != null) {
        if (client.isClosed()) {
          isBreakLine=true;
          reconnectWs();
          AppLogger.d("心跳包检测WebSocket连接状态：已关闭"+System.currentTimeMillis());
        } else if (client.isOpen()) {
          AppLogger.d("心跳包检测WebSocket连接状态：已连接"+System.currentTimeMillis());
          //需要再判断从断开连接到已经连接的状态
          if(isBreakLine){
            EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_PKConnected,"connected"));
            isBreakLine=false;
          }
        } else {
          AppLogger.d("心跳包检测WebSocket连接状态：已断开"+System.currentTimeMillis());
        }
        //发送心跳
        sendMsg("{\"data\":\"999\"}");
      } else {
        //如果client已为空，重新初始化连接
        initSocketClient(httpHeaders);
        AppLogger.d("心跳包检测WebSocket连接状态：client已为空，重新初始化连接");
      }
      //每隔一定的时间，对长连接进行一次心跳检测
      mHandler.postDelayed(this, HEART_BEAT_RATE);
    }
  };

  public boolean isOpen(){
    if (client != null) {
      closeSelf=false;
      return client.isOpen();
    }
    return false;
  }

  /**
   * 开启重连
   */
  private void reconnectWs() {
    mHandler.removeCallbacks(heartBeatRunnable);
    mHandler.removeCallbacksAndMessages(null);
    new Thread() {
      @Override
      public void run() {
        try {
          client.reconnectBlocking();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }.start();
  }
}
