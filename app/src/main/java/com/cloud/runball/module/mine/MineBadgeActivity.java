package com.cloud.runball.module.mine;

import android.content.Intent;
import android.view.View;
import android.view.LayoutInflater;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.module.mine.adapter.MineBadgeRecycleAdapter;
import com.cloud.runball.bean.MedalInfo;
import com.cloud.runball.model.MedalInfoModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import java.util.Locale;
import com.cloud.runball.databinding.LayoutMineBadgeBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author ns467
 */
public class MineBadgeActivity extends BaseActivity implements MineBadgeRecycleAdapter.OnItemClickListener {

    private LayoutMineBadgeBinding binding;
    RecyclerView ryBadge;

    boolean isZh=true;

    private MineBadgeRecycleAdapter mMineBadgeRecycleAdapter;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_mine_badge;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = LayoutMineBadgeBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_mine_badge);
    }

    @Override
    protected void initView() {
        ryBadge = binding.ryBadge;
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if(language.startsWith("zh")){
            isZh=true;
        }else{
            isZh=false;
        }
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        //初始化我的数据信息
        GridLayoutManager manager = new GridLayoutManager(context, 3);
        ryBadge.setLayoutManager(manager);
        Observable<MedalInfoModel> observable  =apiServer.getAllBadges();
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<MedalInfoModel>() {
                @Override
                public void onSuccess(MedalInfoModel o){
                    mMineBadgeRecycleAdapter = new MineBadgeRecycleAdapter(MineBadgeActivity.this,o.getUser_medal(),isZh);
                    mMineBadgeRecycleAdapter.setOnItemClickListener(MineBadgeActivity.this);
                    ryBadge.setAdapter(mMineBadgeRecycleAdapter);
                }

                @Override
                public void onError(int code, String msg) {
                    AppLogger.d(msg);
                }
            })
        );
    }


    @Override
    protected void setOnResult() {

    }

    @Override
    protected void supportToolbar() {
        super.supportToolbar();
    }

    @Override
    public void onItemClick(View view, MedalInfo data) {
        Intent it = new Intent(this, BadgeActivity.class);
        it.putExtra("data",data);
        startActivity(it);
    }
}
