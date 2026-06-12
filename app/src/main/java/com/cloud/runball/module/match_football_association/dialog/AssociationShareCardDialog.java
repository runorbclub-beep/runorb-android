package com.cloud.runball.module.match_football_association.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.ScreenWindowManager;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.utils.BallUtils;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.utils.QrCodeUtils;
import com.cloud.runball.utils.ScreenUtils;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AssociationShareCardDialog {

  static View mView;
  static Dialog dialog;

  public static void show(
      Context context,
      String cardTitle,
      String userName, String area, String portraitPath,
      long date, float dataMinute, float exponent,
      int halfMarathon, int marathon,
      String maxRpm, float distance, String costTime,
      String qrCode,
      ConfirmCallBack confirmCallBack,
      boolean isEnd
  ) {
    if (dialog == null) {
      mView = LayoutInflater.from(context).inflate(R.layout.dialog_association_share_card, null);

      TextView tvCardTitle = mView.findViewById(R.id.tvCardTitle);
      tvCardTitle.setText(cardTitle);

      ImageView ivClose = mView.findViewById(R.id.ivClose);
      ivClose.setOnClickListener(v -> {
        if (confirmCallBack != null) {
          dismiss();
          if (isEnd) {
            confirmCallBack.onOther();
          } else {
            confirmCallBack.onCancel();
          }
        }
      });
      mView.findViewById(R.id.tvShare).setOnClickListener(v -> {
        if (confirmCallBack != null) {
          ivClose.setVisibility(View.GONE);
          Bitmap bitmap = getViewBitmap(mView.findViewById(R.id.lyContent));
          ivClose.setVisibility(View.VISIBLE);
          confirmCallBack.onShare(bitmap);
        }
      });
      if (isEnd) {
        mView.findViewById(R.id.tvMore).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.tvMore).setOnClickListener(v -> {
          if (confirmCallBack != null) {
            dismiss();
            confirmCallBack.onMore();
          }
        });
      } else {
        mView.findViewById(R.id.tvMore).setVisibility(View.GONE);
      }

      WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
      if (ScreenUtils.isNavigationBarShow(windowManager)) {
        NestedScrollView lyScrollView = mView.findViewById(R.id.lyScrollView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) lyScrollView.getLayoutParams();
        params.bottomMargin = (int) (context.getResources().getDisplayMetrics().density * 25);
        lyScrollView.setLayoutParams(params);
      }

      TextView tvUserName = mView.findViewById(R.id.tvUserName);
      tvUserName.setText(userName);
      TextView tvArea = mView.findViewById(R.id.tvArea);
      tvArea.setText(area);
      ImageView ivPortrait = mView.findViewById(R.id.ivPortrait);
      if (portraitPath.startsWith("http")) {
        Picasso.with(mView.getContext())
            .load(portraitPath)
            .transform(new CircleTransform(context))
            .into(ivPortrait);
      } else {
        Picasso.with(mView.getContext())
            .load(Constant.getBaseUrl() + "/" + portraitPath)
            .transform(new CircleTransform(context))
            .into(ivPortrait);
      }

      TextView tvDate = mView.findViewById(R.id.tvDate);
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
      tvDate.setText(format.format(new Date(date)));

      int tempSpacing = (int) context.getResources().getDisplayMetrics().density * 40;
      int tempSpacingCount = 0;

      TextView tvDataMinute = mView.findViewById(R.id.tvDataMinute);
      if (dataMinute <= 0) {
        tvDataMinute.setVisibility(View.GONE);
        tempSpacingCount++;
      } else {
        tvDataMinute.setVisibility(View.VISIBLE);
      }
      tvDataMinute.setText(context.getString(R.string.lbl_match_tab_rank_3) + "：" + BallUtils.formatDistance(dataMinute));

      TextView tvExponent = mView.findViewById(R.id.tvExponent);
      if (exponent <= 0) {
        tvExponent.setVisibility(View.GONE);
        tempSpacingCount++;
      } else {
        tvExponent.setVisibility(View.VISIBLE);
      }
      tvExponent.setText(context.getString(R.string.lbl_match_tab_rank_1) + String.format("：%s", exponent));

//      TextView tvHalfMarathon = mView.findViewById(R.id.tvHalfMarathon);
//      if (halfMarathon <= 0) {
//        tvHalfMarathon.setVisibility(View.GONE);
//        tempSpacingCount++;
//      } else {
//        tvHalfMarathon.setVisibility(View.VISIBLE);
//      }
//      tvHalfMarathon.setText(context.getString(R.string.lbl_main_match_match_half_time, TimeUtils.formatDurationFull(halfMarathon)));

      TextView tvMarathon = mView.findViewById(R.id.tvMarathon);
      if (marathon <= 0) {
        tvMarathon.setVisibility(View.GONE);
        tempSpacingCount++;
      } else {
        tvMarathon.setVisibility(View.VISIBLE);
      }
      tvMarathon.setText(context.getString(R.string.lbl_main_match_match_full_time, TimeUtils.formatDurationFull(marathon)));

      LinearLayout layAchievement = mView.findViewById(R.id.layAchievement);
      ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layAchievement.getLayoutParams();
      params.topMargin = tempSpacing * tempSpacingCount + (int) context.getResources().getDisplayMetrics().density * 10;
      layAchievement.setLayoutParams(params);

      TextView tvMaxRpm = mView.findViewById(R.id.tvMaxRpm);
      tvMaxRpm.setText(context.getString(R.string.lbl_match_tab_rank_5) + "：" + maxRpm + "(rpm)");

      TextView tvDistance = mView.findViewById(R.id.tvDistance);
      tvDistance.setText(BallUtils.formatDistanceNoKM(distance));

      TextView tvCostTime = mView.findViewById(R.id.tvCostTime);
      tvCostTime.setText(costTime);

      ImageView ivQrCode = mView.findViewById(R.id.ivQrCode);
      int qrCodeBitmapSize = (int) (context.getResources().getDisplayMetrics().density * 70);
      Bitmap qrCodeBitmap = QrCodeUtils.createQRCodeBitmap(qrCode, qrCodeBitmapSize, qrCodeBitmapSize);
      qrCodeBitmap = QrCodeUtils.addLogo(qrCodeBitmap, BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo));
      ivQrCode.setImageBitmap(qrCodeBitmap);

      dialog = new Dialog(context, R.style.dialog2);
      dialog.setCancelable(true);
      dialog.setContentView(mView);
      dialog.setCanceledOnTouchOutside(true);

      Window window = dialog.getWindow();
      WindowManager.LayoutParams lp = window.getAttributes();
      lp.width = ScreenWindowManager.widthScreen(context);
      lp.height = ScreenWindowManager.heightScreen(context);
      lp.alpha = 1.0f;
      window.setAttributes(lp);
      dialog.show();
    } else {
      if (!isShowing()) {
        dialog.show();
      }
    }
  }

  public static boolean isShowing() {
    if (dialog != null) {
      return dialog.isShowing();
    }
    return false;
  }


  public static void dismiss() {
    if (dialog != null && dialog.isShowing()) {
      dialog.dismiss();
      mView=null;
      dialog=null;
    }
  }

  public interface ConfirmCallBack {
    void onOther();
    void onCancel();
    void onMore();
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
