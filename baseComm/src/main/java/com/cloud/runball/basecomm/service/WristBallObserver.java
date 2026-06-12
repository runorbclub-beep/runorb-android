package com.cloud.runball.basecomm.service;


import com.cloud.runball.basecomm.base.BaseView;
import com.cloud.runball.basecomm.service.gson.BaseException;
import com.google.gson.JsonParseException;
import org.json.JSONException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

public abstract class WristBallObserver<T> extends DisposableObserver<T> {

    protected BaseView view;

    private boolean isShowDialog;

    public WristBallObserver() {

    }
    public WristBallObserver(BaseView view) {
        this.view = view;
    }

    public WristBallObserver(BaseView view, boolean isShowDialog) {
        this.view = view;
        this.isShowDialog = isShowDialog;
    }

    @Override
    protected void onStart() {
        if (view != null && isShowDialog) {
            view.showLoading();
        }
    }

    @Override
    public void onNext(T o) {
        onSuccess(o);
    }

    @Override
    public void onError(Throwable e) {
        if (view != null && isShowDialog) {
            view.hideLoading();
        }
        BaseException be = null;
        if (e != null) {

            if (e instanceof BaseException) {
                be = (BaseException) e;

                //回调到view层 处理 或者根据项目情况处理
                if (view != null) {
                    view.onErrorCode(be.getErrorCode(), be.getErrorMsg());
                } else {
                    onError(be.getErrorCode(),be.getErrorMsg());
                }
            } else {
                if (e instanceof HttpException) {
                    //   HTTP错误
                    be = new BaseException(((HttpException) e).message(), e, ((HttpException) e).code());
                } else if (e instanceof ConnectException
                        || e instanceof UnknownHostException) {
                    //   连接错误
                    be = new BaseException(BaseException.CONNECT_ERROR_MSG, e, BaseException.CONNECT_ERROR);
                } else if (e instanceof InterruptedIOException) {
                    //  连接超时
                    be = new BaseException(BaseException.CONNECT_TIMEOUT_MSG, e, BaseException.CONNECT_TIMEOUT);
                } else if (e instanceof JsonParseException
                        || e instanceof JSONException
                        || e instanceof ParseException) {
                    //  解析错误
                    be = new BaseException(BaseException.PARSE_ERROR_MSG, e, BaseException.PARSE_ERROR);
                }else if (e instanceof javax.net.ssl.SSLHandshakeException) {
                    // 网络连接超时
                    be = new BaseException(((javax.net.ssl.SSLHandshakeException) e).getMessage(), e, BaseException.BAD_NETWORK);
                }
                else {
                    be = new BaseException(BaseException.OTHER_MSG, e, BaseException.OTHER);
                }
            }
        } else {
            be = new BaseException(BaseException.OTHER_MSG, e, BaseException.OTHER);
        }

        onError(be.getErrorCode(),be.getErrorMsg());
        onComplete();
    }

    @Override
    public void onComplete() {
        if (view != null && isShowDialog) {
            view.hideLoading();
        }
    }

    public abstract void onSuccess(T o);

    public abstract void onError(int code,String msg);

}
