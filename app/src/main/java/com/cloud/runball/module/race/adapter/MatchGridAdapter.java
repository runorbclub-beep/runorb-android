package com.cloud.runball.module.race.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.model.PkUserDataModel;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: MatchGridAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/9 15:01
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/9 15:01
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchGridAdapter extends RecyclerView.Adapter<MatchGridAdapter.ViewHolder> {

    public static final int RED = 1;
    public static final int BLUE = 2;

    ArrayList<PkUserDataModel> dataInfo = new ArrayList<>();
    Context mContext;
    private int mWhichTeam;

    public MatchGridAdapter(Context context, ArrayList<PkUserDataModel> list, int whichTeam) {
        this.mContext = context;
        this.dataInfo = list;
        this.mWhichTeam = whichTeam;
    }

    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener clickListener){
      this.mOnItemClickListener=clickListener;
    }

    @NonNull
    @Override
    public MatchGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_match_add_over_item, parent, false);
        final MatchGridAdapter.ViewHolder viewHolder = new MatchGridAdapter.ViewHolder(view);
        //点击item的监听器
        viewHolder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int postion = viewHolder.getLayoutPosition();
                //程序执行到此，会去执行具体实现的onItemClick()方法
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mWhichTeam,dataInfo.get(postion));
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchGridAdapter.ViewHolder holder, int position) {
        PkUserDataModel data = dataInfo.get(position);
        if(data.getIs_ready()==1){
            holder.img_info_avatar_online.setVisibility(View.VISIBLE);
        }else{
            holder.img_info_avatar_online.setVisibility(View.GONE);
        }

        //显示名称
        String showName=data.getUser_name() != null ? data.getUser_name() : String.valueOf(position + 1);
        holder.tvName.setText(showName.length()>7?showName.substring(0,6)+"...":showName);

        //显示头像，记得加上圆角
        if (data.getUser_img() != null) {
            if (data.getUser_img().startsWith("http")) {
                Picasso.with(mContext)
                        .load(data.getUser_img()).centerCrop().transform(new CircleTransform(mContext)).resize(480, 480)
                        .into(holder.img_info_avatar);
            } else {
                Picasso.with(mContext)
                        .load(Constant.getBaseUrl() + "/" + data.getUser_img()).centerCrop().transform(new CircleTransform(mContext)).resize(480, 480)
                        .into(holder.img_info_avatar);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataInfo.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_info_avatar;
        TextView tvName;
        ImageView img_info_avatar_online;
        TextView tv_offline;
        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            img_info_avatar = itemView.findViewById(R.id.img_info_avatar);
            tvName = itemView.findViewById(R.id.tvName);

            img_info_avatar_online = itemView.findViewById(R.id.img_info_avatar_online);
            tv_offline = itemView.findViewById(R.id.tv_offline);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int whichTeam, PkUserDataModel data);
    }

}
