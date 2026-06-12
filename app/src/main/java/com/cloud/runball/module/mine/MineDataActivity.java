package com.cloud.runball.module.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.utils.AppLogger;
import com.google.android.material.tabs.TabLayout;
import com.cloud.runball.R;
import com.cloud.runball.module.mine.adapter.MineDataRecycleAdapter;
import com.cloud.runball.bean.DateRange;
import com.cloud.runball.bean.PlayData;
import com.cloud.runball.model.DateRangeModel;
import com.cloud.runball.model.MineDataModel;
import com.cloud.runball.poplib.PopCommon;
import com.cloud.runball.poplib.PopModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.LayoutMineDataBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 我的数据
 * @author ns467
 */
public class MineDataActivity extends BaseActivity implements MineDataRecycleAdapter.OnItemClickListener, TabLayout.OnTabSelectedListener {

    private LayoutMineDataBinding binding;
    XRecyclerView recyclerview;

    TabLayout tabLayout;

    TextView tvDate;

    ImageView ivPopDownUp;

    LinearLayout lyDay;

    SwipeRefreshLayout swipeRefreshLayout;


    MineDataRecycleAdapter mMineDataRecycleAdapter;

    /**
     * 默认选中本天
     */
    int selectedMenuItemIndex = 0;


    final List<PopModel> list = new ArrayList<>();

    static final String DAY = "day";
    static final String WEEK = "week";
    static final String MONTH = "month";
    static final String YEAR = "year";

    String mStart_date;
    String mStop_date;
    int mPage = 1;
    String mType="day";

    List<PlayData> playDatas = new ArrayList<>();

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_mine_data;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = LayoutMineDataBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_mine_data);
    }

    @Override
    protected void initView() {
        selectedMenuItemIndex = 0;
        recyclerview = binding.recyclerview;
        tabLayout = binding.tabLayout;
        tvDate = binding.tvDate;
        ivPopDownUp = binding.ivPopDownUp;
        lyDay = binding.lyDay;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        tabLayout.setSmoothScrollingEnabled(true);
        tabLayout.addOnTabSelectedListener(this);

        //菜单menu列表
        list.clear();
        list.add(new PopModel(getString(R.string.menu_today)));
        list.add(new PopModel(getString(R.string.menu_week)));
        list.add(new PopModel(getString(R.string.menu_month)));
        list.add(new PopModel(getString(R.string.menu_year)));


        // 通过 setEnabled(false) 禁用下拉刷新
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setSize(CircularProgressDrawable.DEFAULT);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        requestDataRange(new Date(), selectedMenuItemIndex);
        tvDate.setText(list.get(selectedMenuItemIndex).getItemDesc());


        mMineDataRecycleAdapter = new MineDataRecycleAdapter();
        mMineDataRecycleAdapter.setOnItemClickListener(this);

        //初始化我的数据信息
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(manager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(mMineDataRecycleAdapter);
        recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        recyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
        recyclerview.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        recyclerview.setPullRefreshEnabled(false);
        recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if(recyclerview!=null){
                    recyclerview.refreshComplete();
                }
            }

            @Override
            public void onLoadMore() {
                requestMineData(mPage + 1, mStart_date, mStop_date,mType);
            }
        });
    }


    /**
     * 初始化导航栏
     *
     * @param model
     */
    private void initTabs(DateRangeModel model) {
        if (model != null && model.getDate_range().size() > 0) {
            tabLayout.removeAllTabs();
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

            int size = model.getDate_range().size();
            for (int pos = 0; pos < size; pos++) {
                DateRange dateRange = model.getDate_range().get(pos);
                TabLayout.Tab tab = tabLayout.newTab();
                tab.setText(dateRange.getTitle());
                tab.setTag(dateRange);
                tabLayout.addTab(tab, pos == size - 1 ? true : false);
            }

            if (size > 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 横向平滑滚动偏移
                        int x = Math.max(tabLayout.getTabAt(0).view.getWidth() * (size - 1), 140 * (size - 1));
                        tabLayout.smoothScrollTo(x, 0);
                    }
                }, 100);
            }
        }
    }


    @Override
    protected void setOnResult() {

    }


    public void onViewClick(View view) {
        if (view.getId() == R.id.tvDate || view.getId() == R.id.ivPopDownUp || view.getId() == R.id.lyDay) {
            showItemPop(tvDate);
        }
    }

    @Override
    protected void addListener() {
        if (binding != null) {
            binding.tvDate.setOnClickListener(this::onViewClick);
            binding.ivPopDownUp.setOnClickListener(this::onViewClick);
            binding.lyDay.setOnClickListener(this::onViewClick);
        }
    }


    /**
     * 获取时间范围
     *
     * @param date
     * @param index day、week、month、year
     */
    private void requestDataRange(Date date, int index) {
         WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        String type = "day";
        if (index == 1) {
            type = "week";
        } else if (index == 2) {
            type = "month";
        } else if (index == 3) {
            type = "year";
        }

        String finalType = type;

        selectedMenuItemIndex = index;
        tabLayout.removeAllTabs();
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        HashMap<String, Object> map = new HashMap<>();
        map.put("start_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
        map.put("type", type);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"),new JSONObject(map).toString());
        Observable<DateRangeModel> observable = apiServer.getDataRange(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<DateRangeModel>() {
                @Override
                public void onSuccess(DateRangeModel dateRangeModel) {
                    //根据所选的tab再去请求数据
                    mPage = 1;
                    mType= finalType;
                    if (dateRangeModel != null && dateRangeModel.getDate_range().size() > 0) {
                        int size = dateRangeModel.getDate_range().size();
                        mStart_date = dateRangeModel.getDate_range().get(size - 1).getStart_date();
                        mStop_date = dateRangeModel.getDate_range().get(size - 1).getStop_date();
                    }
                    //排列tab
                    initTabs(dateRangeModel);
                }

                @Override
                public void onError(int code, String msg) {
                    AppLogger.d(msg);
                    mType= finalType;
                }
            })
        );


    }

    /**
     * 请求某天某月某年等时间数据
     * @param page
     * @param start_date
     * @param stop_date
     * @param type
     */
    private void requestMineData(int page, String start_date, String stop_date,String type) {
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(5);
        map.put("page", page);
        map.put("limit", 10);
        map.put("start_date", start_date);
        map.put("stop_date", stop_date);
        map.put("type", type);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"),new JSONObject(map).toString());
        Observable<MineDataModel> observable = apiServer.getMineData(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<MineDataModel>() {
                @Override
                public void onSuccess(MineDataModel mineDataModel) {
                    if (mineDataModel != null && mineDataModel.getCount() > 0 && mMineDataRecycleAdapter!=null) {
                        Logger.d(mineDataModel+";onSuccess==============>"+mineDataModel.getCount()+";page="+page);
                        mPage = page;
                        mType=type;
                        mStart_date = start_date;
                        mStop_date = stop_date;
                        //累计加载
                        playDatas.addAll(mineDataModel.getPlay_data());
                        mMineDataRecycleAdapter.notifyDataSetChanged(playDatas);
                    }

                    if(recyclerview!=null){
                        recyclerview.loadMoreComplete();
                    }
                }

                @Override
                public void onError(int code, String msg) {
                    Logger.d(msg);
                    if(swipeRefreshLayout!=null){
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            })
        );
    }

    private void showItemPop(View targetView) {
        PopCommon popCommon = new PopCommon(this, list, new PopCommon.OnPopCommonListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playDatas.clear();
                mMineDataRecycleAdapter.notifyDataSetChanged(playDatas);
                requestDataRange(new Date(), position);
                tvDate.setText(list.get(position).getItemDesc());
            }

            @Override
            public void onDismiss() {

            }
        });
        popCommon.showAsDropDown(targetView);
    }

    @Override
    public void onItemClick(View view, PlayData data) {
        Intent it = new Intent(getApplicationContext(), MineDataDetailActivity.class);
        it.putExtra("data", data);
        startActivity(it);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        playDatas.clear();
        mMineDataRecycleAdapter.notifyDataSetChanged(playDatas);
        DateRange dateRange = (DateRange) tab.getTag();
        this.mPage=1;
        mStart_date = dateRange.getStart_date();
        mStop_date = dateRange.getStop_date();
        requestMineData(1, mStart_date, mStop_date,mType);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
