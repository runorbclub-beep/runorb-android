package com.cloud.runball.basecomm.page;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseActivity extends AppCompatActivity {

  protected CompositeDisposable disposable = new CompositeDisposable();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    View preInflated = onCreateContentView(getLayoutInflater());
    if (preInflated != null) {
      setContentView(preInflated);
    } else {
      setContentView(onLayoutId());
    }
    onContent(savedInstanceState);
  }

  protected void setEmptyStatusBar() {
    Window window = getWindow();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.setStatusBarColor(Color.TRANSPARENT);
    }
    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    View immersiveView = getImmersiveView();
    if (immersiveView != null) {
      int height;
      int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
      if (resourceId > 0) {
        height = getResources().getDimensionPixelSize(resourceId);
        immersiveView.setPadding(immersiveView.getPaddingLeft(), immersiveView.getPaddingTop() + height, immersiveView.getPaddingRight(), immersiveView.getPaddingBottom());
      }
    }
  }

  protected View getImmersiveView() {
    return null;
  }

  /**
   * Optional hook for subclasses to provide a pre-inflated content view (e.g., ViewBinding.getRoot()).
   * If this returns non-null, BaseActivity will use it for setContentView instead of onLayoutId().
   */
  protected View onCreateContentView(LayoutInflater inflater) {
    return null;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (disposable != null) {
      disposable.dispose();
    }
  }

  @LayoutRes
  protected abstract int onLayoutId();

  protected abstract void onContent(@Nullable Bundle savedInstanceState);

}
