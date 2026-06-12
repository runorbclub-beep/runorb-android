package com.cloud.runball.module.match;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.XCommonNavigatorAdapter;
import com.cloud.runball.basecomm.base.XFragmentStateAdapter;
import com.cloud.runball.basecomm.listener.TabItemClickListener;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.MatchStageData;
import com.cloud.runball.fragment.MatchRankFragment;
import com.cloud.runball.model.MatchStagesModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: MatchRankActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/11 17:35
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/11 17:35
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRankActivity extends AppCompatActivity implements TabItemClickListener,Toolbar.OnMenuItemClickListener{



    MagicIndicator magic_indicator;
    ViewPager2 viewPager2;
    protected Toolbar toolbar;
    protected TextView tvToolBarTitle;
    String title;
    String sys_match_id;
    int is_group=0;
    int is_exponent=0;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        title=this.getIntent().getStringExtra("title");
        sys_match_id=this.getIntent().getStringExtra("sys_match_id");
        is_group=this.getIntent().getIntExtra("is_group",0);
        is_exponent=this.getIntent().getIntExtra("is_exponent",0);

        magic_indicator=this.findViewById(R.id.magic_indicator);
        viewPager2=this.findViewById(R.id.viewPager2);
        //加上排行
        supportToolbar(title+getString(R.string.title_rank_prex));

        requestMatchRankTabs(sys_match_id);
    }

    private void showTypeTabs(MatchStagesModel matchStagesModel){
        int listSize=matchStagesModel.getList().size();
        if(listSize>0){
            List<Fragment> fragments = new ArrayList<>();
            String[] titles=new String[listSize];
            int index=0;
            for (MatchStageData matchStageData : matchStagesModel.getList()) {
                fragments.add(MatchRankFragment.newInstance(sys_match_id,matchStageData.getMatchs_stage_id(),is_group,is_exponent));
                titles[index]=matchStageData.getMatch_stage_title();
                index+=1;
            }
            viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
            viewPager2.setAdapter(new XFragmentStateAdapter(this,fragments));

            CommonNavigator commonNavigator = new CommonNavigator(this);
            commonNavigator.setAdapter(new XCommonNavigatorAdapter(titles,this));
            magic_indicator.setNavigator(commonNavigator);
            LinearLayout titleContainer = commonNavigator.getTitleContainer();
            titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            titleContainer.setDividerDrawable(new ColorDrawable() {
                @Override
                public int getIntrinsicWidth() {
                    return UIUtil.dip2px(MatchRankActivity.this, 15);
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
    }

    private void requestMatchRankTabs(String sys_match_id){
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("sys_match_id", sys_match_id);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<MatchStagesModel> observable = apiServer.matchStageInfo(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<MatchStagesModel>() {
                @Override
                public void onSuccess(MatchStagesModel matchStagesModel) {
                    showTypeTabs(matchStagesModel);
                }

                @Override
                public void onError(int code, String msg) {
                    AppLogger.d("-----赛段tabs--------" + msg);
                }
            })
        );

    }

    protected void setOnResult() {
        finish();
    }

    protected void supportToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvToolBarTitle = (TextView) findViewById(R.id.tvToolBarTitle);
        toolbar.setTitle("");
        tvToolBarTitle.setText(title);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitleMargin(0, 5, 5, 5);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.btn_return);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnResult();
            }
        });
        toolbar.setOnMenuItemClickListener(this);
    }



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }


    @Override
    public void onTabItemClick(int index) {
        viewPager2.setCurrentItem(index);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
