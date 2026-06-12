package com.cloud.runball.module.clan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;

public class JoinClanDialog extends BaseDialog {

  private EditText etInfo;

  private Callback callback;

  public JoinClanDialog(Context context) {
    super(context, R.layout.dialog_join_clan);
  }

  @Override
  protected void onContentView(View contentView) {
    etInfo = contentView.findViewById(R.id.etInfo);

    ImageView ivClose = contentView.findViewById(R.id.ivClose);
    ivClose.setOnClickListener(v -> {
      if (dialog != null) {
        dialog.dismiss();
      }
    });

    contentView.findViewById(R.id.tvSubmit).setOnClickListener(view -> {
      if (callback != null) {
        callback.onSubmit(dialog, etInfo.getText().toString());
      }
    });
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public interface Callback {
    void onSubmit(Dialog dialog, String remark);
  }

}
