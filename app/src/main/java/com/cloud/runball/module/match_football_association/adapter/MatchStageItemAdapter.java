package com.cloud.runball.module.match_football_association.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.model.RankMatchDetailModel;
import com.cloud.runball.module.match_football_association.entity.MatchStage;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: StageItemAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/28 13:30
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/28 13:30
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchStageItemAdapter extends RecyclerView.Adapter<MatchStageItemAdapter.ViewHolder>{

  private StageItemRuleListener mStageItemRuleListener;
  private List<MatchStage> dataInfo = new ArrayList<>();

  private Context mContext;

  public MatchStageItemAdapter(Context context, List<MatchStage> infos) {
    this.mContext = context;
    this.dataInfo = infos;
  }

  public void notifyDataSetChanged(List<MatchStage> infos) {
    this.dataInfo = infos;
    this.notifyDataSetChanged();
  }

  public void setStageItemRuleListener(StageItemRuleListener listener){
    this.mStageItemRuleListener=listener;
  }

  //用于缓存的ViewHolder
  static class ViewHolder extends RecyclerView.ViewHolder {

    View vPoint;
    View vPointVLine;

    TextView tvStageStatus;
    TextView tvName;
    TextView tvRule;
    TextView tvTime;
    TextView tvRuleContent;
    View myView;

    LinearLayout layRule;

    public ViewHolder(View itemView) {
      super(itemView);
      myView = itemView;

      vPoint = itemView.findViewById(R.id.vPoint);
      vPointVLine = itemView.findViewById(R.id.vPointVLine);

      tvStageStatus = itemView.findViewById(R.id.tvStageStatus);
      tvName = itemView.findViewById(R.id.tvName);
      tvRule = itemView.findViewById(R.id.tvRule);
      tvTime = itemView.findViewById(R.id.tvTime);
      tvRuleContent = itemView.findViewById(R.id.tvRuleContent);

      layRule = itemView.findViewById(R.id.layRule);
    }
  }

  @NonNull
  @Override
  public MatchStageItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_match_stage, parent, false);
    final MatchStageItemAdapter.ViewHolder viewHolder = new ViewHolder(view);
//    viewHolder.tvRule.setOnClickListener(v -> {
//      int pos = viewHolder.getLayoutPosition();
//      if(mStageItemRuleListener!=null){
//        mStageItemRuleListener.invoke(dataInfo.get(pos).getMatchStagePromotionRule());
//      }
//    });
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull MatchStageItemAdapter.ViewHolder holder, int position) {
    MatchStage data = dataInfo.get(position);
    holder.tvName.setText(data.getMatchStageTitle());
//    holder.tvRule.setTag(data.getMatchStagePromotionRule());
//
    if(!TextUtils.isEmpty(data.getMatchStagePromotionRule())) {
      holder.tvRuleContent.setText(
          data.getMatchStagePromotionRule()
              .replaceAll("<div style='color:#767779'>", "")
              .replaceAll("</div>", "")
      );
    } else {
      holder.tvRuleContent.setText("");
    }
    holder.tvStageStatus.setTextColor(mContext.getResources().getColor(R.color.match_status_over));
    if(data.getMatchStageStatus() == 1) {
      String str = mContext.getResources().getString(R.string.lbl_match_before);
      holder.tvStageStatus.setTextColor(mContext.getResources().getColor(R.color.match_status_yellow));
      holder.tvStageStatus.setText("(" + str + ")");
    }else if(data.getMatchStageStatus() == 2) {
      String str = mContext.getResources().getString(R.string.lbl_match_ing);
      holder.tvStageStatus.setTextColor(mContext.getResources().getColor(R.color.match_status_yellow));
      holder.tvStageStatus.setText("("+str+")");
    }else if(data.getMatchStageStatus() == 3) {
      String str = mContext.getResources().getString(R.string.lbl_match_over);
      holder.tvStageStatus.setTextColor(mContext.getResources().getColor(R.color.match_status_deep_red));
      holder.tvStageStatus.setText("("+str+")");
    }
    holder.tvTime.setText(data.getStartTime() + "-" + data.getStopTime());

    holder.vPoint.setVisibility(View.VISIBLE);
    holder.vPointVLine.setVisibility(View.VISIBLE);

    if(position == dataInfo.size() - 1){
      //holder.vPoint.setVisibility(View.GONE);
      holder.vPointVLine.setVisibility(View.GONE);
    }

    if (position == dataInfo.size() - 1) {
      holder.layRule.setVisibility(View.GONE);
    } else {
      holder.layRule.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public int getItemCount() {
    return dataInfo.size();
  }

  public interface StageItemRuleListener{
    public void invoke(String rule);
  }

}
