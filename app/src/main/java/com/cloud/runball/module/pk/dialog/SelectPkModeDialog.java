package com.cloud.runball.module.pk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;

public class SelectPkModeDialog extends BaseDialog {

  private OnCallback onCallback;

  public SelectPkModeDialog(Context context) {
    super(context, R.layout.dialog_select_pk_mode);
  }

  @Override
  protected void onContentView(View contentView) {
    TextView tvModeDouble = contentView.findViewById(R.id.tvModeDouble);
    tvModeDouble.setOnClickListener(v -> {
      if (onCallback != null) {
        onCallback.onDoubleMode(dialog);
      }
    });

    TextView tvModeTeam = contentView.findViewById(R.id.tvModeTeam);
    tvModeTeam.setOnClickListener(v -> {
      if (onCallback != null) {
        onCallback.onTeamMode(dialog);
      }
    });

    TextView tvCancel = contentView.findViewById(R.id.tvCancel);
    tvCancel.setOnClickListener(v -> {
      if (onCallback != null) {
        onCallback.onCancel(dialog);
      }
    });
  }

  public void setOnCallback(OnCallback onCallback) {
    this.onCallback = onCallback;
  }

  public interface OnCallback {
    void onDoubleMode(Dialog dialog);
    void onTeamMode(Dialog dialog);
    void onCancel(Dialog dialog);
  }

}
