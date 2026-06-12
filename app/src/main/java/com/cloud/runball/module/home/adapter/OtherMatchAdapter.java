package com.cloud.runball.module.home.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.listener.OnItemClickListener;
import com.cloud.runball.bean.OtherMatchInfo;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.module.home.adapter
 * @ClassName: OtherMatchAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/20 18:28
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/20 18:28
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class OtherMatchAdapter extends RecyclerView.Adapter<OtherMatchAdapter.ViewHolder>{

    private List<OtherMatchInfo> list;
    private Context mContext;
    Typeface typeface=null;
    public OtherMatchAdapter(Context context, List<OtherMatchInfo> datas){
        this.mContext=context;
        this.list=datas;
        this.typeface = ResourcesCompat.getFont(mContext, R.font.rzsy_2);
    }

    public void notifyDataSetChanged(List<OtherMatchInfo> infos) {
        this.list = infos;
        this.notifyDataSetChanged();
    }


    OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener=listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_other_match_item, parent, false);
        final OtherMatchAdapter.ViewHolder viewHolder = new OtherMatchAdapter.ViewHolder(view);
        viewHolder.myView.setOnClickListener(v -> {
            if(onItemClickListener!=null){
                int pos = viewHolder.getLayoutPosition()-1;
                onItemClickListener.onItemClick(list.get(pos),pos);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OtherMatchInfo record = list.get(position);
        holder.tvTime.setText(record.getDate());
        holder.tvIndex.setText(String.format(mContext.getResources().getString(R.string.lbl_main_match_index),record.getIndex()+1));
        holder.tvName.setText(record.getTitle());
        holder.tvScore.setText(String.valueOf(record.getIntegral()));
        holder.tvNo.setText(String.valueOf(record.getRanking()));
    }

    @Override
    public int getItemCount() {
        return this.list!=null?this.list.size():0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvName;
        TextView tvIndex;
        TextView tvScore;
        TextView tvNo;
        TextView tvNoTag;
        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            tvTime = itemView.findViewById(R.id.tvTime);
            tvName = itemView.findViewById(R.id.tvName);
            tvIndex = itemView.findViewById(R.id.tvIndex);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvNoTag = itemView.findViewById(R.id.tvNoTag);
            tvNo = itemView.findViewById(R.id.tvNo);

            tvNoTag.setTypeface(typeface);
            tvNo.setTypeface(typeface);
        }
    }
}
