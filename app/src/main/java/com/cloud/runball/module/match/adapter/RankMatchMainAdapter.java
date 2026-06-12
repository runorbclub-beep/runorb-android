package com.cloud.runball.module.match.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.bean.RankGroupItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: RankMatchMainAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/14 13:10
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/14 13:10
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankMatchMainAdapter extends RecyclerView.Adapter<RankMatchMainAdapter.ViewHolder> {
    List<RankGroupItem> dataInfo = new ArrayList<>();
    Context mContext;
    String mSelfGroupID;
    public RankMatchMainAdapter(Context context, List<RankGroupItem> infos) {
        this.mContext = context;
        this.dataInfo = infos;
    }

    public void selectMe(String selfGroupID){
        this.mSelfGroupID=selfGroupID;
    }

    public void notifyDataSetChanged(List<RankGroupItem> infos) {
        this.dataInfo = infos;
        this.notifyDataSetChanged();
    }

    public void setDataInfo(List<RankGroupItem> infos){
        this.dataInfo = infos;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFlag;
        TextView tvTitle;
        TextView tvDistance;
        ProgressBar progressBar;

        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            ivFlag = itemView.findViewById(R.id.ivFlag);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    @NonNull
    @Override
    public RankMatchMainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_match_main2_item, parent, false);
        final RankMatchMainAdapter.ViewHolder viewHolder = new RankMatchMainAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankMatchMainAdapter.ViewHolder holder, int position) {
        RankGroupItem data = dataInfo.get(position);
        if(position==0){
            holder.ivFlag.setBackgroundResource(R.mipmap.red_flag_1);
        }else if(position==1){
            holder.ivFlag.setBackgroundResource(R.mipmap.red_flag_2);
        }else if(position==2){
            holder.ivFlag.setBackgroundResource(R.mipmap.red_flag_3);
        }else if(position==3){
            holder.ivFlag.setBackgroundResource(R.mipmap.red_flag_4);
        }else if(position==4){
            holder.ivFlag.setBackgroundResource(R.mipmap.red_flag_5);
        }else if(position==5){
            holder.ivFlag.setBackgroundResource(R.mipmap.red_flag_6);
        }else if(position==6){
            holder.ivFlag.setBackgroundResource(R.mipmap.red_flag_7);
        }else if(position==7){
            holder.ivFlag.setBackgroundResource(R.mipmap.red_flag_8);
        }else{
            holder.ivFlag.setBackgroundResource(R.mipmap.red_flag_1);
        }

        if(data.getUser_group_id().equalsIgnoreCase(mSelfGroupID) && !TextUtils.isEmpty(mSelfGroupID)){
            holder.progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.match_progressbar_me));
        }else{
            holder.progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.match_progressbar));
        }

        holder.tvTitle.setText(data.getUser_group_name());
        holder.tvDistance.setText(data.getDistance_poor());
        holder.progressBar.setProgress((int)(data.getDistance_percentage()*100));
    }

    @Override
    public int getItemCount() {
        return dataInfo!=null?dataInfo.size():0;
    }
}
