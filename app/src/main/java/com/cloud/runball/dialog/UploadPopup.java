package com.cloud.runball.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.constant.PlayingDataConstant;
import com.cloud.runball.service.sql.entity.PlayInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/5 17:08
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/5 17:08
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class UploadPopup {

  private List<PlayInfo> data = new ArrayList<>();

  static UploadPopup singleton;
  PopupWindow popWindow;
  private RecyclerView rvUploadList;
  private OnClickCallback onClickCallback;

  public static UploadPopup self(){
    if (singleton == null) {
      synchronized (UploadPopup.class) {
        if (singleton == null) {
          singleton = new UploadPopup();
        }
      }
    }
    return singleton;
  }

  public void reset(List<PlayInfo> data) {
    this.data.clear();
    if (data != null) {
      this.data.addAll(data);
    }
    if (rvUploadList != null && rvUploadList.getAdapter() != null) {
      rvUploadList.getAdapter().notifyDataSetChanged();
    }
  }

  public UploadPopup build(Context context, OnClickCallback onClickCallback) {
    if(popWindow != null){
      popWindow.dismiss();
      popWindow = null;
    }
    this.onClickCallback = onClickCallback;

    View contentView = LayoutInflater.from(context).inflate(R.layout.popup_upload, null);
    rvUploadList = contentView.findViewById(R.id.rvUploadList);
    rvUploadList.setAdapter(new UploadAdapter());

    popWindow = new PopupWindow(contentView);
    popWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
    popWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    popWindow.setFocusable(true);
    popWindow.setTouchInterceptor((v, event) -> {
      popWindow=null;
      return false;
    });
    return this;
  }

  public void show(View view, List<PlayInfo> data) {
    this.data.clear();
    if (data != null) {
      this.data.addAll(data);
    }
    if (rvUploadList.getAdapter() != null) {
      rvUploadList.getAdapter().notifyDataSetChanged();
    }
//    popWindow.showAsDropDown(view);

    int[] arr = new int[2];
    view.getLocationOnScreen(arr);
    popWindow.showAtLocation(view, Gravity.TOP, 0, arr[1] + view.getHeight());
  }

  public void dismiss(){
    if(popWindow!=null){
      popWindow.dismiss();
      popWindow=null;
    }
  }

  class UploadAdapter extends RecyclerView.Adapter<UploadViewHolder> {

    @NonNull
    @Override
    public UploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_upload, parent, false);
      return new UploadViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadViewHolder holder, int position) {
      switch (data.get(position).getUploadStatus()) {
        case PlayingDataConstant.Update.STATUS_UPDATE_DEFAULT:
          holder.ivStatus.setImageResource(R.mipmap.spot_grey);
          break;
        case PlayingDataConstant.Update.STATUS_UPDATE_UPLOADING:
          holder.ivStatus.setImageResource(R.mipmap.spot_yellow);
          break;
        case PlayingDataConstant.Update.STATUS_UPDATE_SUCCESS:
          holder.ivStatus.setImageResource(R.mipmap.popup_upload_ok);
          break;
        case PlayingDataConstant.Update.STATUS_UPDATE_FAIL:
          holder.ivStatus.setImageResource(R.mipmap.popup_upload_erorr);
          break;
      }
      holder.tvCreateTime.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(data.get(position).getStopTime()*1000)));
      holder.tvDistance.setText(data.get(position).getDistance() + "");
      holder.tvMaxRpm.setText(data.get(position).getMaxSpeed() + "");
      holder.tvTime.setText(TimeUtils.formatDuration((int) (data.get(position).getStopTime() - data.get(position).getStartTime())));
      holder.itemView.setOnClickListener(view -> {
        if (onClickCallback != null) {
          onClickCallback.onClick(data.get(position));
        }
      });
    }

    @Override
    public int getItemCount() {
      return data == null ? 0 : data.size();
    }
  }

  private class UploadViewHolder extends RecyclerView.ViewHolder {

    ImageView ivStatus;
    TextView tvCreateTime, tvMaxRpm, tvDistance, tvTime;

    public UploadViewHolder(@NonNull View itemView) {
      super(itemView);
      ivStatus = itemView.findViewById(R.id.ivStatus);
      tvCreateTime = itemView.findViewById(R.id.tvCreateTime);
      tvMaxRpm = itemView.findViewById(R.id.tvMaxRpm);
      tvDistance = itemView.findViewById(R.id.tvDistance);
      tvTime = itemView.findViewById(R.id.tvTime);
    }
  }

  public interface OnClickCallback {
    void onClick(PlayInfo itemData);
  }

}
