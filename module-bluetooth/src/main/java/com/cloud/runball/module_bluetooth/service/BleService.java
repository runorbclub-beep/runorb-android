package com.cloud.runball.module_bluetooth.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.data.BleScanState;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.cloud.runball.module_bluetooth.R;
import com.cloud.runball.module_bluetooth.constant.ServiceNoticeConstant;
import com.cloud.runball.module_bluetooth.constant.ServiceSendConstant;
import com.cloud.runball.module_bluetooth.data.DeviceBallInfo;
import com.cloud.runball.module_bluetooth.data.event.BallInfo;
import com.cloud.runball.module_bluetooth.data.event.BallRunDetail;
import com.cloud.runball.module_bluetooth.data.event.MatchTimingInfo;
import com.cloud.runball.module_bluetooth.data.event.ServiceNoticeEvent;
import com.cloud.runball.module_bluetooth.data.event.ServiceSendEvent;
import com.cloud.runball.module_bluetooth.utils.BlePackDataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BleService extends Service {

  private DeviceBallInfo deviceBallInfo;

  // 服务UUID
  private final String UUID_SERVICE = "0000ae30-0000-1000-8000-00805f9b34fb";
  // 特性UUID - 读取
  private final String UUID_READ = "0000ae02-0000-1000-8000-00805f9b34fb";
  // 特性UUID - 写入
  private final String UUID_WRITE = "0000ae01-0000-1000-8000-00805f9b34fb";

  // 蓝牙连接成功后连接时长计时
  private ScheduledExecutorService timingExecutor = null;

  // 赛事本地计时
  private ScheduledExecutorService matchTimingExecutor = null;
  private int matchRunningTime = 0;

  // 是否摇动中
  private final AtomicBoolean isRunning = new AtomicBoolean(false);
  // 是否连接中
  private final AtomicBoolean isConnecting = new AtomicBoolean(false);

  // 设备摇动速度
  private int speed = 0;
  // 设备圈数
  private int circle = 0;
  // 设备连接时长
  private int totalTime = 0;
  // 设备摇动时长
  private int runningTime = 0;

  private final Handler handler = new Handler();
  // 请求延时时间
  private final int delayMillis = 100;

  private static final String CHANNEL = "gentleman";

  private int reCount = 0;

  private boolean isAdd = false;

  @Override
  public void onCreate() {
    super.onCreate();
    EventBus.getDefault().register(this);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d("BleService", "onStartCommand");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel notificationChannel = new NotificationChannel(CHANNEL, "ble_service", NotificationManager.IMPORTANCE_LOW);
      notificationChannel.enableLights(true);
//      notificationChannel.setLightColor(getColor(R.color.colorPrimary));
      notificationChannel.enableVibration(false);
      NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
      if (notificationManager != null) {
        notificationManager.createNotificationChannel(notificationChannel);
      }
    }

//    RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.item_notification);
//
//    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_CHANGE_CAMERA), 0);
//    remoteViews.setOnClickPendingIntent(R.id.change_camera, pendingIntent);
//
//    pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_CAPTURE), 0);
//    remoteViews.setOnClickPendingIntent(R.id.capture, pendingIntent);
//
//    pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_FINISH), 0);
//    remoteViews.setOnClickPendingIntent(R.id.finish, pendingIntent);

    Notification notification = new NotificationCompat.Builder(this, CHANNEL)
        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//        .setContent(remoteViews)
        .setContentTitle(getString(R.string.service_content_title))
//        .setContentText("运行")
        .setSmallIcon(R.drawable.ic_bluetooth)
        .build();

    if (Build.VERSION.SDK_INT < 34) {
      startForeground(1, notification);
    } else {
      startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);
    }
    return START_STICKY;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessage(ServiceSendEvent event) {
    Log.d("BleService", "onMessage - code = " + event.getCode());
    switch (event.getCode()) {
      case ServiceSendConstant.CODE_SCAN_OPEN: {
        scanDevice();
      } break;
      case ServiceSendConstant.CODE_SCAN_STOP: {
        // 判断是否正在搜索
        if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) {
          BleManager.getInstance().cancelScan();
        }
      } break;
      case ServiceSendConstant.CODE_CONNECT_DEVICE: {
        if (event.getData() == null) {
          return;
        }
        DeviceBallInfo deviceBallInfo = (DeviceBallInfo) event.getData();
        if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) {
          BleManager.getInstance().cancelScan();
        }

        if (this.deviceBallInfo != null) {
          BleManager.getInstance().disconnectAllDevice();
        }

        handler.postDelayed(() -> {
          this.deviceBallInfo = deviceBallInfo;
          connectDevice(this.deviceBallInfo.getBleDevice());
        }, delayMillis);
      } break;
      case ServiceSendConstant.CODE_CONNECT_FINISH: {
        if (this.deviceBallInfo == null) {
          return;
        }
        DeviceBallInfo deviceBallInfo = (DeviceBallInfo) event.getData();
        if (deviceBallInfo == null) {
          if (BleManager.getInstance().isConnected(this.deviceBallInfo.getBleDevice())) {
            BleManager.getInstance().disconnect(this.deviceBallInfo.getBleDevice());
          }
          return;
        }
        if (!deviceBallInfo.getBleDevice().getMac().equals(this.deviceBallInfo.getBleDevice().getMac())) {
          if (BleManager.getInstance().isConnected(this.deviceBallInfo.getBleDevice())) {
            BleManager.getInstance().disconnect(this.deviceBallInfo.getBleDevice());
          }
          return;
        }
      } break;
      case ServiceSendConstant.CODE_REQUEST_ELECTRICITY: {
        if (this.deviceBallInfo == null) {
          return;
        }
        if (BleManager.getInstance().isConnected(this.deviceBallInfo.getBleDevice())) {
          sendBluetoothData(this.deviceBallInfo.getBleDevice(), BlePackDataUtils.requestElectricity());
        }
      } break;
      case ServiceSendConstant.CODE_CIRCLE_CLEAR: {
        if (this.deviceBallInfo == null) {
          return;
        }
        if (BleManager.getInstance().isConnected(this.deviceBallInfo.getBleDevice())) {
          sendBluetoothData(this.deviceBallInfo.getBleDevice(), BlePackDataUtils.requestResetCache());
        }
      } break;
      case ServiceSendConstant.CODE_START_MATCH_TIMING: {
        startMatchTimingExecutor();
      } break;
      case ServiceSendConstant.CODE_CLOSE_MATCH_TIMING: {
        stopMatchTimingExecutor();
      } break;
    }
  }

  private void scanDevice() {
    if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) {
      Log.d("BleService", "already scanning");
      return;
    }
    BleManager.getInstance().scan(new BleScanCallback() {
      @Override
      // 会回到主线程，参数表示本次扫描动作是否开启成功。由于蓝牙没有打开，上一次扫描没有结束等原因，会造成扫描开启失败
      public void onScanStarted(boolean success) {
        Log.d("BleService", "onScanStarted - " + success);
        EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_SCAN_START));
      }

      @Override
      // 扫描过程中所有被扫描到的结果回调。由于扫描及过滤的过程是在工作线程中的，此方法也处于工作线程中。
      // 同一个设备会在不同的时间，携带自身不同的状态（比如信号强度等），出现在这个回调方法中，出现次数取决于周围的设备量及外围设备的广播间隔
      public void onLeScan(BleDevice bleDevice) {
        Log.d("BleService", "onLeScan");
      }

      @Override
      // 扫描过程中的所有过滤后的结果回调。与onLeScan区别之处在于：它会回到主线程；
      // 同一个设备只会出现一次；出现的设备是经过扫描过滤规则过滤后的设备
      public void onScanning(BleDevice bleDevice) {
        Log.d("BleService", "onScanning");
        if(bleDevice.getName()!=null && bleDevice.getName().startsWith("Run")) {
          DeviceBallInfo deviceBallInfo = new DeviceBallInfo();
          deviceBallInfo.setBleDevice(bleDevice);
          EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_SCAN_DEVICE, deviceBallInfo));
        }
      }

      @Override
      // 本次扫描时段内所有被扫描且过滤后的设备集合。它会回到主线程，相当于onScanning设备之和
      public void onScanFinished(List<BleDevice> scanResultList) {
        Log.d("BleService", "onScanFinished");
        EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_SCAN_FINISHED));
      }
    });
  }

  private void connectDevice(BleDevice bleDevice) {
    BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
      @Override
      public void onStartConnect() {
        Log.d("BleService", "onStartConnect");
        isRunning.set(false);
        isConnecting.set(false);
        EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_CONNECT_START));
      }
      @Override
      public void onConnectFail(BleDevice bleDevice, BleException exception) {
        Log.d("BleService", "onConnectFail - " + exception.getDescription());
        isRunning.set(false);
        isConnecting.set(false);

        if (reCount > 0) {
          EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_CONNECT_FAIL));
          deviceBallInfo = null;
        } else {
          handler.postDelayed(() -> {
            EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CONNECT_DEVICE, deviceBallInfo));
          }, 500);
        }
        reCount ++;
      }
      @Override
      public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
        Log.d("BleService", "onConnectSuccess - status = " + status);
        handler.postDelayed(() -> {
          sendBluetoothData(bleDevice, BlePackDataUtils.requestResetCache());
        }, delayMillis);
        isRunning.set(false);
        isConnecting.set(true);
        startTimingExecutor();
        EventBus.getDefault().post(new ServiceNoticeEvent<>(
            ServiceNoticeConstant.CODE_CONNECT_SUCCESS,
            new BallInfo(bleDevice.getName(), bleDevice.getMac())
        ));
        handler.postDelayed(() -> {
          openBluetoothNotifyListener(bleDevice);
        }, delayMillis * 2);

        reCount = 0;
      }
      @Override
      public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
        Log.d("BleService", "onDisConnected - status = " + status);
        isRunning.set(false);
        isConnecting.set(false);
        deviceBallInfo = null;
        gatt.disconnect();
        gatt.close();
        stopTimingExecutor();
        EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_CONNECT_FINISHED));

        reCount = 0;
      }
    });
  }

  private void openBluetoothNotifyListener(BleDevice bleDevice) {
    BleManager.getInstance().notify(bleDevice, UUID_SERVICE, UUID_READ, new BleNotifyCallback() {
      @Override
      public void onNotifySuccess() {
        // 打开通知操作成功
        Log.d("BleService", "onNotifySuccess");
        EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_NOTIFY_SUCCESS));
        handler.postDelayed(() -> {
          sendBluetoothData(bleDevice, BlePackDataUtils.requestElectricity());
        }, delayMillis * 3);
      }
      @Override
      public void onNotifyFailure(BleException exception) {
        // 打开通知操作失败
        Log.d("BleService", "onNotifyFailure - exception = " + exception.getDescription());
        isRunning.set(false);
        EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_NOTIFY_FAILURE));
      }
      @Override
      public void onCharacteristicChanged(byte[] data) {
        // 打开通知后，设备发过来的数据将在这里出现
        String text;
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
          sb.append(String.format("%02x ", b & 0xff));
        }
        text = sb.toString().toUpperCase();

        switch (BlePackDataUtils.getPackType(data)) {
          case BlePackDataUtils.RPM_SPEED:
//            int rpmSpeed = BlePackDataUtils.analysisRpmSpeed(data) / 10;
//            int speed1 = BlePackDataUtils.analysisSpeed1(data) / 10;
            int speed = BlePackDataUtils.analysisSpeed2(data) / 10;
            int circle = BlePackDataUtils.analysisCircle(data) + 28;
            if (isRunning.get()) {
              BleService.this.speed = speed;
              BleService.this.circle = circle;
//              Log.d("BleService", "onCharacteristicChanged rpmSpeed = " + rpmSpeed);
//              Log.d("BleService", "onCharacteristicChanged speed1 = " + speed1);
              Log.d("BleService", "onCharacteristicChanged speed = " + speed);
              Log.d("BleService", "onCharacteristicChanged circle = " + circle);
              if (speed == 0 && BleService.this.circle == circle) {
                isRunning.set(false);
                BleService.this.runningTime = 0;
                EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_NOTIFY_RUN_FINISH));
                new Handler().postDelayed(() -> {
                  sendBluetoothData(bleDevice, BlePackDataUtils.requestResetCache());
                }, 2000);
              }
            } else {
              if (speed >= 300) {
                BleService.this.speed = speed;
                BleService.this.circle = circle;
//                Log.d("BleService", "onCharacteristicChanged rpmSpeed = " + rpmSpeed);
//                Log.d("BleService", "onCharacteristicChanged speed1 = " + speed1);
                Log.d("BleService", "onCharacteristicChanged speed = " + speed);
                Log.d("BleService", "onCharacteristicChanged circle = " + circle);
                isRunning.set(true);
                EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_NOTIFY_RUN_START));
              }
            }
            break;
          case BlePackDataUtils.VER:
            Log.d("BleService", "onCharacteristicChanged");
            EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_NOTIFY_BALL_VER));
            break;
          case BlePackDataUtils.ELECTRICITY:
            int electricity = BlePackDataUtils.analysisElectricity(data);
            EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_NOTIFY_ELECTRICITY, electricity));
            if (electricity <= 20) {
              Toast.makeText(BleService.this, R.string.tip_electricity_not_enough, Toast.LENGTH_SHORT).show();
            }
            Log.d("BleService", "onCharacteristicChanged electricity = " + electricity);
            break;
          default:
            break;
        }
        Log.d("BleService", "text = " + text);
      }
    });
  }



  private void startMatchTimingExecutor() {
    stopMatchTimingExecutor();
    matchTimingExecutor = Executors.newScheduledThreadPool(1);
    matchTimingExecutor.scheduleAtFixedRate(() -> {
      matchRunningTime++;
      Log.d("BleService", "matchTimingExecutor matchRunningTime = " + matchRunningTime + ", isRunning = " + isRunning.get());
      MatchTimingInfo data = new MatchTimingInfo(matchRunningTime, isRunning.get());
      EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_NOTIFY_MATCH_TIME, data));
    }, 1, 1, TimeUnit.SECONDS);
  }

  private void stopMatchTimingExecutor() {
    if (matchTimingExecutor != null) {
      matchTimingExecutor.shutdownNow();
      matchTimingExecutor = null;
    }
    matchRunningTime = 0;
  }

  private void startTimingExecutor() {
    stopTimingExecutor();
    timingExecutor = Executors.newScheduledThreadPool(1);
    timingExecutor.scheduleAtFixedRate(() -> {
      if (!isConnecting.get()) {
        return;
      }
      if (isAdd) {
        isAdd = false;
      } else {
        isAdd = true;
      }
      EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_NOTIFY_TOTAL_TIME, totalTime));
      if (!isAdd) {
        totalTime++;
      }

      if (totalTime % 10 == 0) {
        sendBluetoothData(this.deviceBallInfo.getBleDevice(), BlePackDataUtils.requestElectricity());
      }

      if (isRunning.get()) {
        BallRunDetail ballDetail = new BallRunDetail(speed, circle, runningTime);
        Log.d("BleService", "startTiming ballDetail speed = " + speed + ", circle = " + circle + ", runningTime = " + runningTime);
        if (!isAdd) {
          runningTime++;
        }
        EventBus.getDefault().post(new ServiceNoticeEvent<>(ServiceNoticeConstant.CODE_NOTIFY_RUNNING, ballDetail));
      }
    }, 500, 500, TimeUnit.MILLISECONDS);
  }

  private void stopTimingExecutor() {
    if (timingExecutor != null) {
      timingExecutor.shutdownNow();
      timingExecutor = null;
    }
    runningTime = 0;
    totalTime = 0;
  }

  /**
   * 关闭设备通知
   * @param bleDevice
   */
  private void closeBluetoothNotifyListener(BleDevice bleDevice) {
    BleManager.getInstance().stopNotify(bleDevice, UUID_SERVICE, UUID_READ);
  }

  private void sendBluetoothData(BleDevice bleDevice, byte[] message) {
//    HexUtil.hexStringToBytes(message)
    BleManager.getInstance().write(bleDevice, UUID_SERVICE, UUID_WRITE, message, new BleWriteCallback() {
      @Override
      public void onWriteSuccess(int current, int total, byte[] justWrite) {
        Log.d("BleService", "onWriteSuccess - " + HexUtil.formatHexString(justWrite));
      }
      @Override
      public void onWriteFailure(BleException exception) {
        Log.d("BleService", "onWriteFailure - exception" + exception.getDescription());
      }
    });
  }

  @Override
  public void onDestroy() {
    stopForeground(true);
    Log.d("BleService", "onDestroy");
    super.onDestroy();
    BleManager.getInstance().destroy();
    EventBus.getDefault().unregister(this);
  }

}
