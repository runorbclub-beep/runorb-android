package com.cloud.runball.module.clan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;

public class ClanNoticeDialog extends BaseDialog {

  private TextView tvJoinClan, tvCreateClan;

  public ClanNoticeDialog(Context context) {
    super(context, R.layout.dialog_clan_notice);
  }

  @Override
  protected void onContentView(View contentView) {
    ImageView ivClose = contentView.findViewById(R.id.ivClose);
    ivClose.setOnClickListener(v -> {
      if (dialog != null) {
        dialog.dismiss();
      }
    });

    tvJoinClan = contentView.findViewById(R.id.tvJoinClan);
    tvCreateClan = contentView.findViewById(R.id.tvCreateClan);
  }

  public void setOnJoinClanCallback(OnJoinClanCallback callback) {
    tvJoinClan.setVisibility(View.VISIBLE);
    tvJoinClan.setOnClickListener(view -> {
      if (callback != null) {
        callback.onClick(dialog);
      }
    });
  }

  public void setOnCreateClanCallback(OnCreateClanCallback callback) {
    tvCreateClan.setVisibility(View.VISIBLE);
    tvCreateClan.setOnClickListener(view -> {
      if (callback != null) {
        callback.onClick(dialog);
      }
    });
  }

  public interface OnJoinClanCallback {
    void onClick(Dialog dialog);
  }

  public interface OnCreateClanCallback {
    void onClick(Dialog dialog);
  }

}
