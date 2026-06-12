package com.cloud.runball.module.match.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cloud.runball.R;
import com.cloud.runball.model.RankMatchDetailModel;
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
public class StageItemAdapter extends RecyclerView.Adapter<StageItemAdapter.ViewHolder>{
    List<RankMatchDetailModel.RankMatchStateItem> dataInfo=new ArrayList<>();

    Context mContext;

    public StageItemAdapter(Context context, List<RankMatchDetailModel.RankMatchStateItem> infos) {
        this.mContext = context;
        this.dataInfo = infos;
    }

    public void notifyDataSetChanged(List<RankMatchDetailModel.RankMatchStateItem> infos) {
        this.dataInfo = infos;
        this.notifyDataSetChanged();
    }

    StageItemRuleListener mStageItemRuleListener;
    public void setStageItemRuleListener(StageItemRuleListener listener){
      this.mStageItemRuleListener=listener;
    }

    //用于缓存的ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder {

        View vPoint;
        View vPointVLine;

        TextView tvStageStatus;
        TextView tvName;
        TextView tvRule;
        TextView tvTime;
        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;

            vPoint = itemView.findViewById(R.id.vPoint);
            vPointVLine = itemView.findViewById(R.id.vPointVLine);

            tvStageStatus = itemView.findViewById(R.id.tvStageStatus);
            tvName = itemView.findViewById(R.id.tvName);
            tvRule = itemView.findViewById(R.id.tvRule);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }

    @NonNull
    @Override
    public StageItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_stage_item, parent, false);
        final StageItemAdapter.ViewHolder viewHolder = new StageItemAdapter.ViewHolder(view);
        viewHolder.tvRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   int pos = viewHolder.getLayoutPosition();
                   if(mStageItemRuleListener!=null){
                       mStageItemRuleListener.invoke(dataInfo.get(pos).getMatch_stage_promotion_rule());
                   }
             }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StageItemAdapter.ViewHolder holder, int position) {
        RankMatchDetailModel.RankMatchStateItem data = dataInfo.get(position);
        holder.tvName.setText(data.getMatch_stage_title());
        holder.tvRule.setTag(data.getMatch_stage_promotion_rule());
        holder.tvStageStatus.setTextColor(mContext.getResources().getColor(R.color.match_status_over));
        if(data.getMatchs_stage_status()==1){
            String str=mContext.getResources().getString(R.string.lbl_match_before);
            holder.tvStageStatus.setTextColor(mContext.getResources().getColor(R.color.match_status_yellow));
            holder.tvStageStatus.setText("("+str+")");
        }else if(data.getMatchs_stage_status()==2){
            String str=mContext.getResources().getString(R.string.lbl_match_ing);
            holder.tvStageStatus.setTextColor(mContext.getResources().getColor(R.color.match_status_green));
            holder.tvStageStatus.setText("("+str+")");
        }else if(data.getMatchs_stage_status()==3){
            String str=mContext.getResources().getString(R.string.lbl_match_over);
            holder.tvStageStatus.setTextColor(mContext.getResources().getColor(R.color.match_status_deep_red));
            holder.tvStageStatus.setText("("+str+")");
        }
        holder.tvTime.setText(data.getStart_time()+"-"+data.getStop_time());

        holder.vPoint.setVisibility(View.VISIBLE);
        holder.vPointVLine.setVisibility(View.VISIBLE);

        if(position==dataInfo.size() - 1){
            //holder.vPoint.setVisibility(View.GONE);
            holder.vPointVLine.setVisibility(View.GONE);
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
