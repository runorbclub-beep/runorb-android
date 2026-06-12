package com.cloud.runball.module.mine_record.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.module.mine_record.entity.MineRankingRecordInfo;

import java.util.List;

public class MineRankingRecordAdapter extends RecyclerView.Adapter<MineRankingRecordAdapter.ViewHolder> {

  private List<MineRankingRecordInfo> data;
  private OnItemClickListener listener;

  public MineRankingRecordAdapter(List<MineRankingRecordInfo> data) {
    this.data = data;
  }

  public void setListener(OnItemClickListener listener) {
    this.listener = listener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_mine_ranking_record, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    MineRankingRecordInfo itemData = data.get(position);

    if (itemData.getRank() < 1) {
      holder.tvRank.setText("/");
    } else {
      holder.tvRank.setText(itemData.getRank() + "");
    }

    holder.tvObject.setText(itemData.getObjectName());
    if (!TextUtils.isEmpty(itemData.getUnit())) {
      holder.tvObject.append("(" + itemData.getUnit() + ")");
    }

    if (TextUtils.isEmpty(itemData.getAchievement())) {
      holder.tvAchievement.setText("/");
    } else {
      holder.tvAchievement.setText(itemData.getAchievement());
    }

    if (TextUtils.isEmpty(itemData.getDatetime())) {
      holder.tvDatetime.setText("/");
    } else {
      holder.tvDatetime.setText(itemData.getDatetime());
    }

    holder.itemView.setOnClickListener(v -> {
      if (listener != null) {
        listener.onItemClick();
      }
    });
  }

  @Override
  public int getItemCount() {
    return data == null ? 0 : data.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView tvRank;
    TextView tvObject;
    TextView tvAchievement;
    TextView tvDatetime;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      tvRank = itemView.findViewById(R.id.tvRank);
      tvObject = itemView.findViewById(R.id.tvObject);
      tvAchievement = itemView.findViewById(R.id.tvAchievement);
      tvDatetime = itemView.findViewById(R.id.tvDatetime);
    }
  }

  public interface OnItemClickListener {
    void onItemClick();
  }

}
