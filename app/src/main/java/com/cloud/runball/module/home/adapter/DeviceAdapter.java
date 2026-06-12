package com.cloud.runball.module.home.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.bean.DeviceNicknameData;
import com.cloud.runball.model.DeviceInfo;
import com.cloud.runball.model.DeviceWithServerModel;
import com.cloud.runball.utils.AppLogger;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private boolean onBind;

    private List<DeviceInfo> deviceInfos;
    private boolean canChecked;
    private Context mContext;
    private OnItemConnectClickListener callback;
    private List<DeviceWithServerModel> deviceNicknameList;


    public DeviceAdapter(Context context, List<DeviceInfo> deviceInfo) {
        this.mContext = context;
        this.deviceInfos = deviceInfo;
        deviceNicknameList = SPUtils.getData(context, "bleDeviceList", DeviceWithServerModel.class);
    }

    public DeviceAdapter(Context context, List<DeviceInfo> deviceInfo, boolean canChecked) {
        this.mContext = context;
        this.deviceInfos = deviceInfo;
        this.canChecked = canChecked;
        deviceNicknameList = SPUtils.getData(context, "bleDeviceList", DeviceWithServerModel.class);
    }

    public void setDevices(List<DeviceInfo> deviceInfo) {
        this.deviceInfos = deviceInfo;
    }

    public void OnItemConnectClickListener(OnItemConnectClickListener listener) {
        this.callback = listener;
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_device_info_name;
        TextView tv_device_info_connected;
        TextView tvBelong;
        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            tv_device_info_name = itemView.findViewById(R.id.tv_device_info_name);
            tv_device_info_connected = itemView.findViewById(R.id.tv_device_info_connected);
            tvBelong = itemView.findViewById(R.id.tvBelong);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_device_item_1, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        //是否已经连接
        viewHolder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onBind) {
                    if (callback != null) {
                        int pos = viewHolder.getLayoutPosition() - 1;
                        AppLogger.d("--------------onClick---------------" + deviceInfos.toString() + ";pos=" + pos);
                        if (pos <= deviceInfos.size()-1 && pos >= 0) {
                            notifyDataSetChanged();
                            DeviceInfo data = deviceInfos.get(pos);
                            callback.onItemConnectClick(data);
                        }
                    }
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        onBind = true;
        DeviceInfo device = deviceInfos.get(position);
        holder.tv_device_info_name.setText(device.getName());
        if (deviceNicknameList != null) {
            for (int i = 0; i < deviceNicknameList.size(); i++) {
                DeviceWithServerModel item = deviceNicknameList.get(i);
                if (device.getName().equals(item.getDevice_name())) {
                    if (!TextUtils.isEmpty(item.getName())) {
                        holder.tv_device_info_name.setText(item.getName());
                    }
                    holder.tvBelong.setVisibility(View.VISIBLE);
                }
            }
        }


        holder.tv_device_info_name.setTag(device.getMac());

        holder.tv_device_info_connected.setText(device.getConnected() ? mContext.getResources().getString(R.string.state_connected) :
                mContext.getResources().getString(R.string.state_disconnected));
        onBind = false;
    }

    @Override
    public int getItemCount() {
        return deviceInfos.size();
    }

    public interface OnItemConnectClickListener {
        void onItemConnectClick(DeviceInfo data);
    }
}
