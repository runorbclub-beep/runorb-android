package com.cloud.runball.module.match_football_association.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.module.match_football_association.entity.AssociationMatchRankInfo;
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
public class AssociationPersonalRankingAdapter extends RecyclerView.Adapter<AssociationPersonalRankingAdapter.ViewHolder> {

  private List<AssociationMatchRankInfo> dataInfo;
  int isShowUnit = 0;
  Object tag = null;
  boolean isIndex = false;
  boolean isShow = false;

  private OnItemClickListener onItemClickListener;

  public AssociationPersonalRankingAdapter(List<AssociationMatchRankInfo> infos, int showUnit, Object tag, boolean isIndex, boolean isShow) {
    this.dataInfo = infos;
    this.isShowUnit = showUnit;
    this.tag = tag;
    this.isIndex = isIndex;
    this.isShow = isShow;
  }

  public void notifyDataSetChanged(List<AssociationMatchRankInfo> infos) {
    this.dataInfo = infos;
    this.notifyDataSetChanged();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView ivHead;
    TextView tvName;
    TextView tvShow;
    TextView tvUnit;
    View myView;
    TextView tvRank;
    TextView tvTime;
    FrameLayout fyHead;
    TextView tvArea;

    ImageView ivRank;

    View vDivider;

    public ViewHolder(View itemView) {
      super(itemView);
      myView = itemView;
      ivHead = itemView.findViewById(R.id.ivHead);
      tvName = itemView.findViewById(R.id.tvName);
      tvRank = itemView.findViewById(R.id.tvRank);
      tvTime = itemView.findViewById(R.id.tvTime);
      ivRank = itemView.findViewById(R.id.ivRank);
      tvShow = itemView.findViewById(R.id.tvShow);
      tvUnit = itemView.findViewById(R.id.tvUnit);
      fyHead = itemView.findViewById(R.id.fyHead);
      tvArea = itemView.findViewById(R.id.tvArea);
      vDivider = itemView.findViewById(R.id.vDivider);
    }
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_association_personal_ranking, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    AssociationMatchRankInfo itemData = dataInfo.get(position);

    Drawable drawableSex = null;
    if (SexConstant.SEX_MAN.equals(itemData.getSysSexId())) {
      drawableSex = holder.itemView.getResources().getDrawable(R.mipmap.ic_man);
      drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
    } else if (SexConstant.SEX_WOMEN.equals(itemData.getSysSexId())) {
      drawableSex = holder.itemView.getResources().getDrawable(R.mipmap.ic_women);
      drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
    }
    holder.tvName.setCompoundDrawables(null, null, drawableSex, null);
    holder.tvName.setText(itemData.getUserName());
    holder.tvArea.setText(itemData.getAddress());

    if (isShow) {
      if(itemData.getIndex() <= 3) {
        holder.ivRank.setVisibility(View.VISIBLE);
        holder.tvRank.setVisibility(View.GONE);
        holder.fyHead.setBackgroundResource(R.drawable.border_ranking_portrait_top);
        if(itemData.getIndex() == 1) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_1);
        } else if(itemData.getIndex() == 2) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_2);
        } else if(itemData.getIndex() == 3) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_3);
        }
      } else{
        holder.ivRank.setVisibility(View.GONE);
        holder.tvRank.setVisibility(View.VISIBLE);
        holder.tvRank.setText(String.valueOf(itemData.getIndex()));
        holder.fyHead.setBackgroundResource(R.drawable.border_ranking_portrait);
      }

      if ("0".equals(itemData.getValue()) || "00:00:00".equals(itemData.getValue())) {
        holder.ivRank.setVisibility(View.GONE);
        holder.tvRank.setVisibility(View.VISIBLE);
        holder.tvRank.setText("/");
        holder.tvShow.setVisibility(View.GONE);
        holder.tvUnit.setVisibility(View.GONE);
        holder.tvTime.setVisibility(View.GONE);
        holder.vDivider.setVisibility(View.GONE);
      } else {
        holder.tvShow.setVisibility(View.VISIBLE);
        holder.tvShow.setText(itemData.getValue());
        if(isShowUnit == 1) {
          holder.tvUnit.setVisibility(View.VISIBLE);
          if (!TextUtils.isEmpty(itemData.getUnit())) {
            holder.tvUnit.setText("（" + itemData.getUnit() + "）");
          }
        }else{
          holder.tvUnit.setVisibility(View.GONE);
        }
        holder.tvTime.setVisibility(View.VISIBLE);
        holder.vDivider.setVisibility(View.VISIBLE);
        //如果是摇跑指数,则显示指数
        if(isIndex) {
          holder.tvTime.setText(holder.itemView.getContext().getString(R.string.lbl_rank_index) + itemData.getTime());
        }else{
          holder.tvTime.setText(itemData.getTime());
        }
      }
    } else {
      holder.ivRank.setVisibility(View.GONE);
      holder.tvRank.setVisibility(View.VISIBLE);
      holder.tvRank.setText("/");

      holder.tvShow.setVisibility(View.GONE);
      holder.tvUnit.setVisibility(View.GONE);
      holder.tvTime.setVisibility(View.GONE);
      holder.vDivider.setVisibility(View.GONE);
    }

    String imgUrl = itemData.getUserImg();
    if(!imgUrl.startsWith("http")) {
      imgUrl = Constant.getBaseUrl() + "/" + imgUrl;
    }
    Picasso.with(holder.itemView.getContext())
        .load(imgUrl)
        .transform(new CircleTransform(holder.itemView.getContext()))
        .placeholder(R.mipmap.default_head)
        .into(holder.ivHead);

    holder.itemView.setOnClickListener(v -> {
      if (onItemClickListener != null) {
        onItemClickListener.onItemClick(itemData);
      }
    });
  }

  @Override
  public int getItemCount() {
    return dataInfo != null ? dataInfo.size() : 0;
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  public interface OnItemClickListener {
    void onItemClick(AssociationMatchRankInfo itemData);
  }

}
