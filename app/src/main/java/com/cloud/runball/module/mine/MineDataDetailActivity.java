package com.cloud.runball.module.mine;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.AppUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.cloud.runball.R;
import com.cloud.runball.module.mine.adapter.TurnCircleAdapter;
import com.cloud.runball.bean.PlayData;
import com.cloud.runball.bean.UserPlayData;
import com.cloud.runball.model.UserPlayDataModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.ChartUtils;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.ActivityMineDataDetailBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MineDataDetailActivity extends BaseActivity {


    private ActivityMineDataDetailBinding binding;

    LineChart lcTurnCharts;
    RecyclerView ryTurns;
    LinearLayout lyTurns;
    LinearLayout vProgressTag;
    //xxx米
    TextView tv_title_Distance;
    TextView tvDistanceUnit;
    //万圈
    TextView tv_circle;
    //用时
    TextView tv_time;
    //最大转速
    TextView tvSpeedMax;
    //耐力
    TextView tvEnduranceMax;
    //用时
    TextView tvDurationMax;


    TurnCircleAdapter mTurnCircleAdapter;
    PlayData data;
    long user_play_id=0;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine_data_detail;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityMineDataDetailBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_data_detail);
    }

    @Override
    protected void initView() {
        lcTurnCharts = binding.lcTurnCharts;
        ryTurns = binding.ryTurns;
        lyTurns = binding.lyTurns;
        vProgressTag = binding.vProgressTag;
        tv_title_Distance = binding.tvTitleDistance;
        tvDistanceUnit = binding.tvDistanceUnit;
        tv_circle = binding.tvCircle;
        tv_time = binding.tvTime;
        tvSpeedMax = binding.tvSpeedMax;
        tvEnduranceMax = binding.tvEnduranceMax;
        tvDurationMax = binding.tvDurationMax;

        data=(PlayData)this.getIntent().getSerializableExtra("data");

        if(data!=null){
            //xxxkm
            tv_title_Distance.setText(String.valueOf(data.getDistance()));
            tvDistanceUnit.setText(String.valueOf(data.getDistance_format()));
            //万圈
            tv_circle.setText(String.valueOf(data.getCircle_count_format()+data.getCircle_count_unit()));
            //用时
            tv_time.setText(String.valueOf(data.getStart_time_format()));

            //最大转速
            tvSpeedMax.setText(String.valueOf(data.getSpeed_max_format()));
            //耐力
            tvEnduranceMax.setText(String.valueOf(data.getEndurance_max()));
            //用时
            tvDurationMax.setText(String.valueOf(data.getDuration_format()));

            user_play_id= Long.valueOf(data.getUser_play_id());
        }else{
            user_play_id =this.getIntent().getLongExtra("user_play_id",0);
        }
        initRecycleView();
        playItemDetail(user_play_id,true);
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    protected void supportToolbar() {
        super.supportToolbar();
    }




    private void initRecycleView() {
        //初始化我的数据信息
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        ryTurns.setLayoutManager(manager);
        //int px=AppUtils.px2dip(getApplicationContext(),vProgressTag.getWidth());
        //mTurnCircleAdapter = new TurnCircleAdapter((int)(px*2));
        //ryTurns.setAdapter(mTurnCircleAdapter);



        ViewTreeObserver viewTreeObserver = vProgressTag.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                vProgressTag.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int px= AppUtils.px2dip(getApplicationContext(),vProgressTag.getWidth());
                final float scale = context.getResources().getDisplayMetrics().density;
                Logger.d("px:" + px+";scale="+scale+";ViewWidth="+vProgressTag.getWidth());
                mTurnCircleAdapter = new TurnCircleAdapter((int)(px*2));
                mTurnCircleAdapter.updateMeasure((int)(px*2));
                ryTurns.setAdapter(mTurnCircleAdapter);
            }
        });
    }


    private void updateTopData(UserPlayData data){
        //xxx米

        tv_title_Distance.setText(data.getDistance_format());
        tvDistanceUnit.setText(String.valueOf(data.getDistance_unit()));

        //格式化圈
        tv_circle.setText(String.valueOf(data.getCircle_count_format()+data.getCircle_count_unit()));

        //用时
        tv_time.setText(String.valueOf(data.getStart_time_format()));

        //最大转速
        tvSpeedMax.setText(String.valueOf(data.getSpeed_max()));
        //耐力
        tvEnduranceMax.setText(String.valueOf(data.getEndurance_max()));
        //用时
        tvDurationMax.setText(String.valueOf(data.getDuration_format()));
    }

    private void initChartStyle(List<UserPlayData.UserPlayDetailDTO> list) {
        if(list!=null && list.size()>0){
            List<String> lables=new ArrayList<>();
            List<Entry> values = new ArrayList<>();
            //基线
            int limit=0;
            for(int i=0;i<list.size();i++){
                int speed=list.get(i).getSpeed();
                lables.add("");
                values.add(new Entry(i, speed));
                if(limit<=speed){
                    limit=speed;
                }
            }

            ChartUtils.initChart(this,lcTurnCharts,true,limit);
            ChartUtils.notifyDataSetChanged(this, lcTurnCharts, R.drawable.shape_line_turn, Color.parseColor("#ffe08a4a"), false,values,lables);
        }
    }


    private void playItemDetail(long user_play_id,boolean need_format){
         WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("user_play_id", user_play_id);
        map.put("need_format", need_format);

        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"),new JSONObject(map).toString());
        Observable<UserPlayDataModel> observable = apiServer.playInfo(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserPlayDataModel>() {
                @Override
                public void onSuccess(UserPlayDataModel userPlayDetailModel) {
                    if(userPlayDetailModel!=null && userPlayDetailModel.getUser_play()!=null){
                        //区间转速
                        mTurnCircleAdapter.setData(userPlayDetailModel.getUser_play().getSection_duration());
                        //转速曲线
                        initChartStyle(userPlayDetailModel.getUser_play().getUser_play_detail());
                        //
                        updateTopData(userPlayDetailModel.getUser_play());
                    }
                }

                @Override
                public void onError(int code, String msg) {
                    Logger.d(msg);
                }
            })
        );
    }

}
