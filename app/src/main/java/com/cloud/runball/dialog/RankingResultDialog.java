package com.cloud.runball.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.utils.QrCodeUtils;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;

public class RankingResultDialog extends BaseDialog {

  private ImageView ivPortrait;
  private TextView tvUserName;
  private TextView tvCreateTime;
  private ImageView ivQrCode;

  private TextView tvMaxSpeed;
  private TextView tvTurnOneMinute;
  private TextView tvExponent;
  private TextView tvMarathon;
  private TextView tvDuration;
  private TextView tvTurnMetre;

  private ImageView ivClose;
  private TextView tvShare;

  private ConfirmCallBack confirmCallBack;

  public RankingResultDialog(Context context) {
    super(context, R.layout.dialog_ranking_result);
  }

  @Override
  protected void onContentView(View contentView) {
    ivPortrait = contentView.findViewById(R.id.ivPortrait);
    tvUserName = contentView.findViewById(R.id.tvUserName);
    tvCreateTime = contentView.findViewById(R.id.tvCreateTime);
    ivQrCode = contentView.findViewById(R.id.ivQrCode);
    tvMaxSpeed = contentView.findViewById(R.id.tvMaxSpeed);
    tvTurnOneMinute = contentView.findViewById(R.id.tvTurnOneMinute);
    tvExponent = contentView.findViewById(R.id.tvExponent);
    tvMarathon = contentView.findViewById(R.id.tvMarathon);
    tvDuration = contentView.findViewById(R.id.tvDuration);
    tvTurnMetre = contentView.findViewById(R.id.tvTurnMetre);

    ivClose = contentView.findViewById(R.id.ivClose);
    ivClose.setOnClickListener(v -> {
      dialog.dismiss();
    });

    tvShare = contentView.findViewById(R.id.tvShare);
    tvShare.setOnClickListener(v -> {
      if (confirmCallBack != null) {
        Bitmap bitmap = getViewBitmap(contentView.findViewById(R.id.layContent));
        confirmCallBack.onShare(bitmap);
      }
    });
  }

  public void setData(
      String portraitUrl, String userName, String createTime, String qrCode,
      float maxSpeed, String turnOneMinute, String exponent, String marathon,
      String duration, String turnMetre, ConfirmCallBack confirmCallBack) {

    this.confirmCallBack = confirmCallBack;

    if (!portraitUrl.startsWith("http")) {
      portraitUrl = Constant.getBaseUrl() + "/" + portraitUrl;
    }
    Picasso.with(ivPortrait.getContext())
        .load(portraitUrl)
        .transform(new CircleTransform(ivPortrait.getContext()))
        .into(ivPortrait);

    tvUserName.setText(userName);

    tvCreateTime.setText(createTime);

    int qrCodeBitmapSize = (int) (ivQrCode.getResources().getDisplayMetrics().density * 70);
    Bitmap qrCodeBitmap = QrCodeUtils.createQRCodeBitmap(qrCode, qrCodeBitmapSize, qrCodeBitmapSize);
    qrCodeBitmap = QrCodeUtils.addLogo(qrCodeBitmap, BitmapFactory.decodeResource(ivQrCode.getResources(), R.mipmap.logo));
    ivQrCode.setImageBitmap(qrCodeBitmap);

    tvMaxSpeed.setText(maxSpeed + "");
    tvTurnOneMinute.setText(turnOneMinute);
    tvExponent.setText(exponent);
    tvMarathon.setText(marathon);
    tvDuration.setText(duration);
    tvTurnMetre.setText(turnMetre);
  }

  public interface ConfirmCallBack {
    void onCancel();
    void onShare(Bitmap bitmap);
  }

  private static Bitmap getViewBitmap(View view) {
    view.clearFocus();
    view.setPressed(false);
    boolean willNotCache = view.willNotCacheDrawing();
    view.setWillNotCacheDrawing(false);
    int color = view.getDrawingCacheBackgroundColor();
    view.setDrawingCacheBackgroundColor(0);
    if (color != 0) {
      view.destroyDrawingCache();
    }
    view.buildDrawingCache();
    Bitmap cacheBitmap = view.getDrawingCache();
    if (cacheBitmap == null) {
      return null;
    }
    Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
    view.destroyDrawingCache();
    view.setWillNotDraw(willNotCache);
    view.setDrawingCacheBackgroundColor(color);
    return bitmap;
  }

}
