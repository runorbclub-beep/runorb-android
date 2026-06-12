package com.cloud.runball.module.match.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.model.ListPkItem;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: MatchRecordDetailTeamAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/16 16:08
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/16 16:08
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRecordDetailTeamAdapter extends RecyclerView.Adapter<MatchRecordDetailTeamAdapter.ViewHolder> {

  private List<ListPkItem> pkDataItemModels = new ArrayList<>();

  private Context mContext;

  public MatchRecordDetailTeamAdapter(Context ctx, List<ListPkItem> list) {
    this.mContext = ctx;
    this.pkDataItemModels = list;
  }

  public void setMatchRecords(List<ListPkItem> list) {
    this.pkDataItemModels = list;
  }

  /**
   * 用于缓存的ViewHolder
   */
  class ViewHolder extends RecyclerView.ViewHolder {
    ImageView img_rank;
    TextView tvRank;
    ImageView ivHead;
    TextView tvName;
    TextView tvSpeed;
    TextView tvDistance;

    View myView;

    public ViewHolder(View itemView) {
      super(itemView);
      myView = itemView;
      img_rank = itemView.findViewById(R.id.img_rank);
      tvRank = itemView.findViewById(R.id.tvRank);
      ivHead = itemView.findViewById(R.id.ivHead);
      tvName = itemView.findViewById(R.id.tvName);
      tvSpeed = itemView.findViewById(R.id.tvSpeed);
      tvDistance = itemView.findViewById(R.id.tvDistance);
    }
  }

  @NonNull
  @Override
  public MatchRecordDetailTeamAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_detail_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull MatchRecordDetailTeamAdapter.ViewHolder holder, int position) {
    ListPkItem pkDataItemModel = pkDataItemModels.get(position);

    if(position == 0) {
      holder.tvRank.setVisibility(View.GONE);
      holder.img_rank.setVisibility(View.VISIBLE);
      holder.img_rank.setBackgroundResource(R.mipmap.match_range_1);
    }else if(position == 1) {
      holder.tvRank.setVisibility(View.GONE);
      holder.img_rank.setVisibility(View.VISIBLE);
      holder.img_rank.setBackgroundResource(R.mipmap.match_range_2);
    } else if(position == 2) {
      holder.tvRank.setVisibility(View.GONE);
      holder.img_rank.setVisibility(View.VISIBLE);
      holder.img_rank.setBackgroundResource(R.mipmap.match_range_3);
    } else {
      holder.img_rank.setVisibility(View.GONE);
      holder.tvRank.setVisibility(View.VISIBLE);
      holder.tvRank.setText(String.valueOf(position+1));
    }

    //下载图片
    if (pkDataItemModel.getUser_img().startsWith("http")) {
      Picasso.with(mContext)
          .load(pkDataItemModel.getUser_img()).transform(new CircleTransform(mContext)).resize(480, 480)
          .into(holder.ivHead);
    } else {
      Picasso.with(mContext)
          .load(Constant.getBaseUrl() + "/" + pkDataItemModel.getUser_img()).transform(new CircleTransform(mContext)).resize(480, 480)
          .into(holder.ivHead);
    }

    holder.tvName.setText(pkDataItemModel.getUser_name());
    holder.tvSpeed.setText(holder.itemView.getContext().getString(R.string.lbl_main_match_record_speed, pkDataItemModel.getSpeed_max() + ""));
    holder.tvDistance.setText(holder.itemView.getContext().getString(R.string.lbl_main_match_record_distance, pkDataItemModel.getDistance()));
  }

  @Override
  public int getItemCount() {
    return pkDataItemModels!=null?pkDataItemModels.size():0;
  }
}
