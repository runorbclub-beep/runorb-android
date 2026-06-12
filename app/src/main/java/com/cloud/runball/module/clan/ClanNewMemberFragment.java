package com.cloud.runball.module.clan;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.model.ClanMemberItem;
import com.cloud.runball.model.ClanMemberModel;
import com.cloud.runball.module.clan.adapter.ClanMemberAdapter;
import com.cloud.runball.module.clan.dialog.JoinClanPendingDialog;
import com.cloud.runball.module.social.MineHomepageActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.FragmentClanNewMemberBinding;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ClanNewMemberFragment extends BaseFragment {

  private FragmentClanNewMemberBinding binding;
  XRecyclerView recyclerview;

  View ryEmpty;

  private final List<ClanMemberItem> memberData = new ArrayList<>();
  private int page = 1;
  private String clanId;

  WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  public static ClanNewMemberFragment newInstance(String clanId) {
    ClanNewMemberFragment fragment = new ClanNewMemberFragment();
    Bundle bundle = new Bundle();
    bundle.putString("clan_id", clanId);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_clan_new_member;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentClanNewMemberBinding.bind(view);
    recyclerview = binding.recyclerview;
    ryEmpty = binding.ryEmpty;
  }

  @Override
  protected void onLazyLoad() {
    Bundle bundle = getArguments();
    if (bundle == null) {
      return;
    }
    clanId = bundle.getString("clan_id");
    initNewMemberList();
    loadNewMemberListData(true, 1);
  }

  private void initNewMemberList() {
    recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    recyclerview.setEmptyView(ryEmpty);
    recyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
    recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        loadNewMemberListData(true, 1);
      }
      @Override
      public void onLoadMore() {
        loadNewMemberListData(false, page + 1);
      }
    });

    ClanMemberAdapter adapter = new ClanMemberAdapter(true, 1, memberData, new ClanMemberAdapter.OnItemClickListener() {
      @Override
      public void onPending(ClanMemberItem itemData) {
        JoinClanPendingDialog dialog = new JoinClanPendingDialog(ClanNewMemberFragment.this.getContext());
        dialog.setRemark(itemData.getRemark());
        dialog.setCallback((dialog1, isPass) -> {
          postReviewApplyClanMember(dialog1, itemData.getUserId(), isPass);
        });
      }

      @Override
      public void onItemClick(ClanMemberItem itemData) {
        MineHomepageActivity.startAction(ClanNewMemberFragment.this.getContext(), itemData.getUserId());
      }
      @Override
      public void onItemMoreClick(ClanMemberItem itemData) {

      }
    });
    recyclerview.setAdapter(adapter);
  }

  private void loadNewMemberListData(boolean isRefresh, int page) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("limit", 10);
    map.put("page", page);
    map.put("user_clan_id", clanId);
    // 1待审核成员 2获取俱乐部成员
    map.put("status", 1);
    // 0是不含队长的 1是含队长的
    map.put("is_captain", 0);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.getClanMemberList(requestBody)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ClanMemberModel>() {
              @Override
              public void onSuccess(ClanMemberModel model) {
                List<ClanMemberItem> data = model.getList();
                if (isRefresh) {
                  memberData.clear();
                  ClanNewMemberFragment.this.page = 1;
                } else {
                  if (data == null || data.size() == 0) {
                    return;
                  }
                  ClanNewMemberFragment.this.page++;
                }
                if (data != null) {
                  memberData.addAll(data);
                }
                ClanMemberAdapter adapter = (ClanMemberAdapter) recyclerview.getAdapter();
                if (adapter != null) {
                  adapter.notifyDataSetChanged();
                }
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("getClanMemberList --- " + msg);
                Toast.makeText(ClanNewMemberFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
              }
              @Override
              public void onComplete() {
                super.onComplete();
                if (recyclerview != null) {
                  recyclerview.loadMoreComplete();
                  recyclerview.refreshComplete();
                }
              }
            })
    );
  }

  private void postReviewApplyClanMember(Dialog dialog, String memberId, boolean isPass) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("user_clan_id", clanId);
    map.put("member_user_id", memberId);
    // status 状态：1待审核 2已通过 0已拒绝
    map.put("status", isPass ? 2 : 0);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.postReviewApplyClanMember(requestBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<Boolean>() {
              @Override
              public void onSuccess(Boolean model) {
                loadNewMemberListData(true, 1);
                if (dialog != null) {
                  dialog.dismiss();
                }
                EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("editClanInfo --- " + msg);
                Toast.makeText(ClanNewMemberFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }


}
