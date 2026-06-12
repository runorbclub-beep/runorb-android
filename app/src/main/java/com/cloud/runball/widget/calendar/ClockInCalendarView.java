package com.cloud.runball.widget.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.bean.MonthDayDistanceInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClockInCalendarView extends FrameLayout {

  private RecyclerView rvCalender;
  private ClockInCalendarViewAdapter adapter;
  private final List<ClockInCalendarItemData> monthData = new ArrayList<>();
  private final List<ClockInCalendarItemData> data = new ArrayList<>();

  private ImageView ivOpen;
  private boolean isOpen = false;

  private int targetYear;
  private int targetMonth;

  public ClockInCalendarView(@NonNull Context context) {
    super(context);
    init(context);
  }

  public ClockInCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public ClockInCalendarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    View contentView = LayoutInflater.from(context).inflate(R.layout.view_clock_in_calendar, null);

    Calendar calendar = Calendar.getInstance();
    targetYear = calendar.get(Calendar.YEAR);
    targetMonth = calendar.get(Calendar.MONTH);

    rvCalender = contentView.findViewById(R.id.rvCalender);
    ivOpen = contentView.findViewById(R.id.ivOpen);
    ivOpen.setOnClickListener(v -> {
      if (isOpen) {
        isOpen = false;
        ivOpen.setImageResource(R.mipmap.ic_arrow_down_white);
      } else {
        isOpen = true;
        ivOpen.setImageResource(R.mipmap.ic_arrow_up_white);
      }
      refreshShow();
    });

    adapter = new ClockInCalendarViewAdapter(data);
    rvCalender.setAdapter(adapter);

    addView(contentView);
  }

  public void setItemClickListener(ClockInCalendarViewAdapter.OnItemClickListener listener) {
    if (adapter != null) {
      adapter.setOnItemClickListener(listener);
    }
  }

  public void setYearMonth(int year, int month, List<MonthDayDistanceInfo> distanceData, String targetDistance) {

    targetYear = year;
    targetMonth = month;

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
    int dayCount = calendar.getActualMaximum(Calendar.DATE);
    monthData.clear();
    for (int i = 0 ; i < weekDay - 1; i++) {
      monthData.add(new ClockInCalendarItemData(true));
    }
    for (int i = 0; i < dayCount; i++) {
      calendar.set(Calendar.DAY_OF_MONTH, i + 1);
      float distance = 0;
      String unit = null;
      for (int j = 0; j < distanceData.size(); j++) {
        Date date = calendar.getTime();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
        MonthDayDistanceInfo itemDistanceData = distanceData.get(j);
        if (dateStr.equals(itemDistanceData.getDate())) {
          distance = itemDistanceData.getDistance();
          unit = itemDistanceData.getUnit();
          break;
        }
      }
      monthData.add(new ClockInCalendarItemData(0, calendar.getTime(), distance, unit, targetDistance));
    }
    refreshShow();
  }

  private void refreshShow() {
    data.clear();
    if (isOpen) {
      data.addAll(monthData);
    } else {
      Calendar calendar = Calendar.getInstance();
      int week = calendar.get(Calendar.WEEK_OF_MONTH);

      calendar.setTime(new Date());
      int curYear = calendar.get(Calendar.YEAR);
      int curMonth = calendar.get(Calendar.MONTH);

      if (String.valueOf(targetYear).equals(String.valueOf(curYear)) && String.valueOf(targetMonth).equals(String.valueOf(curMonth))) {
        for (int i = (week - 1) * 7; i < week * 7; i++) {
          if (i >= monthData.size()) {
            monthData.add(new ClockInCalendarItemData(true));
          }
          data.add(monthData.get(i));
        }
      } else {
        for (int i = 0; i < 7; i++) {
          data.add(monthData.get(i));
        }
      }

    }
    adapter.notifyDataSetChanged();
  }

}
