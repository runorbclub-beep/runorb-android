package com.cloud.runball.module.rank;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.base.XCommonNavigatorAdapter;
import com.cloud.runball.basecomm.base.XFragmentStateAdapter;
import com.cloud.runball.basecomm.listener.TabItemClickListener;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.RankTypeInfo;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.ResourceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.cloud.runball.databinding.ActivityMineMatchBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: RankingActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/2 18:01
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/2 18:01
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankingActivity extends BaseActivity implements TabItemClickListener {


    private ActivityMineMatchBinding binding;

    MagicIndicator magic_indicator;
    ViewPager2 viewPager2;

    String user_age_type = "";
    String user_type = "";
    String address = "";
    String sys_sex_id = "";

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine_match;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityMineMatchBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected void initView() {
        magic_indicator = binding.magicIndicator;
        viewPager2 = binding.viewPager2;
        if (getIntent() != null) {
            user_age_type = this.getIntent().getStringExtra("user_age_type");
            user_type = this.getIntent().getStringExtra("user_type");
            address = this.getIntent().getStringExtra("address");
            sys_sex_id = this.getIntent().getStringExtra("sys_sex_id");
        }
        initRequestRankTypes();
    }

    @Override
    protected void setOnResult() {

    }

    private void initRequestRankTypes(){
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        Observable<ResponseBody> observable = apiServer.requestRankTypes();
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody){
                    try{
                        JSONObject jsonObject=new JSONObject(responseBody.string());
                        if(jsonObject.optInt("code")==1){
                            Gson gson=new Gson();
                            List<RankTypeInfo> rankTypeInfos = gson.fromJson(jsonObject.optString("data"), new TypeToken<List<RankTypeInfo>>(){}.getType());
                            initTabNav(rankTypeInfos);
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onError(int code, String msg) {
                    AppLogger.d(msg);
                }
            })
        );
    }

    private void initTabNav(List<RankTypeInfo> rankTypes) {
        boolean isZh = ResourceUtils.isZhCn(this);
        List<Fragment> fragments = new ArrayList<>();
        String[] titles=new String[rankTypes.size()];
        int index = 0;
        for(RankTypeInfo typeInfo:rankTypes){
            fragments.add(PersonalRankFragment.newInstance(typeInfo.getType(), user_age_type, user_type, address, sys_sex_id, null, true));
            titles[index] = isZh ? typeInfo.getTitle_zh() : typeInfo.getTitle_en();
            index++;
        }

        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager2.setAdapter(new XFragmentStateAdapter(this, fragments));

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new XCommonNavigatorAdapter(titles, this));
        magic_indicator.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer();
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return UIUtil.dip2px(RankingActivity.this, 15);
            }
        });
        final FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(magic_indicator);
        fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
        fragmentContainerHelper.setDuration(300);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
            @Override
            public void onPageSelected(int position) {
                fragmentContainerHelper.handlePageSelected(position);
            }
        });
    }

    @Override
    protected String getTitleLabel() {
        return this.getIntent().getStringExtra("title");
    }



    @Override
    public void onTabItemClick(int index) {
        viewPager2.setCurrentItem(index);
    }

}
