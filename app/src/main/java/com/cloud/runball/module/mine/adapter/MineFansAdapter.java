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
 * @ClassName: MineFansAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/1 11:46
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/1 11:46
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MineFansAdapter  extends RecyclerView.Adapter<MineFansAdapter.ItemViewHolder>{

    List<String> dataInfo;

    Context mContext;

    public MineFansAdapter(Context context, List<String> infos) {
        this.mContext = context;
        this.dataInfo = infos;
    }

    public void notifyDataSetChanged(List<String> infos) {
        this.dataInfo = infos;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_mine_fans_layout_item, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        //设置圆角
        //Picasso.with(mContext).load(headpic).memoryPolicy(MemoryPolicy.NO_CACHE)
        //        .transform(new RoundTransform(mContext)).into(holder.tvHead);
    }

    @Override
    public int getItemCount() {
        return dataInfo!=null?dataInfo.size():0;
    }

    //用于缓存的ViewHolder
    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_circle;
        TextView tv_time;
        ImageView img_attend;
        ImageView tvHead;
        View myView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            tv_circle = itemView.findViewById(R.id.tv_circle);
            tv_time = itemView.findViewById(R.id.tv_time);
            img_attend = itemView.findViewById(R.id.img_attend);
            tvHead = itemView.findViewById(R.id.tvHead);
        }
    }
}
