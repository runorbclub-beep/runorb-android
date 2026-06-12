package com.cloud.runball.widget.calendar;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ClockInCalendarViewAdapter extends RecyclerView.Adapter<ClockInCalendarViewAdapter.ViewHolder> {

  private List<ClockInCalendarItemData> data;
  private int curYear;
  private int curMonth;
  private int curDay;

  private OnItemClickListener listener;

  public ClockInCalendarViewAdapter(List<ClockInCalendarItemData> data) {
    this.data = data;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    curYear = calendar.get(Calendar.YEAR);
    curMonth = calendar.get(Calendar.MONTH);
    curDay = calendar.get(Calendar.DAY_OF_MONTH);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_clock_in_calendar, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ClockInCalendarItemData itemData = data.get(position);
    if (itemData.isPlaceholder()) {
      holder.tvDay.setVisibility(View.GONE);
      holder.tvDistance.setVisibility(View.GONE);
    } else {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(itemData.getDate());
      int itemYear = calendar.get(Calendar.YEAR);
      int itemMonth = calendar.get(Calendar.MONTH);
      int itemDay = calendar.get(Calendar.DAY_OF_MONTH);

      if (itemYear == curYear && itemMonth == curMonth && itemDay == curDay) {
        holder.tvDay.setBackgroundResource(R.drawable.bg_item_clock_in_today);
      } else {
        holder.tvDay.setBackgroundColor(Color.TRANSPARENT);
      }
      holder.tvDay.setVisibility(View.VISIBLE);
      holder.tvDay.setText(itemDay + "");

      if (itemData.getDistance() > 0) {
        holder.tvDistance.setVisibility(View.VISIBLE);
        holder.tvDistance.setText(new BigDecimal(itemData.getDistance() + "").setScale(1, BigDecimal.ROUND_DOWN).toString());
        if (!TextUtils.isEmpty(itemData.getUnit())) {
          holder.tvDistance.append(itemData.getUnit());
        }
        if (!TextUtils.isEmpty(itemData.getTargetDistance()) && itemData.getDistance() > Float.parseFloat(itemData.getTargetDistance())) {
          holder.tvDistance.setBackgroundResource(R.drawable.bg_item_clock_in_distance_yellow);
          holder.tvDistance.setTextColor(Color.parseColor("#1E1D1F"));
        } else {
          holder.tvDistance.setBackgroundResource(R.drawable.bg_item_clock_in_distance_gray);
          holder.tvDistance.setTextColor(Color.parseColor("#FFFFFF"));
        }
      } else {
        holder.tvDistance.setVisibility(View.GONE);
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
    return data.size();
  }

  public void setOnItemClickListener(OnItemClickListener listener) {
    this.listener = listener;
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    private TextView tvDay;
    private TextView tvDistance;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      tvDay = itemView.findViewById(R.id.tvDay);
      tvDistance = itemView.findViewById(R.id.tvDistance);
    }
  }

  public interface OnItemClickListener {
    void onItemClick(ClockInCalendarItemData itemData);
  }

}
