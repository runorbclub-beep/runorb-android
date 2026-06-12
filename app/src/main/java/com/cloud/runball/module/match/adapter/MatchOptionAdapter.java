package com.cloud.runball.module.match.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.model.RankMatchDetailModel;
import com.cloud.runball.utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: MatchOptionAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/12 16:15
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/12 16:15
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchOptionAdapter extends RecyclerView.Adapter<MatchOptionAdapter.ViewHolder> {
    List<RankMatchDetailModel.RankMatchFormItem> dataInfo=new ArrayList<>();

    Context mContext;

    public MatchOptionAdapter(Context context, List<RankMatchDetailModel.RankMatchFormItem> infos) {
        this.mContext = context;
        this.dataInfo = infos;
    }

    public void notifyDataSetChanged(List<RankMatchDetailModel.RankMatchFormItem> infos) {
        this.dataInfo = infos;
        this.notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivTitle;
        TextView tvTitle;
        TextView tvValues;
        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            ivTitle = itemView.findViewById(R.id.ivTitle);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvValues = itemView.findViewById(R.id.tvValues);
        }
    }

    @NonNull
    @Override
    public MatchOptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_match_detail_layout_item, parent, false);
        final MatchOptionAdapter.ViewHolder viewHolder = new MatchOptionAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchOptionAdapter.ViewHolder holder, int position) {
        RankMatchDetailModel.RankMatchFormItem data = dataInfo.get(position);
        holder.tvTitle.setText(data.getLabel());
        holder.tvValues.setText(Html.fromHtml(data.getValue()));

        //下载图片
        if (data.getIcon().startsWith("http")) {
            Picasso.with(mContext)
                    .load(data.getIcon())
                    .into(holder.ivTitle);
        } else {
            Picasso.with(mContext)
                    .load(Constant.getBaseUrl() + "/" + data.getIcon())
                    .into(holder.ivTitle);
        }
    }

    @Override
    public int getItemCount() {
        return dataInfo.size();
    }


}
