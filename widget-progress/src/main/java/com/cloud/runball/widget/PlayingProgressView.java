package com.cloud.runball.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlayingProgressView extends View {

  private Paint barBgPaint;
  private Paint progressPaint;
  private int barBgColor;
  private int barProgressColorStart;
  private int barProgressColorEnd;
  private float barHeight;

  private Paint lightSpotPaint;

  private Paint lightNodePaint;
  private Paint darkNodePaint;
  private float nodeSize;
  private int nodeColorLight;
  private int nodeColorDark;
  private float nodeTextPadding;
  private Paint nodeArriveTextPaint;
  private int nodeArriveTextColor;
  private float nodeArriveTextSize;

  private Paint textPaint;
  private int textColor;
  private String barStartText;
  private float barStartTextPadding;

  private float barTextSize;

  private final List<NodeInfo> nodeInfoList = new ArrayList<>();

  private float progressValue = 0;

  private Bitmap lightBitmap;

  public PlayingProgressView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PlayingProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
    setLayerType(LAYER_TYPE_SOFTWARE, null);
  }

  private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PlayingProgressView, defStyleAttr, 0);
    barBgColor = ta.getColor(R.styleable.PlayingProgressView_bar_bg_color, Color.GRAY);
    barProgressColorStart = ta.getColor(R.styleable.PlayingProgressView_bar_progress_color_start, Color.RED);
    barProgressColorEnd = ta.getColor(R.styleable.PlayingProgressView_bar_progress_color_end, Color.RED);
    barHeight = ta.getDimension(R.styleable.PlayingProgressView_bar_height, 50);

    barStartText = ta.getString(R.styleable.PlayingProgressView_bar_start_text);
    barStartTextPadding = ta.getDimension(R.styleable.PlayingProgressView_bar_start_text_padding, 50);

    barTextSize = ta.getDimension(R.styleable.PlayingProgressView_bar_text_size, 50);
    textColor = ta.getColor(R.styleable.PlayingProgressView_bar_text_color, Color.GRAY);

    nodeArriveTextColor = ta.getColor(R.styleable.PlayingProgressView_node_arrive_text_color, Color.BLUE);
    nodeArriveTextSize = ta.getDimension(R.styleable.PlayingProgressView_node_arrive_text_size, 50);
    nodeSize = ta.getDimension(R.styleable.PlayingProgressView_node_size, 50) / 2;
    nodeColorLight = ta.getColor(R.styleable.PlayingProgressView_node_color_light, Color.parseColor("#FDE833"));
    nodeColorDark = ta.getColor(R.styleable.PlayingProgressView_node_color_dark, Color.parseColor("#FDE833"));
    nodeTextPadding = ta.getDimension(R.styleable.PlayingProgressView_node_text_padding, 50);

    progressValue = ta.getFloat(R.styleable.PlayingProgressView_progress_init, 0);
    ta.recycle();

    lightBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.node_ok);

    barBgPaint = new Paint();
    barBgPaint.setColor(barBgColor);
    barBgPaint.setStyle(Paint.Style.FILL);
    barBgPaint.setStrokeCap(Paint.Cap.ROUND);
    barBgPaint.setStrokeWidth(barHeight);
    barBgPaint.setAntiAlias(true);

    progressPaint = new Paint();
    progressPaint.setColor(barProgressColorStart);
    progressPaint.setStyle(Paint.Style.FILL);
    progressPaint.setStrokeCap(Paint.Cap.ROUND);
    progressPaint.setStrokeWidth(barHeight);
    progressPaint.setAntiAlias(true);

    lightSpotPaint = new Paint();
    lightSpotPaint.setColor(Color.parseColor("#FFFFFF"));
    lightSpotPaint.setStrokeWidth(barHeight);
//    lightSpotPaint.setMaskFilter(new BlurMaskFilter(barHeight * 1 / 5, BlurMaskFilter.Blur.NORMAL));
    lightSpotPaint.setAntiAlias(true);

    textPaint = new Paint();
    textPaint.setColor(textColor);
    textPaint.setStrokeWidth(8);
    textPaint.setTextSize(barTextSize);
    textPaint.setStyle(Paint.Style.FILL);
    textPaint.setAntiAlias(true);
//    textPaint.setTextAlign(Paint.Align.LEFT);

    nodeArriveTextPaint = new Paint();
    nodeArriveTextPaint.setColor(nodeArriveTextColor);
    nodeArriveTextPaint.setStrokeWidth(8);
    nodeArriveTextPaint.setTextSize(nodeArriveTextSize);
    nodeArriveTextPaint.setStyle(Paint.Style.FILL);
    nodeArriveTextPaint.setAntiAlias(true);

    lightNodePaint = new Paint();
    lightNodePaint.setColor(nodeColorLight);
    lightNodePaint.setStyle(Paint.Style.FILL);
//    lightNodePaint.setShadowLayer(nodeSize / 5, - nodeSize / 10, nodeSize / 10, Color.parseColor("#000000"));
//    lightNodePaint.setMaskFilter(
//        new EmbossMaskFilter(new float[]{nodeSize, nodeSize, 30}, 0.2f, 60, nodeSize * 2)
//    );
    lightNodePaint.setMaskFilter(new BlurMaskFilter(nodeSize / 10, BlurMaskFilter.Blur.INNER));
    lightNodePaint.setAntiAlias(true);

    darkNodePaint = new Paint();
    darkNodePaint.setColor(nodeColorDark);
    darkNodePaint.setStyle(Paint.Style.FILL);
//    darkNodePaint.setShadowLayer(nodeSize / 5, - nodeSize / 10, nodeSize / 10, Color.parseColor("#000000"));
//    darkNodePaint.setMaskFilter(
//        new EmbossMaskFilter(new float[]{nodeSize, nodeSize, 30}, 0.2f, 60, nodeSize * 2)
//    );
    darkNodePaint.setMaskFilter(new BlurMaskFilter(nodeSize / 10, BlurMaskFilter.Blur.INNER));
    darkNodePaint.setAntiAlias(true);

  }

  private NodeInfo timeNode = null;

  public void setProgressNode(NodeInfo timeNode, List<NodeInfo> data) {
    if (!nodeInfoList.isEmpty()) {
      nodeInfoList.clear();
    }
    nodeInfoList.add(new NodeInfo(NodeConstant.TYPE_START, 0, "", "", "", 0));
    if (timeNode != null) {
      this.timeNode = timeNode;
      nodeInfoList.add(timeNode);
    }
    nodeInfoList.addAll(data);

    progressValue = 0;
//    Handler handler = new Handler();
//    handler.postDelayed(() -> {
      invalidate();
//    }, 1000);

  }

  public void setProgressValue(int time, float distance) {
    if (timeNode != null) {
      if (time <= timeNode.getNodeValue()) {
        progressValue = time / timeNode.getNodeValue() * timeNode.getProgressValue();
        invalidate();
        return;
      }
    }
    if (distance >= nodeInfoList.get(nodeInfoList.size() - 1).getNodeValue()) {
      progressValue = 1F;
      invalidate();
      return;
    }

    progressValue = distance / nodeInfoList.get(nodeInfoList.size() - 1).getNodeValue() * (1 - (timeNode == null ? 0 : timeNode.getProgressValue())) + (timeNode == null ? 0 :timeNode.getProgressValue());
    invalidate();
  }

  public void setArriveTip(int i, String arriveText) {
    nodeInfoList.get(i).setArriveTip(arriveText);
    invalidate();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    Rect bound = new Rect();
    textPaint.getTextBounds(barStartText, 0, barStartText.length(), bound);

    float progressStartX = bound.width() + barStartTextPadding + getPaddingLeft() + nodeSize;
    float progressEndX = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - nodeSize;
    float progressY = getMeasuredHeight() >> 1;
    canvas.drawLine(progressStartX, progressY, progressEndX, progressY, barBgPaint);

    float curPositionValue = progressStartX + progressValue * (progressEndX - progressStartX);
    progressPaint.setShader(new LinearGradient(progressStartX, progressY, progressEndX, progressY, barProgressColorStart, barProgressColorEnd, Shader.TileMode.CLAMP));
    canvas.drawLine(progressStartX, progressY, curPositionValue, progressY, progressPaint);
    canvas.drawCircle(curPositionValue, progressY, barHeight * 2 / 5, lightSpotPaint);

    for (int i = 0; i < nodeInfoList.size(); i++) {
      NodeInfo nodeInfo = nodeInfoList.get(i);
      float nodeX = progressStartX + nodeInfo.getProgressValue() * (progressEndX - progressStartX);
      float nodeY = getMeasuredHeight() >> 1;

      if (i == 0) {
        canvas.drawCircle(nodeX, nodeY, nodeSize, lightNodePaint);
      } else if (curPositionValue < nodeX) {
        canvas.drawCircle(nodeX, nodeY, nodeSize, darkNodePaint);
        Rect arriveTipBound = new Rect();
        nodeArriveTextPaint.getTextBounds(nodeInfo.getArriveSeatTip(), 0, nodeInfo.getArriveSeatTip().length(), arriveTipBound);
        canvas.drawText(
            nodeInfo.getArriveSeatTip(),
            nodeX - (arriveTipBound.width() >> 1),
            (getMeasuredHeight() >> 1) - barHeight - nodeTextPadding,
            nodeArriveTextPaint
        );
      } else {
//        canvas.drawCircle(nodeX, nodeY, nodeSize, lightNodePaint);
        canvas.drawBitmap(lightBitmap, nodeX - nodeSize, nodeY - nodeSize, lightNodePaint);
        if (nodeInfo.getArriveTip() != null && !"".equals(nodeInfo.getArriveTip())) {
          Rect arriveTipBound = new Rect();
          nodeArriveTextPaint.getTextBounds(nodeInfo.getArriveTip(), 0, nodeInfo.getArriveTip().length(), arriveTipBound);
          canvas.drawText(
              nodeInfo.getArriveTip(),
              nodeX - (arriveTipBound.width() >> 1),
              (getMeasuredHeight() >> 1) - barHeight - nodeTextPadding,
              nodeArriveTextPaint
          );
        } else {
          Rect arriveTipBound = new Rect();
          nodeArriveTextPaint.getTextBounds(nodeInfo.getArriveSeatTip(), 0, nodeInfo.getArriveSeatTip().length(), arriveTipBound);
          canvas.drawText(
              nodeInfo.getArriveSeatTip(),
              nodeX - (arriveTipBound.width() >> 1),
              (getMeasuredHeight() >> 1) - barHeight - nodeTextPadding,
              nodeArriveTextPaint
          );
        }
      }

      if (nodeInfo.getTitle() != null && !"".equals(nodeInfo.getTitle())) {
        Rect titleBound = new Rect();
        textPaint.getTextBounds(nodeInfo.getTitle(), 0, nodeInfo.getTitle().length(), titleBound);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        canvas.drawText(
            nodeInfo.getTitle(),
            nodeX - (titleBound.width() >> 1),
            (getMeasuredHeight() >> 1) + fontMetrics.bottom + barHeight * 2 + nodeTextPadding,
            textPaint
        );
      }

      if (nodeInfo.getViceTitle() != null && !"".equals(nodeInfo.getViceTitle())) {
        Rect titleBound = new Rect();
        textPaint.getTextBounds(nodeInfo.getViceTitle(), 0, nodeInfo.getViceTitle().length(), titleBound);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        canvas.drawText(
            nodeInfo.getViceTitle(),
            nodeX - (titleBound.width() >> 1),
            (getMeasuredHeight() >> 1) + fontMetrics.bottom + barHeight * 2 + nodeTextPadding + titleBound.height() + 10,
            textPaint
        );
      }

    }

    if (barStartText != null && !"".equals(barStartText.trim())) {
      Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
      canvas.drawText(barStartText, getPaddingLeft(), (getMeasuredHeight() >> 1) + fontMetrics.bottom, textPaint);
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
    //宽高都设置为wrap_content
    if(widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
      //宽设置为wrap_content
      setMeasuredDimension(widthSpecSize, (int) barHeight + getPaddingTop() + getPaddingBottom());
    } else if(widthSpecMode == MeasureSpec.AT_MOST) {
      setMeasuredDimension(widthSpecSize, heightSpecSize);
    } else if(heightSpecMode == MeasureSpec.AT_MOST) {
      //高设置为wrap_content
      setMeasuredDimension(widthSpecSize, (int) barHeight + getPaddingTop() + getPaddingBottom());
    } else {
      //宽高都设置为match_parent或具体的dp值
      setMeasuredDimension(widthSpecSize, heightSpecSize);
    }
  }

}
