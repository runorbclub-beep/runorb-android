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
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform2;
import com.squareup.picasso.Picasso;

import java.io.File;

public class EditClanDialog extends BaseDialog {

  private TextView tvTitle;
  private ImageView ivPortrait;
  private EditText etClanName;
  private EditText etClanIntroduction;
  private TextView tvClanArea;
  private EditText etClanContact;
  private EditText etClanRemark;

  private String oldClanName;
  private String oldClanAvatar;
  private String oldAddress;
  private String oldIntroduction;
  private String oldTelephone;
  private String oldCreatedTime;

  private View layRemark;

  private Callback callback;
  private boolean isEdit = false;

  public EditClanDialog(Context context) {
    super(context, R.layout.dialog_edit_clan);
  }

  @Override
  protected void onContentView(View contentView) {
    setLabelRedStar(contentView);

    tvTitle = contentView.findViewById(R.id.tvTitle);
    ivPortrait = contentView.findViewById(R.id.ivPortrait);
    ivPortrait.setOnClickListener(v -> {
      if (callback != null) {
        callback.onPortraitClick();
      }
    });
    etClanName = contentView.findViewById(R.id.etClanName);
    etClanIntroduction = contentView.findViewById(R.id.etClanIntroduction);
    tvClanArea = contentView.findViewById(R.id.tvClanArea);
    tvClanArea.setOnClickListener(v -> {
      if (callback != null) {
        callback.onAreaClick(tvClanArea.getText().toString());
      }
    });
    etClanContact = contentView.findViewById(R.id.etClanContact);
    etClanRemark = contentView.findViewById(R.id.etClanRemark);

    layRemark = contentView.findViewById(R.id.layRemark);

    ImageView ivClose = contentView.findViewById(R.id.ivClose);
    ivClose.setOnClickListener(v -> {
      if (dialog != null) {
        dialog.dismiss();
      }
    });

    contentView.findViewById(R.id.tvSubmit).setOnClickListener(view -> {
      if (callback != null) {
        if (isEdit) {
          String clanName = etClanName.getText().toString().equals(oldClanName) ? null : etClanName.getText().toString();
          String clanAvatar = ivPortrait.getTag() == null ? "" : ivPortrait.getTag().toString();
          if (clanAvatar.equals(oldClanAvatar)) {
            clanAvatar = null;
          }
          String address = tvClanArea.getText().toString().equals(oldAddress) ? null : tvClanArea.getText().toString();
          String introduction = etClanIntroduction.getText().toString().equals(oldIntroduction) ? null : etClanIntroduction.getText().toString();
          String telephone = etClanContact.getText().toString().equals(oldTelephone) ? null : etClanContact.getText().toString();
          String remark = null;
          callback.onSubmit(dialog, isEdit, clanName, clanAvatar, address, introduction, telephone, remark);
        } else {
          String clanName = etClanName.getText().toString();
          String clanAvatar = ivPortrait.getTag() == null ? "" : ivPortrait.getTag().toString();
          String address = tvClanArea.getText().toString();
          String introduction = etClanIntroduction.getText().toString();
          String telephone = etClanContact.getText().toString();
          String remark = etClanRemark.getText().toString();
          callback.onSubmit(dialog, isEdit, clanName, clanAvatar, address, introduction, telephone, remark);
        }
      }
    });
  }

  private void setLabelRedStar(View contentView) {
    TextView tvLabelPortrait = contentView.findViewById(R.id.tvLabelPortrait);
    TextView tvLabelClanName = contentView.findViewById(R.id.tvLabelClanName);
    TextView tvLabelClanArea = contentView.findViewById(R.id.tvLabelClanArea);
    TextView tvLabelContact = contentView.findViewById(R.id.tvLabelContact);
    TextView tvLabelRemark = contentView.findViewById(R.id.tvLabelRemark);

    ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#E26863"));
    SpannableStringBuilder builder;

    builder = new SpannableStringBuilder(tvLabelPortrait.getText());
    builder.setSpan(colorSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvLabelPortrait.setText(builder);

    builder = new SpannableStringBuilder(tvLabelClanName.getText());
    builder.setSpan(colorSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvLabelClanName.setText(builder);

    builder = new SpannableStringBuilder(tvLabelClanArea.getText());
    builder.setSpan(colorSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvLabelClanArea.setText(builder);

    builder = new SpannableStringBuilder(tvLabelContact.getText());
    builder.setSpan(colorSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvLabelContact.setText(builder);

    builder = new SpannableStringBuilder(tvLabelRemark.getText());
    builder.setSpan(colorSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvLabelRemark.setText(builder);
  }

  public void setDate(boolean isEdit) {
    this.isEdit = isEdit;
    if (isEdit) {
      tvTitle.setText(R.string.clan_edit);
      layRemark.setVisibility(View.GONE);
    } else {
      tvTitle.setText(R.string.clan_create);
      layRemark.setVisibility(View.VISIBLE);
    }
  }

  public void setDate(boolean isEdit, String clanName, String clanAvatar, String address, String introduction, String telephone, String createdTime) {
    oldClanName = clanName;
    oldClanAvatar = clanAvatar;
    oldAddress = address;
    oldIntroduction = introduction;
    oldTelephone = telephone;
    oldCreatedTime = createdTime;

    setDate(isEdit);
    etClanName.setText(clanName);

    String clanPortraitUrl = clanAvatar;
    if (!clanPortraitUrl.startsWith("http")) {
      clanPortraitUrl = Constant.getBaseUrl() + "/" + clanPortraitUrl;
    }
    Picasso.with(ivPortrait.getContext())
        .load(clanPortraitUrl)
        .transform(new CircleTransform2(ivPortrait.getContext(),480,480))
        .into(ivPortrait);
    if (clanAvatar.startsWith("http")) {
      ivPortrait.setTag(clanAvatar.replace(Constant.getBaseUrl() + "/", ""));
    } else {
      ivPortrait.setTag(clanAvatar);
    }
    tvClanArea.setText(address);
    etClanIntroduction.setText(introduction);
    etClanContact.setText(telephone);
    etClanRemark.setText(createdTime);
  }

  public void setPortrait(String netFileUrl, String filePath) {
    Picasso.with(ivPortrait.getContext())
        .load(netFileUrl)
        .transform(new CircleTransform2(ivPortrait.getContext(),480,480))
        .into(ivPortrait);
    ivPortrait.setTag(filePath);
  }

  public void setPortrait(File file, String filePath) {
    Picasso.with(ivPortrait.getContext())
        .load(file)
        .transform(new CircleTransform2(ivPortrait.getContext(),480,480))
        .into(ivPortrait);
    ivPortrait.setTag(filePath);
  }

  public void setArea(String area) {
    tvClanArea.setText(area);
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public interface Callback {
    void onPortraitClick();
    void onAreaClick(String area);
    void onSubmit(Dialog dialog, boolean isEdit, String clanName, String clanAvatar, String address, String introduction, String telephone, String remark);
  }

}
