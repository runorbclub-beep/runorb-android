package com.cloud.runball.module.match_football_association.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;
import com.cloud.runball.constant.SexConstant;

public class AssociationMatchTipDialog extends BaseDialog {

  private Callback callback;
  private TextView tvJoinMatchUnits;

  public AssociationMatchTipDialog(Context context) {
    super(context, R.layout.dialog_association_match_tip);
  }

  @Override
  protected void onContentView(View contentView) {
    tvJoinMatchUnits = contentView.findViewById(R.id.tvJoinMatchUnits);

    contentView.findViewById(R.id.tvCancel).setOnClickListener(view -> {
      if (callback != null) {
        callback.onCancel(dialog);
      }
    });

    contentView.findViewById(R.id.tvSubmit).setOnClickListener(view -> {
      if (callback != null) {
        callback.onSubmit(dialog);
      }
    });
  }

  public void setCallback(String joinMatchUnits, Callback callback) {
    tvJoinMatchUnits.setText(joinMatchUnits);
    this.callback = callback;
  }

  public interface Callback {
    void onSubmit(Dialog dialog);
    void onCancel(Dialog dialog);
  }

}
