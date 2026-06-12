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
public class AssociationTeamRankingAdapter extends RecyclerView.Adapter<AssociationTeamRankingAdapter.ViewHolder> {

  List<AssociationMatchRankInfo> dataInfo;
  Context mContext;
  int isShowUnit = 0;
  private OnItemClickListener onItemClickListener;
  private boolean isShow = false;

  public AssociationTeamRankingAdapter(Context context, List<AssociationMatchRankInfo> infos, int showUnit, boolean isShow) {
    this.mContext = context;
    this.dataInfo = infos;
    this.isShowUnit = showUnit;
    this.isShow = isShow;
  }

  public void notifyDataSetChanged(List<AssociationMatchRankInfo> infos) {
    this.dataInfo = infos;
    this.notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_association_team_ranking, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    AssociationMatchRankInfo data = dataInfo.get(position);

    holder.tvName.setText(data.getTeamTag());
    holder.tvJoinSum.setText(holder.itemView.getContext().getString(R.string.association_match_join_sum, data.getJoinSum()));

    if (isShow) {
      if(data.getIndex() <= 3){
        holder.ivRank.setVisibility(View.VISIBLE);
        holder.tvRank.setVisibility(View.GONE);
        if(data.getIndex() == 1) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_1);
        } else if(data.getIndex() == 2) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_2);
        } else if(data.getIndex() == 3) {
          holder.ivRank.setBackgroundResource(R.mipmap.match_range_3);
        }
      }else{
        holder.ivRank.setVisibility(View.GONE);
        holder.tvRank.setVisibility(View.VISIBLE);
        holder.tvRank.setText(String.valueOf(data.getIndex()));
      }

      if ("0".equals(data.getValue()) || "00:00:00".equals(data.getValue())) {
        holder.ivRank.setVisibility(View.GONE);
        holder.tvRank.setVisibility(View.VISIBLE);
        holder.tvRank.setText("/");
        holder.tvShow.setVisibility(View.GONE);
        holder.tvUnit.setVisibility(View.GONE);
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
      }

    } else {
      holder.ivRank.setVisibility(View.GONE);
      holder.tvRank.setVisibility(View.VISIBLE);
      holder.tvRank.setText("/");
      holder.tvShow.setVisibility(View.GONE);
      holder.tvUnit.setVisibility(View.GONE);
    }

    holder.itemView.setOnClickListener(v -> {
      if (onItemClickListener != null) {
        onItemClickListener.onItemClick(data);
      }
    });

  }

  @Override
  public int getItemCount() {
    return dataInfo != null ? dataInfo.size() : 0;
  }

  public void setOnItemClickListener(OnItemClickListener listener) {
    onItemClickListener = listener;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    TextView tvName;
    TextView tvJoinSum;
    TextView tvShow;
    View myView;
    TextView tvRank;

    ImageView ivRank;

    TextView tvUnit;

    public ViewHolder(View itemView) {
      super(itemView);
      myView = itemView;
      tvName = itemView.findViewById(R.id.tvName);
      tvJoinSum = itemView.findViewById(R.id.tvJoinSum);
      tvRank = itemView.findViewById(R.id.tvRank);
      ivRank = itemView.findViewById(R.id.ivRank);
      tvShow = itemView.findViewById(R.id.tvShow);
      tvUnit = itemView.findViewById(R.id.tvUnit);
    }
  }

  public interface OnItemClickListener {
    void onItemClick(AssociationMatchRankInfo data);
  }

}
