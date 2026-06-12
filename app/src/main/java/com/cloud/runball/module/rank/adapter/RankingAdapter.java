package com.cloud.runball.module.rank.adapter;

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
import com.cloud.runball.bean.MatchRankItem;
import com.cloud.runball.constant.SexConstant;
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
public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

  List<MatchRankItem> dataInfo;
  Context mContext;
  boolean isIndex = false;

  private OnItemClickListener listener;

  public RankingAdapter(Context context, List<MatchRankItem> infos, boolean isIndex, OnItemClickListener listener) {
    this.mContext = context;
    this.dataInfo = infos;
    this.isIndex = isIndex;
    this.listener = listener;
  }

  public void notifyDataSetChanged(List<MatchRankItem> infos) {
    this.dataInfo = infos;
    this.notifyDataSetChanged();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
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
  public RankingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_rank_item_1, parent, false);
    final RankingAdapter.ViewHolder viewHolder = new RankingAdapter.ViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull RankingAdapter.ViewHolder holder, int position) {
    MatchRankItem data = dataInfo.get(position);
    if(data.getIndex() <= 3){
      holder.ivRank.setVisibility(View.VISIBLE);
      holder.tvRank.setVisibility(View.GONE);
      holder.fyHead.setBackgroundResource(R.drawable.border_ranking_portrait_top);
      if(data.getIndex() == 1) {
        holder.ivRank.setBackgroundResource(R.mipmap.match_range_1);
      } else if(data.getIndex() == 2) {
        holder.ivRank.setBackgroundResource(R.mipmap.match_range_2);
        holder.fyHead.setBackgroundResource(R.drawable.border_ranking_portrait_top);
      } else if(data.getIndex() == 3) {
        holder.ivRank.setBackgroundResource(R.mipmap.match_range_3);
        holder.fyHead.setBackgroundResource(R.drawable.border_ranking_portrait_top);
      }
    }else{
      holder.ivRank.setVisibility(View.GONE);
      holder.tvRank.setVisibility(View.VISIBLE);
      holder.tvRank.setText(String.valueOf(data.getIndex()));
      holder.fyHead.setBackgroundResource(R.drawable.border_ranking_portrait);
    }

    Drawable drawableSex = null;
    if (SexConstant.SEX_MAN.equals(data.getSys_sex_id())) {
      drawableSex = holder.itemView.getResources().getDrawable(R.mipmap.ic_man);
      drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
    } else if (SexConstant.SEX_WOMEN.equals(data.getSys_sex_id())) {
      drawableSex = holder.itemView.getResources().getDrawable(R.mipmap.ic_women);
      drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
    }
    holder.tvName.setCompoundDrawables(null, null, drawableSex, null);
    holder.tvName.setText(data.getUser_name());

    holder.tvShow.setText(data.getValue());
    if (!TextUtils.isEmpty(data.getUnit())) {
      holder.tvUnit.setVisibility(View.VISIBLE);
      holder.tvUnit.setText("（" + data.getUnit() + "）");
    } else {
      holder.tvUnit.setVisibility(View.GONE);
    }

    holder.tvArea.setText(data.getAddress());

    //如果是摇跑指数,则显示指数
    if(isIndex){
      holder.tvTime.setText(mContext.getString(R.string.lbl_rank_index) + data.getTime());
    }else{
      holder.tvTime.setText(data.getTime());
    }


    if(data.getUser_img().startsWith("http")) {
      Picasso.with(mContext)
          .load(data.getUser_img())
//                    .centerCrop()
          .transform(new CircleTransform(mContext))
          .placeholder(R.mipmap.default_head)
          .into(holder.ivHead);
    } else {
      Picasso.with(mContext)
          .load(Constant.getBaseUrl() + "/" + data.getUser_img())
//                    .centerCrop()
          .transform(new CircleTransform(mContext))
          .placeholder(R.mipmap.default_head)
          .into(holder.ivHead);
    }

    holder.itemView.setOnClickListener(v -> {
      if (listener != null) {
        listener.onItemClick(data);
      }
    });
  }

  @Override
  public int getItemCount() {
    return dataInfo != null ? dataInfo.size() : 0;
  }

  public interface OnItemClickListener {
    void onItemClick(MatchRankItem itemData);
  }

}
