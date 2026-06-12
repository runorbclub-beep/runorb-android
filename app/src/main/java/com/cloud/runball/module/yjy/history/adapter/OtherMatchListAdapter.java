package com.cloud.runball.module.yjy.history.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cloud.runball.R;
import com.cloud.runball.bean.OtherMatchDetailInfo;
import com.cloud.runball.widget.CircleTransform;
import com.littlejie.circleprogress.utils.MiscUtil;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.module.home.adapter
 * @ClassName: OtherMatchListAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/21 14:46
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/21 14:46
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class OtherMatchListAdapter extends RecyclerView.Adapter<OtherMatchListAdapter.ViewHolder>{

  DecimalFormat mDecimalFormat = new DecimalFormat("0.00");
  private List<OtherMatchDetailInfo> list;
  private Context mContext;
  private OnItemClickListener onItemClickListener;

  public OtherMatchListAdapter(Context context, List<OtherMatchDetailInfo> datas){
    this.mContext=context;
    this.list=datas;
  }

  public void notifyDataSetChanged(List<OtherMatchDetailInfo> infos) {
    this.list = infos;
    this.notifyDataSetChanged();
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_other_main_match_list_item, parent, false);
    final OtherMatchListAdapter.ViewHolder viewHolder = new OtherMatchListAdapter.ViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    OtherMatchDetailInfo result = list.get(position);
    if (position == 0) {
      holder.img_rank.setVisibility(View.VISIBLE);
      holder.img_rank.setBackgroundResource(R.mipmap.match_range_1);
    } else if(position == 1) {
      holder.img_rank.setVisibility(View.VISIBLE);
      holder.img_rank.setBackgroundResource(R.mipmap.match_range_2);
    } else if(position == 2) {
      holder.img_rank.setVisibility(View.VISIBLE);
      holder.img_rank.setBackgroundResource(R.mipmap.match_range_3);
    } else {
      holder.img_rank.setVisibility(View.GONE);
      holder.tvRank.setVisibility(View.VISIBLE);
      holder.tvRank.setText(String.valueOf(position + 1));
    }

    holder.tvName.setText(result.getTitle());
    holder.tvCount.setText(String.format(mContext.getResources().getString(R.string.lbl_main_match_record_person_2), formatNum(result.getNum())));
    holder.tvIndex.setText(String.format(mContext.getResources().getString(R.string.lbl_main_match_index), result.getIndex()+1));

    double distance = Double.parseDouble(result.getDistance()) / 1000.0f;
    if(distance > 0 && position >= 3){
      holder.tvRank.setVisibility(View.VISIBLE);
    }else{
      holder.tvRank.setVisibility(View.GONE);
    }

    holder.tvDistance.setText(String.format(mContext.getResources().getString(R.string.lbl_main_match_record_distance2), formatDistance(distance)));
    holder.tvScore.setText(String.format(mContext.getResources().getString(R.string.lbl_main_match_record_score_1), String.valueOf(result.getIntegral())));

    String name = "horse_" + (result.getIndex() + 1) + "_stop";
    int id = mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());
    if(id != 0) {
      Picasso.with(holder.itemView.getContext())
          .load(id)
          .transform(new CircleTransform(holder.itemView.getContext()))
          .placeholder(R.mipmap.default_head)
          .into(holder.ivHead);
    }

    holder.itemView.setOnClickListener(v -> {
      if (onItemClickListener != null) {
        onItemClickListener.onItemClick(result);
      }
    });
  }

  @Override
  public int getItemCount() {
    return list!=null?list.size():0;
  }

  private String formatDistance(double distance){
    if(distance<1000){
      return mDecimalFormat.format(distance)+"km";
    }else if(distance>=1000 && distance < 10000){
      return mDecimalFormat.format(distance/1000.0)+"k km";
    }else{
      return mDecimalFormat.format(distance/10000.0)+"w km";
    }
  }

  private String formatNum(int num){
    if(num<1000){
      return String.valueOf(num);
    }else if(num>=1000 && num < 10000){
      return String.valueOf(num/1000)+"k";
    }else{
      return String.valueOf(num/10000.0)+"w";
    }
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    ImageView img_rank;
    ImageView ivHead;
    TextView tvIndex;
    TextView tvName;
    TextView tvRank;
    TextView tvCount;
    TextView tvScore;
    TextView tvDistance;

    View myView;

    public ViewHolder(View itemView) {
      super(itemView);
      myView = itemView;
      img_rank = itemView.findViewById(R.id.img_rank);
      ivHead = itemView.findViewById(R.id.ivHead);
      tvRank= itemView.findViewById(R.id.tvRank);
      tvIndex = itemView.findViewById(R.id.tvIndex);
      tvName = itemView.findViewById(R.id.tvName);
      tvScore = itemView.findViewById(R.id.tvScore);
      tvCount = itemView.findViewById(R.id.tvCount);
      tvDistance = itemView.findViewById(R.id.tvDistance);

    }
  }

  public interface OnItemClickListener {
    void onItemClick(OtherMatchDetailInfo itemData);
  }

}
