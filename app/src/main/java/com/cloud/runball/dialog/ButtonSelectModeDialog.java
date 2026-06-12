package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;

import java.util.ArrayList;
import java.util.List;

public class ButtonSelectModeDialog extends BaseDialog {

  private TextView tvCancel;
  private RecyclerView rvList;
  private List<ModeInfo> data = new ArrayList<>();
  private int selectedPosition = -1;
  private OnModeClickListener listener;

  public ButtonSelectModeDialog(Context context) {
    super(context, R.layout.dialog_button_select_mode);
  }

  @Override
  protected void onContentView(View contentView) {
    tvCancel = contentView.findViewById(R.id.tvCancel);
    tvCancel.setOnClickListener(v -> {
      dismiss();
    });
    rvList = contentView.findViewById(R.id.rvList);
    ModeAdapter adapter = new ModeAdapter();
    GridLayoutManager layoutManager = new GridLayoutManager(contentView.getContext(), 2);
    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {
        if (position == 0) {
          return 2;
        }
        return 1;
      }
    });
    rvList.setLayoutManager(layoutManager);
    rvList.setAdapter(adapter);
  }

  public void setOnModeClickListener(int selectedPosition, List<ModeInfo> data, OnModeClickListener listener) {
    this.selectedPosition = selectedPosition;
    this.data.addAll(data);
    this.listener = listener;

  }

  public class ModeAdapter extends RecyclerView.Adapter<ModeViewHolder> {

    @NonNull
    @Override
    public ModeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_mode, parent, false);
      return new ModeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ModeViewHolder holder, int position) {
      holder.tvName.setText(data.get(position).name);

      if (selectedPosition == position) {
        holder.tvName.setBackgroundResource(R.drawable.selector_small_corner_selected);
        holder.tvName.setTextColor(Color.parseColor("#1E1D1F"));
      } else {
        holder.tvName.setBackgroundResource(R.drawable.selector_small_corner_unselected);
        holder.tvName.setTextColor(Color.parseColor("#FFFFFF"));
      }

      holder.itemView.setOnClickListener(v -> {
        selectedPosition = holder.getAbsoluteAdapterPosition();
        notifyDataSetChanged();
        if (listener != null) {
          listener.onClick(dialog, data.get(position));
        }
      });
    }

    @Override
    public int getItemCount() {
      return data.size();
    }
  }

  public class ModeViewHolder extends RecyclerView.ViewHolder {
    private TextView tvName;
    public ModeViewHolder(@NonNull View itemView) {
      super(itemView);
      tvName = itemView.findViewById(R.id.tvName);
    }
  }

  public static class ModeInfo {
    public String name;
    public int value;
    public ModeInfo(String name, int value) {
      this.name = name;
      this.value = value;
    }
  }

  public interface OnModeClickListener {
    void onClick(Dialog dialog, ModeInfo modeInfo);
  }

}
