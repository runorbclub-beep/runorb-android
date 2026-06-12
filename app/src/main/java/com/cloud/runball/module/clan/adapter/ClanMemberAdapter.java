package com.cloud.runball.module.clan.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.model.ClanMemberItem;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ClanMemberAdapter extends RecyclerView.Adapter<ClanMemberAdapter.ViewHolder> {

  private List<ClanMemberItem> data = null;
  private boolean isPending = false;
  private int captainStatus;

  private OnItemClickListener listener;

  public ClanMemberAdapter(boolean isPending, int captainStatus, List<ClanMemberItem> data, OnItemClickListener listener) {
    this.isPending = isPending;
    this.data = data;
    this.listener = listener;
  }

  public void setListener(OnItemClickListener listener) {
    this.listener = listener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_clan_member, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ClanMemberItem itemData = data.get(position);

    String img;
    if(itemData.getUserImg().startsWith("http")) {
      img = itemData.getUserImg();
    } else {
      img = Constant.getBaseUrl() + "/" + itemData.getUserImg();
    }
    Picasso.with(holder.itemView.getContext())
        .load(img)
        .transform(new CircleTransform(holder.itemView.getContext()))
//        .placeholder(R.mipmap.default_head)
        .into(holder.ivHead);

    holder.tvName.setText(itemData.getUserName());

    Drawable drawableSex = null;
    if (SexConstant.SEX_MAN.equals(itemData.getSysSexId())) {
      drawableSex = holder.itemView.getContext().getResources().getDrawable(R.mipmap.ic_man);
    } else if (SexConstant.SEX_WOMEN.equals(itemData.getSysSexId())) {
      drawableSex = holder.itemView.getContext().getResources().getDrawable(R.mipmap.ic_women);
    }
    drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
    holder.tvName.setCompoundDrawables(null, null, drawableSex, null);

    holder.tvArea.setText(itemData.getAddress());
    holder.tvMark.setVisibility(View.GONE);

    if (isPending) {
      holder.tvPending.setVisibility(View.VISIBLE);
    } else {
      holder.tvPending.setVisibility(View.GONE);
    }
    holder.tvPending.setOnClickListener(v -> {
      if (listener != null) {
        listener.onPending(itemData);
      }
    });
    if (!isPending && captainStatus == 1) {
      holder.tvMore.setVisibility(View.VISIBLE);
    } else {
      holder.tvMore.setVisibility(View.GONE);
    }
    holder.tvMore.setOnClickListener(v -> {
      if (listener != null) {
        listener.onItemMoreClick(itemData);
      }
    });
    holder.itemView.setOnClickListener(v -> {
      if (listener != null) {
        listener.onItemClick(itemData);
      }
    });
  }

  @Override
  public int getItemCount() {
    return data == null ? 0 : data.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    ImageView ivHead;
    TextView tvName;
    TextView tvArea;

    TextView tvPending;
    TextView tvMark;

    TextView tvMore;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      ivHead = itemView.findViewById(R.id.ivHead);
      tvName = itemView.findViewById(R.id.tvName);
      tvArea = itemView.findViewById(R.id.tvArea);

      tvPending = itemView.findViewById(R.id.tvPending);
      tvMark = itemView.findViewById(R.id.tvMark);

      tvMore = itemView.findViewById(R.id.tvMore);
    }

  }

  public interface OnItemClickListener {
    void onPending(ClanMemberItem itemData);
    void onItemClick(ClanMemberItem itemData);
    void onItemMoreClick(ClanMemberItem itemData);
  }

}
