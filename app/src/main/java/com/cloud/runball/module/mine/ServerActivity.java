package com.cloud.runball.module.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;

import com.cloud.runball.App;
import com.cloud.runball.BuildConfig;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.module.login.LoginOtherActivity;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.databinding.ActivityServerBinding;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: ServerActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/12 17:02
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/12 17:02
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ServerActivity extends BaseActivity {

    private ActivityServerBinding binding;

    FrameLayout lyChina;
    FrameLayout lyNation;
    TextView tvChinaServer;
    TextView tvNationServer;

    int clickId=-1;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_server;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityServerBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected void initView() {
        lyChina = binding.lyChina;
        lyNation = binding.lyNation;
        tvChinaServer = binding.tvChinaServer;
        tvNationServer = binding.tvNationServer;
        Integer serverType = (Integer) SPUtils.get(this, "server",
                "googleplay".equals(BuildConfig.FLAVOR) ? Constant.NATION_SERVER_TYPE : Constant.CHINA_SERVER_TYPE);
        if(serverType.intValue() == Constant.CHINA_SERVER_TYPE){
            tvChinaServer.setVisibility(View.VISIBLE);
            tvNationServer.setVisibility(View.GONE);
        }else{
            tvChinaServer.setVisibility(View.GONE);
            tvNationServer.setVisibility(View.VISIBLE);
        }
        // Replace @OnClick with listeners
        lyChina.setOnClickListener(this::onClick);
        lyNation.setOnClickListener(this::onClick);
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        listener=null;
        clickId=-1;
    }

    @Override
    protected String getTitleLabel() {
         return getString(R.string.title_server);
    }

    public void onClick(View v) {
        Integer serverType = (Integer) SPUtils.get(this, "server", "googleplay".equals(BuildConfig.FLAVOR)?Constant.NATION_SERVER_TYPE:Constant.CHINA_SERVER_TYPE);
        if(v.getId()==R.id.lyChina){
            if(serverType.intValue()==Constant.NATION_SERVER_TYPE){
                clickId=R.id.lyChina;
                showDialog(getResources().getString(R.string.tip_warn),getResources().getString(R.string.tip_server),R.id.lyChina);
            }
        }else if(v.getId()==R.id.lyNation){
            //跟当前不同服务器则需切换
            if(serverType.intValue()==Constant.CHINA_SERVER_TYPE){
                clickId=R.id.lyNation;
                showDialog(getResources().getString(R.string.tip_warn),getResources().getString(R.string.tip_server),R.id.lyNation);
            }
        }
    }

    private void showDialog(String title, String message,@IdRes int clickId) {
        AlertDialog dialog= new AlertDialog.Builder(ServerActivity.this).setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.btn_confirm, listener).create();

        dialog.show();
    }

    DialogInterface.OnClickListener listener= (dialog, which) -> {
        if(clickId!=-1){
            handlerClick(clickId);
        }
        dialog.dismiss();
        dialog=null;
        finish();
    };


    private void handlerClick(@IdRes int clickId){
        Integer serverType = (Integer) SPUtils.get(this, "server", "googleplay".equals(BuildConfig.FLAVOR)?Constant.NATION_SERVER_TYPE:Constant.CHINA_SERVER_TYPE);
        if(clickId==R.id.lyChina){
            if(serverType.intValue()==Constant.NATION_SERVER_TYPE){
                //跟当前不同服务器则需切换
                Constant.setServer(Constant.CHINA_SERVER_TYPE);
                SPUtils.put(getApplicationContext(),"server",Constant.CHINA_SERVER_TYPE);
                startLoginOtherActivity();
            }
        }else if(clickId==R.id.lyNation){
            //跟当前不同服务器则需切换
            if(serverType.intValue()==Constant.CHINA_SERVER_TYPE){
                Constant.setServer(Constant.NATION_SERVER_TYPE);
                SPUtils.put(getApplicationContext(),"server",Constant.NATION_SERVER_TYPE);
                startLoginOtherActivity();
            }
        }
    }

    /**
     * 切换服务器需要清理的信息
     */
    private void switchServerForResult(){
        App.self().clearCacheData();
    }


    private void startLoginOtherActivity(){
        Intent it=new Intent(this, LoginOtherActivity.class);
        it.putExtra("resultCode",true);
        startActivityLaunch.launch(it);
    }

    ActivityResultLauncher<Intent> startActivityLaunch =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        int resultCode=result.getResultCode();
        if(resultCode==LoginOtherActivity.LoginOtherActivity_result){

        }else if(resultCode==LoginOtherActivity.LoginOtherActivity_result2){
            //关闭
            switchServerForResult();
            finish();
        }else{
            finish();
        }
    });

}
