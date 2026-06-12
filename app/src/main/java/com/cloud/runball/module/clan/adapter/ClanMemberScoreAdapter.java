package com.cloud.runball.module.clan.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.model.ClanMemberRankModel;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ClanMemberScoreAdapter extends RecyclerView.Adapter<ClanMemberScoreAdapter.ViewHolder> {

  private List<ClanMemberRankModel.ClanMemberScore> data = null;
  private int type;
  private OnItemClickListener onItemClickListener;

  public ClanMemberScoreAdapter(int type, List<ClanMemberRankModel.ClanMemberScore> data) {
    this.type = type;
    this.data = data;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_clan_menber_score, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ClanMemberRankModel.ClanMemberScore itemData = data.get(position);

    String imgUrl = itemData.getUser().getUserImg();
    if(!imgUrl.startsWith("http")) {
      imgUrl = Constant.getBaseUrl() + "/" + imgUrl;
    }
    Picasso.with(holder.itemView.getContext())
        .load(imgUrl)
        .transform(new CircleTransform(holder.itemView.getContext()))
        .into(holder.ivHead);

    holder.tvName.setText(itemData.getUser().getUserName());
    holder.tvArea.setText(itemData.getUser().getAddress());

    String value = null, time = null, unit = null;
    if (type == 1) {
      value = itemData.getSpeedMax();
      time = TimeUtils.dateToString(itemData.getSpeedMaxTime() * 1000);
      unit = itemData.getSpeedMaxUnit();
    } else if (type == 2) {
      value = itemData.getExponentMolecular();
      time = TimeUtils.dateToString(itemData.getExponentMolecularTime() * 1000);
      unit = itemData.getExponentMolecularUnit();
    } else if (type == 3) {
      value = itemData.getRunballExponent();
      time = TimeUtils.dateToString(itemData.getRunballExponentTime() * 1000);
    } else if (type == 4) {
      value = itemData.getMarathons();
      time = TimeUtils.dateToString(itemData.getMarathonTime() * 1000);
    }
    if (TextUtils.isEmpty(value) || "0".equals(value) || "00:00:00".equals(value)) {
      value = null;
    }
    if (TextUtils.isEmpty(value)) {
      holder.tvValue.setVisibility(View.GONE);
      holder.tvUnit.setVisibility(View.GONE);
      holder.tvValue.setVisibility(View.GONE);
      holder.tvUnit.setVisibility(View.GONE);
      holder.vDivider.setVisibility(View.GONE);
      holder.tvTime.setVisibility(View.GONE);

      holder.ivRank.setVisibility(View.GONE);
      holder.tvRank.setVisibility(View.VISIBLE);
      holder.tvRank.setText("/");
    } else {
      holder.tvValue.setVisibility(View.VISIBLE);
      holder.tvValue.setText(value);
      holder.vDivider.setVisibility(View.VISIBLE);
      holder.tvTime.setVisibility(View.VISIBLE);
      holder.tvTime.setText(time);
      if (TextUtils.isEmpty(unit)) {
        holder.tvUnit.setVisibility(View.GONE);
      } else {
        holder.tvUnit.setVisibility(View.VISIBLE);
        holder.tvUnit.setText(holder.itemView.getContext().getString(R.string.format_brackets, unit));
      }

      if(position <= 2) {
        holder.ivRank.setVisibility(View.VISIBLE);
        holder.tvRank.setVisibility(View.GONE);
        if(position == 0) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_1);
        } else if(position == 1) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_2);
        } else if(position == 2) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_3);
        }
      }else{
        holder.ivRank.setVisibility(View.GONE);
        holder.tvRank.setVisibility(View.VISIBLE);
        holder.tvRank.setText(String.valueOf(position));
      }
    }

    holder.itemView.setOnClickListener(v -> {
      if (onItemClickListener != null) {
        onItemClickListener.onItemClick(itemData);
      }
    });

  }

  @Override
  public int getItemCount() {
    return data == null ? 0 : data.size();
  }

  public void setOnItemClickListener(OnItemClickListener listener) {
    this.onItemClickListener = listener;
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    ImageView ivRank;
    TextView tvRank;
    ImageView ivHead;
    TextView tvName;
    TextView tvArea;
    View vDivider;
    TextView tvTime;
    TextView tvValue;
    TextView tvUnit;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      ivRank = itemView.findViewById(R.id.ivRank);
      tvRank = itemView.findViewById(R.id.tvRank);
      ivHead = itemView.findViewById(R.id.ivHead);
      tvName = itemView.findViewById(R.id.tvName);
      tvArea = itemView.findViewById(R.id.tvArea);
      vDivider = itemView.findViewById(R.id.vDivider);
      tvTime = itemView.findViewById(R.id.tvTime);
      tvValue = itemView.findViewById(R.id.tvValue);
      tvUnit = itemView.findViewById(R.id.tvUnit);
    }

  }

  public interface OnItemClickListener{
    void onItemClick(ClanMemberRankModel.ClanMemberScore itemData);
  }

}
