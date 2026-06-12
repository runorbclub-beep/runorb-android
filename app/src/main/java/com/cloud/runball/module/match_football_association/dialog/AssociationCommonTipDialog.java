package com.cloud.runball.module.match_football_association.dialog;

import android.content.Context;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;

public class AssociationCommonTipDialog extends BaseDialog {

  private Toolbar toolbar;
  private TextView tvContent;

  public AssociationCommonTipDialog(Context context) {
    super(context, R.layout.dialog_association_common_tip);
  }

  @Override
  protected void onContentView(View contentView) {
    toolbar = contentView.findViewById(R.id.toolbar);
    tvContent = contentView.findViewById(R.id.tvContent);
  }

  public void setReturn(OnReturnListener listener) {
    toolbar.setVisibility(View.VISIBLE);
    toolbar.setNavigationOnClickListener(v -> {
      if (listener != null) {
        listener.onClick();
      }
    });
  }

  public void setContent(String tip) {
    tvContent.setText(tip);
  }

  public void setContent(Spanned tip) {
    tvContent.setText(tip);
  }

  public interface OnReturnListener {
    void onClick();
  }

}
