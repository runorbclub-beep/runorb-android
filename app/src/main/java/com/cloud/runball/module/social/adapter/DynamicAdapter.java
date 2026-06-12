package com.cloud.runball.module.social.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.TimeUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.ViewHolder> {

  private List<ItemData> data;

  public DynamicAdapter(List<ItemData> data) {
    this.data = data;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_dynamic, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ItemData itemData = data.get(position);

    SimpleDateFormat dateFormat = new SimpleDateFormat(holder.itemView.getContext().getString(R.string.format_year_month_day), Locale.getDefault());
    holder.tvDatetime.setText(dateFormat.format(itemData.getDatetime() * 1000));
    String content = null;
    if (itemData.getType() == 0) {
      content = holder.itemView.getContext().getString(R.string.dynamic_content_max_speed, itemData.getContent());
    } else if (itemData.getType() == 1) {
      DecimalFormat mDecimalFormat = new DecimalFormat("0.000");
      String distanceFormat = mDecimalFormat.format(Float.parseFloat(itemData.getContent()) / 1000);
      content = holder.itemView.getContext().getString(R.string.dynamic_content_one_minute, distanceFormat);
    } else if (itemData.getType() == 2) {
      content = holder.itemView.getContext().getString(R.string.dynamic_content_exponent, itemData.getContent());
    } else if (itemData.getType() == 3) {
      String marathonFormat = TimeUtils.formatDuration3(Integer.parseInt(itemData.getContent()));
      content = holder.itemView.getContext().getString(R.string.dynamic_content_marathon, marathonFormat);
    } else if (itemData.getType() == 4) {
      content = holder.itemView.getContext().getString(R.string.dynamic_content_create);
    }
    holder.tvContent.setText(content);
  }

  @Override
  public int getItemCount() {
    return data == null ? 0 : data.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView tvDatetime;
    TextView tvContent;
    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      tvDatetime = itemView.findViewById(R.id.tvDatetime);
      tvContent = itemView.findViewById(R.id.tvContent);
    }

  }

  public static class ItemData {
    private int type;
    private long datetime;
    private String content;

    public ItemData(int type, long datetime, String content) {
      this.type = type;
      this.datetime = datetime;
      this.content = content;
    }

    public int getType() {
      return type;
    }

    public void setType(int type) {
      this.type = type;
    }

    public long getDatetime() {
      return datetime;
    }

    public void setDatetime(long datetime) {
      this.datetime = datetime;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }

}
