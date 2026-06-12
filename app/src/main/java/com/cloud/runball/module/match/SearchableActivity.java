package com.cloud.runball.module.match;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.base.RecyclerViewDivider;
import com.cloud.runball.module.match.adapter.SearchResultAdapter;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.cloud.runball.databinding.SearchQueryResultsBinding;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: SearchableActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/26 18:33
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/26 18:33
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class SearchableActivity extends BaseActivity implements SearchResultAdapter.OnItemClickListener {

    public static final String SEARCH="search";

    private SearchQueryResultsBinding binding;
    ImageView img_return;
    TextView tvCancel;
    RecyclerView rvResult;
    TagFlowLayout flowlayout;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.search_query_results;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = SearchQueryResultsBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_privacy);
    }

    @Override
    protected void initView() {
        HiddenNavigation();
        img_return = binding.imgReturn;
        tvCancel = binding.tvCancel;
        rvResult = binding.rvResult;
        flowlayout = binding.flowlayout;


        List<String> list=new ArrayList<>();
        list.add("2021年度总决赛-100米");
        list.add("第一季度季后赛-100米");

        SearchResultAdapter adapter=new SearchResultAdapter(this,list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        rvResult.addItemDecoration(new RecyclerViewDivider());
        rvResult.setHasFixedSize(true);
        rvResult.setLayoutManager(manager);
        rvResult.setItemAnimator(new DefaultItemAnimator());
        rvResult.setAdapter(adapter);
        adapter.setOnItemClickListener(this);


        List<String> mVals=new ArrayList<>();
        mVals.add("2021年度总决赛");
        mVals.add("2021年度总决赛");
        mVals.add("马拉松");
        mVals.add("100米");

        flowlayout.setAdapter(new TagAdapter<String>(mVals) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                final LayoutInflater mInflater = LayoutInflater.from(getApplication());
                TextView tv = (TextView) mInflater.inflate(R.layout.search_query_hot_item,flowlayout,false);
                tv.setText(s);
                return tv;
            }
        });


        flowlayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                if(selectPosSet.size()>0){
                    int pos=selectPosSet.iterator().next();
                    Toast.makeText(getApplicationContext(),mVals.get(pos),Toast.LENGTH_LONG).show();
                }
            }
        });

        // Replace @OnClick with listeners
        img_return.setOnClickListener(this::onViewClicked);
        tvCancel.setOnClickListener(this::onViewClicked);
    }

    @Override
    protected void setOnResult() {

    }

    private void doMySearch( String query){

    }

    public void onViewClicked(View v){
        if(v.getId()==R.id.img_return || v.getId()==R.id.tvCancel){
            finish();
        }
    }


    @Override
    public void onItemClick(View view, String data) {
        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
    }
}
