package com.cloud.runball.module.login;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.UserInfo;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.orhanobut.logger.Logger;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 登陆部分
 */
public class LoginActivity extends BaseActivity {


    //private CallbackManager fbCallback = CallbackManager.Factory.create();

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.app_name);
    }

    @Override
    protected void initView() {
        WristBallServer apiServer =WristBallRetrofitHelper.getInstance().getWristBallService();
        Observable<List<UserInfo>> observable  =apiServer.getUsers();

        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<List<UserInfo>>() {

                @Override
                public void onSuccess(List<UserInfo> o) {
                    Logger.d(o.toString());
                }

                @Override
                public void onError(int code, String msg) {

                }
            })
        );
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    protected void supportToolbar() {

    }

}
