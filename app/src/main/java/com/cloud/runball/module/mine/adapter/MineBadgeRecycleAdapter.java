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
import com.cloud.runball.bean.MedalInfo;
import com.cloud.runball.utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MineBadgeRecycleAdapter extends RecyclerView.Adapter<MineBadgeRecycleAdapter.ViewHolder> {

    List<MedalInfo> dataInfo;
    boolean isZh;
    Context mContext;
    public MineBadgeRecycleAdapter(Context context, List<MedalInfo> infos,boolean isZh) {
        this.mContext=context;
        this.dataInfo = infos;
        this.isZh=isZh;
    }

    public void notifyDataSetChanged(List<MedalInfo> infos) {
        this.dataInfo = infos;
        this.notifyDataSetChanged();
    }

    private MineBadgeRecycleAdapter.OnItemClickListener onItemClickListener;


    public void setOnItemClickListener(MineBadgeRecycleAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBadge;
        TextView tvBadge;
        View myView;  //自定义View用于recyclerview的点击事件

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            ivBadge = itemView.findViewById(R.id.ivBadge);
            tvBadge = itemView.findViewById(R.id.tvBadge);
        }
    }

    @NonNull
    @Override
    public MineBadgeRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mine_badge_item, parent, false);
        final MineBadgeRecycleAdapter.ViewHolder viewHolder = new MineBadgeRecycleAdapter.ViewHolder(view);
        //点击item的监听器
        viewHolder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int postion = viewHolder.getLayoutPosition();
                //程序执行到此，会去执行具体实现的onItemClick()方法
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v,dataInfo.get(postion));
                }
                //Toast.makeText(v.getContext(), "你动了"+bean.getMyName()+"一下", Toast.LENGTH_LONG).show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MineBadgeRecycleAdapter.ViewHolder holder, int position) {
        MedalInfo data = dataInfo.get(position);
        if(isZh){
            holder.tvBadge.setText(data.getUser_medal_name_cn());
        }else{
            holder.tvBadge.setText(data.getUser_medal_name_en());
        }
        if(data.getMedal_image_active().startsWith("http")){
            Picasso.with(mContext)
                    .load(data.getMedal_image_active())
                    .into(holder.ivBadge);
        }else{
            Picasso.with(mContext)
                    .load(Constant.getBaseUrl()+"/"+data.getMedal_image_active())
                    .into(holder.ivBadge);
        }
    }

    @Override
    public int getItemCount() {
        return dataInfo.size();
    }


    public interface OnItemClickListener {
        void onItemClick(View view, MedalInfo data);
    }
}
