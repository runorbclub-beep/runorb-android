package com.cloud.runball.module.mine_record.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.bean.MinePkInfoV2;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class MinePkRecordAdapter extends RecyclerView.Adapter<MinePkRecordAdapter.ViewHolder> {

  private List<MinePkInfoV2> data;
  private OnItemClickListener listener;

  public MinePkRecordAdapter(List<MinePkInfoV2> data) {
    this.data = data;
  }

  public void setListener(OnItemClickListener listener) {
    this.listener = listener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_mine_pk_record, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    MinePkInfoV2 itemData = data.get(position);

    holder.tvPkDatetime.setText(itemData.getStopTime());

    if (TextUtils.isEmpty(itemData.getDistance())) {
      holder.tvDistanceLeft.setText("0km");
    } else {
      DecimalFormat mDecimalFormat = new DecimalFormat("0.000");
      String distanceFormat = mDecimalFormat.format(Float.parseFloat(itemData.getDistance()));
      holder.tvDistanceLeft.setText(distanceFormat + "km");
    }

    if (TextUtils.isEmpty(itemData.getbDistance())) {
      holder.tvDistanceRight.setText("0km");
    } else {
      DecimalFormat mDecimalFormat = new DecimalFormat("0.000");
      String distanceFormat = mDecimalFormat.format(Float.parseFloat(itemData.getbDistance()));
      holder.tvDistanceRight.setText(distanceFormat + "km");
    }


    if (itemData.getIsWin() == 1) {
      holder.tvPkDatetime.setBackgroundResource(R.mipmap.bg_pk_result_datetime_win);
      holder.tvPkResult.setText(R.string.pk_win);
      holder.layResult.setBackgroundResource(R.drawable.bg_pk_result_circular_win);
      holder.tvLeftMark.setBackgroundResource(R.drawable.bg_pk_result_mark_left_win);
      holder.tvRightMark.setBackgroundResource(R.drawable.bg_pk_result_mark_right_fail);
      holder.tvDistanceLeft.setTextColor(Color.parseColor("#FDE833"));
      holder.tvDistanceRight.setTextColor(Color.parseColor("#ADADAD"));
      holder.tvPortraitLeft.setBackgroundResource(R.drawable.border_portrait);
      holder.tvPortraitLeft.setTextColor(Color.parseColor("#FDE833"));
      holder.tvPortraitRight.setBackgroundResource(R.drawable.border_oval_gray);
      holder.tvPortraitRight.setTextColor(Color.parseColor("#ADADAD"));
      holder.ivPortraitLeft.setBackgroundResource(R.drawable.border_portrait);
      holder.ivPortraitRight.setBackgroundResource(R.drawable.border_oval_gray);

    } else {
      holder.tvPkDatetime.setBackgroundResource(R.mipmap.bg_pk_result_datetime_fail);
      holder.tvPkResult.setText(R.string.pk_fail);
      holder.layResult.setBackgroundResource(R.drawable.bg_pk_result_circular_fail);
      holder.tvLeftMark.setBackgroundResource(R.drawable.bg_pk_result_mark_left_fail);
      holder.tvRightMark.setBackgroundResource(R.drawable.bg_pk_result_mark_right_win);
      holder.tvDistanceLeft.setTextColor(Color.parseColor("#ADADAD"));
      holder.tvDistanceRight.setTextColor(Color.parseColor("#FDE833"));
      holder.tvPortraitLeft.setBackgroundResource(R.drawable.border_oval_gray);
      holder.tvPortraitLeft.setTextColor(Color.parseColor("#ADADAD"));
      holder.tvPortraitRight.setBackgroundResource(R.drawable.border_portrait);
      holder.tvPortraitRight.setTextColor(Color.parseColor("#FDE833"));
      holder.ivPortraitLeft.setBackgroundResource(R.drawable.border_oval_gray);
      holder.ivPortraitRight.setBackgroundResource(R.drawable.border_portrait);
    }

    if (itemData.getPkType() == 1) {
      holder.tvPkType.setText(R.string.pk_type_team);
      holder.tvPortraitLeft.setVisibility(View.VISIBLE);
      holder.tvPortraitRight.setVisibility(View.VISIBLE);
      holder.ivPortraitLeft.setVisibility(View.GONE);
      holder.ivPortraitRight.setVisibility(View.GONE);
      holder.tvPortraitLeft.setText(itemData.getMyCount() + holder.itemView.getContext().getString(R.string.person_count));
      holder.tvPortraitRight.setText(itemData.getbCount() + holder.itemView.getContext().getString(R.string.person_count));
    } else {
      holder.tvPkType.setText(R.string.pk_type_personal);
      holder.tvPortraitLeft.setVisibility(View.GONE);
      holder.tvPortraitRight.setVisibility(View.GONE);
      holder.ivPortraitLeft.setVisibility(View.VISIBLE);
      holder.ivPortraitRight.setVisibility(View.VISIBLE);
      if (itemData.getUserImg().startsWith("http")) {
        Picasso.with(holder.itemView.getContext())
            .load(itemData.getUserImg())
            .transform(new CircleTransform(holder.itemView.getContext()))
            .into(holder.ivPortraitLeft);
      } else {
        Picasso.with(holder.itemView.getContext())
            .load(Constant.getBaseUrl() + "/" + itemData.getUserImg())
            .transform(new CircleTransform(holder.itemView.getContext()))
            .into(holder.ivPortraitLeft);
      }
      if (itemData.getbUserImg().startsWith("http")) {
        Picasso.with(holder.itemView.getContext())
            .load(itemData.getbUserImg())
            .transform(new CircleTransform(holder.itemView.getContext()))
            .into(holder.ivPortraitRight);
      } else {
        Picasso.with(holder.itemView.getContext())
            .load(Constant.getBaseUrl() + "/" + itemData.getbUserImg())
            .transform(new CircleTransform(holder.itemView.getContext()))
            .into(holder.ivPortraitRight);
      }
    }

    holder.itemView.setOnClickListener(v -> {
      if (listener != null) {
        listener.onItemClick(itemData);
      }
    });
  }

  @Override
  public int getItemCount() {
    return data == null ? 0 : data.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView tvPkDatetime;

    LinearLayout layResult;
    TextView tvPkResult;
    TextView tvPkType;

    TextView tvLeftMark;
    TextView tvRightMark;

    ImageView ivPortraitLeft;
    TextView tvPortraitLeft;
    TextView tvDistanceLeft;

    ImageView ivPortraitRight;
    TextView tvPortraitRight;
    TextView tvDistanceRight;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      tvPkDatetime = itemView.findViewById(R.id.tvPkDatetime);
      layResult = itemView.findViewById(R.id.layResult);
      tvPkResult = itemView.findViewById(R.id.tvPkResult);
      tvPkType = itemView.findViewById(R.id.tvPkType);
      tvLeftMark = itemView.findViewById(R.id.tvLeftMark);
      tvRightMark = itemView.findViewById(R.id.tvRightMark);
      ivPortraitLeft = itemView.findViewById(R.id.ivPortraitLeft);
      tvPortraitLeft = itemView.findViewById(R.id.tvPortraitLeft);
      tvDistanceLeft = itemView.findViewById(R.id.tvDistanceLeft);
      ivPortraitRight = itemView.findViewById(R.id.ivPortraitRight);
      tvPortraitRight = itemView.findViewById(R.id.tvPortraitRight);
      tvDistanceRight = itemView.findViewById(R.id.tvDistanceRight);
    }
  }

  public interface OnItemClickListener {
    void onItemClick(MinePkInfoV2 itemData);
  }

}
