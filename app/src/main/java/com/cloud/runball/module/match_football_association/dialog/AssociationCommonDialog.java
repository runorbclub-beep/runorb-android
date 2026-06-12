package com.cloud.runball.module.match_football_association.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;

public class AssociationCommonDialog extends BaseDialog {

  private TextView tvTitle;
  private TextView tvContent;
  private LinearLayout layButton;

  public AssociationCommonDialog(Context context) {
    super(context, R.layout.dialog_association_common);
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

  public void addBtn(String text, boolean isSubmit, OnClickCallback callback) {
    LinearLayout itemView = (LinearLayout) LayoutInflater.from(dialog.getContext()).inflate(R.layout.item_association_dialog_common_btn, layButton, false);
    TextView tvBtn = itemView.findViewById(R.id.tvBtn);
    if (isSubmit) {
      tvBtn.setBackgroundResource(R.drawable.selector_match_btn_submit);
    } else {
      tvBtn.setBackgroundResource(R.drawable.selector_match_btn_cancel);
    }
    tvBtn.setText(text);
    tvBtn.setOnClickListener(v -> {
      if (callback != null) {
        callback.onClick(dialog);
      }
    });
    layButton.addView(itemView);
  }

  public interface OnClickCallback {
    void onClick(Dialog commonDialog);
  }

}
