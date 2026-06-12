package com.cloud.runball.basecomm.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.reactivex.disposables.CompositeDisposable;

/**
 * date: 2021/9/21
 * author: hwl
 * description:
 */
public abstract class BaseFragment extends Fragment {

  private boolean isOnceInit = false;
  protected final CompositeDisposable disposable = new CompositeDisposable();

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(setLayoutId(), container, false);
    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    onContentView(view, savedInstanceState);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (!isOnceInit) {
      onLazyLoad();
      isOnceInit = true;
    }
    if (!isHidden()) {
      onFragmentShow();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (isOnceInit && !isHidden()) {
      onFragmentHidden();
    }
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (isOnceInit) {
      if (hidden) {
        onFragmentHidden();
      } else {
        onFragmentShow();
      }
    }
  }

  protected void onFragmentShow() {

  }

  protected void onFragmentHidden() {

  }

  protected void adaptImmersiveStatusBar() {
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

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    isOnceInit = false;
    if (!disposable.isDisposed()) {
      disposable.dispose();
    }
  }

  protected abstract int setLayoutId();

  protected abstract void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState);

  protected abstract void onLazyLoad();

}
