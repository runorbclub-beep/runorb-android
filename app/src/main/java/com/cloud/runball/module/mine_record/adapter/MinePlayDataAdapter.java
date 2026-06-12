package com.cloud.runball.module.mine_record.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.constant.ModuleConstant;
import com.cloud.runball.module.mine_record.entity.MinePlayDataInfo;

import java.util.List;

public class MinePlayDataAdapter extends RecyclerView.Adapter<MinePlayDataAdapter.ViewHolder> {

  private List<MinePlayDataInfo> data;
  private OnItemClickListener listener;

  public MinePlayDataAdapter(List<MinePlayDataInfo> data) {
    this.data = data;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_mine_play_data, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    MinePlayDataInfo itemData = data.get(position);
    holder.tvDistance.setText(holder.itemView.getContext().getString(R.string.lbl_rank_distance) + itemData.getDistanceFormat());
    if (itemData.getDistanceUnit() != null) {
      holder.tvDistanceUnit.setVisibility(View.VISIBLE);
      holder.tvDistanceUnit.setText(itemData.getDistanceUnit());
    } else {
      holder.tvDistanceUnit.setVisibility(View.GONE);
    }
    String source = "";
    switch (itemData.getSource()) {
      case ModuleConstant.MODULE_RANKING:
        holder.ivIcon.setImageResource(R.mipmap.ic_module_ranking);
        source = holder.itemView.getContext().getString(R.string.title_go_module_ranking);
        break;
      case ModuleConstant.MODULE_PK:
        holder.ivIcon.setImageResource(R.mipmap.ic_module_pk);
        source = holder.itemView.getContext().getString(R.string.title_go_module_pk);
        break;
      case ModuleConstant.MODULE_UPUP:
        holder.ivIcon.setImageResource(R.mipmap.ic_module_upup);
        source = holder.itemView.getContext().getString(R.string.title_go_module_upup);
        break;
      case ModuleConstant.MODULE_EVENTS:
        holder.ivIcon.setImageResource(R.mipmap.ic_module_events);
        source = holder.itemView.getContext().getString(R.string.title_go_module_events);
        break;
      case ModuleConstant.MODULE_FREE_STYLE:
        holder.ivIcon.setImageResource(R.mipmap.ic_module_free_style);
        source = holder.itemView.getContext().getString(R.string.title_go_module_free_style);
        break;
    }
    holder.tvSource.setText(holder.itemView.getContext().getString(R.string.source, source));
    holder.tvCreateTime.setText(itemData.getStartTimeFormat());
    holder.tvDuration.setText(holder.itemView.getContext().getString(R.string.lbl_time_desc) + itemData.getDurationFormat());
    if (itemData.isLocal()) {
      holder.uploadStatus.setText(holder.itemView.getContext().getString(R.string.upload_fail));
      holder.uploadStatus.setTextColor(Color.parseColor("#E26863"));
    } else {
      holder.uploadStatus.setText(holder.itemView.getContext().getString(R.string.upload_success));
      holder.uploadStatus.setTextColor(Color.parseColor("#888888"));
    }
    if (position < data.size() - 1) {
      holder.vDivider.setVisibility(View.VISIBLE);
    } else {
      holder.vDivider.setVisibility(View.GONE);
    }
    holder.itemView.setOnClickListener(v -> {
      if (listener != null) {
        listener.onItemClick(data.get(position));
      }
    });
  }

  @Override
  public int getItemCount() {
    return data == null ? 0 : data.size();
  }

  public void setOnItemClickListener(OnItemClickListener listener) {
    this.listener = listener;
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivIcon;
    private TextView tvDistance;
    private TextView tvDistanceUnit;
    private TextView tvSource;
    private TextView tvCreateTime;
    private TextView tvDuration;
    private TextView uploadStatus;
    private View vDivider;
    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      ivIcon = itemView.findViewById(R.id.ivIcon);
      tvDistance = itemView.findViewById(R.id.tvDistance);
      tvDistanceUnit = itemView.findViewById(R.id.tvDistanceUnit);
      tvSource = itemView.findViewById(R.id.tvSource);
      tvCreateTime = itemView.findViewById(R.id.tvCreateTime);
      tvDuration = itemView.findViewById(R.id.tvDuration);
      uploadStatus = itemView.findViewById(R.id.uploadStatus);
      vDivider = itemView.findViewById(R.id.vDivider);
    }
  }

  public interface OnItemClickListener {
    void onItemClick(MinePlayDataInfo itemData);
  }

}
