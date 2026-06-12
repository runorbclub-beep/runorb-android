package com.cloud.runball.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author ns467
 */
public class PushReceiver extends BroadcastReceiver {

    final String ACTION="com.cloud.runball";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equalsIgnoreCase(ACTION)){
            // 获取推送消息数据
            String message = intent.getStringExtra("com.avoscloud.Data");
            String channel = intent.getStringExtra("com.avoscloud.Channel");
            System.out.println("message=" + message + ", channel=" + channel);
        }

    }
}
