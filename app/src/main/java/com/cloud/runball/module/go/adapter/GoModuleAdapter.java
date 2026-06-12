package com.cloud.runball.module.go.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;

import java.util.ArrayList;
import java.util.List;

public class GoModuleAdapter extends RecyclerView.Adapter<GoModuleAdapter.ViewHolder> {

  private final List<GoModuleItem> data = new ArrayList<>();
  private OnItemClickListener onItemClickListener;

  public GoModuleAdapter(List<GoModuleItem> data) {
    this.data.addAll(data);
  }

  public List<GoModuleItem> getData() {
    return data;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_go_module, null);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    GoModuleItem itemData = data.get(position);
    holder.ivModuleIcon.setImageResource(itemData.getIconId());
    holder.tvTitle.setText(itemData.getTitle());
    holder.tvViceTitle.setText(itemData.getViceTitle());
    holder.ivModule.setImageResource(itemData.getBgId());
    holder.itemView.setOnClickListener(v -> {
      if (onItemClickListener != null) {
        onItemClickListener.onItemClick(itemData);
      }
    });

    if (TextUtils.isEmpty(itemData.getMark())) {
      holder.tvMark.setVisibility(View.GONE);
    } else {
      holder.tvMark.setVisibility(View.VISIBLE);
      holder.tvMark.setText(itemData.getMark());
    }

  }

  @Override
  public int getItemCount() {
    return data == null ? 0 : data.size();
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    ImageView ivModuleIcon;
    TextView tvTitle;
    TextView tvViceTitle;
    ImageView ivModule;
    TextView tvMark;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      ivModuleIcon = itemView.findViewById(R.id.ivModuleIcon);
      tvTitle = itemView.findViewById(R.id.tvTitle);
      tvViceTitle = itemView.findViewById(R.id.tvViceTitle);
      ivModule = itemView.findViewById(R.id.ivModule);
      tvMark = itemView.findViewById(R.id.tvMark);
    }
  }

  public interface OnItemClickListener {
    void onItemClick(GoModuleItem itemData);
  }

}
