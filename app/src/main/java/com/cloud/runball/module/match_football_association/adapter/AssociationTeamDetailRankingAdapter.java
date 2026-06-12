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
import com.cloud.runball.module.match_football_association.entity.model.AssociationTeamDetailRankingModel;
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

public class AssociationTeamDetailRankingAdapter extends RecyclerView.Adapter<AssociationTeamDetailRankingAdapter.ViewHolder> {

  List<AssociationTeamDetailRankingModel.MyRankingInfo> dataInfo;
  Context mContext;
  int isShowUnit = 0;
  boolean isIndex = false;
  boolean isShow = false;

  public AssociationTeamDetailRankingAdapter(Context context, List<AssociationTeamDetailRankingModel.MyRankingInfo> infos, int showUnit, boolean isIndex, boolean isShow) {
    this.mContext = context;
    this.dataInfo = infos;
    this.isShowUnit = showUnit;
    this.isIndex = isIndex;
    this.isShow = isShow;
  }

  public void notifyDataSetChanged(List<AssociationTeamDetailRankingModel.MyRankingInfo> infos) {
    this.dataInfo = infos;
    this.notifyDataSetChanged();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView ivHead;
    TextView tvName;
    TextView tvShow;
    View myView;
    TextView tvRank;
    TextView tvTime;
    FrameLayout fyHead;
    TextView tvArea;

    ImageView ivRank;

    TextView tvUnit;
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
      fyHead = itemView.findViewById(R.id.fyHead);
      tvArea = itemView.findViewById(R.id.tvArea);
      tvUnit = itemView.findViewById(R.id.tvUnit);
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
    AssociationTeamDetailRankingModel.MyRankingInfo data = dataInfo.get(position);

    Drawable drawableSex = null;
    if (SexConstant.SEX_MAN.equals(data.getSysSexId())) {
      drawableSex = holder.itemView.getResources().getDrawable(R.mipmap.ic_man);
      drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
    } else if (SexConstant.SEX_WOMEN.equals(data.getSysSexId())) {
      drawableSex = holder.itemView.getResources().getDrawable(R.mipmap.ic_women);
      drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
    }
    holder.tvName.setCompoundDrawables(null, null, drawableSex, null);
    holder.tvName.setText(data.getUserName());
    holder.tvArea.setText(data.getAddress());

    if (isShow) {
      if(data.getIndex() <= 3) {
        holder.ivRank.setVisibility(View.VISIBLE);
        holder.tvRank.setVisibility(View.GONE);
        holder.fyHead.setBackgroundResource(R.drawable.border_ranking_portrait_top);
        if(data.getIndex() == 1) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_1);
        } else if(data.getIndex() == 2) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_2);
        } else if(data.getIndex() == 3) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_3);
        }
      } else{
        holder.ivRank.setVisibility(View.GONE);
        holder.tvRank.setVisibility(View.VISIBLE);
        holder.tvRank.setText(String.valueOf(data.getIndex()));
        holder.fyHead.setBackgroundResource(R.drawable.border_ranking_portrait);
      }

      if ("0".equals(data.getValue()) || "00:00:00".equals(data.getValue())) {
        holder.ivRank.setVisibility(View.GONE);
        holder.tvRank.setVisibility(View.VISIBLE);
        holder.tvRank.setText("/");
        holder.tvShow.setVisibility(View.GONE);
        holder.tvUnit.setVisibility(View.GONE);
        holder.tvTime.setVisibility(View.GONE);
        holder.vDivider.setVisibility(View.GONE);
      } else {
        holder.tvShow.setVisibility(View.VISIBLE);
        holder.tvShow.setText(data.getValue());
        if(isShowUnit == 1) {
          holder.tvUnit.setVisibility(View.VISIBLE);
          if (!TextUtils.isEmpty(data.getUnit())) {
            holder.tvUnit.setText("（" + data.getUnit() + "）");
          }
        }else{
          holder.tvUnit.setVisibility(View.GONE);
        }
        holder.tvTime.setVisibility(View.VISIBLE);
        holder.vDivider.setVisibility(View.VISIBLE);
        //如果是摇跑指数,则显示指数
        if(isIndex) {
          holder.tvTime.setText(mContext.getString(R.string.lbl_rank_index)+data.getTime());
        }else{
          holder.tvTime.setText(data.getTime());
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

    if(data.getUserImg().startsWith("http")) {
      Picasso.with(mContext)
          .load(data.getUserImg())
//                    .centerCrop()
          .transform(new CircleTransform(mContext))
          .placeholder(R.mipmap.default_head)
          .into(holder.ivHead);
    } else {
      Picasso.with(mContext)
          .load(Constant.getBaseUrl() + "/" + data.getUserImg())
//                    .centerCrop()
          .transform(new CircleTransform(mContext))
          .placeholder(R.mipmap.default_head)
          .into(holder.ivHead);
    }
  }

  @Override
  public int getItemCount() {
    return dataInfo!=null?dataInfo.size():0;
  }

}
