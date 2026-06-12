package com.cloud.runball.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.RecycleViewDivider;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.module.mine.MatchRecordDetailActivity;
import com.cloud.runball.module.mine.MatchRecordTeamDetailActivity;
import com.cloud.runball.module.match.adapter.MatchRecordAdapter;
import com.cloud.runball.bean.PKDataResp;
import com.cloud.runball.model.PKDataRespModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
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
 * @Package: com.cloud.runball.fragment
 * @ClassName: MatchRecordFragment
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/15 19:35
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/15 19:35
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRecordFragment extends Fragment implements View.OnClickListener, MatchRecordAdapter.OnItemClickListener {



    private boolean isFirstLoad = true;
    int page=1;
    List<PKDataResp> list = new ArrayList<>();

    private CompositeDisposable disposable = new CompositeDisposable();

    public static MatchRecordFragment newInstance() {
        return new MatchRecordFragment();
    }

    public MatchRecordFragment() {
    }

    XRecyclerView recyclerview;
    MatchRecordAdapter mMatchAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_match_record, container, false);
        initView(root);

        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        if (isFirstLoad) {
            isFirstLoad = false;
            onLazyLoadData();
        }
    }

    private void onLazyLoadData(){
        //默认填第一页
        requestPKData(page);
    }

    private void initView(View root) {
        recyclerview = root.findViewById(R.id.recyclerview);

        mMatchAdapter = new MatchRecordAdapter(list);
        mMatchAdapter.setOnItemClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL));
        recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        recyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
        recyclerview.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mMatchAdapter.notifyDataSetChanged();
                recyclerview.refreshComplete();
            }
            @Override
            public void onLoadMore() {
                requestPKData(page+1);
            }
        });
        recyclerview.setAdapter(mMatchAdapter);
        recyclerview.setLoadingMoreEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(recyclerview!=null){
            recyclerview.destroy();
            recyclerview = null;
        }
        isFirstLoad = true;
        disposable.dispose();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(int position, PKDataResp data) {
       //查看详情
       if(data.getPk_type()==0){
           //双人比赛
           startMatchRecordDetail(0,data.getPk_room_id(),data.getUser_group());
       }else{
           //团队比赛
           startMatchRecordTeamDetail(1,data.getPk_room_id(),data.getUser_group());
       }
    }

    /**
     * 双人PK
     * @param pk_room_id
     * @param user_group
     */
    private void startMatchRecordDetail(int pk_type,String pk_room_id,String user_group){
        Intent it=new Intent(getActivity(), MatchRecordDetailActivity.class);
        it.putExtra("pk_type",pk_type);
        it.putExtra("pk_room_id",pk_room_id);
        it.putExtra("user_group",user_group);
        startActivity(it);
    }

    /**
     * 团队pK
     * @param pk_room_id
     * @param user_group
     */
    private void startMatchRecordTeamDetail(int pk_type,String pk_room_id,String user_group){
        Intent it=new Intent(getActivity(), MatchRecordTeamDetailActivity.class);
        it.putExtra("pk_type",pk_type);
        it.putExtra("pk_room_id",pk_room_id);
        it.putExtra("user_group",user_group);
        startActivity(it);
    }

    /**
     * @param tempPage
     */
    private void requestPKData(int tempPage) {
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("page", tempPage);
        map.put("limit", 10);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<PKDataRespModel> observable = apiServer.myPK(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<PKDataRespModel>() {
                @Override
                public void onSuccess(PKDataRespModel pKDataRespModel) {
                    //把token保存起来
                    AppLogger.d(pKDataRespModel.toString());
                    if(pKDataRespModel.getCount()>0){
                        page=tempPage;
                        list.addAll(pKDataRespModel.getList());
                        if(recyclerview!=null && mMatchAdapter!=null){
                            mMatchAdapter.notifyDataSetChanged();
                            recyclerview.loadMoreComplete();
                        }
                    }
                }
                @Override
                public void onError(int code, String msg) {
                    AppLogger.d(msg);
                }
            })
        );

    }
}
