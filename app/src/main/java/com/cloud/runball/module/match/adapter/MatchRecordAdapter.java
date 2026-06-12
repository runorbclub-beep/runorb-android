package com.cloud.runball.module.match.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.bean.PKDataResp;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: MatchRecordAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/15 19:27
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/15 19:27
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRecordAdapter extends RecyclerView.Adapter<MatchRecordAdapter.ViewHolder> {


    private List<PKDataResp> pkDatas = new ArrayList<>();


    private MatchRecordAdapter.OnItemClickListener callback;


    public MatchRecordAdapter(List<PKDataResp> list) {
        this.pkDatas = list;
    }


    public void setMatchRecords(List<PKDataResp> list) {
        this.pkDatas = list;
    }

    public void setOnItemClickListener(MatchRecordAdapter.OnItemClickListener listener) {
        this.callback = listener;
    }


    //用于缓存的ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvSpeed;
        TextView tvDistance;
        TextView tvGroup;
        Button btnDetail;
        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            tvTime = itemView.findViewById(R.id.tvTime);
            tvSpeed = itemView.findViewById(R.id.tvSpeed);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvGroup = itemView.findViewById(R.id.tvGroup);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }


    @NonNull
    @Override
    public MatchRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_match_record_item, parent, false);
        final MatchRecordAdapter.ViewHolder viewHolder = new MatchRecordAdapter.ViewHolder(view);
        //点击查看详情
        viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    int pos = viewHolder.getLayoutPosition()-1;
                    if (pos < pkDatas.size() && pos >= 0) {
                        callback.onItemClick(pos, pkDatas.get(pos));
                    }
                }
            }

        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchRecordAdapter.ViewHolder holder, int position) {
        PKDataResp dataResp = pkDatas.get(position);
        holder.tvTime.setText(dataResp.getStart_date());
        holder.tvSpeed.setText(String.valueOf(dataResp.getSpeed_max()));
        holder.tvDistance.setText(dataResp.getDistance() + "km");

        if(dataResp.getPk_type()==0){
            holder.tvGroup.setText(R.string.lbl_match_corner_tip_2);
        }else if(dataResp.getPk_type()==1){
            holder.tvGroup.setText(R.string.lbl_match_corner_tip_3);
        }
    }

    @Override
    public int getItemCount() {
        return pkDatas.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, PKDataResp data);
    }
}
