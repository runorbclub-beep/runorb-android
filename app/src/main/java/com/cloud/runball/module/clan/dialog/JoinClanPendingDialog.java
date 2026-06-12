package com.cloud.runball.module.clan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;
import com.cloud.runball.constant.SexConstant;

public class JoinClanPendingDialog extends BaseDialog {

  private TextView tvInfo;
  private Callback callback;

  public JoinClanPendingDialog(Context context) {
    super(context, R.layout.dialog_join_clan_pending);
  }

  @Override
  protected void onContentView(View contentView) {
    ImageView ivClose = contentView.findViewById(R.id.ivClose);
    ivClose.setOnClickListener(v -> {
      if (dialog != null) {
        dialog.dismiss();
      }
    });

    tvInfo = contentView.findViewById(R.id.tvInfo);

    contentView.findViewById(R.id.tvFail).setOnClickListener(view -> {
      if (callback != null) {
        callback.onSubmit(dialog, false);
      }
    });
    contentView.findViewById(R.id.tvPass).setOnClickListener(view -> {
      if (callback != null) {
        callback.onSubmit(dialog, true);
      }
    });
  }

  public void setRemark(String remark) {
    tvInfo.setText(tvInfo.getContext().getString(R.string.format_join_remark, remark));
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public interface Callback {
    void onSubmit(Dialog dialog, boolean isPass);
  }

}
