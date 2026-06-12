package com.cloud.runball.module.match.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cloud.runball.R;
import com.cloud.runball.model.RankMatchDataModel;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: MatchAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/1 10:46
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/1 10:46
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    final String HTTP="http";
    List<RankMatchDataModel> dataInfo;
    Context mContext;
    private int is_members=0;

    public MatchAdapter(Context context, List<RankMatchDataModel> infos) {
        this.mContext = context;
        this.dataInfo = infos;
    }

    public void notifyDataSetChanged(List<RankMatchDataModel> infos) {
        this.dataInfo = infos;
        this.notifyDataSetChanged();
    }

    public void updateMembers(int is_members){
        this.is_members = is_members;
    }

    private MatchAdapter.OnItemClickListener onItemClickListener;


    public void setOnItemClickListener(MatchAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView tvImage;
        TextView tvTime;
        TextView tvTitle;
        TextView tvNum;
        TextView tvStatus;
        Button btnAttendDetail;

        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;

            tvImage = itemView.findViewById(R.id.tvImage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvNum = itemView.findViewById(R.id.tvNum);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAttendDetail = itemView.findViewById(R.id.btnAttendDetail);
        }
    }

    @NonNull
    @Override
    public MatchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_match_item, parent, false);
        final MatchAdapter.ViewHolder viewHolder = new MatchAdapter.ViewHolder(view);

        viewHolder.btnAttendDetail.setOnClickListener(v -> {
            int postion = viewHolder.getLayoutPosition()-1;
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v,OnItemClickListener.BUTTON, dataInfo.get(postion));
            }
        });
        //点击item的监听器
        viewHolder.myView.setOnClickListener(v -> {
            int postion = viewHolder.getLayoutPosition()-1;//获取到当前点击的是哪一个item
            //程序执行到此，会去执行具体实现的onItemClick()方法
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, OnItemClickListener.DETAIL, dataInfo.get(postion));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchAdapter.ViewHolder holder, int position) {
        RankMatchDataModel data = dataInfo.get(position);

        holder.tvTime.setText(data.getStart_time());
        holder.tvTitle.setText(data.getMatch_title());
        holder.tvNum.setText(String.format(mContext.getResources().getString(R.string.lbl_match_sign_num),data.getMatch_user_sign_count()));

        holder.btnAttendDetail.setBackgroundResource(R.drawable.selector_button_btn);
        holder.btnAttendDetail.setEnabled(true);
        holder.btnAttendDetail.setText(R.string.lbl_match_ranking_check);

        if(data.getMatch_status()!=3){
            if(data.getMatch_status()==1){
               //未开始,按钮显示即将开始
                holder.btnAttendDetail.setEnabled(false);
                holder.btnAttendDetail.setBackgroundResource(R.drawable.selector_login_status_btn);
                holder.btnAttendDetail.setText(R.string.lbl_match_sign_now2);
            }else{
                //赛事报名条件 0：开放报名，1：关闭报名，2：允许会员报名
                if(data.getJoin_status()==1 || data.getJoin_status()==2){
                    holder.btnAttendDetail.setBackgroundResource(R.drawable.selector_login_status_btn);
                    holder.btnAttendDetail.setEnabled(false);
                    if(is_members==1){
                        holder.btnAttendDetail.setBackgroundResource(R.drawable.selector_button_btn);
                        holder.btnAttendDetail.setEnabled(true);
                    }
                }else{
                    holder.btnAttendDetail.setBackgroundResource(R.drawable.selector_button_btn);
                    holder.btnAttendDetail.setEnabled(true);
                }
                //进行中
                holder.btnAttendDetail.setText(R.string.lbl_match_sign_now);
                if(data.getUser_join_status()!=null){
                    if(data.getUser_join_status().getIs_join()==1){
                        //已报名，立即比赛
                        holder.btnAttendDetail.setText(R.string.lbl_match_right_now);
                    }
                }
            }
        }



        holder.tvStatus.setText("#"+data.getMatch_status_title());

        if(data.getMatch_image().startsWith(HTTP)){
            Picasso.with(mContext)
                    .load(data.getMatch_image()).transform(new CircleTransform(mContext,28,28)).resize(480, 480)
                    .error(R.mipmap.match_sub_logo_default)
                    //.fit().centerCrop()
                    .into(holder.tvImage);
        }else{
            Picasso.with(mContext)
                    .load(Constant.getBaseUrl()+"/"+data.getMatch_image()).transform(new CircleTransform(mContext,28,28)).resize(480, 480)
                    .error(R.mipmap.match_sub_logo_default)
                    //.fit().centerCrop()
                    .into(holder.tvImage);
        }
    }



    @Override
    public int getItemCount() {
        return dataInfo.size();
    }


    public interface OnItemClickListener {
        static int BUTTON=0;
        static int DETAIL=1;
        void onItemClick(View view,int type, RankMatchDataModel data);
    }

}
