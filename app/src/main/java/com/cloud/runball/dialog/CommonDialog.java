package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;

public class CommonDialog extends BaseDialog {

  private TextView tvTitle;
  private TextView tvContent;
  private LinearLayout layButton;

  public CommonDialog(Context context) {
    super(context, R.layout.dialog_common);
  }

  @Override
  protected void onContentView(View contentView) {
    tvTitle = contentView.findViewById(R.id.tvTitle);
    tvContent = contentView.findViewById(R.id.tvContent);
    layButton = contentView.findViewById(R.id.layButton);
  }

  public void setContent(String title, String tip) {
    if (title == null || "".equals(title)) {
      tvTitle.setVisibility(View.GONE);
    } else {
      tvTitle.setVisibility(View.VISIBLE);
      tvTitle.setText(title);
    }
    tvContent.setText(tip);
  }

  public void setContent(String title, Spanned tip) {
    tvTitle.setText(title);
    tvContent.setText(tip);
  }

  public void addBtn(String text, OnClickCallback callback) {
    TextView view = (TextView) LayoutInflater.from(dialog.getContext()).inflate(R.layout.item_dialog_common_btn, layButton, false);
    view.setText(text);
    view.setOnClickListener(v -> {
      if (callback != null) {
        callback.onClick(dialog);
      }
    });
    layButton.addView(view);
  }

  public interface OnClickCallback {
    void onClick(Dialog commonDialog);
  }

}
