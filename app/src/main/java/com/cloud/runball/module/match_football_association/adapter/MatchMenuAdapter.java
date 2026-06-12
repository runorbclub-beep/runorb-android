package com.cloud.runball.module.match_football_association.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.constant.ChampionshipsConstant;
import com.cloud.runball.model.RankMatchDataModel;
import com.cloud.runball.module.match_football_association.entity.MatchMenu;
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
public class MatchMenuAdapter extends RecyclerView.Adapter<MatchMenuAdapter.ViewHolder> {

  private final String HTTP = "http";
  private List<MatchMenu> dataInfo;
  private Context mContext;
  private int is_members=0;

  private OnItemClickListener onItemClickListener;

  public MatchMenuAdapter(Context context, List<MatchMenu> infos) {
    this.mContext = context;
    this.dataInfo = infos;
  }

  public void notifyDataSetChanged(List<MatchMenu> infos) {
    this.dataInfo = infos;
    this.notifyDataSetChanged();
  }

  public void updateMembers(int is_members){
    this.is_members = is_members;
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView tvImage;
    TextView tvTime;
    TextView tvTitle;
    TextView tvNum;
    TextView tvStatus;
    Button btnAttendDetail;
    ImageView ivHot;

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
      ivHot = itemView.findViewById(R.id.ivHot);
    }
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_match_menu, parent, false);
    final ViewHolder viewHolder = new ViewHolder(view);

    viewHolder.btnAttendDetail.setOnClickListener(v -> {
      int position = viewHolder.getLayoutPosition() - 1;
      if (onItemClickListener != null) {
        onItemClickListener.onItemClick(v, OnItemClickListener.BUTTON, dataInfo.get(position));
      }
    });
    //点击item的监听器
    viewHolder.myView.setOnClickListener(v -> {
      int position = viewHolder.getLayoutPosition() - 1;//获取到当前点击的是哪一个item
      //程序执行到此，会去执行具体实现的onItemClick()方法
      if (onItemClickListener != null) {
        onItemClickListener.onItemClick(v, OnItemClickListener.DETAIL, dataInfo.get(position));
      }
    });
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    MatchMenu data = dataInfo.get(position);

    holder.tvTime.setText(data.getStartTime());
    holder.tvTitle.setText(data.getMatchTitle());
    holder.tvNum.setText(String.format(mContext.getResources().getString(R.string.lbl_match_sign_num),data.getMatchUserSignCount()));

    int matchStatus = data.getMatchStatus();
    int joinStatus = data.getJoinStatus();
    int userJoinStatus = data.getUserJoinStatus().getIsJoin();
    if (matchStatus == ChampionshipsConstant.MATCH_STATUS_NOT_STARTED) {
      if (userJoinStatus == ChampionshipsConstant.USER_JOIN_STATUS_NO) { // 用户未报名
        holder.btnAttendDetail.setText(R.string.lbl_match_sign_now);
        holder.btnAttendDetail.setBackgroundResource(R.drawable.selector_match_btn_sign_up);
        holder.btnAttendDetail.setTextColor(Color.parseColor("#25282C"));
        //赛事报名条件 0：开放报名，1：关闭报名，2：允许会员报名
        if (joinStatus == ChampionshipsConstant.JOIN_STATUS_CLOSE) {
          holder.btnAttendDetail.setEnabled(false);
        } else if (joinStatus == ChampionshipsConstant.JOIN_STATUS_MEMBER) {
          if (is_members == 1) {
            holder.btnAttendDetail.setEnabled(false);
          } else {
            holder.btnAttendDetail.setEnabled(true);
          }
        } else {
          holder.btnAttendDetail.setEnabled(true);
        }
      } else {
        holder.btnAttendDetail.setText(R.string.lbl_match_attend);
        holder.btnAttendDetail.setBackgroundResource(R.drawable.selector_match_btn_sign_up2);
        holder.btnAttendDetail.setTextColor(Color.parseColor("#FFFFFF"));
      }
    } else if (matchStatus == ChampionshipsConstant.MATCH_STATUS_PLAYING) {
      if (userJoinStatus == ChampionshipsConstant.USER_JOIN_STATUS_NO) { // 用户未报名
        holder.btnAttendDetail.setText(R.string.lbl_match_sign_now);
        holder.btnAttendDetail.setBackgroundResource(R.drawable.selector_match_btn_sign_up);
        holder.btnAttendDetail.setTextColor(Color.parseColor("#25282C"));
        //赛事报名条件 0：开放报名，1：关闭报名，2：允许会员报名
        if (joinStatus == ChampionshipsConstant.JOIN_STATUS_CLOSE) {
          holder.btnAttendDetail.setEnabled(false);
        } else if (joinStatus == ChampionshipsConstant.JOIN_STATUS_MEMBER) {
          if (is_members == 1) {
            holder.btnAttendDetail.setEnabled(false);
          } else {
            holder.btnAttendDetail.setEnabled(true);
          }
        } else {
          holder.btnAttendDetail.setEnabled(true);
        }
      } else {
//                holder.btnAttendDetail.setText(R.string.lbl_match_attend);
//                holder.btnAttendDetail.setBackgroundResource(R.drawable.selector_match_btn_sign_up2);
//                holder.btnAttendDetail.setTextColor(Color.parseColor("#FFFFFF"));
        holder.btnAttendDetail.setText(R.string.lbl_match_right_now);
        holder.btnAttendDetail.setBackgroundResource(R.drawable.selector_match_btn_sign_up);
        holder.btnAttendDetail.setTextColor(Color.parseColor("#25282C"));
      }
    } else if (matchStatus == ChampionshipsConstant.MATCH_STATUS_FINISH) {
      holder.btnAttendDetail.setBackgroundResource(R.drawable.selector_match_btn_sign_up_3);
      holder.btnAttendDetail.setEnabled(true);
      holder.btnAttendDetail.setText(R.string.association_match_check_ranking);
    }

    if (matchStatus == ChampionshipsConstant.MATCH_STATUS_NOT_STARTED) {
      holder.tvStatus.setVisibility(View.GONE);
      holder.tvStatus.setText(R.string.association_match_status_no_start);
    } else if (matchStatus == ChampionshipsConstant.MATCH_STATUS_PLAYING) {
      holder.tvStatus.setVisibility(View.VISIBLE);
      holder.tvStatus.setText(R.string.association_match_status_playing);
      holder.tvStatus.setBackgroundResource(R.drawable.mark_match_status_list_item_playing);
    } else if (matchStatus == ChampionshipsConstant.MATCH_STATUS_FINISH) {
      holder.tvStatus.setVisibility(View.VISIBLE);
      holder.tvStatus.setText(R.string.association_match_status_finish);
      holder.tvStatus.setBackgroundResource(R.drawable.mark_match_status_list_item_finish);
    }

    if(data.getMatchImage().startsWith(HTTP)){
      Picasso.with(mContext)
          .load(data.getMatchImage()).transform(new CircleTransform(mContext,28,28)).resize(480, 480)
          .error(R.mipmap.match_sub_logo_default)
          //.fit().centerCrop()
          .into(holder.tvImage);
    }else{
      Picasso.with(mContext)
          .load(Constant.getBaseUrl()+"/"+data.getMatchImage()).transform(new CircleTransform(mContext,28,28)).resize(480, 480)
          .error(R.mipmap.match_sub_logo_default)
          //.fit().centerCrop()
          .into(holder.tvImage);
    }

    if (data.getIsHot() == 1) {
      holder.ivHot.setVisibility(View.VISIBLE);
    } else {
      holder.ivHot.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public int getItemCount() {
    return dataInfo.size();
  }

  public interface OnItemClickListener {
    static int BUTTON = 0;
    static int DETAIL = 1;
    void onItemClick(View view, int type, MatchMenu data);
  }

}
