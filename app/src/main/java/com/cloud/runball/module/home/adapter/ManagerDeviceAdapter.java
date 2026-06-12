package com.cloud.runball.module.home.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.App;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.bean.DeviceNicknameData;
import com.cloud.runball.model.DeviceWithServerModel;
import com.cloud.runball.module_bluetooth.utils.BleUtils;
import com.cloud.runball.utils.AppLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: ManagerDeviceAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/3/29 16:11
 * @UpdateUser: zhd
 * @UpdateDate: 2021/3/29 16:11
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ManagerDeviceAdapter extends RecyclerView.Adapter<ManagerDeviceAdapter.ViewHolder> {
    private boolean onBind;

    private List<DeviceWithServerModel> deviceInfos=new ArrayList<>();
    private boolean canChecked;
    private Context mContext;
    private ManagerDeviceAdapter.OnItemBindClickListener callback;
    private ManagerDeviceAdapter.OnItemCheckedClickListener checkedClickListener;
    private OnItemChangeNicknameClickListener onItemChangeNicknameClickListener;

    public ManagerDeviceAdapter(Context context, List<DeviceWithServerModel> deviceInfo) {
        this.mContext = context;
        this.deviceInfos = deviceInfo;
    }

    public ManagerDeviceAdapter(Context context, List<DeviceWithServerModel> deviceInfo, boolean canChecked) {
        this.mContext = context;
        this.deviceInfos = deviceInfo;
        this.canChecked = canChecked;
    }

    public void setDevices(List<DeviceWithServerModel> deviceInfo){
        this.deviceInfos = deviceInfo;
        this.notifyDataSetChanged();
    }

    public void OnItemBindClickListener(ManagerDeviceAdapter.OnItemBindClickListener listener){
        this.callback=listener;
    }

    public void OnItemCheckedClickListener(ManagerDeviceAdapter.OnItemCheckedClickListener listener){
        this.checkedClickListener=listener;
    }

    public void setOnItemChangeNicknameClickListener(OnItemChangeNicknameClickListener listener) {
        this.onItemChangeNicknameClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox chbSelected;
        TextView tvDeviceInfoName;
        TextView tvDeviceNickname;
        TextView tvChangeNickname;
        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            chbSelected = itemView.findViewById(R.id.chbSelected);
            chbSelected.setVisibility(canChecked ? View.VISIBLE : View.INVISIBLE);
            tvDeviceInfoName = itemView.findViewById(R.id.tv_device_info_name);
            tvChangeNickname = itemView.findViewById(R.id.tvChangeNickname);
            tvDeviceNickname = itemView.findViewById(R.id.tv_device_nickname);
        }
    }


    @NonNull
    @Override
    public ManagerDeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_device_item, parent, false);
        final ManagerDeviceAdapter.ViewHolder viewHolder = new ManagerDeviceAdapter.ViewHolder(view);

        //是否已经绑定
        viewHolder.tvChangeNickname.setOnClickListener(v -> {
            if(!onBind) {
                if(onItemChangeNicknameClickListener!=null){
                    int pos = viewHolder.getLayoutPosition()-1;
                    AppLogger.d("--------------onClick---------------"+deviceInfos.toString()+";pos="+pos);
                    if(pos<=deviceInfos.size()-1 && pos>=0){
                        //notifyDataSetChanged();
                        DeviceWithServerModel data = deviceInfos.get(pos);
                        onItemChangeNicknameClickListener.onItemChangeClick(pos, data);
                    }
                }
            }
        });

        //单选框
        viewHolder.chbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!onBind) {
                    int pos = viewHolder.getLayoutPosition()-1;
                    AppLogger.d("--------------onCheckedChanged---------------"+deviceInfos.toString()+";pos="+pos);
                    if(pos<=deviceInfos.size()-1 && pos>=0){
                        deviceInfos.get(pos).setChecked(isChecked);
                        //notifyDataSetChanged();
                        if(checkedClickListener!=null){
                            checkedClickListener.onItemCheckedClick(deviceInfos.get(pos));
                        }
                    }
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ManagerDeviceAdapter.ViewHolder holder, int position) {
        onBind = true;
        DeviceWithServerModel device = deviceInfos.get(position);

        String name = "";
        BluetoothDevice bluetoothDevice = BleUtils.getConnectedDevice();
        if (bluetoothDevice != null) {
            name = bluetoothDevice.getName();
        }
        SpannableString spannableString;
        if (device.getDevice_uid().equals(name)) {
            String appendText = holder.itemView.getResources().getString(R.string.conn_device_self);
            spannableString = new SpannableString(device.getDevice_name() + appendText);
            spannableString.setSpan(
                    new ForegroundColorSpan(holder.itemView.getResources().getColor(R.color.conn)),
                    device.getDevice_name().length(),
                    spannableString.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        } else {
            String appendText = holder.itemView.getResources().getString(R.string.no_conn_device_self);
            spannableString = new SpannableString(device.getDevice_name() + appendText);
            spannableString.setSpan(
                    new ForegroundColorSpan(holder.itemView.getResources().getColor(R.color.no_conn)),
                    device.getDevice_name().length(),
                    spannableString.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        holder.tvDeviceInfoName.setText(spannableString);
        // 动态设置文字，需重新设置跑马灯，才会有跑马灯效果
        holder.tvDeviceInfoName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.tvDeviceInfoName.setSingleLine(true);
        holder.tvDeviceInfoName.setSelected(true);
        holder.tvDeviceInfoName.setFocusable(true);
        holder.tvDeviceInfoName.setFocusableInTouchMode(true);

        holder.tvDeviceInfoName.setTag(device.getDevice_uid());

        String nickname = device.getName();
        if (TextUtils.isEmpty(nickname)) {
            holder.tvDeviceNickname.setTextColor(holder.itemView.getResources().getColor(R.color.no_nickname));
            holder.tvDeviceNickname.setText(holder.itemView.getResources().getString(R.string.no_nickname));
        } else {
            holder.tvDeviceNickname.setTextColor(holder.itemView.getResources().getColor(R.color.device_nickname));
            holder.tvDeviceNickname.setText(device.getName());
        }
        // 动态设置文字，需重新设置跑马灯，才会有跑马灯效果
        holder.tvDeviceNickname.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.tvDeviceNickname.setSingleLine(true);
        holder.tvDeviceNickname.setSelected(true);
        holder.tvDeviceNickname.setFocusable(true);
        holder.tvDeviceNickname.setFocusableInTouchMode(true);

        holder.chbSelected.setChecked(device.getChecked());
        onBind = false;
    }

    @Override
    public int getItemCount() {
        return deviceInfos.size();
    }

    public interface OnItemBindClickListener {
        void onItemBindClick(int position, DeviceWithServerModel data);
    }

    public interface OnItemCheckedClickListener {
        void onItemCheckedClick(DeviceWithServerModel data);
    }

    public interface OnItemChangeNicknameClickListener {
        void onItemChangeClick(int position, DeviceWithServerModel data);
    }
}
