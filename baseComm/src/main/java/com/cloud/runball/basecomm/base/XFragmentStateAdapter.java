package com.cloud.runball.basecomm.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cloud.runball.basecomm.page.BaseFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: XFragmentStateAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/18 16:29
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/18 16:29
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class XFragmentStateAdapter extends FragmentStateAdapter {

  private final List<Fragment> fragments = new ArrayList<>();
  private final List<Long> ids = new ArrayList<>();

  public XFragmentStateAdapter(FragmentActivity fragmentActivity) {
    super(fragmentActivity);
  }

  public XFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
    super(fragmentActivity);
    this.ids.clear();
    long id = new Date().getTime();
    for (int i = 0; i < fragments.size(); i++) {
      ids.add(id + i);
    }
    this.fragments.addAll(fragments);
  }

  public XFragmentStateAdapter(Fragment fragment) {
    super(fragment);
  }

  public XFragmentStateAdapter(@NonNull Fragment fragment, List<Fragment> fragments) {
    super(fragment);
    this.ids.clear();
    long id = new Date().getTime();
    for (int i = 0; i < fragments.size(); i++) {
      ids.add(id + i);
    }
    this.fragments.addAll(fragments);
  }

  public void setFragments(List<Fragment> fragments) {
    this.ids.clear();
    long id = new Date().getTime();
    for (int i = 0; i < fragments.size(); i++) {
      ids.add(id + i);
    }
    this.fragments.clear();
    this.fragments.addAll(fragments);
  }

  public List<Fragment> getFragments() {
    return fragments;
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    return fragments.get(position);
  }

  @Override
  public int getItemCount() {
    return this.fragments != null ? this.fragments.size() : 0;
  }

  @Override
  public long getItemId(int position) {
//    return super.getItemId(position);
    return ids.get(position);
  }

  @Override
  public boolean containsItem(long itemId) {
//    return super.containsItem(itemId);
    return ids.contains(itemId);
  }
}
