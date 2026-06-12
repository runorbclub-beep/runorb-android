package com.cloud.runball.module.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.bean.MatchRankData;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: PersonMatchAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/1 17:03
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/1 17:03
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class PersonMatchAdapter extends RecyclerView.Adapter<PersonMatchAdapter.ViewHolder> {
    List<MatchRankData> dataInfo;
    Context mContext;
    int is_exponent=0;
    public PersonMatchAdapter(Context context, List<MatchRankData> infos) {
        this.mContext = context;
        this.dataInfo = infos;
        this.is_exponent=0;
    }

    public void setExponent(int is_exponent){
        this.is_exponent=is_exponent;
    }

    public void notifyDataSetChanged(List<MatchRankData> infos) {
        this.dataInfo = infos;
        this.notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHead;
        TextView tvName;
        TextView tvTime;
        TextView tvDistance;
        View myView;
        TextView tvRank;
        ImageView ivRank;

        TextView tvPreUnit;


        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            ivHead = itemView.findViewById(R.id.ivHead);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvRank = itemView.findViewById(R.id.tvRank);
            ivRank = itemView.findViewById(R.id.ivRank);

            tvPreUnit = itemView.findViewById(R.id.tvPreUnit);
        }
    }

    @NonNull
    @Override
    public PersonMatchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_person_match_item, parent, false);
        final PersonMatchAdapter.ViewHolder viewHolder = new PersonMatchAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PersonMatchAdapter.ViewHolder holder, int position) {
        MatchRankData data = dataInfo.get(position);
        if(data.getMatch_ranking()<=3){
            holder.ivRank.setVisibility(View.VISIBLE);
            holder.tvRank.setVisibility(View.GONE);
            if(data.getMatch_ranking()==1){
                holder.ivRank.setBackgroundResource(R.mipmap.match_range_1);
            }else if(data.getMatch_ranking()==2){
                holder.ivRank.setBackgroundResource(R.mipmap.match_range_2);
            }else if(data.getMatch_ranking()==3){
                holder.ivRank.setBackgroundResource(R.mipmap.match_range_3);
            }else{
                holder.ivRank.setVisibility(View.GONE);
            }
        }else{
            holder.ivRank.setVisibility(View.GONE);
            holder.tvRank.setVisibility(View.VISIBLE);
            holder.tvRank.setText(String.valueOf(data.getMatch_ranking()));
        }

        holder.tvName.setText(data.getName());
        holder.tvTime.setText(TimeUtils.formatDurationFull(data.getMatch_grade()));
        //如果是摇跑指数，则显示指数单位Pre
        if(is_exponent==1){
            holder.tvPreUnit.setText(R.string.lbl_rank_index);
        }else{
            holder.tvPreUnit.setText(R.string.lbl_rank_time);
        }

        if(data.getImage().startsWith("http")){
            Picasso.with(mContext)
                    .load(data.getImage()).centerCrop().transform(new CircleTransform(mContext)).resize(480, 480)
                    .into(holder.ivHead);
        }else{
            Picasso.with(mContext)
                    .load(Constant.getBaseUrl() + "/" + data.getImage()).centerCrop().transform(new CircleTransform(mContext)).resize(480, 480)
                    .into(holder.ivHead);
        }

    }

    @Override
    public int getItemCount() {
        return dataInfo!=null?dataInfo.size():0;
    }
}
