package com.cloud.runball.module.match;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.App;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.base.BaseView;
import com.cloud.runball.basecomm.base.RecyclerViewDivider;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.module.race.CreateRankMatchAddActivity;
import com.cloud.runball.module.login.LoginOtherActivity;
import com.cloud.runball.module.match.adapter.MatchOptionAdapter;
import com.cloud.runball.module.match.adapter.StageItemAdapter;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.model.RankMatchDetailModel;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.CheckHelper;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.dialog.PKRuleDialog;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import com.cloud.runball.databinding.ActivityMatchDetailBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: MatchDetailActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/1 10:25
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/1 10:25
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchDetailActivity extends BaseActivity implements View.OnClickListener, StageItemAdapter.StageItemRuleListener {

    protected WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

    public static final int REPORT_OK=99;

    private ActivityMatchDetailBinding binding;
    ImageView ivBanner;
    Button btnSignUp;
    TextView tvTitle;
    TextView tvStatus;
    RecyclerView recyclerview;
    TextView tvGroupName;
    TextView tvGroupID;
    RecyclerView recyclerviewStages;
    CollapsingToolbarLayout collapsing_toolbar_layout;

    MatchOptionAdapter mMatchOptionAdapter;
    List<RankMatchDetailModel.RankMatchFormItem> rankOptions=new ArrayList<>();
    List<RankMatchDetailModel.RankMatchStateItem> dataInfo=new ArrayList<>();
    String sys_sys_match_id;
    String sys_match_id;
    int is_group;

    String matchTitle;
    RankMatchDetailModel mDetailModel;
    StageItemAdapter mStageItemAdapter;
    int matchStatus=-1;
    int is_join=-1;
    int is_members=0;

    boolean signSuccess=false;

    public static void startAction(Context context, String sysMatchId) {
        Intent intent= new Intent(context, MatchDetailActivity.class);
        intent.putExtra("sys_match_id", sysMatchId);
        context.startActivity(intent);
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_match_detail;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityMatchDetailBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected void initView() {
        ivBanner = binding.ivBanner;
        btnSignUp = binding.btnSignUp;
        tvTitle = binding.tvTitle;
        tvStatus = binding.tvStatus;
        recyclerview = binding.recyclerview;
        tvGroupName = binding.tvGroupName;
        tvGroupID = binding.tvGroupID;
        recyclerviewStages = binding.recyclerviewStages;
        collapsing_toolbar_layout = binding.collapsingToolbarLayout;

        //小米UI更新了最新系统，跟先前不兼容了
        //if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //AndroidBarUtils.setTranslucent(this);
        //}


        sys_match_id=getIntent().getStringExtra("sys_match_id");


        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
        collapsing_toolbar_layout.setTitle(getString(R.string.match_detail_title));
        //通过CollapsingToolbarLayout修改字体颜色
        collapsing_toolbar_layout.setExpandedTitleColor(Color.WHITE);
        collapsing_toolbar_layout.setCollapsedTitleTextColor(Color.WHITE);


        mMatchOptionAdapter=new MatchOptionAdapter(this,rankOptions);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);


        recyclerview.addItemDecoration(new RecyclerViewDivider());
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(manager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(mMatchOptionAdapter);

        //规则
        LinearLayoutManager manager2 = new LinearLayoutManager(this);
        manager2.setOrientation(LinearLayoutManager.VERTICAL);
        mStageItemAdapter=new StageItemAdapter(this,dataInfo);
        mStageItemAdapter.setStageItemRuleListener(this);
        recyclerviewStages.addItemDecoration(new RecyclerView.ItemDecoration(){
            @Override
            public void getItemOffsets(Rect outRect, View view,
                                       RecyclerView parent, RecyclerView.State state) {
                outRect.top = -5;
            }
        });
        recyclerviewStages.setHasFixedSize(true);
        recyclerviewStages.setLayoutManager(manager2);
        recyclerviewStages.setAdapter(mStageItemAdapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        //请求锦标赛赛事详情
        requestRankMatchDetail(sys_match_id);
    }

    @Override
    protected void setOnResult() {
           if(signSuccess){
               setResult(REPORT_OK);
           }
    }

    @Override
    protected void supportToolbar() {
        super.supportToolbar();
    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.match_detail_title);
    }

    public void onViewClicked(View v) {
        AppLogger.d("-----------MatchDetailActivity-onViewClicked--------------------");
        if(v.getId()==R.id.btnSignUp){
            if(CheckHelper.onCheckFunc()==CheckHelper.PHONE){
                goToLogin();
            }else{
                if(matchStatus!=3){
                    //未开始---->已开始
                    if(is_join==0){
                        //未报名
                        joinRankMatch(sys_sys_match_id,sys_match_id,is_group);
                    }else if(is_join==1){
                        //已经报名
                        //根据所处赛段进去进入主页面赛段还是摇跑赛赛段
                        if(mDetailModel!=null){
                            if(mDetailModel.getView_type()==1){
                                startRankMatchMainActivity(matchTitle,mDetailModel.getSys_match_id(),mDetailModel.getUser_join_status().getUser_group_id(),mDetailModel.getMatchs_stage_id());
                            }else if(mDetailModel.getView_type()==2){
                                startListMatchActivity(mDetailModel.getSys_match_id(),mDetailModel.getMatchs_stage_id(),mDetailModel.getUser_join_status().getUser_group_id(),mDetailModel.getMatch_title());
                            }else if(mDetailModel.getView_type()==0){
                                Toast.makeText(App.self().getApplicationContext(),R.string.lbl_match_no_match,Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }else if(matchStatus==3){
                    //已经结束
                    startRankActivity(matchTitle,mDetailModel.getSys_match_id(),mDetailModel.getIs_group(),mDetailModel.getIs_exponent());
                }
            }
        }
    }

    /**
     * 打开用户手机号登录页面
     */
    private void goToLogin(){
        Intent it=new Intent(this, LoginOtherActivity.class);
        startActivity(it);
    }

    /**
     * 报名
     * @param sys_sys_match_id
     * @param sys_match_id
     * @param is_group
     */
    private void joinRankMatch(String sys_sys_match_id,String sys_match_id,int is_group){
        if(is_group==0){
            //个人赛直接报名
            Locale locale = getResources().getConfiguration().locale;
            String language = locale.getLanguage();
            if(language.startsWith("zh")){
                language="zh-CN";
            }else{
                language="en-US";
            }
            requestRankMatchSign(sys_sys_match_id,sys_match_id,language);
        }else{
            startRankCreateMatchActivity(sys_sys_match_id,sys_match_id,is_group);
        }
    }

    /**
     * 报名参赛
     * @param sys_sys_match_id
     * @param sys_match_id
     * @param language
     */
    private void requestRankMatchSign(String sys_sys_match_id,String sys_match_id,String language){
        HashMap<String, Object> map = new HashMap<>();
        map.put("sys_sys_match_id", sys_sys_match_id);
        map.put("sys_match_id", sys_match_id);
        map.put("language", language);
        map.put("is_group", 0);

        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<ResponseBody> observable = apiServer.matchSign(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    try{
                        JSONObject jsonObject=new JSONObject(responseBody.string());
                        int code=jsonObject.optInt("code");
                        if(code==1){
                            //报名成功,显示立即比赛
                            Toast.makeText(getApplication(),jsonObject.optString("msg"),Toast.LENGTH_SHORT).show();
                            if(mDetailModel.getUser_join_status()!=null){
                                mDetailModel.getUser_join_status().setIs_join(1);
                                btnSignUp.setText(R.string.lbl_match_right_now);
                            }
                            signSuccess=true;
                            //请求锦标赛赛事详情
                            requestRankMatchDetail(sys_match_id);
                            //finish();
                        }else if(code==2){
                            String msg=jsonObject.optString("msg");
                            Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
                            autoLogin();
                        }else{
                            Toast.makeText(getApplication(),jsonObject.optString("msg"),Toast.LENGTH_SHORT).show();
                            //showDialog(getString(R.string.tip), jsonObject.optString("msg"),null);
                        }
                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                @Override
                public void onError(int code, String msg) {
                    if(code==2){
                        Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
                        autoLogin();
                    }
                    AppLogger.d("---requestRankMatchSign-----"+msg);
                }
            })
        );
    }


    /**
     * 自动登录
     */
    private void autoLogin() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("sys_country", AppDataManager.getInstance().getCountry());
        map.put("device_uid", AppDataManager.getInstance().getAndroidId());
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<UserInfoModel> observable = apiServer.autoLogin(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserInfoModel>() {
                @Override
                public void onSuccess(UserInfoModel userInfoModel) {
                    //把token保存起来
                    AppLogger.d("---onSuccess--UserInfoModel=" + userInfoModel);
                    SPUtils.put(getApplication(), "token", userInfoModel.getUser_info().getToken());
                    AppDataManager.getInstance().setUserInfoModel(userInfoModel);
                    WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken());
                    //登录成功发送获取比赛tabs
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_RANK_MATCH));
                    //弹出登录框
                    startLoginOtherActivity();
                }

                @Override
                public void onError(int code, String msg) {
                    AppLogger.d(msg);
                }
            })
        );
    }


    private void startLoginOtherActivity(){
        Intent it=new Intent(this, LoginOtherActivity.class);
        it.putExtra("resultCode",true);
        startActivityLaunch.launch(it);
    }

    ActivityResultLauncher<Intent> startActivityLaunch =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        int resultCode=result.getResultCode();
        if(resultCode==LoginOtherActivity.LoginOtherActivity_result){

        }else{

        }
    });


    /**
     * 进入报名页面
     * @param sys_sys_match_id
     * @param sys_match_id
     * @param is_group
     */
    private void startRankCreateMatchActivity(String sys_sys_match_id,String sys_match_id,int is_group){
        Intent it=new Intent(this, CreateRankMatchAddActivity.class);
        it.putExtra("sys_sys_match_id",sys_sys_match_id);
        it.putExtra("sys_match_id",sys_match_id);
        it.putExtra("is_group",is_group);
        startActivity(it);
    }

    /**
     * 进入摇跑赛页面
     * @param sys_match_id      赛事ID
     * @param matchs_stage_id   赛段ID
     * @param user_group_id     所属队伍ID
     */
    private void startListMatchActivity(String sys_match_id,String matchs_stage_id,String user_group_id,String match_title){
        Intent it=new Intent(this, MatchMainActivity2.class);
        it.putExtra("sys_match_id",sys_match_id);
        it.putExtra("matchs_stage_id",matchs_stage_id);
        it.putExtra("user_group_id",user_group_id);
        it.putExtra("match_title",match_title);
        startActivity(it);
    }

    /**
     * 锦标赛(主界面赛事)
     * @param title
     * @param sys_match_id
     * @param user_group_id  所属用户组ID
     */
    private void startRankMatchMainActivity(String title,String sys_match_id,String user_group_id,String matchs_stage_id){
        Intent it = new Intent(this, RankMatchMainActivity.class);
        it.putExtra("title",title);
        it.putExtra("sys_match_id",sys_match_id);
        it.putExtra("user_group_id",user_group_id);
        it.putExtra("matchs_stage_id",matchs_stage_id);
        startActivity(it);
    }

    /**
     * 排行榜
     * @param title
     * @param sys_match_id
     * @param is_group
     */
    private void startRankActivity(String title,String sys_match_id,int is_group,int is_exponent){
        Intent it = new Intent(this, MatchRankActivity.class);
        it.putExtra("title",title);
        it.putExtra("sys_match_id",sys_match_id);
        it.putExtra("is_group",is_group);
        it.putExtra("is_exponent",is_exponent);
        startActivity(it);
    }

    private void requestRankMatchDetail(String sys_match_id){
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("sys_match_id", sys_match_id);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<RankMatchDetailModel> observable = apiServer.matchInfo(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<RankMatchDetailModel>() {
                @Override
                public void onSuccess(RankMatchDetailModel detailModel) {
                    showDetail(detailModel);
                }

                @Override
                public void onError(int code, String msg) {
                    AppLogger.d("--------requestRankMatchDetail-------code-"+msg);
                }
            })
        );
    }

    private void showDetail(RankMatchDetailModel detailModel){
        mDetailModel=detailModel;
        //会员报名情况
        is_members=AppDataManager.getInstance().getUserInfoModel().getUser_info().getIs_members();

        tvTitle.setText(detailModel.getMatch_title());
        //赛事状态
        if(detailModel.getMatch_status()==3){
            //赛事已结束
            tvStatus.setTextColor(getResources().getColor(R.color.match_status_deep_red));
            tvStatus.setText("("+detailModel.getMatch_status_title()+")");
        }else{
            if(detailModel.getUser_join_status()!=null){
                if(detailModel.getUser_join_status().getIs_join()==0){
                    //未加入
                    tvStatus.setTextColor(getResources().getColor(R.color.match_status_yellow));
                    String str=getString(R.string.lbl_match_status_info_1);
                    tvStatus.setText("("+str+")");
                }else if(detailModel.getUser_join_status().getIs_join()==1){
                    //已经加入
                    tvStatus.setTextColor(getResources().getColor(R.color.match_status_green));
                    String str2=getString(R.string.lbl_match_status_info_2);
                    tvStatus.setText("("+str2+")");
                }
            }
        }


        if (detailModel.getMatch_image().startsWith("http")) {
            Picasso.with(this)
                    .load(detailModel.getMatch_image())
                    .fit().centerCrop()
                    .into(ivBanner);
        } else {
            Picasso.with(this)
                    .load(Constant.getBaseUrl() + "/" + detailModel.getMatch_image())
                    .fit().centerCrop()
                    .into(ivBanner);
        }

        matchTitle=detailModel.getMatch_title();
        sys_sys_match_id=detailModel.getSys_sys_match_id();
        is_group=detailModel.getIs_group();
        btnSignUp.setVisibility(View.VISIBLE);
        //比赛状态
        matchStatus=detailModel.getMatch_status();
        if(detailModel.getUser_join_status()!=null){
            is_join=detailModel.getUser_join_status().getIs_join();
            //显示所属队伍名称以及ID
            if(!TextUtils.isEmpty(detailModel.getUser_join_status().getUser_group_id())){
                tvGroupName.setText(detailModel.getUser_join_status().getGroup_title());
                tvGroupID.setText("ID:"+detailModel.getUser_join_status().getUser_group_id());
            }else{
                tvGroupName.setVisibility(View.GONE);
                tvGroupID.setVisibility(View.GONE);
            }
        }else{
            btnSignUp.setTextColor(getResources().getColor(R.color.btn_color_yellow_grey));
            btnSignUp.setEnabled(false);
        }

        //显示赛段信息
        showStages(detailModel.getStage());

        btnSignUp.setEnabled(true);
        btnSignUp.setText(R.string.lbl_match_ranking_check);

        if(matchStatus!=3){
            if(matchStatus==1){
               //未开始,按钮显示即将开始
                btnSignUp.setEnabled(false);
                btnSignUp.setBackgroundResource(R.drawable.selector_login_status_btn);
                btnSignUp.setText(R.string.lbl_match_sign_now2);
                btnSignUp.setTextColor(getResources().getColor(R.color.btn_color_grey));
            }else{
                //赛事报名条件 0：开放报名，1：关闭报名，2：允许会员报名
                if(detailModel.getJoin_status()==1 || detailModel.getJoin_status()==2){
                    btnSignUp.setBackgroundResource(R.drawable.selector_login_status_btn);
                    btnSignUp.setTextColor(getResources().getColor(R.color.btn_color_grey));
                    btnSignUp.setEnabled(false);
                    if(is_members==1){
                        btnSignUp.setBackgroundResource(R.drawable.selector_match_btn_sign_up);
                        btnSignUp.setTextColor(getResources().getColor(R.color.match_status_yellow));
                        btnSignUp.setEnabled(true);
                    }
                }else{
                    btnSignUp.setBackgroundResource(R.drawable.selector_match_btn_sign_up);
                    btnSignUp.setTextColor(getResources().getColor(R.color.match_status_yellow));
                    btnSignUp.setEnabled(true);
                }
               //进行中
                btnSignUp.setText(R.string.lbl_match_sign_now);
                if(detailModel.getUser_join_status()!=null){
                    if(detailModel.getUser_join_status().getIs_join()==1){
                        //已报名，立即比赛
                        btnSignUp.setText(R.string.lbl_match_right_now);
                    }
                }
            }
        }


        rankOptions.clear();
        rankOptions.addAll(detailModel.getForm_array());
        recyclerview.forceLayout();
        recyclerview.setAdapter(mMatchOptionAdapter);
        mMatchOptionAdapter.notifyDataSetChanged(rankOptions);
    }

    private void showStages(List<RankMatchDetailModel.RankMatchStateItem> stateItems){
       if(stateItems!=null && stateItems.size()>0){
           dataInfo.clear();
           dataInfo.addAll(stateItems);
           mStageItemAdapter.notifyDataSetChanged();
       }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void invoke(String rule) {
        PKRuleDialog.show(this,rule);
    }
}