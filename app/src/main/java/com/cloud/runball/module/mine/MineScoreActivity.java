package com.cloud.runball.module.mine;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.databinding.LayoutMineScoreBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.bean.Achievement;
import com.cloud.runball.bean.ChartData;
import com.cloud.runball.model.ScoreDataModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.ChartUtils;
import com.orhanobut.logger.Logger;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 我的成就
 * @author ns467
 */
public class MineScoreActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    /**
     * 默认选择第一个标签
     */
    int selectedIndex = 0;

    private LayoutMineScoreBinding binding;

    LineChart lcTurnCharts;
    RadioGroup rgDate;
    RadioButton rbWeek;
    RadioButton rbMonth;
    RadioButton rbYear;
    TextView tv_info_time;
    TextView tv_info_turn;
    TextView tv_info_distance;
    TextView tv_info_win_times;
    TextView tv_info_date;
    ImageView ivArrowLeft;
    ImageView ivArrowRight;

    Achievement mAchievement;

    Calendar calendar;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_mine_score;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = LayoutMineScoreBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {
        rgDate.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initView() {
        lcTurnCharts = binding.lcTurnCharts;
        rgDate = binding.rgDate;
        rbWeek = binding.rbWeek;
        rbMonth = binding.rbMonth;
        rbYear = binding.rbYear;
        tv_info_time = binding.tvInfoTime;
        tv_info_turn = binding.tvInfoTurn;
        tv_info_distance = binding.tvInfoDistance;
        tv_info_win_times = binding.tvInfoWinTimes;
        tv_info_date = binding.tvInfoDate;
        ivArrowLeft = binding.ivArrowLeft;
        ivArrowRight = binding.ivArrowRight;
        //默认当天
        selectedIndex = 0;
        requestScore(selectedIndex, new Date());
        Date date = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.getTime().equals(date) || calendar.getTime().after(date)) {
            ivArrowRight.setVisibility(View.INVISIBLE);
        }
        tv_info_date.setText(new SimpleDateFormat("yyyy/MM/dd").format(date));

        // Replace @OnClick with listeners
        ivArrowLeft.setOnClickListener(this::onViewClick);
        ivArrowRight.setOnClickListener(this::onViewClick);
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_mine_score);
    }

    @Override
    protected void supportToolbar() {
        super.supportToolbar();
    }


    /**
     * week,month,year
     *
     * @param selectedIndex
     * @param date
     */
    private void requestScore(int selectedIndex, Date date) {
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        String type = "week";
        if (selectedIndex == 0) {
            type = "week";
        } else if (selectedIndex == 1) {
            type = "month";
        } else if (selectedIndex == 2) {
            type = "year";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("type", type);
        map.put("stop_date", format.format(date));

        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"),new JSONObject(map).toString());

        Observable<ScoreDataModel> observable = apiServer.getScore(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ScoreDataModel>() {
                @Override
                public void onSuccess(ScoreDataModel scoreDataModel) {
                    updateAchievement(scoreDataModel.getAchievement());
                    initChartStyle(scoreDataModel.getChart_data());
                    //更新日期文本显示
                    updateDateInfoShow(date);
                    //Logger.d(scoreDataModel.toString());
                }

                @Override
                public void onError(int code, String msg) {
                    Logger.d(msg);
                }
            })
        );
    }


    private void updateDateInfoShow( Date date){
        if(selectedIndex==1){
            tv_info_date.setText(new SimpleDateFormat("yyyy/MM").format(date));
        }else if(selectedIndex==2){
            tv_info_date.setText(new SimpleDateFormat("yyyy").format(date));
        }else{
            tv_info_date.setText(new SimpleDateFormat("yyyy/MM/dd").format(date));
        }
    }

    private void updateAchievement(Achievement mAchievement) {
        if (mAchievement != null) {
            tv_info_time.setText(mAchievement.getDuration_format()!=null?mAchievement.getDuration_format():"");
            tv_info_turn.setText(mAchievement.getSpeed_max_format()!=null?mAchievement.getSpeed_max_format():"");

            tv_info_distance.setText(mAchievement.getDistance_format()!=null?mAchievement.getDistance_format():"");
            tv_info_win_times.setText(String.valueOf(mAchievement.getWin_num()));
        }
    }





    /**
     * 初始化曲线数据
     *
     * @param list
     */
    private void initChartStyle(List<ChartData> list) {
        ChartUtils.initChart(this, lcTurnCharts);

        if (list == null) {
            return;
        }

        //x轴显示标签
        List<String> lables = new ArrayList<>();
        List<Entry> values = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            //AppLogger.d("x轴标签:"+list.get(i).getDate_format());
            values.add(new Entry(i, list.get(i).getSpeed_max(), list.get(i).getSpeed_max_format()));
            lables.add(list.get(i).getDate_format());
        }
        // "speed_max_format": "0.000"

        ChartUtils.notifyDataSetChanged(this, lcTurnCharts, R.drawable.shape_line_score, Color.parseColor("#FFD9C623"), true, values, lables);
    }

    public void onViewClick(View view) {
        if (view.getId() == R.id.ivArrowLeft) {
            //前
            //calendar.add(Calendar.DAY_OF_MONTH, -1);

            if(selectedIndex==0){
                calendar.add(Calendar.WEEK_OF_MONTH,-1);
            }else if(selectedIndex==1){
                calendar.add(Calendar.MONTH,-1);
            }else if(selectedIndex==2){
                calendar.add(Calendar.YEAR,-1);
            }

            ivArrowRight.setVisibility(View.VISIBLE);
        } else if (view.getId() == R.id.ivArrowRight) {
            //后面时间不可对比
            //calendar.add(Calendar.DAY_OF_MONTH, 1);
            if(selectedIndex==0){
                calendar.add(Calendar.WEEK_OF_MONTH,1);
            }else if(selectedIndex==1){
                calendar.add(Calendar.MONTH,1);
            }else if(selectedIndex==2){
                calendar.add(Calendar.YEAR,1);
            }

            if (calendar.getTime().after(new Date())) {
                ivArrowRight.setVisibility(View.INVISIBLE);
                //calendar.add(Calendar.DAY_OF_MONTH, -1);
                if(selectedIndex==0){
                    calendar.add(Calendar.WEEK_OF_MONTH,-1);
                }else if(selectedIndex==1){
                    calendar.add(Calendar.MONTH,-1);
                }else if(selectedIndex==2){
                    calendar.add(Calendar.YEAR,-1);
                }
                return;
            }
        }



        requestScore(selectedIndex, calendar.getTime());

        adjustArrowRight();
    }

    private void adjustArrowRight(){
        //目标日期
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int weekInMonth = calendar.get(Calendar.WEEK_OF_MONTH);

        //当前日期
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(new Date());
        int tempYear = tempCalendar.get(Calendar.YEAR);
        int tempMonth = tempCalendar.get(Calendar.MONTH);
        int tempWeekInMonth = tempCalendar.get(Calendar.WEEK_OF_MONTH);

        //int tempDayOfMonth = tempCalendar.get(Calendar.DAY_OF_MONTH);
        if (year >= tempYear && month >= tempMonth && weekInMonth >= tempWeekInMonth) {
            ivArrowRight.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == rbWeek.getId()) {
            rbWeek.setBackgroundResource(R.drawable.selector_score_date_title);
            rbMonth.setBackgroundResource(R.drawable.shape_date_title_unchecked);
            rbYear.setBackgroundResource(R.drawable.shape_date_title_unchecked);
            this.selectedIndex = 0;
        } else if (checkedId == rbMonth.getId()) {
            rbWeek.setBackgroundResource(R.drawable.shape_date_title_unchecked);
            rbMonth.setBackgroundResource(R.drawable.selector_score_date_title);
            rbYear.setBackgroundResource(R.drawable.shape_date_title_unchecked);
            this.selectedIndex = 1;
        } else if (checkedId == rbYear.getId()) {
            rbWeek.setBackgroundResource(R.drawable.shape_date_title_unchecked);
            rbMonth.setBackgroundResource(R.drawable.shape_date_title_unchecked);
            rbYear.setBackgroundResource(R.drawable.selector_score_date_title);
            this.selectedIndex = 2;
        }
        calendar.setTime(new Date());
        //选择周月年再请求数据
        requestScore(selectedIndex, calendar.getTime());
        adjustArrowRight();
    }
}