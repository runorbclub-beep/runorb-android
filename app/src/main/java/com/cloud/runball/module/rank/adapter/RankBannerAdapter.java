package com.cloud.runball.module.rank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloud.runball.R;
import com.cloud.runball.bean.banner.RankBannerData;
import com.cloud.runball.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: ImageAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/1 9:44
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/1 9:44
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankBannerAdapter extends RecyclerView.Adapter<RankBannerAdapter.ViewHolder> {

  private List<RankBannerData> dataInfo = new ArrayList<>();

  public RankBannerAdapter() {

  }

  public void setData(List<RankBannerData> infos){
    this.dataInfo = infos;
  }

  private RankBannerAdapter.OnItemClickListener onItemClickListener;


  public void setOnItemClickListener(RankBannerAdapter.OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  @NonNull
  @Override
  public RankBannerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_match_banner, parent, false);
    final RankBannerAdapter.ViewHolder viewHolder = new RankBannerAdapter.ViewHolder(view);
    //点击item的监听器
    viewHolder.myView.setOnClickListener(v -> {
      //程序执行到此，会去执行具体实现的onItemClick()方法
      if (onItemClickListener != null) {
        onItemClickListener.onItemClick(v, (RankBannerData) v.getTag());
      }
    });
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    RankBannerData data = dataInfo.get(position);
    holder.myView.setTag(data);
    if (data.getImgUrl().startsWith("http")) {
      Glide.with(holder.itemView)
          .load(data.getImgUrl())
          .error(R.mipmap.banner_default)
          .into(holder.ivImage);
    } else {
      Glide.with(holder.itemView)
          .load(Constant.getBaseUrl() + "/" + data.getImgUrl())
          .error(R.mipmap.banner_default)
          .into(holder.ivImage);
    }
  }

  @Override
  public int getItemCount() {
    return dataInfo != null ? dataInfo.size() : 0;
  }


  public interface OnItemClickListener {
    void onItemClick(View view, RankBannerData data);
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    ImageView ivImage;
    View myView;

    public ViewHolder(View itemView) {
      super(itemView);
      myView = itemView;
      ivImage = itemView.findViewById(R.id.ivImage);
    }
  }

}