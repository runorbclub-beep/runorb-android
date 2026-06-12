package com.cloud.runball.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.constant.PlayingDataConstant;
import com.cloud.runball.service.sql.entity.PlayInfo;
import com.cloud.runball.utils.BallUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SysnDataDialog extends BaseDialog {

  private TextView tvCancel;
  private TextView tvSubmit;

  private LinearLayout layList;
  private LinearLayout layEmpty;

  private RecyclerView rvDataList;
  private final List<PlayInfo> data = new ArrayList<>();
  private OnClickCallback onClickCallback;

  private DecimalFormat mDecimalFormat = new DecimalFormat("0.000");

  private int status;
  private final int STATUS_EMPTY = 1;
  private final int STATUS_OK = 2;
  private final int STATUS_WORK = 3;

  public SysnDataDialog(Context context) {
    super(context, R.layout.dialog_sysn_data);
  }

  @Override
  protected void onContentView(View contentView) {
    tvCancel = contentView.findViewById(R.id.tvCancel);
    tvCancel.setOnClickListener(v -> {
      onClickCallback.onClose(this);
    });
    tvSubmit = contentView.findViewById(R.id.tvSubmit);
    tvSubmit.setOnClickListener(v -> {
      if (onClickCallback != null) {
        if (status == STATUS_EMPTY) {
          onClickCallback.onClose(this);
        } else if (status == STATUS_OK) {
          onClickCallback.onClose(this);
        } else if (status == STATUS_WORK) {
          for (int i = 0; i <  data.size(); i++) {
            PlayInfo item = data.get(i);
            if (
                PlayingDataConstant.Update.STATUS_UPDATE_INCOMPLETE.equals(item.getUploadStatus())
                    || PlayingDataConstant.Update.STATUS_UPDATE_FAIL.equals(item.getUploadStatus())
                    || PlayingDataConstant.Update.STATUS_UPDATE_DEFAULT.equals(item.getUploadStatus())) {
              onClickCallback.onSysn(this, item, i);
            }
          }
        }
      }
    });

    rvDataList = contentView.findViewById(R.id.rvDataList);
    SysnAdapter adapter = new SysnAdapter();
    rvDataList.setAdapter(adapter);

    layList = contentView.findViewById(R.id.layList);
    layEmpty = contentView.findViewById(R.id.layEmpty);
  }

  public void updateItem(int position, String status) {
    this.data.get(position).setUploadStatus(status);
    SysnAdapter adapter = (SysnAdapter) rvDataList.getAdapter();
    if (adapter != null) {
      adapter.notifyItemChanged(position);
    }
    int needUpdateCount = 0;
    int okCount = 0;
    for (PlayInfo item : data) {
      if (
          PlayingDataConstant.Update.STATUS_UPDATE_INCOMPLETE.equals(item.getUploadStatus())
              || PlayingDataConstant.Update.STATUS_UPDATE_DEFAULT.equals(item.getUploadStatus())
              || PlayingDataConstant.Update.STATUS_UPDATE_FAIL.equals(item.getUploadStatus())
      ) {
        needUpdateCount++;
      } else if (PlayingDataConstant.Update.STATUS_UPDATE_UPLOADING.equals(item.getUploadStatus())) {
        item.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
        needUpdateCount++;
      } else if (PlayingDataConstant.Update.STATUS_UPDATE_SUCCESS.equals(item.getUploadStatus())) {
        okCount++;
      }
    }
    if (needUpdateCount > 0) {
      this.status = STATUS_WORK;
    } else if (okCount > 0) {
      this.status = STATUS_OK;
    }

    if (this.status == STATUS_EMPTY) {
      layEmpty.setVisibility(View.VISIBLE);
      rvDataList.setVisibility(View.GONE);
      tvCancel.setVisibility(View.GONE);
      tvSubmit.setVisibility(View.VISIBLE);
      tvSubmit.setText(dialog.getContext().getText(R.string.btn_done));
    } else if (this.status == STATUS_OK) {
      layEmpty.setVisibility(View.GONE);
      rvDataList.setVisibility(View.VISIBLE);
      tvCancel.setVisibility(View.GONE);
      tvSubmit.setVisibility(View.VISIBLE);
      tvSubmit.setText(dialog.getContext().getText(R.string.btn_back));
    } else if (this.status == STATUS_WORK) {
      layEmpty.setVisibility(View.GONE);
      rvDataList.setVisibility(View.VISIBLE);
      tvCancel.setVisibility(View.VISIBLE);
      tvCancel.setText(dialog.getContext().getText(R.string.btn_back));
      tvSubmit.setVisibility(View.VISIBLE);
      tvSubmit.setText(dialog.getContext().getText(R.string.btn_synchronous));
    }
  }

  public void updateData(List<PlayInfo> data) {
    if (data == null || data.size() == 0) {
      this.data.clear();
      status = STATUS_EMPTY;
      SysnAdapter adapter = (SysnAdapter) rvDataList.getAdapter();
      if (adapter != null) {
        adapter.notifyDataSetChanged();
      }
      layEmpty.setVisibility(View.VISIBLE);
      layList.setVisibility(View.GONE);
      status = STATUS_EMPTY;
      tvCancel.setVisibility(View.GONE);
      tvSubmit.setVisibility(View.VISIBLE);
      tvSubmit.setText(dialog.getContext().getText(R.string.btn_done));
      return;
    }
    this.data.clear();
    this.data.addAll(data);
    int needUpdateCount = 0;
    int okCount = 0;
    for (PlayInfo item : data) {
      if (
          PlayingDataConstant.Update.STATUS_UPDATE_INCOMPLETE.equals(item.getUploadStatus()) ||
              PlayingDataConstant.Update.STATUS_UPDATE_DEFAULT.equals(item.getUploadStatus()) ||
              PlayingDataConstant.Update.STATUS_UPDATE_FAIL.equals(item.getUploadStatus())
      ) {
        needUpdateCount++;
      } else if (PlayingDataConstant.Update.STATUS_UPDATE_UPLOADING.equals(item.getUploadStatus())) {
        item.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
        needUpdateCount++;
      } else if (PlayingDataConstant.Update.STATUS_UPDATE_SUCCESS.equals(item.getUploadStatus())) {
        okCount++;
      }
    }
    if (needUpdateCount > 0) {
      status = STATUS_WORK;
    } else if (okCount > 0) {
      status = STATUS_OK;
    }
    SysnAdapter adapter = (SysnAdapter) rvDataList.getAdapter();
    if (adapter != null) {
      adapter.notifyDataSetChanged();
    }

    if (status == STATUS_EMPTY) {
      layEmpty.setVisibility(View.VISIBLE);
      layList.setVisibility(View.GONE);
      tvCancel.setVisibility(View.GONE);
      tvSubmit.setVisibility(View.VISIBLE);
      tvSubmit.setText(dialog.getContext().getText(R.string.btn_done));
    } else if (status == STATUS_OK) {
      layEmpty.setVisibility(View.GONE);
      layList.setVisibility(View.VISIBLE);
      tvCancel.setVisibility(View.GONE);
      tvSubmit.setVisibility(View.VISIBLE);
      tvSubmit.setText(dialog.getContext().getText(R.string.btn_back));
    } else if (status == STATUS_WORK) {
      layEmpty.setVisibility(View.GONE);
      layList.setVisibility(View.VISIBLE);
      tvCancel.setVisibility(View.VISIBLE);
      tvCancel.setText(dialog.getContext().getText(R.string.btn_back));
      tvSubmit.setVisibility(View.VISIBLE);
      tvSubmit.setText(dialog.getContext().getText(R.string.btn_synchronous));
    }
  }

  public void setOnClickCallback(OnClickCallback callback) {
    this.onClickCallback = callback;
  }

  private class SysnAdapter extends RecyclerView.Adapter<SysnViewHolder> {

    @NonNull
    @Override
    public SysnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_upload, parent, false);
      return new SysnViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SysnViewHolder holder, int position) {
      switch (data.get(position).getUploadStatus()) {
        case PlayingDataConstant.Update.STATUS_UPDATE_INCOMPLETE:
          holder.ivStatus.setImageResource(R.mipmap.spot_grey);
          break;
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
      holder.tvCreateTime.setText(new SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(data.get(position).getStopTime() * 1000)));
      float meter = data.get(position).getDistance() / 1000;
      holder.tvDistance.setText(mDecimalFormat.format(meter));
      holder.tvMaxRpm.setText(data.get(position).getMaxSpeed() + "");
      holder.tvTime.setText(TimeUtils.formatDuration((int) (data.get(position).getStopTime() - data.get(position).getStartTime())));
      holder.itemView.setOnClickListener(view -> {
        if (onClickCallback != null) {
          onClickCallback.onClick(SysnDataDialog.this, data.get(position), position);
        }
      });
    }

    @Override
    public int getItemCount() {
      return data.size();
    }
  }

  private static class SysnViewHolder extends RecyclerView.ViewHolder {

    ImageView ivStatus;
    TextView tvCreateTime, tvMaxRpm, tvDistance, tvTime;

    public SysnViewHolder(@NonNull View itemView) {
      super(itemView);
      ivStatus = itemView.findViewById(R.id.ivStatus);
      tvCreateTime = itemView.findViewById(R.id.tvCreateTime);
      tvMaxRpm = itemView.findViewById(R.id.tvMaxRpm);
      tvDistance = itemView.findViewById(R.id.tvDistance);
      tvTime = itemView.findViewById(R.id.tvTime);
    }
  }

  public interface OnClickCallback {
    void onClick(SysnDataDialog dialog, PlayInfo itemData, int position);
    void onSysn(SysnDataDialog dialog, PlayInfo itemData, int position);
    void onClose(SysnDataDialog dialog);
  }


}
