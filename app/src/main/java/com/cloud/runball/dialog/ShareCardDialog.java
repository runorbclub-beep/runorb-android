package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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

public class ShareCardDialog {

  static View mView;
  static Dialog dialog;

  public static void show(
      Context context,
      String userName, String area, String portraitPath,
      long date, float dataMinute, float exponent,
      int halfMarathon, int marathon,
      String maxRpm, float distance, String costTime,
      String qrCode,
      ConfirmCallBack confirmCallBack
  ) {
    if (dialog == null) {
      mView = LayoutInflater.from(context).inflate(R.layout.dialog_share_card, null);
      ImageView ivClose = mView.findViewById(R.id.ivClose);
      ivClose.setOnClickListener(v -> {
        if (confirmCallBack != null) {
          dismiss();
          confirmCallBack.onCancel();
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
      mView.findViewById(R.id.tvMore).setOnClickListener(v -> {
        if (confirmCallBack != null) {
          dismiss();
          confirmCallBack.onMore();
        }
      });

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
      tvExponent.setText(String.format("YPL：%s", exponent));

      TextView tvHalfMarathon = mView.findViewById(R.id.tvHalfMarathon);
      if (halfMarathon <= 0) {
        tvHalfMarathon.setVisibility(View.GONE);
        tempSpacingCount++;
      } else {
        tvHalfMarathon.setVisibility(View.VISIBLE);
      }
      tvHalfMarathon.setText(context.getString(R.string.lbl_main_match_match_half_time, TimeUtils.formatDurationFull(halfMarathon)));

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
      tvMaxRpm.setText(maxRpm);

      TextView tvDistance = mView.findViewById(R.id.tvDistance);
      tvDistance.setText(BallUtils.formatDistance(distance));

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

  public static void showShareUpup(
      Context context,
      String userName, String area, String portraitPath,
      String date, int upupStatus, int horsePath, int horseNum,
      String horseTitle, int helpPlayers, double helpDistance, int ranking,
      double myHelpDistance, int myHelpTime, int score,
      String qrCode,
      ConfirmCallBack confirmCallBack
  ) {
    if (dialog == null) {
      mView = LayoutInflater.from(context).inflate(R.layout.dialog_share_card_upup, null);
      ImageView ivClose = mView.findViewById(R.id.ivClose);
      ivClose.setOnClickListener(v -> {
        if (confirmCallBack != null) {
          dismiss();
          confirmCallBack.onCancel();
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
      mView.findViewById(R.id.tvMore).setOnClickListener(v -> {
        if (confirmCallBack != null) {
          dismiss();
          confirmCallBack.onMore();
        }
      });

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
      tvDate.setText(date);

      ImageView ivHorse = mView.findViewById(R.id.ivHorse);

      // 0：未开始 1：进行中 2：开始报名 3：已结束
      TextView tvUpupStatus = mView.findViewById(R.id.tvUpupStatus);
      if (upupStatus == 1) {
        tvUpupStatus.setText("(" + context.getString(R.string.lbl_other_match_status_2) + ")");
        tvUpupStatus.setTextColor(Color.parseColor("#11C867"));
        if(horsePath == 0) {
          ivHorse.setBackgroundResource(R.drawable.horse_1_animate_6);
        } else if(horsePath == 1) {
          ivHorse.setBackgroundResource(R.drawable.horse_2_animate_6);
        } else if(horsePath == 2) {
          ivHorse.setBackgroundResource(R.drawable.horse_3_animate_6);
        } else if(horsePath == 3) {
          ivHorse.setBackgroundResource(R.drawable.horse_4_animate_6);
        } else if(horsePath == 4) {
          ivHorse.setBackgroundResource(R.drawable.horse_5_animate_6);
        } else if(horsePath == 5) {
          ivHorse.setBackgroundResource(R.drawable.horse_6_animate_6);
        } else if(horsePath == 6) {
          ivHorse.setBackgroundResource(R.drawable.horse_7_animate_6);
        } else if(horsePath == 7) {
          ivHorse.setBackgroundResource(R.drawable.horse_8_animate_6);
        } else {
          ivHorse.setBackgroundResource(R.drawable.horse_1_animate_6);
        }
      } else if (upupStatus == 3) {
        tvUpupStatus.setText("(" + context.getString(R.string.lbl_other_match_status_3) + ")");
        tvUpupStatus.setTextColor(Color.parseColor("#E26863"));
        if(horsePath == 0) {
          ivHorse.setBackgroundResource(R.drawable.horse_1_stop);
        } else if(horsePath == 1) {
          ivHorse.setBackgroundResource(R.drawable.horse_2_stop);
        } else if(horsePath == 2) {
          ivHorse.setBackgroundResource(R.drawable.horse_3_stop);
        } else if(horsePath == 3) {
          ivHorse.setBackgroundResource(R.drawable.horse_4_stop);
        } else if(horsePath == 4) {
          ivHorse.setBackgroundResource(R.drawable.horse_5_stop);
        } else if(horsePath == 5) {
          ivHorse.setBackgroundResource(R.drawable.horse_6_stop);
        } else if(horsePath == 6) {
          ivHorse.setBackgroundResource(R.drawable.horse_7_stop);
        } else if(horsePath == 7) {
          ivHorse.setBackgroundResource(R.drawable.horse_8_stop);
        } else {
          ivHorse.setBackgroundResource(R.drawable.horse_1_stop);
        }
      }

      TextView tvHorseNum = mView.findViewById(R.id.tvHorseNum);
      tvHorseNum.setText(context.getString (R.string.lbl_main_match_index, (horseNum + 1)));

      TextView tvHorseTitle = mView.findViewById(R.id.tvHorseTitle);
      tvHorseTitle.setText(horseTitle);

      TextView tvHelpPlayers = mView.findViewById(R.id.tvHelpPlayers);
      String prefixHelpPlayers = context.getString(R.string.lbl_main_match_match_help_players_label);
      String textHelpPlayers = helpPlayers+"";
      SpannableStringBuilder HelpPlayersStyle = new SpannableStringBuilder(prefixHelpPlayers + textHelpPlayers);
      HelpPlayersStyle.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")),0, prefixHelpPlayers.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
      tvHelpPlayers.setText(HelpPlayersStyle);

      TextView tvHelpDistance = mView.findViewById(R.id.tvHelpDistance);
      String prefixHelpDistance = context.getString(R.string.lbl_main_match_match_help_distance_label);
      String textHelpDistance = BallUtils.formatDistance(helpDistance);
      SpannableStringBuilder HelpDistanceStyle = new SpannableStringBuilder(prefixHelpDistance + textHelpDistance);
      HelpDistanceStyle.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")),0, prefixHelpDistance.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
      tvHelpDistance.setText(HelpDistanceStyle);

      TextView tvRanking = mView.findViewById(R.id.tvRanking);
      String prefixRanking = context.getString(R.string.lbl_main_match_match_ranking_label);
      String textRanking = ""+ranking;
      SpannableStringBuilder RankingStyle = new SpannableStringBuilder(prefixRanking + textRanking);
      RankingStyle.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")),0, prefixRanking.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
      tvRanking.setText(RankingStyle);

      TextView tvMyHelpDistance = mView.findViewById(R.id.tvMyHelpDistance);
      tvMyHelpDistance.setText(context.getString(R.string.lbl_main_match_record_my_distance, BallUtils.formatDistance(myHelpDistance)));

      TextView tvMyHelpTime = mView.findViewById(R.id.tvMyHelpTime);
//            tvMyHelpTime.setText("总助力时间：" + myHelpTime);
      tvMyHelpTime.setVisibility(View.GONE);

      TextView tvScore = mView.findViewById(R.id.tvScore);
      if (upupStatus == 3) {
        tvScore.setVisibility(View.VISIBLE);
        tvScore.setText(context.getString(R.string.lbl_main_match_record_score) + score);
      } else {
        tvScore.setVisibility(View.GONE);
      }

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
