package com.cloud.runball.module.match_football_association.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.model.RankMatchDetailModel;
import com.cloud.runball.module.match_football_association.entity.MatchDetailInfoItem;
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
public class MatchDetailOptionAdapter extends RecyclerView.Adapter<MatchDetailOptionAdapter.ViewHolder> {

  private List<MatchDetailInfoItem> dataInfo = new ArrayList<>();
  boolean isQuartets;
  String joinMatchUnits;

  public MatchDetailOptionAdapter(List<MatchDetailInfoItem> data) {
    this.dataInfo = data;
  }

  public void notifyDataSetChanged(List<MatchDetailInfoItem> data, boolean isQuartets, String joinMatchUnits) {
    this.dataInfo = data;
    this.isQuartets = isQuartets;
    this.joinMatchUnits = joinMatchUnits;
    this.notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_match_detail_option, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    MatchDetailInfoItem data = dataInfo.get(position);

    String label = data.getLabel();
    String labelValue = data.getValue();

    if (!TextUtils.isEmpty(labelValue)) {
      labelValue = labelValue.replaceAll("<div style='color:#767779'>", "")
          .replaceAll("</div>", "")
          .replaceAll("<p>", "")
          .replaceAll("</p>", "");
    }
    if (holder.itemView.getContext().getString(R.string.label_association_match_join).equals(data.getLabel())) {
      if (labelValue.contains("0")) {
        if (isQuartets) {
          labelValue = labelValue.replace(
              "0",
              holder.itemView.getContext().getString(R.string.limit)
                  + joinMatchUnits
                  + holder.itemView.getContext().getString(R.string.join)
          );
        } else {
          labelValue = labelValue.replace("0", holder.itemView.getContext().getString(R.string.association_match_join_type_open));
        }
      }
      if (labelValue.contains("1")) {
        labelValue = labelValue.replace("1", holder.itemView.getContext().getString(R.string.association_match_join_close));
      }
      if (labelValue.contains("2")) {
        labelValue = labelValue.replace("2", holder.itemView.getContext().getString(R.string.association_match_join_vip));
      }
    }

    String text = label + "\u0020\u0020\u0020" + labelValue;

    SpannableStringBuilder style=new SpannableStringBuilder(text);
    style.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")), 0, label.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
    style.setSpan(new ForegroundColorSpan(Color.parseColor("#ADADAD")), label.length(), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
    holder.tvContent.setText(style);



//    holder.tvTitle.setText(data.getLabel());
//    String labelValue = data.getValue();
//    if ("报名要求".equals(data.getLabel())) {
//      if (!TextUtils.isEmpty(labelValue)) {
//        labelValue = labelValue.replaceAll("<div style='color:#767779'>", "");
//        labelValue = labelValue.replaceAll("</div>", "");
//        if (labelValue.contains("0")) {
//          if (isQuartets) {
//            labelValue = labelValue.replace("0", "指定" + joinMatchUnits + "报名");
//          } else {
//            labelValue = labelValue.replace("0", "开放报名");
//          }
//        }
//        if (labelValue.contains("1")) {
//          labelValue = labelValue.replace("1", "关闭报名");
//        }
//        if (labelValue.contains("2")) {
//          labelValue = labelValue.replace("2", "会员报名");
//        }
//        holder.tvValues.setText(Html.fromHtml(labelValue));
//      } else {
//        holder.tvValues.setText("");
//      }
//    } else {
//      labelValue = labelValue
//          .replaceAll("<div style='color:#767779'>", "")
//          .replaceAll("</div>", "")
//          .replaceAll("<p>", "")
//          .replaceAll("</p>", "");
//      holder.tvValues.setText(Html.fromHtml(labelValue));
//    }

    //下载图片
    if (data.getIcon().startsWith("http")) {
      Picasso.with(holder.itemView.getContext())
          .load(data.getIcon())
          .into(holder.ivTitle);
    } else {
      Picasso.with(holder.itemView.getContext())
          .load(Constant.getBaseUrl() + "/" + data.getIcon())
          .into(holder.ivTitle);
    }
  }

  @Override
  public int getItemCount() {
    return dataInfo.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView ivTitle;
//    TextView tvTitle;
//    TextView tvValues;

    TextView tvContent;

    View myView;

    public ViewHolder(View itemView) {
      super(itemView);
      myView = itemView;
      ivTitle = itemView.findViewById(R.id.ivTitle);
//      tvTitle = itemView.findViewById(R.id.tvTitle);
//      tvValues = itemView.findViewById(R.id.tvValues);
      tvContent = itemView.findViewById(R.id.tvContent);
    }
  }

}
