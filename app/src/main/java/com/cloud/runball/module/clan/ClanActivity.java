package com.cloud.runball.module.clan;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.XCommonNavigatorAdapter;
import com.cloud.runball.basecomm.base.XFragmentStateAdapter;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.model.ClanInfoModel;
import com.cloud.runball.model.OthersInfoModel;
import com.cloud.runball.module.clan.dialog.JoinClanDialog;
import com.cloud.runball.module.match_football_association.dialog.AssociationCommonDialog;
import com.cloud.runball.module.social.MineHomepageActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.view.magic_indicator.SpotNavigatorAdapter;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.ActivityClanBinding;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ClanActivity extends BaseActivity {
  private ActivityClanBinding binding;
  Toolbar toolbar;
  ImageView ivPortrait;
  TextView tvClanName;
  TextView tvMemberCount;
  TextView tvArea;
  TextView tvOperation;
  MagicIndicator indicator;
  ViewPager2 viewPager;

  private String clanId;
  private ClanInfoModel clanInfo;

  private boolean isUserJoining = false;

  private final WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  public static void startAction(Context context, String clanId) {
    Intent intent = new Intent(context, ClanActivity.class);
    intent.putExtra("clanId", clanId);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_clan;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityClanBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    ivPortrait = binding.ivPortrait;
    tvClanName = binding.tvClanName;
    tvMemberCount = binding.tvMemberCount;
    tvArea = binding.tvArea;
    tvOperation = binding.tvOperation;
    indicator = binding.indicator;
    viewPager = binding.viewPager;
    EventBus.getDefault().register(this);
    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });

    // Replace @OnClick with listener
    tvOperation.setOnClickListener(this::onClick);

    initTabPage();

    Intent intent = getIntent();
    if (intent == null) {
      finish();
      return;
    }

    clanId = intent.getStringExtra("clanId");
    getUserClanInfo();
  }
  public void onClick(View view) {
    if (view.getId() == R.id.tvOperation) {
      if (isUserJoining) {
        AssociationCommonDialog dialog = new AssociationCommonDialog(this);
        dialog.setContent(getString(R.string.tip), getString(R.string.tip_repeat_clan));
        dialog.addBtn(getString(R.string.btn_confirm), true, commonDialog -> {
          commonDialog.dismiss();
        });
      } else {
        // 4不可加入 1待审核  2已加入  3未加入
        if (clanInfo.getUserStatus() == 0) {

        } else if (clanInfo.getUserStatus() == 1) {

        } else if (clanInfo.getUserStatus() == 2) {

        } else if (clanInfo.getUserStatus() == 3) {
          JoinClanDialog dialog = new JoinClanDialog(this);
          dialog.setCallback(new JoinClanDialog.Callback() {
            @Override
            public void onSubmit(Dialog dialog, String remark) {
              postApplyJoinClan(dialog, remark);
            }
          });
        }
      }

//      else if (clanInfo.getUserStatus() == 4) {
//
//      }
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onServiceNoticeEvent(MessageEvent event) {
    switch (event.getEvetId()) {
      case MessageEvent.REFRESH: {
        getUserClanInfo();
      }
    }
  }

  /**
   * 初始化排行榜分页组件
   */
  private void initTabPage() {
    viewPager.setAdapter(new XFragmentStateAdapter(this));

//    XCommonNavigatorAdapter xCommonNavigatorAdapter = new XCommonNavigatorAdapter();
    SpotNavigatorAdapter xCommonNavigatorAdapter = new SpotNavigatorAdapter();
    xCommonNavigatorAdapter.setRankItemClickListener(index -> {
      viewPager.setCurrentItem(index);
    });

    CommonNavigator commonNavigator = new CommonNavigator(this);
    commonNavigator.setAdjustMode(true);
    commonNavigator.setAdapter(xCommonNavigatorAdapter);
    indicator.setNavigator(commonNavigator);

    LinearLayout titleContainer = commonNavigator.getTitleContainer();
    titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    titleContainer.setDividerDrawable(new ColorDrawable() {
      @Override
      public int getIntrinsicWidth() {
        return UIUtil.dip2px(ClanActivity.this, 15);
      }
    });

    FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(indicator);
    fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
    fragmentContainerHelper.setDuration(300);
    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
      @Override
      public void onPageSelected(int position) {
        fragmentContainerHelper.handlePageSelected(position);
      }
    });
  }

  /**
   * 填充个人排行榜分页数据
   */
  private void showTabData() {
    List<Fragment> fragments = new ArrayList<>();
    String[] titles;
    String[] spotInfo;
    // 0不可加入 1待审核  2已加入  3未加入
    if (clanInfo.getUserCaptainStatus() == 1) {
      titles = new String[]{ getString(R.string.tab_clan_homepage), getString(R.string.tab_clan_member), getString(R.string.tab_clan_new_member), getString(R.string.tab_clan_settings) };
      spotInfo = new String[] { "", "", clanInfo.getReviewCount() + "", "" };
    } else {
      titles = new String[]{ getString(R.string.tab_clan_homepage), getString(R.string.tab_clan_member), getString(R.string.tab_clan_settings) };
      spotInfo = new String[] { "", "", "" };
    }
    fragments.add(ClanHomepageFragment.newInstance(clanInfo.getId(), clanInfo.getUserCaptainStatus(), clanInfo.getUserStatus(), clanInfo.getIntroduction(), clanInfo.getAvgAchievement()));
    fragments.add(ClanMemberFragment.newInstance(clanInfo.getId(), clanInfo.getUserCaptainStatus(), clanInfo.getUserStatus(), clanInfo.getCaptainInfo()));
    if (clanInfo.getUserCaptainStatus() == 1) {
      fragments.add(ClanNewMemberFragment.newInstance(clanInfo.getId()));
    }
    fragments.add(ClanSettingsFragment.newInstance(
        clanInfo.getId(), clanInfo.getUserStatus(), clanInfo.getUserCaptainStatus(), clanInfo.getTitle(), clanInfo.getClanAvatar(),
        clanInfo.getAddress(), clanInfo.getIntroduction(), clanInfo.getTelephone(), clanInfo.getCreatedAt(),
        clanInfo.getCaptainInfo(), isUserJoining
    ));

    CommonNavigator commonNavigator = (CommonNavigator) indicator.getNavigator();
    SpotNavigatorAdapter xCommonNavigatorAdapter = (SpotNavigatorAdapter) commonNavigator.getAdapter();
    xCommonNavigatorAdapter.setTitleViewText(titles, spotInfo);
    xCommonNavigatorAdapter.notifyDataSetChanged();

    XFragmentStateAdapter fragmentStateAdapter = (XFragmentStateAdapter) viewPager.getAdapter();
    if (fragmentStateAdapter != null) {
      fragmentStateAdapter.setFragments(fragments);
      fragmentStateAdapter.notifyDataSetChanged();
    }
  }

  private void showTopLayInfo() {
    if (clanInfo == null) {
      return;
    }
    String imgUrl;
    if(clanInfo.getClanAvatar().startsWith("http")) {
      imgUrl = clanInfo.getClanAvatar();
    } else {
      imgUrl = Constant.getBaseUrl() + "/" + clanInfo.getClanAvatar();
    }
    Picasso.with(this)
        .load(imgUrl)
//        .transform(new CircleTransform(this))
//        .placeholder(R.mipmap.default_head)
        .into(ivPortrait);

    tvClanName.setText(clanInfo.getTitle());
    tvMemberCount.setText(getString(R.string.format_clan_member_count, clanInfo.getUserCount() + ""));
    tvArea.setText(clanInfo.getAddress());

    if(AppDataManager.getInstance().getUserInfoModel() != null) {
      //没有手机号或邮箱
      if("游客".equals(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_type_name())) {
        tvOperation.setVisibility(View.GONE);
        return;
      }
    }

    // 0不可加入 1待审核  2已加入  3未加入
    if (clanInfo.getUserStatus() == 0) {
      tvOperation.setVisibility(View.GONE);
    } else if (clanInfo.getUserStatus() == 1) {
      tvOperation.setVisibility(View.VISIBLE);
      tvOperation.setText(R.string.btn_pending);
    } else if (clanInfo.getUserStatus() == 2) {
      tvOperation.setVisibility(View.GONE);
    } else if (clanInfo.getUserStatus() == 3 && clanInfo.getUserCaptainStatus() == 2) {
      tvOperation.setVisibility(View.VISIBLE);
      tvOperation.setText(R.string.btn_join);
    }
//    else if (clanInfo.getUserStatus() == 4) {
//      tvOperation.setVisibility(View.GONE);
//    } else {
//      tvOperation.setVisibility(View.GONE);
//    }

  }

  private void getUserClanInfo() {
    HashMap<String, Object> map = new HashMap<>();
    map.put("id", this.clanId);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.getUserClanInfo(requestBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ClanInfoModel>() {
              @Override
              public void onSuccess(ClanInfoModel model) {
                clanInfo = model;


                String uid = AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_id();
                getUserOthersInfo(uid);


              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("getClanList --- " + msg);
              }
            })
    );
  }

  public void postApplyJoinClan(Dialog dialog, String remark) {
    if (TextUtils.isEmpty(remark)) {
      Toast.makeText(this, R.string.tip_join_remark_is_must_not_empty, Toast.LENGTH_SHORT).show();
      return;
    }
    HashMap<String, Object> map = new HashMap<>();
    map.put("user_clan_id", clanId);
    map.put("remark", remark);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.postApplyJoinClan(requestBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<Boolean>() {
              @Override
              public void onSuccess(Boolean model) {
                if (dialog != null) {
                  dialog.dismiss();
                }
                Toast.makeText(ClanActivity.this, R.string.tip_clan_join_pending, Toast.LENGTH_SHORT).show();
//                getUserClanInfo();
                EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("getClanList --- " + msg);
                Toast.makeText(ClanActivity.this, msg, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }

  private void getUserOthersInfo(String id) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("member_user_id", id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.getUserOthersInfo(requestBody)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<OthersInfoModel>() {
              @Override
              public void onSuccess(OthersInfoModel model) {
                isUserJoining = model.getUserClanMembers() != null;

                showTopLayInfo();
                showTabData();
              }
              @Override
              public void onError(int code, String msg) {
                Toast.makeText(ClanActivity.this, msg, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    XFragmentStateAdapter fragmentStateAdapter = (XFragmentStateAdapter) viewPager.getAdapter();
    if (fragmentStateAdapter != null) {
      List<Fragment> fragments = fragmentStateAdapter.getFragments();
      if (fragments != null) {
        for (Fragment fragment: fragments) {
          if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
          }
        }
      }
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }
}
