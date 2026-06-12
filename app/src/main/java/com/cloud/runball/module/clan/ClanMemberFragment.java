package com.cloud.runball.module.clan;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.dialog.CommonDialog;
import com.cloud.runball.model.ClanInfoModel;
import com.cloud.runball.model.ClanMemberItem;
import com.cloud.runball.model.ClanMemberModel;
import com.cloud.runball.module.clan.adapter.ClanMemberAdapter;
import com.cloud.runball.module.rank.PersonalRankFragment;
import com.cloud.runball.module.social.MineHomepageActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.FragmentClanMemberBinding;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ClanMemberFragment extends BaseFragment {
  private FragmentClanMemberBinding binding;
  TextView tvPendingState;
  TextView tvLabelClanAdmin;
  View layAdmin;
  ImageView ivHead;
  TextView tvName;
  TextView tvArea;
  TextView tvMark;
  TextView tvLabelClanMember;
  XRecyclerView recyclerview;
  View ryEmpty;
  View layBottom;

  private final List<ClanMemberItem> memberData = new ArrayList<>();
  private int page = 1;
  private String clanId;
  private int captainStatus;
  private int pendingState;
  private ClanInfoModel.CaptainInfo captainInfo;

  WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  public static ClanMemberFragment newInstance(String clanId, int captainStatus, int pendingState, ClanInfoModel.CaptainInfo captainInfo) {
    ClanMemberFragment fragment = new ClanMemberFragment();
    Bundle bundle = new Bundle();
    bundle.putString("clan_id", clanId);
    bundle.putInt("captainStatus", captainStatus);
    bundle.putSerializable("captain_info", captainInfo);
    bundle.putInt("pendingState", pendingState);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_clan_member;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentClanMemberBinding.bind(view);
    tvPendingState = binding.tvPendingState;
    tvLabelClanAdmin = binding.tvLabelClanAdmin;
    layAdmin = binding.layAdmin.getRoot();
    ivHead = binding.layAdmin.ivHead;
    tvName = binding.layAdmin.tvName;
    tvArea = binding.layAdmin.tvArea;
    tvMark = binding.layAdmin.tvMark;
    tvLabelClanMember = binding.tvLabelClanMember;
    recyclerview = binding.recyclerview;
    ryEmpty = binding.ryEmpty;
    layBottom = binding.layBottom;
    // Replace @OnClick with listener
    layAdmin.setOnClickListener(this::onClick);
  }

  @Override
  protected void onLazyLoad() {
    Bundle bundle = getArguments();
    if (bundle == null) {
      return;
    }
    clanId = bundle.getString("clan_id");
    captainStatus = bundle.getInt("captainStatus");
    pendingState = bundle.getInt("pendingState");
    captainInfo = (ClanInfoModel.CaptainInfo) bundle.getSerializable("captain_info");

    // 0审核中 1正常 2已拒绝
    if (pendingState == 0) {
      tvPendingState.setVisibility(View.VISIBLE);
      tvLabelClanAdmin.setVisibility(View.GONE);
      layAdmin.setVisibility(View.GONE);
      tvLabelClanMember.setVisibility(View.GONE);
      recyclerview.setVisibility(View.GONE);
      ryEmpty.setVisibility(View.GONE);
      return;
    } else if (pendingState == 1) {
      tvPendingState.setVisibility(View.GONE);
    } else if (pendingState == 2) {
      tvPendingState.setVisibility(View.GONE);
    }

    // 0不可加入 1待审核  2已加入  3未加入
    if (pendingState == 0) {
      layBottom.setVisibility(View.GONE);
    } else if (pendingState == 1) {
      if (captainStatus == 1) {
        layBottom.setVisibility(View.VISIBLE);
      } else {
        layBottom.setVisibility(View.GONE);
      }
    } else if (pendingState == 2) {
      layBottom.setVisibility(View.GONE);
    } else if (pendingState == 3) {
      layBottom.setVisibility(View.GONE);
    } else {
      layBottom.setVisibility(View.GONE);
    }

    if (captainInfo != null) {
      String imgUrl = captainInfo.getUserImg();
      if(!captainInfo.getUserImg().startsWith("http")) {
        imgUrl = Constant.getBaseUrl() + "/" + imgUrl;
      }
      Picasso.with(ClanMemberFragment.this.getContext())
          .load(imgUrl)
          .transform(new CircleTransform(ClanMemberFragment.this.getContext()))
          .placeholder(R.mipmap.default_head)
          .into(ivHead);
      tvName.setText(captainInfo.getUserName());
      tvArea.setText(captainInfo.getAddress());
      tvMark.setVisibility(View.VISIBLE);
      tvMark.setText(R.string.captain);

      Drawable drawableSex = null;
      if (SexConstant.SEX_MAN.equals(captainInfo.getSysSexId())) {
        drawableSex = getResources().getDrawable(R.mipmap.ic_man);
      } else if (SexConstant.SEX_WOMEN.equals(captainInfo.getSysSexId())) {
        drawableSex = getResources().getDrawable(R.mipmap.ic_women);
      }
      drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
      tvName.setCompoundDrawables(null, null, drawableSex, null);
    }

    initMemberList();
    loadMemberListData(true, 1);
  }

  public void onClick(View view) {
    if (view.getId() == R.id.layAdmin) {
      if (captainInfo != null) {
        MineHomepageActivity.startAction(ClanMemberFragment.this.getContext(), captainInfo.getUserId());
      }
    }
  }

  private void initMemberList() {
    recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    recyclerview.setEmptyView(ryEmpty);
    recyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
    recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        loadMemberListData(true, 1);
      }
      @Override
      public void onLoadMore() {
        loadMemberListData(false, page + 1);
      }
    });

    ClanMemberAdapter adapter = new ClanMemberAdapter(false, captainStatus, memberData, new ClanMemberAdapter.OnItemClickListener() {
      @Override
      public void onPending(ClanMemberItem itemData) {

      }
      @Override
      public void onItemClick(ClanMemberItem itemData) {
        MineHomepageActivity.startAction(ClanMemberFragment.this.getContext(), itemData.getUserId());
      }
      @Override
      public void onItemMoreClick(ClanMemberItem itemData) {
        CommonDialog dialog = new CommonDialog(ClanMemberFragment.this.getContext());
        dialog.setContent("", "是否删除该成员？");
        dialog.addBtn(getString(R.string.btn_cancel), commonDialog -> {
          commonDialog.dismiss();
        });
        dialog.addBtn(getString(R.string.btn_ok), commonDialog -> {
          delMember(commonDialog, itemData.getUserId());
        });
      }
    });
    recyclerview.setAdapter(adapter);
  }

  private void loadMemberListData(boolean isRefresh, int page) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("limit", 10);
    map.put("page", page);
    map.put("user_clan_id", clanId);
    // 1待审核成员 2获取俱乐部成员
    map.put("status", 2);
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
                  ClanMemberFragment.this.page = 1;
                } else {
                  if (data == null || data.size() == 0) {
                    return;
                  }
                  ClanMemberFragment.this.page++;
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
                Toast.makeText(ClanMemberFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
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

  private void delMember(Dialog dialog, String uid) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("user_clan_id", clanId);
    map.put("member_user_id", uid);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.delUserClanMember(requestBody)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<Object>() {
              @Override
              public void onSuccess(Object model) {
                if (dialog != null) {
                  dialog.dismiss();
                }
                EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("delUserClanMember --- " + msg);
                Toast.makeText(ClanMemberFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }

}
