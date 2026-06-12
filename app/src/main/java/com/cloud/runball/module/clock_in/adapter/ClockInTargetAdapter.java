package com.cloud.runball.module.clock_in.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.model.ClockInTarget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClockInTargetAdapter extends RecyclerView.Adapter<ClockInTargetAdapter.ViewHolder> {

  private List<ClockInTargetItem> data;

  private OnItemClickListener listener;

  public ClockInTargetAdapter(List<ClockInTargetItem> data) {
    this.data = data;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_clock_in_target, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ClockInTargetItem itemData = data.get(position);
    ClockInTarget clockInTarget = itemData.getClockInTarget();

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
    if (dateFormat.format(new Date()).equals(dateFormat.format(itemData.getDate()))) {
      holder.ivSpot.setImageResource(R.mipmap.spot_yellow);
      holder.tvMonth.setTextColor(Color.parseColor("#F7DC29"));
      holder.tvYear.setTextColor(Color.parseColor("#F7DC29"));
    } else {
      holder.ivSpot.setImageResource(R.mipmap.spot_grey);
      holder.tvMonth.setTextColor(Color.parseColor("#ADADAD"));
      holder.tvYear.setTextColor(Color.parseColor("#ADADAD"));
    }


    holder.tvMonth.setText(new SimpleDateFormat(holder.itemView.getContext().getString(R.string.format_month), Locale.getDefault()).format(itemData.getDate()));
    holder.tvYear.setText(new SimpleDateFormat(holder.itemView.getContext().getString(R.string.format_year), Locale.getDefault()).format(itemData.getDate()));


    if (itemData.isEmpty()) {
      holder.layContent.setVisibility(View.GONE);
      holder.progressBar.setVisibility(View.GONE);
      holder.tvCompleteDay.setVisibility(View.GONE);
      holder.tvStatusInProgress.setVisibility(View.GONE);
      holder.tvStatusIsComplete.setVisibility(View.GONE);
      if (itemData.isOverdue()) {
        holder.layAdd.setVisibility(View.GONE);
        holder.layText.setVisibility(View.VISIBLE);
      } else {
        holder.layAdd.setVisibility(View.VISIBLE);
        holder.layText.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(v -> {
          if (listener != null) {
            listener.onEdit(itemData);
          }
        });
      }
    } else {
      holder.layAdd.setVisibility(View.GONE);
      holder.layText.setVisibility(View.GONE);
      holder.tvTargetDatNum.setText(holder.itemView.getContext().getString(R.string.target_day_num, clockInTarget.getMinDays() + ""));
      holder.tvDatTargetDistance.setText(holder.itemView.getContext().getString(R.string.target_day_distance, clockInTarget.getTargetDistance() + ""));
      holder.layAdd.setVisibility(View.GONE);
      holder.layContent.setVisibility(View.VISIBLE);

      // 是否完成：0否 1进行中 2完成 3未开始
      if (clockInTarget.getStatus() == 0) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.progressBar.setMax(clockInTarget.getMinDays());
        holder.progressBar.setProgress(clockInTarget.getFulfilDays());
        holder.progressBar.setProgressDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.progress_drawable2));
        holder.tvCompleteDay.setVisibility(View.VISIBLE);
        holder.tvCompleteDay.setText(clockInTarget.getFulfilDays() + "/" + clockInTarget.getMinDays());
        holder.tvStatusInProgress.setVisibility(View.GONE);
        holder.tvStatusIsComplete.setVisibility(View.VISIBLE);
        holder.tvStatusIsComplete.setImageResource(R.mipmap.close_gray);
        holder.ivProgressSpot.setVisibility(View.GONE);
      } else if (clockInTarget.getStatus() == 1) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.progressBar.setMax(clockInTarget.getMinDays());
        holder.progressBar.setProgress(clockInTarget.getFulfilDays());
        holder.progressBar.setProgressDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.progress_drawable));
        holder.tvCompleteDay.setVisibility(View.VISIBLE);
        holder.tvCompleteDay.setText(clockInTarget.getFulfilDays() + "/" + clockInTarget.getMinDays());
        holder.tvStatusInProgress.setVisibility(View.VISIBLE);
        holder.tvStatusInProgress.setText(R.string.progress_in);
        holder.tvStatusIsComplete.setVisibility(View.GONE);
        holder.ivProgressSpot.setVisibility(View.VISIBLE);
        holder.itemView.setOnClickListener(v -> {
          if (listener != null) {
            listener.onEdit(itemData);
          }
        });
      } else if (clockInTarget.getStatus() == 2) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.progressBar.setMax(clockInTarget.getMinDays());
        holder.progressBar.setProgress(clockInTarget.getFulfilDays());
        holder.tvCompleteDay.setVisibility(View.VISIBLE);
        holder.tvCompleteDay.setText(clockInTarget.getFulfilDays() + "/" + clockInTarget.getMinDays());
        holder.tvStatusInProgress.setVisibility(View.GONE);
        holder.tvStatusIsComplete.setVisibility(View.VISIBLE);
        holder.tvStatusIsComplete.setImageResource(R.mipmap.ok_yellow);
        holder.ivProgressSpot.setVisibility(View.GONE);
      } else if (clockInTarget.getStatus() == 3) {
        holder.progressBar.setVisibility(View.GONE);
        holder.tvCompleteDay.setVisibility(View.GONE);
        holder.tvStatusInProgress.setVisibility(View.GONE);
        holder.tvStatusIsComplete.setVisibility(View.GONE);
        holder.ivProgressSpot.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(v -> {
          if (listener != null) {
            listener.onEdit(itemData);
          }
        });
      }


    }
  }

  @Override
  public int getItemCount() {
    return data == null ? 0 : data.size();
  }

  public void setListener(OnItemClickListener listener) {
    this.listener = listener;
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    private LinearLayout layAdd;
    private ConstraintLayout layContent;

    private TextView tvYear;
    private TextView tvMonth;
    private ImageView ivSpot;
    private TextView tvTargetDatNum;
    private TextView tvDatTargetDistance;
    private ProgressBar progressBar;
    private TextView tvCompleteDay;
    private TextView tvStatusInProgress;
    private ImageView tvStatusIsComplete;

    private ImageView ivProgressSpot;

    private  TextView layText;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      layAdd = itemView.findViewById(R.id.layAdd);
      layContent = itemView.findViewById(R.id.layContent);

      tvYear = itemView.findViewById(R.id.tvYear);
      tvMonth = itemView.findViewById(R.id.tvMonth);
      ivSpot = itemView.findViewById(R.id.ivSpot);
      tvTargetDatNum = itemView.findViewById(R.id.tvTargetDatNum);
      tvDatTargetDistance = itemView.findViewById(R.id.tvDatTargetDistance);
      progressBar = itemView.findViewById(R.id.progressBar);
      tvCompleteDay = itemView.findViewById(R.id.tvCompleteDay);
      tvStatusInProgress = itemView.findViewById(R.id.tvStatusInProgress);
      tvStatusIsComplete = itemView.findViewById(R.id.tvStatusIsComplete);

      ivProgressSpot = itemView.findViewById(R.id.ivProgressSpot);

      layText = itemView.findViewById(R.id.layText);
    }
  }

  public interface OnItemClickListener {
    void onEdit(ClockInTargetItem itemDate);
  }

}
