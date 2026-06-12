package com.cloud.runball.module.match.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.model.ListPkItem;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: MatchRecordDetailAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/16 13:30
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/16 13:30
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRecordDetailAdapter extends RecyclerView.Adapter<MatchRecordDetailAdapter.ViewHolder> {

    private List<ListPkItem> pkDataItemModels = new ArrayList<>();

    private Context mContext;

    public MatchRecordDetailAdapter(Context ctx, List<ListPkItem> list) {
        this.mContext = ctx;
        this.pkDataItemModels = list;
    }

    public void setMatchRecords(List<ListPkItem> list) {
        this.pkDataItemModels = list;
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_rank;
        TextView tvRank;
        ImageView ivHead;
        TextView tvName;
        TextView tvSpeed;
        TextView tvDistance;

        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            img_rank = itemView.findViewById(R.id.img_rank);
            tvRank = itemView.findViewById(R.id.tvRank);
            ivHead = itemView.findViewById(R.id.ivHead);
            tvName = itemView.findViewById(R.id.tvName);
            tvSpeed = itemView.findViewById(R.id.tvSpeed);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }
    }


    @NonNull
    @Override
    public MatchRecordDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_detail_item, parent, false);
        final MatchRecordDetailAdapter.ViewHolder viewHolder = new MatchRecordDetailAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchRecordDetailAdapter.ViewHolder holder, int position) {
        ListPkItem pkItem = pkDataItemModels.get(position);

        if (pkItem.getIs_win() == 1) {
            holder.img_rank.setBackgroundResource(R.mipmap.match_range_1);
        } else {
            holder.img_rank.setBackgroundResource(R.mipmap.match_range_2);
        }

        //下载图片
        if(pkItem.getUser_img().startsWith("http")){
            Picasso.with(mContext)
                    .load(pkItem.getUser_img()).centerCrop().transform(new CircleTransform(mContext)).resize(480, 480)
                    .into(holder.ivHead);
        }else{
            Picasso.with(mContext)
                    .load(Constant.getBaseUrl() + "/" + pkItem.getUser_img()).centerCrop().transform(new CircleTransform(mContext)).resize(480, 480)
                    .into(holder.ivHead);
        }


        holder.tvName.setText(pkItem.getUser_name());
        holder.tvSpeed.setText(holder.itemView.getContext().getString(R.string.lbl_main_match_record_speed, pkItem.getSpeed_max() + ""));
        holder.tvDistance.setText(holder.itemView.getContext().getString(R.string.lbl_main_match_record_distance, pkItem.getDistance()));
    }

    @Override
    public int getItemCount() {
        return pkDataItemModels.size();
    }
}
