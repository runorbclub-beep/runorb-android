package com.cloud.runball.module.mine_record;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.XCommonNavigatorAdapter;
import com.cloud.runball.basecomm.base.XFragmentStateAdapter;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.MinePkInfoV2;
import com.cloud.runball.model.MinePkListV2Model;
import com.cloud.runball.model.UserPkWinRateModel;
import com.cloud.runball.module.mine.MatchRecordDetailActivity;
import com.cloud.runball.module.mine.MatchRecordTeamDetailActivity;
import com.cloud.runball.module.mine_record.adapter.MinePkRecordAdapter;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.ActivityMinePkRecordBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MinePkRecordActivity extends BaseActivity {

  private ActivityMinePkRecordBinding binding;
  Toolbar toolbar;

  MagicIndicator magicIndicator;

  ViewPager2 viewPager;

  private XCommonNavigatorAdapter commonNavigatorAdapter;

  public static void startAction(Context context) {
    Intent intent = new Intent(context, MinePkRecordActivity.class);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_mine_pk_record;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityMinePkRecordBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    magicIndicator = binding.magicIndicator;
    viewPager = binding.viewPager;
    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });

    List<Fragment> fragments = new ArrayList<>();
    String[] titles = { getString(R.string.all), getString(R.string.pk_win), getString(R.string.pk_fail) };

    fragments.add(MinePkRecordSubFragment.newInstance(0));
    fragments.add(MinePkRecordSubFragment.newInstance(1));
    fragments.add(MinePkRecordSubFragment.newInstance(2));

    viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    viewPager.setAdapter(new XFragmentStateAdapter(this, fragments));

    CommonNavigator commonNavigator = new CommonNavigator(this);
    commonNavigatorAdapter = new XCommonNavigatorAdapter(titles, index -> {
      viewPager.setCurrentItem(index);
    });
    commonNavigator.setAdapter(commonNavigatorAdapter);
    commonNavigator.setAdjustMode(true);
    magicIndicator.setNavigator(commonNavigator);
    LinearLayout titleContainer = commonNavigator.getTitleContainer();
    titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    titleContainer.setDividerDrawable(new ColorDrawable() {
      @Override
      public int getIntrinsicWidth() {
        return UIUtil.dip2px(MinePkRecordActivity.this, 15);
      }
    });
    final FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(magicIndicator);
    fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
    fragmentContainerHelper.setDuration(300);
    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
      @Override
      public void onPageSelected(int position) {
        fragmentContainerHelper.handlePageSelected(position);
      }
    });

    getUserPkWinRate();

  }


  private void getUserPkWinRate() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<UserPkWinRateModel> observable = apiServer.getUserPkWinRate();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<UserPkWinRateModel>() {
              @Override
              public void onSuccess(UserPkWinRateModel model) {
                if (model != null) {
                  commonNavigatorAdapter.setTitleViewText(
                      new String[] {
                          getString(R.string.all) + "(" + model.getTotal() + ")",
                          getString(R.string.pk_win) + "(" + model.getVictory() + ")",
                          getString(R.string.pk_fail) + "(" + model.getBurden() + ")"
                      }
                  );
                }
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("getUserPkWinRate " + msg);
              }
            })
    );
  }

}
