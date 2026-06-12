package com.cloud.runball.module.mine.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cloud.runball.R;
import com.cloud.runball.bean.RankInfo;

import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: RankListAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/13 14:32
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/13 14:32
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankListAdapter extends RecyclerView.Adapter<RankListAdapter.ViewHolder>{
    List<RankInfo> dataInfo;
    Context mContext;
    Drawable left0;
    Drawable left1;
    public RankListAdapter(Context context) {
        this.mContext = context;
        this.left0=this.mContext.getResources().getDrawable(R.mipmap.rank_item_icon);
        left0.setBounds(0, 0, 60, 60);
        this.left1=this.mContext.getResources().getDrawable(R.mipmap.rank_item_icon1);
        left1.setBounds(0, 0, 60, 60);
    }

    public void notifyDataSetChanged(List<RankInfo> infos) {
        this.dataInfo = infos;
        this.notifyDataSetChanged();
    }

    private onItemClickListener callback;
    public void setOnItemClickListener(onItemClickListener listener){
        this.callback=listener;
    }

    private onLongClickListener onLongClickListener;
    public void setOnLongClickListener(onLongClickListener listener){
        this.onLongClickListener=listener;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        View myView;
        TextView tvName;
        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            tvName = itemView.findViewById(R.id.tvName);
        }
    }

    @NonNull
    @Override
    public RankListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rank_type_list, parent, false);
        final RankListAdapter.ViewHolder viewHolder = new RankListAdapter.ViewHolder(view);
        //是否已经绑定
        viewHolder.myView.setOnClickListener(v -> {
            if(callback!=null){
                int pos = viewHolder.getLayoutPosition();
                if(dataInfo!=null && pos<=dataInfo.size()-1 && pos>=0){
                    callback.onItemClick(dataInfo.get(pos));
                }
            }
        });

        viewHolder.myView.setOnLongClickListener(v -> {
            if(onLongClickListener!=null){
                int pos = viewHolder.getLayoutPosition();
                if(onLongClickListener!=null && pos<=dataInfo.size()-1 && pos>=0){
                    onLongClickListener.onLongClick(dataInfo.get(pos));
                }
            }
            return true;
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankListAdapter.ViewHolder holder, int position) {
        RankInfo data = dataInfo.get(position);
        holder.tvName.setText(data.getTitle());
        if("0".equals(data.getUser_age_type())){
            holder.tvName.setCompoundDrawables(left0,null,null, null);
        }else{
            holder.tvName.setCompoundDrawables(left1,null,null,null);
        }
    }

    @Override
    public int getItemCount() {
        return dataInfo!=null?dataInfo.size():0;
    }


    public interface onItemClickListener{
        public void onItemClick(RankInfo info);
    }

    public interface onLongClickListener{
        public void onLongClick(RankInfo info);
    }
}
