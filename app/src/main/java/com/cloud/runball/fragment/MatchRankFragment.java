package com.cloud.runball.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.RecyclerViewDivider;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.module.mine.adapter.PersonMatchAdapter;
import com.cloud.runball.bean.MatchRankData;
import com.cloud.runball.model.MatchRankModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.LayoutMatchRankBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.fragment
 * @ClassName: MatchRankFragment
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/24 9:52
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/24 9:52
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRankFragment extends Fragment implements View.OnClickListener{

    static final String ID="is_group";
    static final String MATCH_ID="sys_match_id";
    static final String STATE_ID="matchs_stage_id";
    static final String IS_EXPONENT="is_exponent";

    private int is_group;
    private String sys_match_id;
    private String matchs_stage_id;
    private int is_exponent;

    private LayoutMatchRankBinding binding;
    private RecyclerView rvRank;
    private PersonMatchAdapter mPersonMatchAdapter;
    private TextView tvRankNum;
    private ImageView ivHead;
    private TextView tvUserName;
    private TextView tvUserSpeed;
    private TextView tvRankTime;
    private LinearLayout lyBottom;
    private TextView tvArea;

    List<MatchRankData> list=new ArrayList<>();

    private CompositeDisposable disposable = new CompositeDisposable();

    public static MatchRankFragment newInstance(String sys_match_id,String matchs_stage_id,int is_group,int is_exponent) {
        MatchRankFragment fragment = new MatchRankFragment();
        Bundle args = new Bundle();
        args.putString(MATCH_ID, sys_match_id);
        args.putString(STATE_ID, matchs_stage_id);
        args.putInt(ID, is_group);
        args.putInt(IS_EXPONENT, is_exponent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sys_match_id = getArguments().getString(MATCH_ID);
            matchs_stage_id = getArguments().getString(STATE_ID);
            is_group = getArguments().getInt(ID,0);
            is_exponent = getArguments().getInt(IS_EXPONENT,0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = LayoutMatchRankBinding.inflate(inflater, container, false);

        rvRank = binding.rvRank;
        tvRankNum = binding.tvRankNum;
        ivHead = binding.ivHead;
        tvUserName = binding.tvUserName;
        tvUserSpeed = binding.tvUserSpeed;
        tvRankTime = binding.tvRankTime;
        lyBottom = binding.lyBottom;
        tvArea = binding.tvArea;

        mPersonMatchAdapter=new PersonMatchAdapter(getActivity(),list);
        mPersonMatchAdapter.setExponent(is_exponent);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        rvRank.addItemDecoration(new RecyclerViewDivider(0));
        rvRank.setHasFixedSize(true);
        rvRank.setLayoutManager(manager);
        rvRank.setItemAnimator(new DefaultItemAnimator());
        rvRank.setAdapter(mPersonMatchAdapter);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestMatchRanking( sys_match_id, matchs_stage_id, is_group,is_exponent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rvRank = null;
        list.clear();
        mPersonMatchAdapter = null;
        disposable.dispose();
        binding = null;
    }

    @Override
    public void onClick(View v) {

    }


    private void requestMatchRanking(String sys_match_id,String matchs_stage_id,int is_group,int is_exponent){
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(4);
        map.put("sys_match_id", sys_match_id);
        map.put("matchs_stage_id", matchs_stage_id);
        map.put("is_group", is_group);
        map.put("is_exponent", is_exponent);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<MatchRankModel> observable = apiServer.matchStageMatchlist(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<MatchRankModel>() {
                @Override
                public void onSuccess(MatchRankModel matchRankModel) {
                    if(mPersonMatchAdapter!=null){
                        list.clear();
                        list.addAll(matchRankModel.getRankingList());
//                        if(matchRankModel.getMy_grade()!=null){
//                            tvRankNum.setText(String.valueOf(matchRankModel.getMy_grade().getMatch_ranking()));
//                        }


                        if (matchRankModel.getMy_grade() != null) {
                            lyBottom.setVisibility(View.VISIBLE);
                            if(matchRankModel.getMy_grade().getUser_img().startsWith("http")) {
                                Picasso.with(MatchRankFragment.this.getContext())
                                    .load(matchRankModel.getMy_grade().getUser_img())
                                    .transform(new CircleTransform(MatchRankFragment.this.getContext()))
                                    .placeholder(R.mipmap.default_head)
                                    .into(ivHead);
                            } else {
                                Picasso.with(MatchRankFragment.this.getContext())
                                    .load(Constant.getBaseUrl() + "/" + matchRankModel.getMy_grade().getUser_img())
                                    .transform(new CircleTransform(MatchRankFragment.this.getContext()))
                                    .placeholder(R.mipmap.default_head)
                                    .into(ivHead);
                            }
                            tvRankNum.setText(String.valueOf(matchRankModel.getMy_grade().getMatch_ranking()));
                            Drawable drawableSex = null;
                            if (SexConstant.SEX_MAN.equals(matchRankModel.getMy_grade().getSys_sex_id())) {
                                drawableSex = getResources().getDrawable(R.mipmap.ic_man);
                                drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
                            } else if (SexConstant.SEX_WOMEN.equals(matchRankModel.getMy_grade().getSys_sex_id())) {
                                drawableSex = getResources().getDrawable(R.mipmap.ic_women);
                                drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
                            }

                            tvArea.setText(matchRankModel.getMy_grade().getAddress());

                            tvUserName.setCompoundDrawables(drawableSex, null, null, null);
                            tvUserName.setText(matchRankModel.getMy_grade().getUser_name());
//                            if (TextUtils.isEmpty(matchRankModel.getMy_grade().getUnit())) {
//                                tvUserSpeed.setText(matchRankModel.getMy_grade().getValue());
//
//                            } else {
//                                tvUserSpeed.setText(matchRankModel.getMy_grade().getValue()+ "(" + matchRankModel.getMy_grade().getUnit() + ")");
//                            }
                            tvRankTime.setText(matchRankModel.getMy_grade().getCreated_time());
                        } else {
                            lyBottom.setVisibility(View.GONE);
                        }

                        mPersonMatchAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onError(int code, String msg) {
                    AppLogger.d("----- 赛事中的赛段tab下列表--------" + msg);
                }
            })
        );
    }

}
