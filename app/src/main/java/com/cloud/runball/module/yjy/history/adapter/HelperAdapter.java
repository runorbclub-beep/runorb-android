package com.cloud.runball.module.yjy.history.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.bean.OtherMatchDetailInfo;
import com.cloud.runball.bean.yjy.YJYHelperRankModel;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.module.home.adapter
 * @ClassName: OtherMatchListAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/21 14:46
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/21 14:46
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class HelperAdapter extends RecyclerView.Adapter<HelperAdapter.ViewHolder>{

  DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

  private final List<YJYHelperRankModel.ShakeInfo> list = new ArrayList<>();
  private Context mContext;
  private boolean isShowTag = false;

  public HelperAdapter(Context context, List<YJYHelperRankModel.ShakeInfo> datas, boolean isShowTag){
    this.mContext=context;
    this.list.addAll(datas);
    this.isShowTag = isShowTag;
  }

  public void addData(List<YJYHelperRankModel.ShakeInfo> infos) {
    this.list.addAll(infos);
    this.notifyDataSetChanged();
  }

  public void setData(List<YJYHelperRankModel.ShakeInfo> infos) {
    this.list.clear();
    this.list.addAll(infos);
    this.notifyDataSetChanged();
  }


  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_yjy_match_helper_history, parent, false);
    final HelperAdapter.ViewHolder viewHolder = new HelperAdapter.ViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    YJYHelperRankModel.ShakeInfo result = list.get(position);
    if (position == 0) {
      holder.img_rank.setVisibility(View.VISIBLE);
      holder.img_rank.setBackgroundResource(R.mipmap.match_range_1);
    } else if(position == 1){
      holder.img_rank.setVisibility(View.VISIBLE);
      holder.img_rank.setBackgroundResource(R.mipmap.match_range_2);
    }else if(position == 2){
      holder.img_rank.setVisibility(View.VISIBLE);
      holder.img_rank.setBackgroundResource(R.mipmap.match_range_3);
    }else{
      holder.img_rank.setVisibility(View.INVISIBLE);
      holder.tvRank.setVisibility(View.GONE);
      holder.tvRank.setText(String.valueOf(position+1));
    }

    holder.layTag.setVisibility(isShowTag ? View.VISIBLE : View.GONE);

    holder.tvName.setText(result.getTitle());
    Drawable drawableSex = null;
    if (SexConstant.SEX_MAN.equals(result.getUsr_user().getSys_sex_id())) {
      drawableSex = holder.itemView.getResources().getDrawable(R.mipmap.ic_man);
      drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
    } else if (SexConstant.SEX_WOMEN.equals(result.getUsr_user().getSys_sex_id())) {
      drawableSex = holder.itemView.getResources().getDrawable(R.mipmap.ic_women);
      drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
    }
    holder.tvHelperName.setCompoundDrawables(drawableSex, null, null, null);
    holder.tvHelperName.setText(result.getUsr_user().getUser_name());
    holder.tvIndex.setText(String.format(mContext.getResources().getString(R.string.lbl_main_match_index), result.getIndex() + 1));

    double distance = Double.parseDouble(result.getDistance()) / 1000.0f;
    if(distance>0 && position >= 3){
      holder.tvRank.setVisibility(View.VISIBLE);
    }else{
      holder.tvRank.setVisibility(View.GONE);
    }

    holder.tvDistance.setText(String.format(mContext.getResources().getString(R.string.lbl_main_match_record_distance2),formatDistance(distance)));
    holder.tvScore.setText(String.format(mContext.getResources().getString(R.string.lbl_main_match_record_score_1),String.valueOf(result.getIntegral())));

    String imgUrl;
    if(result.getUsr_user().getUser_img().startsWith("http")) {
      imgUrl = result.getUsr_user().getUser_img();
    } else {
      imgUrl = Constant.getBaseUrl() + "/" + result.getUsr_user().getUser_img();
    }
    Picasso.with(holder.itemView.getContext())
        .load(imgUrl)
        .transform(new CircleTransform(holder.itemView.getContext()))
        .placeholder(R.mipmap.default_head)
        .into(holder.ivHead);
  }

  @Override
  public int getItemCount() {
    return list!=null?list.size():0;
  }


  private String formatDistance(double distance){
    if(distance<1000){
      return mDecimalFormat.format(distance)+"km";
    }else if(distance>=1000 && distance < 10000){
      return mDecimalFormat.format(distance/1000.0)+"k km";
    }else{
      return mDecimalFormat.format(distance/10000.0)+"w km";
    }
  }

  private String formatNum(int num){
    if(num<1000){
      return String.valueOf(num);
    }else if(num>=1000 && num < 10000){
      return String.valueOf(num/1000)+"k";
    }else{
      return String.valueOf(num/10000.0)+"w";
    }
  }




  class ViewHolder extends RecyclerView.ViewHolder {
    ImageView img_rank;
    ImageView ivHead;
    TextView tvIndex;
    TextView tvName;
    TextView tvRank;
    TextView tvHelperName;
    TextView tvScore;
    TextView tvDistance;
    LinearLayout layTag;

    View myView;

    public ViewHolder(View itemView) {
      super(itemView);
      myView = itemView;
      img_rank = itemView.findViewById(R.id.img_rank);
      ivHead = itemView.findViewById(R.id.ivHead);
      tvRank= itemView.findViewById(R.id.tvRank);
      tvIndex = itemView.findViewById(R.id.tvIndex);
      tvName = itemView.findViewById(R.id.tvName);
      tvScore = itemView.findViewById(R.id.tvScore);
      tvHelperName = itemView.findViewById(R.id.tvHelperName);
      tvDistance = itemView.findViewById(R.id.tvDistance);

      layTag = itemView.findViewById(R.id.layTag);

    }
  }
}
