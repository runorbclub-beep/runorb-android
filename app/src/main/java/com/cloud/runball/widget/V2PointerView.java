package com.cloud.runball.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.cloud.runball.R;

public class V2PointerView extends View {

  // 内层扇形颜色
  private int startColor, centerColor, endColor;
  // 外层环形颜色
  private int ringStartColor, ringCenterColor, ringEndColor;
  // 外层环形宽度
  private float ringWidth;
  // 扇形张开角度
  private float angle;

  private ValueAnimator animator;
  // 是否开启过度动画效果
  private boolean isAnimator = true;

  // 控件中心点
  private final int[] centerPoint = { 0, 0 };

  private Paint paint;
  private Paint ringPaint;
  private final RectF rectF = new RectF();
  private int rectRadius;

  public V2PointerView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public V2PointerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initAttrs(context, attrs, defStyleAttr);
    initPaint();
    animator = new ValueAnimator();
  }

  private void initAttrs(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.V2PointerView, defStyleAttr, 0);
    startColor = ta.getInt(R.styleable.V2PointerView_startColor, 100);
    centerColor = ta.getInt(R.styleable.V2PointerView_centerColor, 100);
    endColor = ta.getInt(R.styleable.V2PointerView_endColor, 100);
    ringStartColor = ta.getInt(R.styleable.V2PointerView_ringStartColor, 100);
    ringCenterColor = ta.getInt(R.styleable.V2PointerView_ringCenterColor, 100);
    ringEndColor = ta.getInt(R.styleable.V2PointerView_ringEndColor, 100);
    angle = ta.getFloat(R.styleable.V2PointerView_angle, 0);
    ringWidth = ta.getDimension(R.styleable.V2PointerView_ringWidth, 0);
    isAnimator = ta.getBoolean(R.styleable.V2PointerView_isAnimator, true);
    if (angle > 300) {
      angle = 300;
    }
    ta.recycle();
  }

  private void initPaint() {
    // 内层扇形
    SweepGradient sweepGradient = new SweepGradient(centerPoint[0], centerPoint[1], startColor, endColor);
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setStyle(Paint.Style.FILL);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
    paint.setShader(sweepGradient);
    paint.setStrokeCap(Paint.Cap.ROUND);
    paint.setAntiAlias(true);

    // 外层圆环
    SweepGradient ringSweepGradient = new SweepGradient(centerPoint[0], centerPoint[1], ringStartColor, ringEndColor);
    ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    ringPaint.setStyle(Paint.Style.STROKE);
    ringPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
    ringPaint.setShader(ringSweepGradient);
    ringPaint.setStrokeWidth(ringWidth);
    ringPaint.setAntiAlias(true);
  }

  /**
   * 因控件大小变化，重新计算控件绘制的相关数值
   * @param width 控件宽度
   * @param height 控件高度
   */
  private void refreshRectF(int width, int height) {
    // 圆环宽度偏移值
    float ringOffsetWidth = ringWidth / 2;

    // 圆盘居中偏移值
    int offsetX = 0, offsetY = 0;
    if (width > height) {
      rectRadius = height;
      offsetX = (width - height) / 2;
    } else {
      rectRadius = width;
      offsetY = (height - width) / 2;
    }

    rectF.left = ringOffsetWidth + offsetX;
    rectF.top = ringOffsetWidth + offsetY;
    rectF.right = rectRadius - ringOffsetWidth + offsetX;
    rectF.bottom = rectRadius - ringOffsetWidth + offsetY;

    SweepGradient sweepGradient = new SweepGradient(centerPoint[0], centerPoint[1], startColor, endColor);
    paint.setShader(sweepGradient);

    SweepGradient ringSweepGradient = new SweepGradient(centerPoint[0], centerPoint[1], ringStartColor, ringEndColor);
    ringPaint.setShader(ringSweepGradient);

  }

  /**
   * 开启过度动画效果
   * @param isAnimator 是否开启
   */
  public void setAnimator(boolean isAnimator) {
    this.isAnimator = isAnimator;
  }

  /**
   * 设置当前转盘值角度
   * @param angle 角度
   */
  public void setAngle(float angle) {
    float newAngle;
    if (angle > 300) {
      newAngle = 300;
    } else if (angle < 0) {
      newAngle = 0;
    } else {
      newAngle = angle;
    }
    if (this.isAnimator) {
      animator.setFloatValues(this.angle, newAngle);
      animator.setDuration(1000);
      animator.removeAllUpdateListeners();
      animator.addUpdateListener(animation -> {
        this.angle = (float) animation.getAnimatedValue();
        invalidate();
      });
      animator.start();
    } else {
      invalidate();
    }

  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.save();

    canvas.rotate(60, centerPoint[0], centerPoint[1]);

    canvas.drawArc(rectF, 60, angle, false, ringPaint);
    canvas.drawArc(rectF, 60, angle, true, paint);

    canvas.restore();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldW, int oldH) {
    super.onSizeChanged(w, h, oldW, oldH);
    centerPoint[0] = w / 2;
    centerPoint[1] = h / 2;

    refreshRectF(w, h);
  }
}
