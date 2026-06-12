package com.cloud.runball.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.module.match.adapter.MatchRecordDetailTeamAdapter;
import com.cloud.runball.model.ListPkItem;
import java.util.ArrayList;
import java.util.List;
import com.cloud.runball.databinding.FragmentMatchRecordTeamBinding;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.fragment
 * @ClassName: MatchRecordTeamFragment
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/16 15:20
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/16 15:20
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRecordTeamFragment extends BaseFragment {

  private FragmentMatchRecordTeamBinding binding;
  RecyclerView recyclerview;

  private MatchRecordDetailTeamAdapter mMatchRecordDetailTeamAdapter;
  private List<ListPkItem> list = new ArrayList<>();

  public static MatchRecordTeamFragment newInstance(List<ListPkItem> list) {
    return new MatchRecordTeamFragment(list);
  }

  public MatchRecordTeamFragment(List<ListPkItem> list) {
    this.list = list;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_match_record_team;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentMatchRecordTeamBinding.bind(view);
    recyclerview = binding.recyclerview;
  }

  @Override
  protected void onLazyLoad() {
    onLazyLoadData();
  }

  private void onLazyLoadData(){
    mMatchRecordDetailTeamAdapter = new MatchRecordDetailTeamAdapter(getActivity(), list);
    //初始化我的数据信息
    recyclerview.setHasFixedSize(true);
    recyclerview.setItemAnimator(new DefaultItemAnimator());
    recyclerview.setAdapter(mMatchRecordDetailTeamAdapter);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    list.clear();
  }

}
