package com.cloud.runball.module.rank.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.bean.MatchRankItem;
import com.cloud.runball.model.ClanRankingModel;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: RankingAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/2 18:10
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/2 18:10
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ClanRankAdapter extends RecyclerView.Adapter<ClanRankAdapter.ViewHolder> {

  private List<ClanRankingModel.ClanRankItem> data;
  private int rankType;

  private OnItemClickListener listener;

  public ClanRankAdapter(int rankType, List<ClanRankingModel.ClanRankItem> data, OnItemClickListener listener) {
    this.rankType = rankType;
    this.data = data;
    this.listener = listener;
  }

  public void notifyDataSetChanged(int rankType, List<ClanRankingModel.ClanRankItem> data) {
    this.rankType = rankType;
    this.data = data;
    this.notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ClanRankAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_clan_rank, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ClanRankAdapter.ViewHolder holder, int position) {
    ClanRankingModel.ClanRankItem itemData = data.get(position);

    holder.tvName.setText(itemData.getTitle());

    String value = null, unit = null;
    // 1最高转速 2摇跑一分钟 3摇跑指数 4马拉松
    if (rankType == 1) {
      value = itemData.getSpeedMax();
      unit = itemData.getSpeedMaxUnit();
    } else if (rankType == 2) {
      value = itemData.getExponentMolecular();
      unit = itemData.getExponentMolecularUnit();
    } else if (rankType == 3) {
      value = itemData.getRunballExponent();
    } else if (rankType == 4) {
      value = itemData.getMarathon();
    }
    if (TextUtils.isEmpty(value) || "0".equals(value) || "00:00:00".equals(value)) {
      holder.tvValue.setText("/");
      holder.tvUnit.setVisibility(View.GONE);
      holder.ivRank.setVisibility(View.GONE);
      holder.tvRank.setVisibility(View.VISIBLE);
      holder.tvRank.setText("/");
    } else {
      holder.tvValue.setText(value);
      if (TextUtils.isEmpty(unit)) {
        holder.tvUnit.setVisibility(View.GONE);
      } else {
        holder.tvUnit.setVisibility(View.VISIBLE);
        holder.tvUnit.setText(holder.itemView.getContext().getString(R.string.format_brackets, unit));
      }

      if(position <= 2) {
        holder.ivRank.setVisibility(View.VISIBLE);
        holder.tvRank.setVisibility(View.GONE);
        holder.ivHead.setBackgroundResource(R.drawable.border_rectangle_portrait_top);
        if(position == 0) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_1);
        } else if(position == 1) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_2);
          holder.ivHead.setBackgroundResource(R.drawable.border_rectangle_portrait_top);
        } else if(position == 2) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_3);
          holder.ivHead.setBackgroundResource(R.drawable.border_rectangle_portrait_top);
        }
      } else {
        holder.ivRank.setVisibility(View.GONE);
        holder.tvRank.setVisibility(View.VISIBLE);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.ivHead.setBackgroundResource(R.drawable.border_rectangle_portrait);
      }
    }

    holder.tvArea.setText(itemData.getAddress());

    holder.tvMemberCount.setText(holder.itemView.getContext().getString(R.string.association_match_join_sum, itemData.getClanCount() + ""));

    String imgUrl;
    if(itemData.getClanAvatar().startsWith("http")) {
      imgUrl = itemData.getClanAvatar();
    } else {
      imgUrl = Constant.getBaseUrl() + "/" + itemData.getClanAvatar();
    }
    Picasso.with(holder.ivHead.getContext())
        .load(imgUrl)
//        .transform(new CircleTransform(holder.ivHead.getContext()))
//        .placeholder(R.mipmap.default_head)
        .into(holder.ivHead);

    holder.itemView.setOnClickListener(v -> {
      if (listener != null) {
        listener.onItemClick(itemData);
      }
    });
  }

  @Override
  public int getItemCount() {
    return data != null ? data.size() : 0;
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    ImageView ivHead;
    TextView tvName;
    TextView tvValue;
    TextView tvUnit;
    TextView tvRank;
    TextView tvMemberCount;
    TextView tvArea;

    ImageView ivRank;

    View vDivider;

    public ViewHolder(View itemView) {
      super(itemView);
      ivHead = itemView.findViewById(R.id.ivHead);
      tvName = itemView.findViewById(R.id.tvName);
      tvRank = itemView.findViewById(R.id.tvRank);
      tvMemberCount = itemView.findViewById(R.id.tvMemberCount);
      ivRank = itemView.findViewById(R.id.ivRank);
      tvValue = itemView.findViewById(R.id.tvValue);
      tvUnit = itemView.findViewById(R.id.tvUnit);
      tvArea = itemView.findViewById(R.id.tvArea);
      vDivider = itemView.findViewById(R.id.vDivider);
    }
  }

  public interface OnItemClickListener {
    void onItemClick(ClanRankingModel.ClanRankItem itemData);
  }

}
