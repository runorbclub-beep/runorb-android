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

import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: SystemAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/22 13:54
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/22 13:54
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class SystemAdapter extends RecyclerView.Adapter<SystemAdapter.ItemViewHolder> {
    List<String> dataInfo;

    Context mContext;

    public SystemAdapter(Context context, List<String> infos) {
        this.mContext = context;
        this.dataInfo = infos;
    }

    public void notifyDataSetChanged(List<String> infos) {
        this.dataInfo = infos;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SystemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_system_layout_item, null);
        return new SystemAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SystemAdapter.ItemViewHolder holder, int position) {
       holder.tvMsg.setText("恭喜您，报名成功！ 恭喜您成功报名【2021年第一季度季后赛】 编号为24351");
    }


    @Override
    public int getItemCount() {
        return dataInfo != null ? dataInfo.size() : 0;
    }

    //用于缓存的ViewHolder
    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView tvHead;
        TextView tvMsg;
        View myView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            tvHead = itemView.findViewById(R.id.tvHead);
            tvMsg = itemView.findViewById(R.id.tvMsg);
        }
    }
}
