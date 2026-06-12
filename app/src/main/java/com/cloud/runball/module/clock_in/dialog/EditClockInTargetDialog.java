package com.cloud.runball.module.clock_in.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;
import com.cloud.runball.model.ClockInTarget;
import com.cloud.runball.module.clock_in.adapter.ClockInTargetItem;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditClockInTargetDialog extends BaseDialog {

  private TextView tvStartMonth;
  private EditText etMonth;
  private EditText etDay;
  private EditText etDistance;

  private ImageView ivClose;
  private TextView tvSubmit;

  private ClockInTargetItem data;

  private OnCallback onCallback;

  public EditClockInTargetDialog(Context context) {
    super(context, R.layout.dialog_add_clock_in_target);
  }

  @Override
  protected void onContentView(View contentView) {
    tvStartMonth = contentView.findViewById(R.id.tvStartMonth);
    etMonth = contentView.findViewById(R.id.etMonth);
    etMonth.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        String value = etMonth.getText().toString();
        if (TextUtils.isEmpty(value)) {
          return;
        }
        if (Integer.parseInt(value) > 12) {
          etMonth.setText(12 + "");
        }
      }
    });

    etDay = contentView.findViewById(R.id.etDay);
    etDay.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        String value = etDay.getText().toString();
        if (TextUtils.isEmpty(value)) {
          return;
        }
        if (Integer.parseInt(value) > 28) {
          etDay.setText(28 + "");
        }
      }
    });

    etDistance = contentView.findViewById(R.id.etDistance);

    ivClose = contentView.findViewById(R.id.ivClose);
    ivClose.setOnClickListener(v -> {
      dialog.dismiss();
    });

    tvSubmit = contentView.findViewById(R.id.tvSubmit);
    tvSubmit.setOnClickListener(v -> {
      if (onCallback != null) {
        String startYearMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(data.getDate());
        int monthNum = 1;
        if (!TextUtils.isEmpty(etMonth.getText())) {
          if (Integer.parseInt(etMonth.getText().toString()) > 0) {
            monthNum = Integer.parseInt(etMonth.getText().toString());
          }
        }
        int minDay = 20;
        if (!TextUtils.isEmpty(etDay.getText())) {
          if (Integer.parseInt(etDay.getText().toString()) > 0) {
            minDay = Integer.parseInt(etDay.getText().toString());
          }
        }
        int targetDistance = 20;
        if (!TextUtils.isEmpty(etDistance.getText())) {
          if (new BigDecimal(etDistance.getText().toString()).intValue() > 0) {
            targetDistance = new BigDecimal(etDistance.getText().toString()).intValue();
          }
        }
        onCallback.onSubmit(
            this,
            startYearMonth,
            monthNum - 1,
            minDay,
            targetDistance);
      }
    });
  }

  public void setOnCallback(ClockInTargetItem data, OnCallback onCallback) {
    this.data = data;
    this.onCallback = onCallback;

    tvStartMonth.setText(new SimpleDateFormat(dialog.getContext().getString(R.string.format_start_year_month), Locale.getDefault()).format(data.getDate()));
    if (!data.isEmpty()) {
      ClockInTarget clockInTarget = data.getClockInTarget();
      etDay.setText(clockInTarget.getMinDays() + "");

      if (!TextUtils.isEmpty(clockInTarget.getTargetDistance())) {
        if (new BigDecimal(clockInTarget.getTargetDistance()).intValue() > 0) {
          etDistance.setText(new BigDecimal(clockInTarget.getTargetDistance()).intValue() + "");
        }
      }
    }
  }

  public interface OnCallback {
    void onSubmit(EditClockInTargetDialog dialog, String startYearMonth, int monthNum, int minDay, int targetDistance);
  }

}
